package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> getBySkuId(Long skuId) {

        long start = System.currentTimeMillis();

        // 将前端页面所有所需要的信息都打包进map集合中统一返回给前端页面
        Map<String, Object> map = new HashMap<>();

        // 需要调用service-product模块查询基础数据
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = productFeignClient.getBySkuId(skuId);
                return skuInfo;
            }
        },threadPoolExecutor);

        // 返回需要查询的sku图片
        CompletableFuture<Void> imgCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SkuImage> skuImageList = productFeignClient.getImagesBySkuId(skuId);
                skuInfo.setSkuImageList(skuImageList);
                map.put("skuInfo",skuInfo);
            }
        },threadPoolExecutor);


        // 返回需要查询的sku价格
        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price = productFeignClient.getPriceBySkuId(skuId);
                map.put("price",price);
            }
        },threadPoolExecutor);


        // 商品分类
        CompletableFuture<Void> categoryCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView categoryView = productFeignClient.getCategoryViewByC3Id(skuInfo.getCategory3Id());
                map.put("categoryView",categoryView);
            }
        },threadPoolExecutor);


        // 销售属性列表
        CompletableFuture<Void> spuSaleAttrCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuInfo.getSpuId(),skuInfo.getId());
                map.put("spuSaleAttrList",spuSaleAttrList);
            }
        },threadPoolExecutor);

        // 销售属性对应skuhash表格
        // dao层返回的结果是一个集合
        CompletableFuture<Void> valuesCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<Map<String,Object>> valuesSkus = productFeignClient.getValuesSku(skuInfo.getSpuId());

                if (valuesSkus!=null && valuesSkus.size()>0){
                    HashMap<String, Object> valuesSkusMap = new HashMap<>();
                    for (Map<String, Object> valuesSku : valuesSkus) {
                        String valueIds = (String) valuesSku.get("valueIds");
                        Integer skuIdForValues = (Integer) valuesSku.get("sku_id");
                        valuesSkusMap.put(valueIds,skuIdForValues);
                    }
                    String valuesSkuJson = JSON.toJSONString(valuesSkusMap);
                    map.put("valuesSkuJson",valuesSkuJson);
                }
            }
        },threadPoolExecutor);

        CompletableFuture.allOf(skuInfoCompletableFuture,imgCompletableFuture,priceCompletableFuture,priceCompletableFuture,categoryCompletableFuture,valuesCompletableFuture).join();

        long end = System.currentTimeMillis();
        System.out.println("item服务执行时间："+(end-start));

        return map;
    }
}
