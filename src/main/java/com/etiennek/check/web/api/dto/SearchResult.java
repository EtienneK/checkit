package com.etiennek.check.web.api.dto;

import java.math.BigDecimal;

public class SearchResult {
	private String itemName;
	private String itemUrl;
	private String storeName;
	private String storeUrl;
	private BigDecimal price;
	private boolean inStock;

	public SearchResult(String itemName, String itemUrl, String storeName, String storeUrl, BigDecimal price,
			boolean inStock) {
		super();
		this.itemName = itemName;
		this.itemUrl = itemUrl;
		this.storeName = storeName;
		this.storeUrl = storeUrl;
		this.price = price;
		this.inStock = inStock;
	}

	public String getItemName() {
		return itemName;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public String getStoreName() {
		return storeName;
	}

	public String getStoreUrl() {
		return storeUrl;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public boolean isInStock() {
		return inStock;
	}

}
