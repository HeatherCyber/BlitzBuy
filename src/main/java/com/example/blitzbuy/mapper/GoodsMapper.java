package com.example.blitzbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blitzbuy.pojo.Goods;
import com.example.blitzbuy.vo.GoodsVo;

import java.util.List;

/**
 * @author Heather
 * @version 1.0
 */
public interface GoodsMapper extends BaseMapper<Goods> {
//    获取参与抢购活动的商品列表
    List<GoodsVo> getGoodsVo();
// 获取指定的商品详情--根据商品id
    GoodsVo getGoodsVoByGoodsId(Long goodsId);

}
