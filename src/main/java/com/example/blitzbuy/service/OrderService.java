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
     * Get flash sale result for polling
     * @param userId user ID
     * @param goodsId goods ID
     * @return result:
     *         - orderId if flash sale successful
     *         - -1 if flash sale failed
     *         - 0 if still in queue
     */
    Long getFlashSaleResult(Long userId, Long goodsId);
    
} 