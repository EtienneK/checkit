package com.etiennek.check.domain.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.FacetedPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.etiennek.check.domain.model.Item;

public interface ItemRepository extends ElasticsearchRepository<Item, String> {
	@Query("{ \"query_string\" : { \"query\" : \"?0\", \"fields\" : [ \"name\" ] , \"default_operator\": \"and\" }}")
	FacetedPage<Item> search(String query, Pageable pageable);

	List<Item> findByLastModifiedDateLessThanAndStoreIdAndInStockTrue(LocalDateTime date, long storeId);
}
