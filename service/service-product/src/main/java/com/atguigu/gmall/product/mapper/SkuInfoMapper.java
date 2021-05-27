package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo>{
    Goods getGoodsBySkuId(@Param("skuId") Long skuId);
}
