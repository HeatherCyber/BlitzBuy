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
//    Get list of goods participating in flash sale activities
    List<GoodsVo> getGoodsVo();
// Get specified goods details - based on goods ID
    GoodsVo getGoodsVoByGoodsId(Long goodsId);

}
