package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.aop.GmallCache;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.product.service.SkuService;
import com.atguigu.gmall.search.client.SearchFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        // 保存sku
        skuInfoMapper.insert(skuInfo);

        //生成sku主键
        Long skuId = skuInfo.getId();

        // 根据主键保存skuImage
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList!=null&&skuImageList.size()>0) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuId);
                skuImageMapper.insert(skuImage);
            }
        }

        // 根据主键保存skuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList!=null&&skuAttrValueList.size()>0){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }

        // 根据主键保存销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList!=null&&skuSaleAttrValueList.size()>0) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    @Override
    public IPage<SkuInfo> list(Long page, Long limit) {
        IPage<SkuInfo> infoIPage = new Page<>(page,limit);

        IPage<SkuInfo> skuInfoIPageResult = skuInfoMapper.selectPage(infoIPage, null);

        return skuInfoIPageResult;
    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);

        //TODO 同步搜索引擎(搜索服务器，添加该商品)
        searchFeignClient.onSale(skuId);
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);

        //TODO 同步搜索引擎(搜索服务器，删除该商品)
        searchFeignClient.cancelSale(skuId);
    }

    @GmallCache
    @Override
    public SkuInfo getBySkuId(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    @GmallCache
    @Override
    public List<SkuImage> getImagesBySkuId(Long skuId) {
        QueryWrapper<SkuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId);
        List<SkuImage> skuImageList = skuImageMapper.selectList(queryWrapper);
        return skuImageList;
    }

    @GmallCache
    @Override
    public BigDecimal getPriceBySkuId(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        BigDecimal price = skuInfo.getPrice();
        return price;
    }

    @GmallCache
    @Override
    public List<Map<String, Object>> getValuesSku(Long spuId) {
        List<Map<String, Object>> maps = skuSaleAttrValueMapper.selectValuesSku(spuId);
        return maps;
    }

    @Override
    public Goods getGoodsBySkuId(Long skuId) {
        Goods goods = skuInfoMapper.getGoodsBySkuId(skuId);
        return goods;
    }
}
