package com.etiennek.check.integration.store.raru;

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
public class RaruExtractor implements Extractor {

	private static String BASE_URL = "http://www.raru.co.za";
	private static String PAGE_URL = BASE_URL + "/boards-dice/all/?dept=boards-dice&all=1&page={page}";

	@Override
	public Observable<Item> extract() {
		return Observable.create(subscriber -> {
			requestPage(1).addCallback(handlePage(subscriber, 0), subscriber::onError);
		});
	}

	private SuccessCallback<? super ResponseEntity<String>> handlePage(Subscriber<? super Item> subscriber, int page) {
		return httpEntity -> {

			List<Item> items = extractItems("div.item", httpEntity).stream().collect(Collectors.toList());

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
		return restCall(PAGE_URL, m().put("page", page).build());
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
		String name = el.select("span[itemprop=name]").text();

		String priceString = el.select("span[itemprop=price]").get(0).childNodes().stream()
				.filter(n -> (n instanceof TextNode) && !n.toString().trim().equals("")).findFirst().get().toString();

		BigDecimal price = new BigDecimal(priceString.replaceAll(",", "").replaceAll("R", "").trim());

		String url = BASE_URL + el.select("td cite a").attr("href").toString();

		StockStatus stockStatus = StockStatus.IN_STOCK_STORE;
		if (el.select("div.avail").text().trim().toLowerCase().contains("out of stock")) {
			stockStatus = StockStatus.OUT_OF_STOCK;
		} else if (el.select("div.avail").text().trim().toLowerCase().contains("pre-order")) {
			stockStatus = StockStatus.PRE_ORDER;
		} else if (el.select("div.avail").text().trim().toLowerCase().contains("unreleased")) {
			stockStatus = StockStatus.UNRELEASED;
		} else if (el.select("div.avail").text().trim().toLowerCase().contains("dispatched in")) {
			stockStatus = StockStatus.IN_STOCK_SUPPLIER;
		}

		String id = BASE_URL + el.select("figure > a").attr("href");

		return new Item(id, name, price, stockStatus, url);
	}
}
