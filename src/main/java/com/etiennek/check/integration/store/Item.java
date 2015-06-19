package com.etiennek.check.integration.store;

import java.math.BigDecimal;

public class Item {
  private String storeId;
  private String name;
  private BigDecimal price;
  private StockStatus stockStatus;
  private String url;

  public Item(String storeId, String name, BigDecimal price, StockStatus stockStatus, String url) {
    this.storeId = storeId;
    this.name = name;
    this.price = price;
    this.stockStatus = stockStatus;
    this.url = url;
  }

  @Override
  public String toString() {
    return "Item [storeId=" + storeId + ", name=" + name + ", price=" + price + ", stockStatus=" + stockStatus
        + ", url=" + url + "]";
  }

}
