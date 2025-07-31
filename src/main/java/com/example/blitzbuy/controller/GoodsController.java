package com.example.blitzbuy.controller;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.vo.GoodsVo;

import jakarta.annotation.Resource;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Heather
 * @version 1.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    //进入商品列表页面
    /**
     *
     * @param model
     * @param user: 通过WebMvcConfigurer解析参数，得到的User对象
     * @return: String，对应的html页面名称
     */
    @RequestMapping("/toList")
    public String toList(Model model, User user){

        //如果用户为空，说明没有成功登录
        if(null == user){
            return "login";
        }
        //如果用户存在，把user对象放入model, 携带给下一个模版使用
        model.addAttribute("user", user);
        //把促销的商品列表也放入到model中
        model.addAttribute("goodsList", goodsService.getGoodsVo());
        //返回商品列表页面
        return "goodsList";
    }

    /**
     * 进入商品详情页面--根据商品id
     * @param model: 用于传递数据到前端
     * @param user: 通过WebMvcConfigurer解析参数，得到的User对象
     * @param goodsId: 商品id,路径变量，从请求路径中获取
     * @return: String，对应的html页面名称
     */
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId){
        // 如果用户为空，说明没有成功登录
        if(null == user){
            return "login";
        }
        // 把用户对象放入model中
        model.addAttribute("user", user);
        // 把查询到的商品详情放入model中
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);
        // 返回秒杀商品详情页面，同时返回秒杀商品的状态
        // 获取秒杀商品的状态：0-未开始，1-进行中，2-已结束
        int status = 0;
        // 剩余时间：秒
        long remainSeconds = 0;
        // 获取秒杀商品的开始时间
        Date startTime = goodsVo.getStartTime();
        // 获取秒杀商品的结束时间
        Date endTime = goodsVo.getEndTime();
        // 获取当前时间
        Date now = new Date();

        if(now.before(startTime)){
            // 秒杀未开始
            status = 0;
            // 距离秒杀开始还有多少秒
            remainSeconds = (startTime.getTime() - now.getTime()) / 1000;
        }else if(now.after(startTime) && now.before(endTime)){
            // 秒杀进行中
            status = 1;
            //  距离秒杀结束还有多少秒
            remainSeconds = (endTime.getTime() - now.getTime()) / 1000;
        }else{
            status = 2;
            remainSeconds = -1;
        }

        //  把status和remainSeconds放入model中
        model.addAttribute("flashSaleStatus", status);
        model.addAttribute("remainSeconds", remainSeconds);

        // 返回秒杀商品详情页面
        return "goodsDetail";
    }
}
