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

    /**
     * Handles user login authentication
     *
     * @param loginVo Contains login credentials (mobile and password)
     * @param httpServletRequest Servlet request object
     * @param httpServletResponse Servlet response object
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
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        // Extract credentials from request
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

//        // Validate required fields
//        if(!StringUtils.hasText(mobile) || !StringUtils.hasText(password)){
//            return  RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }

//        // Verify mobile number format
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        // Check user existence
        User user = userMapper.selectById(mobile);
        if(null == user){
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
        httpServletRequest.getSession().setAttribute(ticket, user);
        //同时将ticket保存到cookie中，使用key-value: "userTicket"-ticket
        CookieUtil.setCookie(httpServletRequest, httpServletResponse, "userTicket", ticket);

        // Return success response
        return RespBean.success();
    }
}
