package com.etiennek.check.integration.store;

import java.math.BigDecimal;

public class Item {
  private String name;
  private BigDecimal price;
  private String url;

  public Item(String name, BigDecimal price, String url) {
    this.name = name;
    this.price = price;
    this.url = url;
  }

  @Override
  public String toString() {
    return "Item [name=" + name + ", price=" + price + ", url=" + url + "]";
  }

}
