package com.etiennek.check.web.api.dto;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class SearchResult {
	private String itemName;
	private String itemUrl;
	private String storeName;
	private String storeUrl;
	private BigDecimal price;
	private BigDecimal normalPrice;
	private String priceString;
	private String normalPriceString;
	private BigDecimal savingsPercent;
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

		DecimalFormat decimalFormat = new DecimalFormat("R ###,###,##0.00");
		priceString = decimalFormat.format(price);
		normalPriceString = decimalFormat.format(normalPrice);
		savingsPercent = (normalPrice.subtract(price)).divide(normalPrice);
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

	public String getPriceString() {
		return priceString;
	}

	public String getNormalPriceString() {
		return normalPriceString;
	}

	public BigDecimal getSavingsPercent() {
		return savingsPercent;
	}

}
