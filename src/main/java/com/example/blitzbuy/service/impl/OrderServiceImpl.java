package com.example.blitzbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blitzbuy.mapper.FlashSaleOrderMapper;
import com.example.blitzbuy.mapper.OrderMapper;
import com.example.blitzbuy.pojo.FlashSaleGoods;
import com.example.blitzbuy.pojo.FlashSaleOrder;
import com.example.blitzbuy.pojo.Order;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.FlashSaleGoodsService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.vo.GoodsVo;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Heather
 * @version 1.0
 * Order Service Implementation Class
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private FlashSaleOrderMapper flashSaleOrderMapper;
    @Resource
    private FlashSaleGoodsService flashSaleGoodsService;

    // Create flash sale order
    @Override
    public Order creatFlashSaleOrder(User user, GoodsVo goodsVo) {
        // Query goods inventory
        FlashSaleGoods flashSaleGoods = flashSaleGoodsService.getOne(
                    new QueryWrapper<FlashSaleGoods>().eq("goods_id", goodsVo.getId()));
        // Complete basic flash sale business logic (will optimize for high concurrency scenarios later)
        // Decrease inventory by 1
        flashSaleGoods.setFlashSaleStock(flashSaleGoods.getFlashSaleStock()-1);
        // Update goods inventory
        flashSaleGoodsService.updateById(flashSaleGoods);
        
        // Create regular order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddress("1234 Elm Street, Apt 56B, Springfield, IL 62704, USA");
        order.setGoodsName(goodsVo.getName());
        order.setGoodsCount(1);
        order.setGoodsPrice(flashSaleGoods.getFlashSalePrice());
        order.setOrderChannel(1);
        order.setOrderStatus(0);
        order.setCreateTime(new Date());
        order.setPayTime(null);
        // Save regular order information
        orderMapper.insert(order);

        // Create flash sale goods order
        FlashSaleOrder flashSaleOrder = new FlashSaleOrder();
        flashSaleOrder.setGoodsId(goodsVo.getId());
        flashSaleOrder.setOrderId(order.getId());
        flashSaleOrder.setUserId(user.getId());
        // Save flash sale goods order information
        flashSaleOrderMapper.insert(flashSaleOrder);

        // Return order object
        return order;
    }

   

    
} 