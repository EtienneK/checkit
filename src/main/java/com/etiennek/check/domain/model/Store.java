package com.etiennek.check.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "store")
public class Store {
  @Id
  private Long id;
  private String name;
  private String url;

  @CreatedDate
  private LocalDateTime createdDate;
  @LastModifiedDate
  private LocalDateTime lastModifiedDate;
  @Version
  private Long version;

  protected Store() {
  }
  
  public Store(Long id, String name, String url) {
    this.id = id;
    this.name = name;
    this.url = url;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

}
