package com.etiennek.check.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.etiennek.check.domain.repo.StoreRepository;

@Controller
@RequestMapping
public class SearchController {
  
  private @Autowired StoreRepository storeRepository;

  @RequestMapping("/")
  String index() {
    return "search/index";
  }

}
