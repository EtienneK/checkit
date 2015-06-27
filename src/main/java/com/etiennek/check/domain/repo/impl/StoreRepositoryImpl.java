package com.etiennek.check.domain.repo.impl;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.etiennek.check.domain.model.Store;
import com.etiennek.check.domain.repo.StoreRepository;
import com.google.common.collect.ImmutableList;

public class StoreRepositoryImpl implements StoreRepository {

	// @formatter:off
	private Map<Long, Store> idIndex = ImmutableList.<Store> builder()

	.add(new Store(1L, "Takealot", "http://www.takealot.com/"))
			.add(new Store(2L, "Timeless Board Games", "http://www.timelessboardgames.co.za/"))
			.add(new Store(3L, "Boardgames.co.za", "http://www.boardgames.co.za/"))

			.build().stream().collect(Collectors.toMap(Store::getId, Function.identity()));

	public Store findOne(long id) {
		return idIndex.get(id);
	}

}