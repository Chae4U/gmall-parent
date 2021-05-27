package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;

    @RequestMapping("{skuId}.html")
    public String index(Model model, @PathVariable("skuId") Long skuId){
        // 访问后台item服务加载数据
        Map<String, Object> map = itemFeignClient.item(skuId);
        model.addAllAttributes(map);
        return "item/index";
    }
}
