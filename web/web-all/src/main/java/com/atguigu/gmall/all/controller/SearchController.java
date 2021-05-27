package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.search.client.SearchFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    @RequestMapping({"index.html","/"})
    public String index(Model model){
        List<JSONObject> jsonObjects = searchFeignClient.index();
        model.addAttribute("list",jsonObjects);
        return "index/index";
    }
}
