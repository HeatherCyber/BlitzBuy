package com.example.blitzbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blitzbuy.pojo.FlashSaleOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Heather
 * @version 1.0
 * 秒杀订单Mapper接口
 */
@Mapper
public interface FlashSaleOrderMapper extends BaseMapper<FlashSaleOrder> {
} 