package com.example.blitzbuy.controller;

import com.example.blitzbuy.pojo.User;
import jakarta.servlet.http.HttpSession;
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

    //进入商品列表页面
    @RequestMapping("toList")
    public String toList(HttpSession session, Model model,
                         @CookieValue("userTicket") String ticket){

        //如果cookie里没有userTicket
        if(!StringUtils.hasText(ticket)){
            //重定向到登录页面
            return "login";
        }
        //如果cookie中已经存放了userTicket,取出session中对应的user
        User user = (User)session.getAttribute(ticket);
        //如果用户为空，说明没有成功登录
        if(null == user){
            return "login";
        }
        //如果用户存在，把user对象放入model, 携带给下一个模版使用
        model.addAttribute("user", user);
        //返回商品列表页面
        return "goodsList";
    }
}
