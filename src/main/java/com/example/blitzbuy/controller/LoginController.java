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

    // Inject UserService
    @Resource
    private UserService userService;

    // Write method to enter login page
    @RequestMapping("toLogin")
    public String toLogin(){
        // Navigate to templates/login.html page
        return "login";
    }

    // Write method to handle user login request
    @RequestMapping("doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo,
                            HttpServletRequest request,
                            HttpServletResponse response){
        return userService.doLogin(loginVo, request, response);
    }
}
