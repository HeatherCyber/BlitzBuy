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
//    Return promotional goods information list
    List<GoodsVo> getGoodsVo();
//    Get specified goods details - based on goods ID
    GoodsVo getGoodsVoByGoodsId(Long goodsId);
  
}
