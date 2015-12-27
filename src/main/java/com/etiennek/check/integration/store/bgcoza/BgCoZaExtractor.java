package com.etiennek.check.integration.store.bgcoza;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import rx.Observable;
import rx.Subscriber;

import com.etiennek.check.integration.store.Extractor;
import com.etiennek.check.integration.store.Item;
import com.etiennek.check.integration.store.StockStatus;

@Component
public class BgCoZaExtractor implements Extractor {

	private static String BASE_URL = "http://www.boardgames.co.za";
	private static String BG_PAGE_URL = BASE_URL + "/all-the-games.html";
	private static String BG_EXP_PAGE_URL = BASE_URL + "/expansion-packs.html";

	@Override
	public Observable<Item> extract() {
		return Observable.from(new String[] { BG_PAGE_URL, BG_EXP_PAGE_URL }).flatMap(url -> {
			return Observable.create(subscriber -> {
				requestPage(url).addCallback(handleItemListPage(subscriber), subscriber::onError);
			});
		});
	}

	private SuccessCallback<? super ResponseEntity<String>> handleItemListPage(Subscriber<? super Item> subscriber) {
		return httpEntity -> {
			extractItems("div.hikashop_products_listing div.hikashop_products div.hikashop_subcontainer", httpEntity)
					.stream().forEach(subscriber::onNext);
			subscriber.onCompleted();
		};
	}

	private ListenableFuture<ResponseEntity<String>> requestPage(String url) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("limit_hikashop_category_information_module_120_17", "0");
		body.add("limitstart_hikashop_category_information_module_120_17", "0");
		body.add("filter_order_hikashop_category_information_module_120_17", "a.ordering");
		body.add("filter_order_Dir_hikashop_category_information_module_120_17", "ASC");

		body.add("limit_hikashop_category_information_module_120_23", "0");
		body.add("limitstart_hikashop_category_information_module_120_23", "0");
		body.add("filter_order_hikashop_category_information_module_120_23", "a.ordering");
		body.add("filter_order_Dir_hikashop_category_information_module_120_23", "ASC");
		return restCall(url, HttpMethod.POST, body);
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
		String name = el.select("span.hikashop_product_name a").text();

		BigDecimal price;

		try {
			price = new BigDecimal(el.select("span.hikashop_product_price").text().replaceAll(",", ".")
					.replaceAll("ZAR", "").replaceAll(" ", "").trim());
		} catch (NumberFormatException e) {
			price = BigDecimal.ZERO;
		}

		String url = BASE_URL + el.select("span.hikashop_product_name a").attr("href").toString();

		StockStatus stockStatus = StockStatus.IN_STOCK_STORE;
		if (!el.select("form div.hikashop_product_no_stock").isEmpty()) {
			stockStatus = StockStatus.OUT_OF_STOCK;
		}

		String id = url.substring(url.lastIndexOf("/") + 1);
		id = id.substring(0, id.indexOf("-"));

		return new Item(id, name, price, stockStatus, url);
	}
}
