package com.example.blitzbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blitzbuy.mapper.FlashSaleOrderMapper;
import com.example.blitzbuy.mapper.OrderMapper;
import com.example.blitzbuy.pojo.FlashSaleGoods;
import com.example.blitzbuy.pojo.FlashSaleOrder;
import com.example.blitzbuy.pojo.Order;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.FlashSaleGoodsService;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.util.MD5Util;
import com.example.blitzbuy.util.UUIDUtil;
import com.example.blitzbuy.vo.GoodsVo;

import jakarta.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // Create flash sale 
    @Transactional
    @Override
    public Order creatFlashSaleOrder(User user, GoodsVo goodsVo) {
        // Query goods inventory
        FlashSaleGoods flashSaleGoods = flashSaleGoodsService.getOne(
                    new QueryWrapper<FlashSaleGoods>().eq("goods_id", goodsVo.getId()));

        // Complete basic flash sale business logic (will optimize for high concurrency scenarios later)
        // flashSaleGoods.setFlashSaleStock(flashSaleGoods.getFlashSaleStock()-1); //Decrease inventory by 1
        // flashSaleGoodsService.updateById(flashSaleGoods);  // Update goods inventory

        // Optimize for high concurrency 
        // Use MySql's row-level lock 
        // If update successful, return true, otherwise return false
        boolean updateResult = flashSaleGoodsService.update(
            new UpdateWrapper<FlashSaleGoods>().
            setSql("flash_sale_stock = flash_sale_stock - 1"). // Decrease inventory by 1
            eq("goods_id", goodsVo.getId()). // Goods ID
            gt("flash_sale_stock", 0)); // Ensure inventory is greater than 0

        // If update failed
        if(!updateResult){
            // Rollback Redis stock since database update failed
            redisTemplate.opsForValue().increment("flashSaleStock:" + goodsVo.getId());
            
            // Save the failure information to Redis(key = flashSaleFail:userID:goodsID, value = 0)
            redisTemplate.opsForValue().set("flashSaleFail:" + user.getId() + ":" + goodsVo.getId(), "0");
            // Return null
            return null;
        }

        // Create regular order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddress("1234 Elm Street, Apt 56B, Springfield, CA 90001, USA");
        order.setGoodsName(goodsVo.getName());
        order.setGoodsCount(1);
        order.setGoodsPrice(flashSaleGoods.getFlashSalePrice());
        order.setOrderChannel(1);
        order.setOrderStatus(0);
        order.setCreateTime(new Date());
        order.setPayTime(null);
        // Save regular order information
        orderMapper.insert(order);

        // Create flash sale order
        FlashSaleOrder flashSaleOrder = new FlashSaleOrder();
        flashSaleOrder.setGoodsId(goodsVo.getId());
        flashSaleOrder.setOrderId(order.getId());
        flashSaleOrder.setUserId(user.getId());
        // Save flash sale order information
        flashSaleOrderMapper.insert(flashSaleOrder);

        //Put the generated flash sale order information into Redis
        //Avoid querying the database repeatedly when verifying repeat purchases
        //(key = flashSaleOrder:userID:goodsID, value = flashSaleOrder)
        // Save flash sale order to Redis for repeat purchase check
        String redisKey = "flashSaleOrder:" + user.getId() + ":" + goodsVo.getId();
        redisTemplate.opsForValue().set(redisKey, flashSaleOrder);

        // Return order object
        return order;
    }

    @Override
    public Long getFlashSaleResult(Long userId, Long goodsId) {
        // Check if flash sale order exists in Redis
        FlashSaleOrder flashSaleOrder = (FlashSaleOrder) redisTemplate.opsForValue().get("flashSaleOrder:" + userId + ":" + goodsId);
        
        if (flashSaleOrder != null) {
            // Flash sale successful, return orderId
            return flashSaleOrder.getOrderId();
        }

        // Check if there's a failure message in Redis
        String failureKey = "flashSaleFail:" + userId + ":" + goodsId;
        String failureResult = (String) redisTemplate.opsForValue().get(failureKey);
        
        if (failureResult != null) {
            // Flash sale failed, return -1
            return -1L;
        }
        
        // Check if message was sent to queue
        String messageSentKey = "flashSaleMessageSent:" + userId + ":" + goodsId;
        String messageSent = (String) redisTemplate.opsForValue().get(messageSentKey);
        
        if (messageSent == null) {
            // Message was not sent, mark as failed
            redisTemplate.opsForValue().set(failureKey, "0", 60, TimeUnit.SECONDS);
            return -1L;
        }

        // Check for timeout - if user has been polling for too long, mark as failed
        String timeoutKey = "flashSaleTimeout:" + userId + ":" + goodsId;
        String startTime = (String) redisTemplate.opsForValue().get(timeoutKey);
        
        if (startTime == null) {
            // First time polling, set start time
            redisTemplate.opsForValue().set(timeoutKey, String.valueOf(System.currentTimeMillis()), 5, TimeUnit.SECONDS);
        } else {
            // Check if polling has been going on for more than 1 second
            long elapsedTime = System.currentTimeMillis() - Long.parseLong(startTime);
            if (elapsedTime > 1000) { // 1 second timeout
                // Mark as failed due to timeout
                redisTemplate.opsForValue().set(failureKey, "0", 60, TimeUnit.SECONDS);
                redisTemplate.delete(timeoutKey);
                return -1L;
            }
        }

        // Still in queue, return 0
        return 0L;
    }

    @Override
    public String createFlashSalePath(User user, Long goodsId) {
        // Create a unique path for flash sale request
        String path = MD5Util.md5(UUIDUtil.uuid());
        // Save the path to Redis(key = flashSalePath:userID:goodsID, value = path)
        String redisKey = "flashSalePath:" + user.getId() + ":" + goodsId;
        // Set the path to expire in 60 seconds
        redisTemplate.opsForValue().set(redisKey, path, 60, TimeUnit.SECONDS);
        // Return the path
        return path;
    }

    @Override
    public Boolean checkFlashSalePath(User user, Long goodsId, String path) {
        if(user == null || goodsId == null || path == null){
            return false;
        }
        // get the request path from Redis
        String redisKey = "flashSalePath:" + user.getId() + ":" + goodsId;
        String redisPath = (String) redisTemplate.opsForValue().get(redisKey);
       
        // check if it matches the request path
        return redisPath.equals(path);
    }

    /**
     * Check the captcha
     * @param user current user
     * @param goodsId goods ID
     * @param captcha captcha string input by user
     * @return true if captcha is valid, false otherwise
     */
    @Override
    public Boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user == null || goodsId == null || captcha == null){
            return false;
        }
        // get the captcha from Redis
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        // check if the captcha is valid
        return captcha.equals(redisCaptcha);
    }
    
} 