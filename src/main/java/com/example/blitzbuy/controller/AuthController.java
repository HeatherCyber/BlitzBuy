package com.example.blitzbuy.controller;

import com.example.blitzbuy.service.UserService;
import com.example.blitzbuy.vo.LoginVo;
import com.example.blitzbuy.vo.RespBean;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication API Controller
 * RESTful API endpoints for authentication
 * 
 * @author Heather
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Resource
    private UserService userService;

    /**
     * User login
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public RespBean login(@Valid @RequestBody LoginVo loginVo,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        return userService.doLogin(loginVo, request, response);
    }

    /**
     * Get current user info
     * GET /api/v1/auth/me
     */
    @GetMapping("/me")
    public RespBean getCurrentUser(com.example.blitzbuy.pojo.User user) {
        if (user == null) {
            return RespBean.error(com.example.blitzbuy.vo.RespBeanEnum.SESSION_ERROR);
        }
        return RespBean.success(user);
    }

    /**
     * User logout
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public RespBean logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear session/cookie
        // Implementation depends on your session management strategy
        return RespBean.success("Logout successful");
    }
}
