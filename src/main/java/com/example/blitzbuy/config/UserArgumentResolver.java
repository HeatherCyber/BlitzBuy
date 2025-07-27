package com.example.blitzbuy.config;

import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.UserService;
import com.example.blitzbuy.util.CookieUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author Heather
 * @version 1.0
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Resource
    private UserService userService;

    /**
     *
     * @param parameter
     * @return: 只有返回true时，才会执行下面的resolveArgument()方法
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
//        获取参数类型
        Class<?> parameterType = parameter.getParameterType();
//        判断参数是否为User
        return parameterType == User.class;
    }

    /**
     * 此方法类似拦截器，自定义解析机制
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return: 返回一个User对象
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest nativeRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        String userTicket = CookieUtil.getCookieValue(nativeRequest, "userTicket");
        if(!StringUtils.hasText(userTicket)){
            return null;
        }
        return userService.getUserByCookie(userTicket, nativeRequest, nativeResponse);
    }
}
