package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.CategoryService;
import com.atguigu.gmall.product.service.SkuService;
import com.atguigu.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product/")
@CrossOrigin
public class ProductApiController {

    @Autowired
    SkuService skuService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SpuService spuService;

    @RequestMapping("getBySkuId/{skuId}")
    SkuInfo getBySkuId(@PathVariable("skuId") Long skuId){
        SkuInfo skuInfo = skuService.getBySkuId(skuId);
        return skuInfo;
    }

    @RequestMapping("getImagesBySkuId/{skuId}")
    List<SkuImage> getImagesBySkuId(@PathVariable("skuId") Long skuId){
        List<SkuImage> skuImageList = skuService.getImagesBySkuId(skuId);
        return skuImageList;
    }

    @RequestMapping("getPriceBySkuId/{skuId}")
    BigDecimal getPriceBySkuId(@PathVariable("skuId") Long skuId){
        BigDecimal price = skuService.getPriceBySkuId(skuId);
        return price;
    }

    @RequestMapping("getCategoryViewByC3Id/{category3Id}")
    BaseCategoryView getCategoryViewByC3Id(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView baseCategoryView = categoryService.getCategoryViewByC3Id(category3Id);
        return baseCategoryView;
    }

    @RequestMapping("getSpuSaleAttrListCheckBySku/{spuId}/{skuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("spuId") Long spuId, @PathVariable("skuId") Long skuId){
        List<SpuSaleAttr> spuSaleAttrList = spuService.getSpuSaleAttrListCheckBySku(spuId,skuId);
        return spuSaleAttrList;
    }

    @RequestMapping("getValuesSku/{spuId}")
    List<Map<String, Object>> getValuesSku(@PathVariable("spuId") Long spuId){
        List<Map<String, Object>> maps = skuService.getValuesSku(spuId);
        return maps;
    }

    @RequestMapping("getCategoryView")
    List<BaseCategoryView> getCategoryView(){
        List<BaseCategoryView> categoryViews = categoryService.getCategoryView();
        return categoryViews;
    }

    @RequestMapping("getGoodsBySkuId/{skuId}")
    Goods getGoodsBySkuId(@PathVariable("skuId") Long skuId){
        Goods goods = skuService.getGoodsBySkuId(skuId);
        return goods;
    }
}
