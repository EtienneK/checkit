package com.etiennek.check.web.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.etiennek.check.domain.model.Item;
import com.etiennek.check.domain.model.Store;
import com.etiennek.check.domain.repo.ItemRepository;
import com.etiennek.check.domain.repo.StoreRepository;
import com.etiennek.check.web.api.dto.SearchResult;

@RestController
@RequestMapping("/api/v1/search")
public class SearchRestController {

	private @Autowired ItemRepository itemRepository;
	private @Autowired StoreRepository storeRepository;

	@RequestMapping
	public DeferredResult<Page<SearchResult>> search(@RequestParam String query, Pageable pageable) {
		DeferredResult<Page<SearchResult>> ret = new DeferredResult<>();

		Page<Item> searchResults = itemRepository.findByNameContainingIgnoreCaseOrderByName(query.trim(), pageable);

		List<SearchResult> mappedResults = searchResults
				.getContent()
				.stream()
				.map(i -> {
					Store store = storeRepository.findOne(i.getStoreId());
					return new SearchResult(i.getName(), i.getUrl(), store.getName(), store.getUrl(), i.getPrice(), i.getNormalPrice(), i
							.isInStock(), i.getNormalPrice().compareTo(i.getPrice()) > 0);
				}).collect(Collectors.toList());

		ret.setResult(new PageImpl<SearchResult>(mappedResults, pageable, searchResults.getTotalElements()));
		return ret;
	}

}
