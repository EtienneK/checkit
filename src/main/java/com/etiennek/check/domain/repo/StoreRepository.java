package com.etiennek.check.domain.repo;

import java.util.concurrent.CompletableFuture;

import org.springframework.data.repository.Repository;

import com.etiennek.check.domain.model.Store;

public interface StoreRepository extends Repository<Store, Long> {
  CompletableFuture<Store> findOne(long id);
  CompletableFuture<Store> findByUrl(String url);
}
