package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.vo.GoodsVo;
import com.example.blitzbuy.vo.RespBean;
import com.example.blitzbuy.vo.RespBeanEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Goods API Controller
 * RESTful API endpoints for goods data
 * 
 * @author Heather
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/goods")
public class GoodsApiController {

    @Resource
    private GoodsService goodsService;

    /**
     * Get all flash sale goods
     * GET /api/v1/goods
     */
    @GetMapping
    public RespBean getGoodsList(User user, HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        List<GoodsVo> goodsList = goodsService.getGoodsVo();
        return RespBean.success(goodsList);
    }

    /**
     * Get goods detail by ID
     * GET /api/v1/goods/{id}
     */
    @GetMapping("/{id}")
    public RespBean getGoodsDetail(@PathVariable Long id, 
                                  User user, 
                                  HttpServletResponse response) {
        // Check if user is logged in
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(id);
        if (goods == null) {
            return RespBean.error(RespBeanEnum.GOODS_NOT_EXIST);
        }

        return RespBean.success(goods);
    }
}
