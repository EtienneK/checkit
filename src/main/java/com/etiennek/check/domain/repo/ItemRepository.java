package com.etiennek.check.domain.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.etiennek.check.domain.model.Item;

@Repository
public interface ItemRepository extends ElasticsearchRepository<Item, String> {
  @Query("{ \"query_string\" : { \"query\" : \"?0\" } }")
  FacetedPage<Item> search(String query, Pageable pageable);
}
