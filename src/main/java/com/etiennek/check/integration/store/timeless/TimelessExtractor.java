package com.etiennek.check.integration.store.timeless;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import rx.Observable;
import rx.Subscriber;

import com.etiennek.check.integration.store.Extractor;
import com.etiennek.check.integration.store.Item;
import com.etiennek.check.integration.store.StockStatus;

public class TimelessExtractor implements Extractor {

  private static String BASE_URL = "http://www.timelessboardgames.co.za";
  private static String BG_PAGE_URL = BASE_URL + "/store/view-all/";
  private static String BG_EPX_PAGE_URL = BASE_URL + "/store/expansions/";

  @Override
  public Observable<Item> extract() {
    return Observable.<String>create(subscriber -> {
      requestPage(BG_EPX_PAGE_URL).addCallback(handleExpansionListPage(subscriber), subscriber::onError);
    })
                     .flatMap((url) -> {
                       return Observable.create(subscriber -> {
                         requestPage(url).addCallback(handleItemListPage(subscriber), subscriber::onError);
                       });
                     });
  }

  private SuccessCallback<? super ResponseEntity<String>> handleExpansionListPage(Subscriber<? super String> subscriber) {
    return httpEntity -> {

      subscriber.onNext(BG_PAGE_URL);
      Jsoup.parse(httpEntity.getBody())
           .select("div.grid_3 div.box span.thumbnail p strong a")
           .stream()
           .map(el -> BASE_URL + "/" + el.attr("href"))
           .forEach(subscriber::onNext);

      subscriber.onCompleted();
    };
  }

  private SuccessCallback<? super ResponseEntity<String>> handleItemListPage(Subscriber<? super Item> subscriber) {
    return httpEntity -> {

      List<Item> items = extractItems("div.grid_3 div.box", httpEntity).stream()
                                                                       .collect(Collectors.toList());

      items.forEach(subscriber::onNext);
      subscriber.onCompleted();
    };
  }

  private ListenableFuture<ResponseEntity<String>> requestPage(String url) {
    return restCall(url);
  }

  private List<Item> extractItems(String itemSelector, ResponseEntity<String> httpEntity) {
    return Jsoup.parse(httpEntity.getBody())
                .select(itemSelector)
                .stream()
                .map(el -> {
                  try {
                    return map(el);
                  } catch (Exception e) {
                    return Item.invalidItem(el.toString(), e);
                  }
                })
                .collect(Collectors.toList());
  }

  private Item map(Element el) {
    String name = el.select("span.thumbnail strong div a")
                    .text();

    String priceText;
    Elements priceElements = el.select("span.thumbnail span.price span");
    if (!priceElements.isEmpty()) {
      // On Sale
      priceText = priceElements.text();
    } else {
      priceElements = el.select("span.thumbnail span.price");
      priceText = priceElements.text();
    }
    BigDecimal price = new BigDecimal(priceText.replaceAll(",", "")
                                               .replaceAll("R", "")
                                               .trim());

    String url = BASE_URL + el.select("span.thumbnail strong div a")
                              .attr("href")
                              .toString();

    StockStatus stockStatus = StockStatus.IN_STOCK_STORE;

    String id = url.substring(url.lastIndexOf("/") + 1);

    return new Item(id, name, price, stockStatus, url);
  }
}
