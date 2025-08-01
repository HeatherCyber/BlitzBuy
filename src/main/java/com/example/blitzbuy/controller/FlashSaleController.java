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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


import com.example.blitzbuy.pojo.User;

/**
 * version 1.0
 * 秒杀控制器 : 处理用户抢购请求, 返回抢购结果(暂不考虑高并发情况)
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

    // 处理用户抢购请求
    @RequestMapping("/doFlashSale")
    public String doFlashSale(Model model, User user, Long goodsId){

        // FlashSale 1.0
        if( user == null){
            return "login";
        }
        model.addAttribute("user", user);

        // 获取goodsVo
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        
        // 秒杀逻辑
        // 1. 查询促销商品的库存
        int stock = goodsVo.getFlashSaleStock();
        if(stock <= 0){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "flashSaleFail";
        }

        // 2. 判断用户是否在复购商品
        FlashSaleOrder flashSaleOrder = flashSaleOrderService.getOne(
            new QueryWrapper<FlashSaleOrder>()
            .eq("user_id", user.getId())
            .eq("goods_id", goodsId));
        if(flashSaleOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "flashSaleFail";
        }


        // 3. 创建抢购订单
        Order order = orderService.creatFlashSaleOrder(user, goodsVo);
        if(order == null){
            model.addAttribute("errmsg", RespBeanEnum.ERROR.getMessage());
            return "flashSaleFail";
        }

        // 4. 如果抢购成功，进入订单详情页
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";

    }

}
