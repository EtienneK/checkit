package com.etiennek.check.integration.store;

import java.math.BigDecimal;

public class Item {

	private String id;
	private String name;
	private BigDecimal price;
	private BigDecimal normalPrice;
	private StockStatus stockStatus;
	private String url;

	private boolean valid = false;
	private String invalidDetails;
	private Exception invalidException;

	public static Item invalidItem(String details, Exception e) {
		Item ret = new Item();
		ret.invalidDetails = details;
		ret.invalidException = e;
		return ret;
	}

	private Item() {
	}

	public Item(String id, String name, BigDecimal price, BigDecimal normalPrice, StockStatus stockStatus, String url) {
		this.valid = true;

		if (id == null)
			throw new NullPointerException("id");
		if (name == null)
			throw new NullPointerException("name");
		if (price == null)
			throw new NullPointerException("price");
		if (normalPrice == null)
			throw new NullPointerException("normalPrice");
		if (stockStatus == null)
			throw new NullPointerException("stockStatus");
		if (url == null)
			throw new NullPointerException("url");

		this.id = id;
		this.name = name;
		this.price = price;
		this.normalPrice = normalPrice;
		this.stockStatus = stockStatus;
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public StockStatus getStockStatus() {
		return stockStatus;
	}

	public String getUrl() {
		return url;
	}

	public String getInvalidDetails() {
		return invalidDetails;
	}

	public Exception getInvalidException() {
		return invalidException;
	}

	public boolean isValid() {
		return valid;
	}

	public BigDecimal getNormalPrice() {
		return normalPrice;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", price=" + price + ", normalPrice=" + normalPrice
				+ ", stockStatus=" + stockStatus + ", url=" + url + ", valid=" + valid + ", invalidDetails="
				+ invalidDetails + ", invalidException=" + invalidException + "]";
	}

}
