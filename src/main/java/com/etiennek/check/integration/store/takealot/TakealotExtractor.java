package com.etiennek.check.integration.store.takealot;

import static com.etiennek.util.Utils.m;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import rx.Observable;
import rx.Subscriber;

import com.etiennek.check.integration.store.Extractor;
import com.etiennek.check.integration.store.Item;
import com.etiennek.check.integration.store.StockStatus;

@Component
public class TakealotExtractor implements Extractor {

	private static String BASE_URL = "http://www.takealot.com";
	private static String PAGE_URL = BASE_URL + "/toys/games-1996?pagesize={pagesize}&page={page}";

	@Override
	public Observable<Item> extract() {
		return Observable.create(subscriber -> {
			requestPage(1).addCallback(handlePage(subscriber, 1), subscriber::onError);
		});
	}

	private SuccessCallback<? super ResponseEntity<String>> handlePage(Subscriber<? super Item> subscriber, int page) {
		return httpEntity -> {

			List<Item> items = extractItems("li.result-item", httpEntity).stream().collect(Collectors.toList());

			if (items.size() == 0) {
				subscriber.onCompleted();
				return;
			}

			items.forEach(subscriber::onNext);
			
			int nextPage = page + 1;
			requestPage(nextPage).addCallback(handlePage(subscriber, nextPage), subscriber::onError);
		};
	}

	private ListenableFuture<ResponseEntity<String>> requestPage(int page) {
		return restCall(PAGE_URL, m().put("pagesize", 100).put("page", page).build());
	}

	private List<Item> extractItems(String itemSelector, ResponseEntity<String> httpEntity) {
		return Jsoup.parse(httpEntity.getBody()).select(itemSelector).stream().map(el -> {
			try {
				return map(el);
			} catch (Exception e) {
				return Item.invalidItem(el.toString(), e);
			}
		}).collect(Collectors.toList());
	}

	private Item map(Element el) {
		String name = el.select("p.p-title").text();

		BigDecimal price = new BigDecimal(el.select("p.price span.amount").text().replaceAll(",", ""));

		String url = BASE_URL + el.select("p.p-title a").attr("href").toString();

		StockStatus stockStatus = StockStatus.OUT_OF_STOCK;
		if (el.select("div.shipping-information span.in-stock").isEmpty() == false) {
			stockStatus = StockStatus.IN_STOCK_STORE;
		} else if (el.select("div.shipping-information span.wha strong").isEmpty() == false) {
			stockStatus = StockStatus.IN_STOCK_SUPPLIER;
		}

		String id = url.substring(url.lastIndexOf("/") + 1);

		return new Item(id, name, price, stockStatus, url);
	}
}
