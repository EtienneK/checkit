package com.etiennek.check.domain.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.etiennek.check.domain.model.Item;

public interface ItemRepository extends CrudRepository<Item, String> {
	Page<Item> findByNameContainingIgnoreCaseOrderByName(@Param("query") String query, Pageable pageable);
	List<Item> findByLastModifiedDateLessThanAndStoreIdAndInStockTrue(LocalDateTime date, long storeId);
}
