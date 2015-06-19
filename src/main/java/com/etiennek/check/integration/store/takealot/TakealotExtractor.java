package com.etiennek.check.integration.store.takealot;

import static com.etiennek.util.Utils.m;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.client.AsyncRestTemplate;

import rx.Observable;
import rx.Subscriber;

import com.etiennek.check.integration.store.Extractor;
import com.etiennek.check.integration.store.Item;
import com.etiennek.check.integration.store.StockStatus;

public class TakealotExtractor implements Extractor {

  private static String BASE_URL = "http://www.takealot.com";
  private static String PAGE_URL = BASE_URL + "/toys/board-games?pagesize={pagesize}&page={page}";

  @Override
  public Observable<Item> extract() {
    return Observable.create(subscriber -> {
      requestPage(1).addCallback(handlePage(subscriber, 1), ex -> subscriber.onError(ex));
    });
  }

  private SuccessCallback<? super ResponseEntity<String>> handlePage(Subscriber<? super Item> subscriber, int page) {
    return (httpEntity) -> {

      List<Item> items = extract(httpEntity).stream()
                                            .filter(item -> item != null)
                                            .collect(Collectors.toList());

      if (items.size() == 0) {
        subscriber.onCompleted();
        return;
      }

      int nextPage = page + 1;
      requestPage(nextPage).addCallback(handlePage(subscriber, nextPage), ex -> subscriber.onError(ex));

      items.forEach(item -> {
        subscriber.onNext(item);
      });

    };
  }

  private ListenableFuture<ResponseEntity<String>> requestPage(int page) {
    AsyncRestTemplate rest = new AsyncRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("User-Agent", "Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");

    return rest.exchange(PAGE_URL, HttpMethod.GET, new HttpEntity<>(headers), String.class, m().put("pagesize", 100)
                                                                                               .put("page", page)
                                                                                               .build());
  }

  private List<Item> extract(ResponseEntity<String> httpEntity) {
    return Jsoup.parse(httpEntity.getBody())
                .select("li.result-item")
                .stream()
                .map((el) -> {
                  try {
                    return map(el);
                  } catch (Exception e) {
                    // TODO: Error logging
                    return null;
                  }
                })
                .collect(Collectors.toList());
  }

  private Item map(Element el) {
    String name = el.select("p.p-title")
                    .get(0)
                    .text();

    BigDecimal price = new BigDecimal(el.select("p.price span.amount")
                                        .get(0)
                                        .text()
                                        .replaceAll(",", ""));

    String url = BASE_URL + el.select("p.p-title a")
                              .get(0)
                              .attr("href")
                              .toString();

    StockStatus stockStatus = StockStatus.OUT_OF_STOCK;
    if (el.select("div.shipping-information span.in-stock")
          .isEmpty() == false) {
      stockStatus = StockStatus.IN_STOCK_STORE;
    } else if (el.select("div.shipping-information span.wha strong")
                 .isEmpty() == false) {
      stockStatus = StockStatus.IN_STOCK_SUPPLIER;
    }

    String storeId = url.substring(url.lastIndexOf("/") + 1);

    return new Item(storeId, name, price, stockStatus, url);
  }
}
