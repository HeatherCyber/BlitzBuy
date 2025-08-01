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
 * 订单Service实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private FlashSaleOrderMapper flashSaleOrderMapper;
    @Resource
    private FlashSaleGoodsService flashSaleGoodsService;

    //创建秒杀订单
    @Override
    public Order creatFlashSaleOrder(User user, GoodsVo goodsVo) {
        // 查询商品库存
        FlashSaleGoods flashSaleGoods = flashSaleGoodsService.getOne(
                    new QueryWrapper<FlashSaleGoods>().eq("goods_id", goodsVo.getId()));
        // 完成基本的秒杀业务逻辑（之后针对高并发场景再做优化）
        // 库存量-1
        flashSaleGoods.setFlashSaleStock(flashSaleGoods.getFlashSaleStock()-1);
        // 更新商品库存
        flashSaleGoodsService.updateById(flashSaleGoods);
        
        //创建普通订单
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
        //保存普通订单信息
        orderMapper.insert(order);

        // 创建秒杀商品订单
        FlashSaleOrder flashSaleOrder = new FlashSaleOrder();
        flashSaleOrder.setGoodsId(goodsVo.getId());
        flashSaleOrder.setOrderId(order.getId());
        flashSaleOrder.setUserId(user.getId());
        // 保存秒杀商品订单信息
        flashSaleOrderMapper.insert(flashSaleOrder);

        // 返回订单对象
        return order;
    }

   

    
} 