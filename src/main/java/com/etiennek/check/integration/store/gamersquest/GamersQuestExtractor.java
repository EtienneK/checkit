package com.etiennek.check.integration.store.gamersquest;

import static com.etiennek.util.Utils.m;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
public class GamersQuestExtractor implements Extractor {

	private static String BASE_URL = "http://www.gamersquestsa.com";
	private static String PAGE_URL = BASE_URL + "/games.html?limit={limit}&p={page}";

	private boolean done = false;

	@Override
	public Observable<Item> extract() {
		return Observable.create(subscriber -> {
			requestPage(1).addCallback(handlePage(subscriber, 1), subscriber::onError);
		});
	}

	private SuccessCallback<? super ResponseEntity<String>> handlePage(Subscriber<? super Item> subscriber, int page) {
		return httpEntity -> {
			if (done) {
				subscriber.onCompleted();
				return;
			}

			List<Item> items = extractItems("#products-list li.item", httpEntity).stream().collect(Collectors.toList());

			int nextPage = page + 1;
			requestPage(nextPage).addCallback(handlePage(subscriber, nextPage), subscriber::onError);

			items.forEach(subscriber::onNext);
		};
	}

	private ListenableFuture<ResponseEntity<String>> requestPage(int page) {
		return restCall(PAGE_URL, m().put("limit", 25).put("page", page).build());
	}

	private List<Item> extractItems(String itemSelector, ResponseEntity<String> httpEntity) {
		Document document = Jsoup.parse(httpEntity.getBody());

		if (document.select("a.next").isEmpty()) {
			done = true;
		}

		return document.select(itemSelector).stream().map(el -> {
			try {
				return map(el);
			} catch (Exception e) {
				return Item.invalidItem(el.toString(), e);
			}
		}).collect(Collectors.toList());
	}

	private Item map(Element el) {
		String name = WordUtils.capitalizeFully(el.select("h2.product-name").text());

		BigDecimal price = new BigDecimal(el.select("span.price").text().replaceAll(",", "").replaceAll("R", ""));

		String url = el.select("h2.product-name a").attr("href").toString();

		StockStatus stockStatus = StockStatus.IN_STOCK_STORE;

		String id = el.select("span.regular-price").attr("id").toString().replaceAll("product-price-", "");

		return new Item(id, name, price, stockStatus, url);
	}

}
