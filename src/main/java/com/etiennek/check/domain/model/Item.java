package com.etiennek.check.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
public class Item {
	@Id
	private String id;
	private String externalId;
	private long storeId;

	private String name;
	private String url;

	private BigDecimal price;
	private BigDecimal normalPrice;

	private boolean inStock;

	@CreatedDate
	private LocalDateTime createdDate;
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	protected Item() {
	}

	public Item(String id, String externalId, long storeId, String name, String url, BigDecimal price,
			BigDecimal normalPrice, boolean inStock) {
		if (id == null)
			throw new NullPointerException("id");
		if (externalId == null)
			throw new NullPointerException("externalId");
		if (name == null)
			throw new NullPointerException("name");
		if (url == null)
			throw new NullPointerException("url");
		if (price == null)
			throw new NullPointerException("price");
		if (normalPrice == null)
			throw new NullPointerException("normalPrice");
		
		this.id = id;
		this.externalId = externalId;
		this.storeId = storeId;
		this.name = name;
		this.url = url;
		this.price = price;
		this.normalPrice = normalPrice;
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

	public BigDecimal getNormalPrice() {
		return normalPrice;
	}

	public Item audit(LocalDateTime lastModifiedDate) {
		// TODO: REMOVE once auditing works on elasticsearch
		this.lastModifiedDate = lastModifiedDate;
		return this;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", externalId=" + externalId + ", storeId=" + storeId + ", name=" + name + ", url="
				+ url + ", price=" + price + ", normalPrice=" + normalPrice + ", inStock=" + inStock + ", createdDate="
				+ createdDate + ", lastModifiedDate=" + lastModifiedDate + "]";
	}

}
