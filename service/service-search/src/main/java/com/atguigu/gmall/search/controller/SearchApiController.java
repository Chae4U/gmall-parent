package com.atguigu.gmall.search.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/search")
public class SearchApiController {

    @Autowired
    SearchService searchService;

    @RequestMapping("index")
    List<JSONObject> index(){
        List<JSONObject> jsonObjects = searchService.getBaseCategoryList();

        return jsonObjects;
    }

    @RequestMapping("onSale/{skuId}")
    void onSale(@PathVariable("skuId") Long skuId){
        searchService.onSale(skuId);
    }

    @RequestMapping("cancelSale/{skuId}")
    void cancelSale(@PathVariable("skuId") Long skuId){
        searchService.cancelSale(skuId);
    }
}
