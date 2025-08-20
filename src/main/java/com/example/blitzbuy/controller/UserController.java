package com.example.blitzbuy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.UserService;
import com.example.blitzbuy.vo.RespBean;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // Get user info
    @RequestMapping("/info")
    public RespBean info(User user){
        return RespBean.success(user);
    }

    // Update user password
    @RequestMapping("/updatePassword")
    @ResponseBody
    public RespBean updatePassword(String userTicket,
                                   String password,
                                   HttpServletRequest request,
                                   HttpServletResponse response){
        return userService.updatePassword(userTicket, password, request, response);
    }
}
