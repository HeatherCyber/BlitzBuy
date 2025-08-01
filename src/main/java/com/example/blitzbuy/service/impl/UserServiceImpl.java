package com.example.blitzbuy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blitzbuy.exception.GlobalException;
import com.example.blitzbuy.mapper.UserMapper;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.UserService;
import com.example.blitzbuy.util.CookieUtil;
import com.example.blitzbuy.util.MD5Util;
import com.example.blitzbuy.util.UUIDUtil;
import com.example.blitzbuy.util.ValidatorUtil;
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
    private RedisTemplate redisTemplate;

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

//      优化下面的校验方法：使用自定义注解+全局异常处理器完成用户校验，而不是手写校验方法

//        // Validate required fields
//        if(!StringUtils.hasText(mobile) || !StringUtils.hasText(password)){
//            return  RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }

//        // Verify mobile number format
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        
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

        //通过验证后，用户成功登录，同时被分配一个唯一的ticket编号
        String ticket = UUIDUtil.uuid();
        //将ticket-user作为一组key-value保存到session中
        //httpServletRequest.getSession().setAttribute(ticket, user);

        System.out.println("使用redisTemplate");
        //为了实现分布式会话管理，把成功登录的用户信息存放到Redis
        // 存储格式： key (“userTicket:UUID编号”)
        // 存储格式： value (user对象)
        redisTemplate.opsForValue().set("userTicket:"+ticket, user);
        System.out.println("使用redisTemplate finished ");
        //同时将ticket保存到cookie中，使用key-value: "userTicket"-ticket
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        // Return success response
        return RespBean.success();
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {

        //如果usericket不为空，就到Redis中获取user信息
        if(!StringUtils.hasText(userTicket)){
            return null;
        }
        User user = (User)redisTemplate.opsForValue().get("userTicket:" + userTicket);

        // 如果获取到的user信息不为空，重置cookie，相当于刷新该cookie的生命周期，重新计时
        if(user != null){
            CookieUtil.setCookie(request,response,"userTicket", userTicket);
        }
        return user;
    }
}
