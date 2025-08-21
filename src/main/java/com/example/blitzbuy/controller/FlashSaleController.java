package com.example.blitzbuy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blitzbuy.pojo.FlashSaleOrder;
import com.example.blitzbuy.pojo.Order;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.service.FlashSaleOrderService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.vo.GoodsVo;
import com.example.blitzbuy.vo.RespBeanEnum;
import jakarta.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


import com.example.blitzbuy.pojo.User;

/**
 * version 2.0
 * Flash Sale Controller: Handle user flash sale requests, return flash sale results (considering high concurrency scenarios)
 */

@Controller
@RequestMapping("/flashSale")
public class FlashSaleController {

    @Resource
    private GoodsService goodsService;

    @Resource
    private FlashSaleOrderService flashSaleOrderService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // Handle user flash sale request
    @RequestMapping("/doFlashSale")
    public String doFlashSale(Model model, User user, Long goodsId){

        // 0. Check if user is logged in
        if( user == null){
            return "login";
        }
        model.addAttribute("user", user);

        // Get goodsVo
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        
        // Flash sale logic
        // 1. Query promotional goods inventory
        int stock = goodsVo.getFlashSaleStock();
        if(stock <= 0){
            model.addAttribute("errmsg", RespBeanEnum.INSUFFICIENT_STOCK.getMessage());
            return "flashSaleFail";
        }

        // 2. Check if user is repurchasing goods (check if the flash sale order exists in Redis)
        FlashSaleOrder flashSaleOrder = (FlashSaleOrder)redisTemplate.opsForValue().get("flashSaleOrder:" + user.getId() + ":" + goodsId);
        if(flashSaleOrder != null){ 
            // If the flash sale order exists in Redis, return flash sale failed
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "flashSaleFail";
        }

        // 3. If not, create flash sale order
        Order order = orderService.creatFlashSaleOrder(user, goodsVo);
        if(order == null){
            model.addAttribute("errmsg", RespBeanEnum.ERROR.getMessage());
            return "flashSaleFail";
        }

        // 4. If flash sale successful, enter order detail page
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";

    }

}
