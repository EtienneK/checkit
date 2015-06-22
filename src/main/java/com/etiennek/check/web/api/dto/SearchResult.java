package com.etiennek.check.web.api.dto;

import java.math.BigDecimal;

public class SearchResult {
  private String itemName;
  private String itemUrl;
  private String storeName;
  private String storeUrl;
  private BigDecimal price;

  public SearchResult(String itemName, String itemUrl, String storeName, String storeUrl, BigDecimal price) {
    this.itemName = itemName;
    this.itemUrl = itemUrl;
    this.storeName = storeName;
    this.storeUrl = storeUrl;
    this.price = price;
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


}
