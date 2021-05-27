package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.aop.GmallCache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    SpuImageMapper spuImageMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public IPage<SpuInfo> spuList(Long page, Long limit, Long category3Id) {
        IPage<SpuInfo> spuInfoPage = new Page<>(page, limit);
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_Id", category3Id);
        IPage<SpuInfo> infoIPageResult = spuInfoMapper.selectPage(spuInfoPage, queryWrapper);
        return infoIPageResult;
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrList;
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {

        // 保存spuInfo
        spuInfoMapper.insert(spuInfo);

        // 生成主键spuId
        Long spuId = spuInfo.getId();

        // 根据主键保存图片信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null && spuImageList.size() > 0) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuId);
                spuImageMapper.insert(spuImage);
            }
        }

        // 根据主键保存spu销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuId);
                spuSaleAttrMapper.insert(spuSaleAttr);
                // 根据主键保存spu销售属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                    spuSaleAttrValue.setSpuId(spuId);
                    // 插入用来唯一的联合主键销售属性id
                    spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getBaseSaleAttrId());
                    //插入销售属性表的销售属性名称
                    spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                    spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                }
            }
        }
    }

    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id",spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(queryWrapper);
        return spuImageList;
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id",spuId);
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.selectList(queryWrapper);

        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            QueryWrapper<SpuSaleAttrValue> queryWrapperValue = new QueryWrapper<>();
            queryWrapperValue.eq("spu_id",spuId);
            queryWrapperValue.eq("base_sale_attr_id",spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueMapper.selectList(queryWrapperValue);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
        }

        return spuSaleAttrList;
    }

    @GmallCache
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long spuId, Long skuId) {
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(spuId,skuId);
        return spuSaleAttrList;
    }
}
