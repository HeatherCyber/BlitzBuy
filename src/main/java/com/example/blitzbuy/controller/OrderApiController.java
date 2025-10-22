package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.Order;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.vo.RespBean;
import com.example.blitzbuy.vo.RespBeanEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

/**
 * Order API Controller
 * RESTful API endpoints for order data
 * 
 * @author Heather
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderApiController {

    @Resource
    private OrderService orderService;

    /**
     * Get order detail by ID
     * GET /api/v1/orders/{id}
     */
    @GetMapping("/{id}")
    public RespBean getOrderDetail(@PathVariable Long id,
                                 com.example.blitzbuy.pojo.User user,
                                 HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Get order from database
        Order order = orderService.getById(id);
        if (order == null) {
            return RespBean.error(RespBeanEnum.ORDER_NOT_EXIST);
        }

        // Check if order belongs to current user
        if (!order.getUserId().equals(user.getId())) {
            return RespBean.error(RespBeanEnum.ACCESS_DENIED);
        }

        return RespBean.success(order);
    }

    /**
     * Get user's orders
     * GET /api/v1/orders
     */
    @GetMapping
    public RespBean getUserOrders(com.example.blitzbuy.pojo.User user,
                                HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Get user's orders
        java.util.List<Order> orders = orderService.list(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order>()
                .eq("user_id", user.getId())
                .orderByDesc("create_date")
        );

        return RespBean.success(orders);
    }

    /**
     * Process payment for an order
     * POST /api/v1/orders/{id}/pay
     */
    @PostMapping("/{id}/pay")
    public RespBean processPayment(@PathVariable Long id,
                                  com.example.blitzbuy.pojo.User user,
                                  HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        
        // Get order from database
        Order order = orderService.getById(id);
        if (order == null) {
            return RespBean.error(RespBeanEnum.ORDER_NOT_EXIST);
        }
        
        // Check if order belongs to current user
        if (!order.getUserId().equals(user.getId())) {
            return RespBean.error(RespBeanEnum.ACCESS_DENIED);
        }
        
        // Check if order is in pending payment status
        if (order.getOrderStatus() != 0) {
            return RespBean.error(RespBeanEnum.ERROR.getCode(), "Order is not in pending payment status");
        }
        
        // Update order status to paid (status = 1)
        order.setOrderStatus(1);
        order.setPayTime(new java.util.Date());
        orderService.updateById(order);
        
        return RespBean.success("Payment processed successfully");
    }
}
