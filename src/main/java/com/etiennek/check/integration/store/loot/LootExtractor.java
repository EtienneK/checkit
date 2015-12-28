package com.etiennek.check.integration.store.loot;

import static com.etiennek.util.Utils.m;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
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
public class LootExtractor implements Extractor {

	private static String BASE_URL = "http://www.loot.co.za";
	private static String PAGE_URL = BASE_URL + "/search/board-games?offset={offset}&sort=&ipp={pagesize}&cat=kbj";

	@Override
	public Observable<Item> extract() {
		return Observable.create(subscriber -> {
			requestPage(1).addCallback(handlePage(subscriber, 0), subscriber::onError);
		});
	}

	private SuccessCallback<? super ResponseEntity<String>> handlePage(Subscriber<? super Item> subscriber, int page) {
		return httpEntity -> {

			List<Item> items = extractItems("div.productListing", httpEntity).stream().collect(Collectors.toList());

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
		final int pageSize = 25;
		return restCall(PAGE_URL, m().put("pagesize", pageSize).put("offset", page * pageSize).build());
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
		String name = el.select("td cite a").text();

		String priceString = el.select("span.price").get(0).childNodes().stream()
				.filter(n -> (n instanceof TextNode) && !n.toString().trim().equals("")).findFirst()
				.map(s -> s.toString()).orElse("0.00");

		BigDecimal price = new BigDecimal(priceString.replaceAll(",", "").replaceAll("R", "").trim());
		BigDecimal normalPrice = price;

		if (!el.select("span.price del").isEmpty()) {
			normalPrice = new BigDecimal(
					el.select("span.price del").text().replaceAll(",", "").replaceAll("R", "").trim());
		}

		String url = BASE_URL + el.select("td cite a").attr("href").toString();

		StockStatus stockStatus = StockStatus.IN_STOCK_SUPPLIER;
		String stockStatusString = el.select("td span.availability").text().trim().toLowerCase();
		if (stockStatusString.equals("in stock")) {
			stockStatus = StockStatus.IN_STOCK_STORE;
		} else if (stockStatusString.equals("out of stock")) {
			stockStatus = StockStatus.OUT_OF_STOCK;
		}

		String id = url.substring(url.lastIndexOf("/") + 1);

		return new Item(id, name, price, normalPrice, stockStatus, url);
	}
}
