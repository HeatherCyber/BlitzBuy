package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
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
}
