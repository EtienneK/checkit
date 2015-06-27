package com.etiennek.check.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "item")
public class Item {
	@Id
	private String id;
	private String externalId;
	private long storeId;

	private String name;
	private String url;

	private BigDecimal price;

	private boolean inStock;

	@CreatedDate
	private LocalDateTime createdDate;
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	protected Item() {
	}

	public Item(String id, String externalId, long storeId, String name, String url, BigDecimal price, boolean inStock) {
		this.id = id;
		this.externalId = externalId;
		this.storeId = storeId;
		this.name = name;
		this.url = url;
		this.price = price;
		this.inStock = inStock;
	}

	public String getId() {
		return id;
	}

	public String getExternalId() {
		return externalId;
	}

	public long getStoreId() {
		return storeId;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public Item audit(LocalDateTime lastModifiedDate) {
		// TODO: REMOVE once auditing works on elasticsearch
		this.lastModifiedDate = lastModifiedDate;
		return this;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", externalId=" + externalId + ", storeId=" + storeId + ", name=" + name + ", url="
				+ url + ", price=" + price + ", inStock=" + inStock + ", createdDate=" + createdDate
				+ ", lastModifiedDate=" + lastModifiedDate + "]";
	}

}
