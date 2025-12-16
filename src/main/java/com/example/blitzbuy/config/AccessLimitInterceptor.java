package com.example.blitzbuy.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.data.redis.core.RedisTemplate;
import jakarta.annotation.Resource;

import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.UserService;
import com.example.blitzbuy.vo.RespBean;
import com.example.blitzbuy.vo.RespBeanEnum;
import com.example.blitzbuy.util.CookieUtil;

import cn.hutool.json.JSONUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * AccessLimitInterceptor: interceptor for rate limiting
 * @author Heather
 * @version 1.0
 */

 @Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;

    /**
     * Intercept the request
     * 1. get the User from the request, put it into thread local
     * 2. check the rate limit by redis
     * 3. if the rate limit is exceeded, return error
     * 4. if the rate limit is not exceeded, continue
     * 
     * @param request
     * @param response
     * @param handler
     * @return true if the request is allowed, false otherwise
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // check if the handler is a method
        if(handler instanceof HandlerMethod){
            // get the user from the request
            User user = getUser(request, response);
            // put the user into thread local
            UserContext.setUser(user);
            // get the method
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // get the AccessLimit annotation
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            // check if the method has AccessLimit annotation
            if(accessLimit != null){
                // get the annotation's parameters
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                boolean needLogin = accessLimit.needLogin();    
                // check if the user is logged in
                if(needLogin && user == null){
                    // if the user is not logged in, return error
                    returnError(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                // get the requestURI and userId to form the key
                String requestURI = request.getRequestURI();
                String key = requestURI + ":" + user.getId();
                Integer count = (Integer) redisTemplate.opsForValue().get(key);
                // if the count is null, set the count to 1
                if(count == null){
                    redisTemplate.opsForValue().set(key, 1, seconds, TimeUnit.SECONDS);
                }else{
                    if(count >= maxCount){
                        // if the count is greater than the maxCount, return error
                        returnError(response, RespBeanEnum.REQUEST_TOO_FREQUENT);
                        return false;
                    }
                    redisTemplate.opsForValue().increment(key);
                }
                return true;
            }
        }
        return true;
    }

    //method to get User from the request
    private User getUser(HttpServletRequest request, HttpServletResponse response){
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(userTicket == null){// if userTicket is not exist, have not login
            // return null
            return null;
        }
        return userService.getUserByCookie(userTicket, request, response);
    }

    //method to return error by stream
    private void returnError(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException{
        
           response.setContentType("application/json;charset=UTF-8");
           PrintWriter out = response.getWriter();
           RespBean error = RespBean.error(respBeanEnum);
           out.write(JSONUtil.toJsonStr(error));
           out.flush();
           out.close();
    }

}
