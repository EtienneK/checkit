package com.etiennek.check.integration.store;

import rx.Observable;

public interface Extractor {
  Observable<Item> extract();
}
