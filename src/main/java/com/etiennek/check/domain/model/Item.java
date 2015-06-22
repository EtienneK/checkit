package com.etiennek.check.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "item")
public class Item {
  @Id
  private String id;
  private String externalId;
  private long storeId;

  private String name;
  private String url;

  private boolean inStock;

  @CreatedDate
  private LocalDateTime createdDate;
  @LastModifiedDate
  private LocalDateTime lastModifiedDate;
  @Version
  private Long version;
}
