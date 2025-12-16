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

    /**
     * Create flash sale order
     * @param user current user
     * @param goodsVo goods information
     * @return Order object if successful, null if failed
     */
    Order creatFlashSaleOrder(User user, GoodsVo goodsVo);
    

    /**
     * Create flash sale path for security check
     * @param user current user
     * @param goodsId goods ID
     * @return path string
     */
    String createFlashSalePath(User user, Long goodsId);

    /**
     * Check the path 
     * @param user current user
     * @param goodsId goods ID
     * @param path path string
     * @return true if path is valid, false otherwise
     */
    Boolean checkFlashSalePath(User user, Long goodsId, String path);

    /**
     * Check the captcha
     * @param user current user
     * @param goodsId goods ID
     * @param captcha captcha string
     * @return true if captcha is valid, false otherwise
     */
    Boolean checkCaptcha(User user, Long goodsId, String captcha);
    
} 