package com.example.blitzbuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blitzbuy.pojo.Order;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.vo.GoodsVo;

/**
 * @author Heather
 * @version 1.0
 * Order Service Interface
 */
public interface OrderService extends IService<Order> {

// Create flash sale order
    Order creatFlashSaleOrder(User user, GoodsVo goodsVo);
    
} 