package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    GoodsRepository goodsRepository;

    @Override
    public List<JSONObject> getBaseCategoryList() {

        // 一级分类集合
        List<JSONObject> c1JsonObjects = new ArrayList<>();

        // 从数据层(product)查询出分类数据
        List<BaseCategoryView> categoryViews = productFeignClient.getCategoryView();

        // 对一级分类数据集合进行处理
        Map<Long, List<BaseCategoryView>> c1Map = categoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        for (Map.Entry<Long, List<BaseCategoryView>> c1Object : c1Map.entrySet()) {
            // 一级分类元素
            JSONObject c1JsonObject = new JSONObject();
            Long c1Id = c1Object.getKey();
            String category1Name = c1Object.getValue().get(0).getCategory1Name();
            c1JsonObject.put("categoryId",c1Id);
            c1JsonObject.put("categoryName",category1Name);
            // 二级分类集合
            List<JSONObject> c2JsonObjects = new ArrayList<>();
            // 对二级分类数据集合进行处理
            Map<Long, List<BaseCategoryView>> c2Map = c1Object.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> c2Object : c2Map.entrySet()) {
                // 二级分类元素
                JSONObject c2JsonObject = new JSONObject();
                Long c2Id = c2Object.getKey();
                String category2Name = c2Object.getValue().get(0).getCategory2Name();
                c2JsonObject.put("categoryId",c2Id);
                c2JsonObject.put("categoryName",category2Name);
                // 三级分类集合
                List<JSONObject> c3JsonObjects = new ArrayList<>();
                // 对三级分类数据集合进行处理
                Map<Long, List<BaseCategoryView>> c3Map = c2Object.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> c3Object : c3Map.entrySet()) {
                    // 三级分类元素
                    JSONObject c3JsonObject = new JSONObject();
                    Long c3Id = c3Object.getKey();
                    String category3Name = c3Object.getValue().get(0).getCategory3Name();
                    c3JsonObject.put("categoryId",c3Id);
                    c3JsonObject.put("categoryName",category3Name);
                    c3JsonObjects.add(c3JsonObject);
                }
                c2JsonObject.put("categoryChild",c3JsonObjects);
                c2JsonObjects.add(c2JsonObject);
            }
            c1JsonObject.put("categoryChild",c2JsonObjects);
            c1JsonObjects.add(c1JsonObject);

        }

        return c1JsonObjects;
    }

    @Override
    public void onSale(Long skuId) {
        // 根据上架的商品skuId，将数据放入es中
        Goods goods = productFeignClient.getGoodsBySkuId(skuId);
        goods.setCreateTime(new Date());
        goodsRepository.save(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
        // 根据下架的商品skuId，将数据从es中删除
        Goods goods = new Goods();
        goods.setId(skuId);
        goodsRepository.delete(goods);
    }
}
