package com.example.blitzbuy.config;

import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
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
     * @return: Only when returning true will the resolveArgument() method below be executed
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
//        Get parameter type
        Class<?> parameterType = parameter.getParameterType();
//        Check if parameter is User
        return parameterType == User.class;
    }

    /**
     * This method is similar to an interceptor, custom resolution mechanism
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return: Returns a User object
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // get the user from the thread local
        return UserContext.getUser();
    }
}
