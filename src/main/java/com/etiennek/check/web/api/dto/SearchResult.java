package com.etiennek.check.web.api.dto;

import java.math.BigDecimal;

public class SearchResult {
	private String itemName;
	private String itemUrl;
	private String storeName;
	private String storeUrl;
	private BigDecimal price;
	private BigDecimal normalPrice;
	private boolean inStock;
	private boolean onSale;

	public SearchResult(String itemName, String itemUrl, String storeName, String storeUrl, BigDecimal price,
			BigDecimal normalPrice, boolean inStock, boolean onSale) {
		super();
		this.itemName = itemName;
		this.itemUrl = itemUrl;
		this.storeName = storeName;
		this.storeUrl = storeUrl;
		this.price = price;
		this.normalPrice = normalPrice;
		this.inStock = inStock;
		this.onSale = onSale;
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

	public BigDecimal getNormalPrice() {
		return normalPrice;
	}

	public boolean isInStock() {
		return inStock;
	}

	public boolean isOnSale() {
		return onSale;
	}

}
