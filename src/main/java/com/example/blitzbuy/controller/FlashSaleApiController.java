package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.OrderService;
import com.example.blitzbuy.vo.RespBean;
import com.example.blitzbuy.vo.RespBeanEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Flash Sale API Controller
 * RESTful API endpoints for flash sale operations
 * 
 * @author Heather
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/flash-sale")
public class FlashSaleApiController {

    @Resource
    private OrderService orderService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Get captcha image URL
     * GET /api/v1/flash-sale/captcha/{goodsId}
     */
    @GetMapping("/captcha/{goodsId}")
    public RespBean getCaptchaUrl(@PathVariable Long goodsId) {
        String captchaUrl = "/flashSale/getCaptcha?goodsId=" + goodsId;
        return RespBean.success(captchaUrl);
    }

    /**
     * Get flash sale path after captcha verification
     * POST /api/v1/flash-sale/path
     */
    @PostMapping("/path")
    public RespBean getFlashSalePath(@RequestParam Long goodsId,
                                   @RequestParam String captcha,
                                   User user,
                                   HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Check captcha
        if (!orderService.checkCaptcha(user, goodsId, captcha)) {
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }

        // Generate flash sale path
        String path = orderService.createFlashSalePath(user, goodsId);
        return RespBean.success(path);
    }

    /**
     * Check if user has already purchased this flash sale item
     * GET /api/v1/flash-sale/check-purchase/{goodsId}
     */
    @GetMapping("/check-purchase/{goodsId}")
    public RespBean checkPurchase(@PathVariable Long goodsId,
                                 User user,
                                 HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Check if user has already purchased this item
        com.example.blitzbuy.pojo.FlashSaleOrder flashSaleOrder = 
            (com.example.blitzbuy.pojo.FlashSaleOrder) redisTemplate.opsForValue()
                .get("flashSaleOrder:" + user.getId() + ":" + goodsId);
        
        if (flashSaleOrder != null) {
            return RespBean.error(RespBeanEnum.REPEAT_PURCHASE);
        }
        
        return RespBean.success("User can purchase this item");
    }

    /**
     * Execute flash sale purchase
     * POST /api/v1/flash-sale/purchase
     */
    @PostMapping("/purchase")
    public RespBean purchase(@RequestParam String path,
                           @RequestParam Long goodsId,
                           User user,
                           HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Call existing flash sale logic
        // We'll need to modify FlashSaleController to extract the logic
        // For now, redirect to existing endpoint
        return RespBean.success("Use existing /flashSale/doFlashSale/{path} endpoint");
    }
}
