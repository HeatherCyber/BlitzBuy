package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.FlashSaleOrder;
import com.example.blitzbuy.pojo.Order;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.service.FlashSaleOrderService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.vo.GoodsVo;
import com.example.blitzbuy.vo.RespBeanEnum;
import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


import com.example.blitzbuy.pojo.User;

/**
 * version 3.0
 * Flash Sale Controller: Handle user flash sale requests, return flash sale results
 * 1. Use Redis to pre-reduce inventory, solve high concurrency problems 
 * 2. Add local JVM memory check before pre-reducing inventory in Redis, reduce unnecessary Redis operations
 */

@Controller
@RequestMapping("/flashSale")
public class FlashSaleController implements InitializingBean {

    @Resource
    private GoodsService goodsService;

    @Resource
    private FlashSaleOrderService flashSaleOrderService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //Define a map to store the goods stock in local JVM memory, key: goodsId, value: hasStock
    private Map<Long, Boolean> goodsStockMap = new HashMap<>();

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

        // 1. Query promotional goods inventory(database)
        int stock = goodsVo.getFlashSaleStock();
        if(stock <= 0){ 
            // If the inventory is less than or equal to 0, return flash sale failed
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


        // 3. Optimization: Check if the inventory is maked as false in local JVM memory
        if(!goodsStockMap.get(goodsId)){
            // If the inventory is maked as false in local JVM memory, return flash sale failed
            model.addAttribute("errmsg", RespBeanEnum.INSUFFICIENT_STOCK.getMessage());
            return "flashSaleFail";
        }

        // 4. Optimization: Pre-reduce inventory in Redis
        // If the inventory is less than 0 in Redis, return flash sale failed
        // Thus, reduce the number of calls to orderService.creatFlashSaleOrder(), avoid crashing the database, and prevent thread accumulation
        // Method: decrement() is atomic, can avoid concurrency problems
        // return the decremented stock
        Long decrementedStock = redisTemplate.opsForValue().decrement("flashSaleStock:" + goodsId); 
        
        // If the inventory is less than 0
        if(decrementedStock < 0){ 
           
            // set the goods stock to false
            goodsStockMap.put(goodsId, false);

            // return flash sale failed
            model.addAttribute("errmsg", RespBeanEnum.INSUFFICIENT_STOCK.getMessage());
            return "flashSaleFail";
        }

        // 5. If not, create flash sale order
        Order order = orderService.creatFlashSaleOrder(user, goodsVo);
        if(order == null){
            model.addAttribute("errmsg", RespBeanEnum.ERROR.getMessage());
            return "flashSaleFail";
        }

        // 6. If flash sale successful, enter order detail page
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVo);
        return "orderDetail";

    }

    /**
     * Initialize flash sale stock in Redis
     * （This method is executed after all properties of the class are initialized）
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // Query all flash sale goods
        List<GoodsVo> goodsVoList = goodsService.getGoodsVo();
        // If the flash sale goods list is empty, return
        if(goodsVoList == null || goodsVoList.isEmpty()){
            return;
        }
        for(GoodsVo goodsVo : goodsVoList){
            
            // 1.Set flash sale stock to Redis(key: flashSaleStock:{goodsId}, value: flashSaleStock)
            redisTemplate.opsForValue().set("flashSaleStock:" + goodsVo.getId(), goodsVo.getFlashSaleStock());

            // 2. Set flash sale stock to local JVM memory
            // Initialize the goods stock map
            // If goodsId : false -> empty stock
            // If goodsId : true -> has stock
            goodsStockMap.put(goodsVo.getId(), true);
        }
    }

}
