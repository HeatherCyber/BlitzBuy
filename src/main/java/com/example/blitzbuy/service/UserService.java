package com.example.blitzbuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.vo.LoginVo;
import com.example.blitzbuy.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Heather
 * @version 1.0
 * User Service Interface - extends MyBatis-Plus's IService for CRUD operations
 * Why extend IService?
 * 1. Provides ready-to-use generic CRUD operations (save/update/delete/list etc.)
 * 2. Integrates with MyBatis-Plus's powerful query wrapper
 * 3. Reduces boilerplate code in service implementation
 *
 * Spring Boot + MyBatis-Plus Best Practice:
 * - Service interfaces should extend IService<T>
 * - ServiceImpl classes should extend ServiceImpl<M,T>
 */
public interface UserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo,
                     HttpServletRequest request,
                     HttpServletResponse response);


//    Query corresponding user information from Redis based on userTicket stored in cookie
    User getUserByCookie(String userTicket,
                         HttpServletRequest request,
                         HttpServletResponse response);
}
