package com.example.blitzbuy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blitzbuy.exception.GlobalException;
import com.example.blitzbuy.mapper.UserMapper;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.UserService;
import com.example.blitzbuy.util.CookieUtil;
import com.example.blitzbuy.util.MD5Util;
import com.example.blitzbuy.util.UUIDUtil;
import com.example.blitzbuy.vo.LoginVo;
import com.example.blitzbuy.vo.RespBean;
import com.example.blitzbuy.vo.RespBeanEnum;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Heather
 * @version 1.0
 * User Service Implementation - Provides business logic for user operations
 *
 *  Key Features:
 *  - Inherits MyBatis-Plus's ServiceImpl for default CRUD operations
 *  - Implements custom business logic beyond basic CRUD
 *  - Follows Spring's dependency injection pattern
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Handles user login authentication
     *
     * @param loginVo Contains login credentials (mobile and password)
     * @param request Servlet request object
     * @param response Servlet response object
     * @return RespBean containing authentication result with:
     *         - SUCCESS status and data when authenticated
     *         - ERROR status with specific error code when failed
     *
     * @implNote Authentication flow:
     * 1. Validates input parameters
     * 2. Verifies mobile number format
     * 3. Checks user existence in database
     * 4. Validates password against stored credentials
     * 5. Returns appropriate response
     */
    @Override
    public RespBean doLogin(LoginVo loginVo,
                            HttpServletRequest request,
                            HttpServletResponse response) {
                                
        // Extract credentials from request
        String id = loginVo.getMobile();
        String password = loginVo.getPassword();

        
        // Check user existence
        User user = userMapper.selectById(id);
        if(user == null){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //Validate password
        if(!MD5Util.midPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        // After verification, user successfully logs in and is assigned a unique ticket number
        String ticket = UUIDUtil.uuid();
        // Save ticket-user as key-value pair to session
        //httpServletRequest.getSession().setAttribute(ticket, user);

        System.out.println("Using redisTemplate");
        // For distributed session management, store successfully logged in user info in Redis
        // Storage format: key ("userTicket:UUID number")
        // Storage format: value (user object)
        redisTemplate.opsForValue().set("userTicket:"+ticket, user);
        System.out.println("Using redisTemplate finished ");
        // Also save ticket to cookie with key-value: "userTicket"-ticket
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        // Return success response
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {

        // If userTicket is not empty, get user info from Redis
        if(!StringUtils.hasText(userTicket)){
            return null;
        }
        User user = (User)redisTemplate.opsForValue().get("userTicket:" + userTicket);

        // If retrieved user info is not empty, reset cookie, equivalent to refreshing cookie lifecycle, restart timer
        if(user != null){
            CookieUtil.setCookie(request,response,"userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, 
                                    String password,
                                    HttpServletRequest request, 
                                    HttpServletResponse response) {

        // Get user info from Redis
        User user = getUserByCookie(userTicket, request, response);
        if(user == null){
            throw new GlobalException(RespBeanEnum.USER_NOT_EXIST);
        }
        // Update user password
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        // Update user password in database
        int result = userMapper.updateById(user);
        if(result == 1){ // Update successful
            // Delete userTicket from Redis
            redisTemplate.delete("userTicket:" + userTicket);
            return RespBean.success();
        }
        // Update failed
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
        
    }
}
