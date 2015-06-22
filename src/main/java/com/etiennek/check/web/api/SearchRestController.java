package com.etiennek.check.web.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.etiennek.check.web.api.dto.SearchResult;

@RestController
@RequestMapping("/api/v1/search")
public class SearchRestController {

  @RequestMapping
  public DeferredResult<List<SearchResult>> search(@RequestParam String query) {
    DeferredResult<List<SearchResult>> ret = new DeferredResult<>();
    ret.setResult(new ArrayList<>());
    return ret;
  }

}
