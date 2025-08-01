package com.example.blitzbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.blitzbuy.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Heather
 * @version 1.0
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
} 