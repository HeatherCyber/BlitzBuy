package com.example.blitzbuy.controller;

import com.example.blitzbuy.service.UserService;
import com.example.blitzbuy.vo.LoginVo;
import com.example.blitzbuy.vo.RespBean;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Heather
 * @version 1.0
 */
@Controller
@Slf4j
@RequestMapping("/login")
public class LoginController {

    //装配UserService
    @Resource
    private UserService userService;

    // 编写方法，可以进入登录页面
    @RequestMapping("/toLogin")
    public String toLogin(){
        //导航到templates/login.html页面
        return "login";
    }

    //编写方法，处理用户登录请求
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo,
                            HttpServletRequest request,
                            HttpServletResponse response){

        return userService.doLogin(loginVo, request, response);

    }
}
