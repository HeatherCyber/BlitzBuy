package com.example.blitzbuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blitzbuy.pojo.Goods;
import com.example.blitzbuy.vo.GoodsVo;

import java.util.List;

/**
 * @author Heather
 * @version 1.0
 */
public interface GoodsService extends IService<Goods> {
//    返回促销商品的信息列表
    List<GoodsVo> getGoodsVo();
//    获取指定的商品详情--根据商品id
    GoodsVo getGoodsVoByGoodsId(Long goodsId);
  
}
