package com.example.blitzbuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blitzbuy.pojo.Order;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.vo.GoodsVo;

/**
 * @author Heather
 * @version 1.0
 * 订单Service接口
 */
public interface OrderService extends IService<Order> {

// 创建秒杀订单
    Order creatFlashSaleOrder(User user, GoodsVo goodsVo);
    
} 