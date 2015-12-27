package com.etiennek.check.integration.store.takealot;

import static com.etiennek.util.Utils.m;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import com.etiennek.check.integration.store.Extractor;
import com.etiennek.check.integration.store.Item;
import com.etiennek.check.integration.store.StockStatus;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import rx.Observable;
import rx.Subscriber;

@Component
public class TakealotExtractor implements Extractor {

	private static String PAGE_URL = "https://api.takealot.com/rest/v-1-4-2/productlines/search?sort=BestSelling Descending&rows=100&start={page}&detail=mlisting&filter=Available:true&filter=Type:7&filter=Category:19910&callback=type";

	@Override
	public Observable<Item> extract() {
		return Observable.create(subscriber -> {
			requestPage(0).addCallback(handlePage(subscriber, 0), subscriber::onError);
		});
	}

	private SuccessCallback<? super ResponseEntity<String>> handlePage(Subscriber<? super Item> subscriber, int page) {
		return httpEntity -> {

			List<Item> items = extractItems(httpEntity).stream().collect(Collectors.toList());

			if (items.size() == 0) {
				subscriber.onCompleted();
				return;
			}

			items.forEach(subscriber::onNext);

			int nextPage = page + 100;
			requestPage(nextPage).addCallback(handlePage(subscriber, nextPage), subscriber::onError);
		};
	}

	private ListenableFuture<ResponseEntity<String>> requestPage(int page) {
		return restCall(PAGE_URL, m().put("page", page).build());
	}

	private List<Item> extractItems(ResponseEntity<String> httpEntity) {
		String jsonBody = httpEntity.getBody().replaceFirst("type\\(", "");
		jsonBody = jsonBody.replace("\"});", "\"}");

		return StreamSupport
				.stream(new JsonParser().parse(jsonBody).getAsJsonObject().get("results").getAsJsonObject()
						.get("productlines").getAsJsonArray().spliterator(), false)
				.map(this::map).collect(Collectors.toList());
	}

	private Item map(JsonElement el) {
		JsonObject object = el.getAsJsonObject();

		String id = object.get("id").getAsString();
		String name = object.get("title").getAsString();
		BigDecimal price = object.get("selling_price").getAsBigDecimal().setScale(2).divide(new BigDecimal("100"));

		StockStatus stockStatus = StockStatus.OUT_OF_STOCK;
		boolean inStock = object.get("shipping_information").getAsJsonObject().get("in_stock").getAsBoolean();
		String inStockString = object.get("shipping_information").getAsJsonObject().get("string").getAsString().trim().toLowerCase();
		int inStockWarehouseCount = object.get("shipping_information").getAsJsonObject().get("stock_warehouses")
				.getAsJsonArray().size();

		if (inStock && inStockWarehouseCount > 0) {
			stockStatus = StockStatus.IN_STOCK_STORE;
		} else if (inStock) {
			stockStatus = StockStatus.IN_STOCK_SUPPLIER;
		} else if (inStockString.trim().toLowerCase().startsWith("not yet released")) {
			stockStatus = StockStatus.UNRELEASED;
		}

		String url = object.get("uri").getAsString();

		return new Item(id, name, price, stockStatus, url);
	}
}
