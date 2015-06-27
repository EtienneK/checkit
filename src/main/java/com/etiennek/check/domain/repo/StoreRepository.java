package com.etiennek.check.domain.repo;

import org.springframework.data.repository.Repository;

import com.etiennek.check.domain.model.Store;

public interface StoreRepository extends Repository<Store, Long> {
	Store findOne(long id);
}
