package com.atguigu.gmall.search.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface SearchService {
    List<JSONObject> getBaseCategoryList();

    void onSale(Long skuId);

    void cancelSale(Long skuId);
}
