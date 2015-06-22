package com.etiennek.check.integration.store;

import java.nio.charset.Charset;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import static com.etiennek.util.Utils.m;
import rx.Observable;

public interface Extractor {

  Observable<Item> extract();

  default ListenableFuture<ResponseEntity<String>> restCall(String url) {
    return restCall(url, m().build(), HttpMethod.GET);
  }

  default ListenableFuture<ResponseEntity<String>> restCall(String url, Map<String, ?> uriVariables) {
    return restCall(url, uriVariables, HttpMethod.GET);
  }

  default ListenableFuture<ResponseEntity<String>> restCall(String url, Map<String, ?> uriVariables, HttpMethod method) {
    return restCall(url, uriVariables, method, null);
  }

  default ListenableFuture<ResponseEntity<String>> restCall(String url, HttpMethod method, Map<String, ?> body) {
    return restCall(url, m().build(), method, body);
  }

  default ListenableFuture<ResponseEntity<String>> restCall(String url, Map<String, ?> uriVariables, HttpMethod method,
      Map<String, ?> body) {
    AsyncRestTemplate rest = new AsyncRestTemplate();

    rest.getMessageConverters()
        .add(0, new FormHttpMessageConverter());
    rest.getMessageConverters()
        .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

    HttpHeaders headers = new HttpHeaders();
    headers.set("User-Agent", "Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");
    return rest.exchange(url, method, new HttpEntity<>(body, headers), String.class, uriVariables);
  }

}
