package com.etiennek.check.domain.repo;

import com.etiennek.check.domain.model.Store;

public interface StoreRepository {
	Store findOne(long id);
}
