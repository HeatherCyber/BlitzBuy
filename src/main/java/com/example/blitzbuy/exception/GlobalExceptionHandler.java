package com.example.blitzbuy.exception;

import com.example.blitzbuy.vo.RespBean;
import com.example.blitzbuy.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Heather
 * @version 1.0
 */

/**
 * Global exception handler for centralized exception processing across all controllers.
 *
 * This class handles exceptions thrown during request processing and converts them
 * into standardized API responses. It supports:
 * -Custom business exceptions ({@link com.example.blitzbuy.exception.GlobalException})
 * -Parameter binding validation errors ({@link BindException})
 * -Fallback handling for all other exceptions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles all uncaught exceptions in the application.
     * @param e The caught exception instance
     * @return Standardized error response wrapped in {@link RespBean}
     */
    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e) {
        // Handle custom business exceptions
        if (e instanceof GlobalException) {
            GlobalException ge = (GlobalException) e;
            return RespBean.error(ge.getRespBeanEnum());
        }
        // Process parameter binding validation errors
        else if (e instanceof BindException) {
            BindException be = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BING_ERROR);
            // Append the first validation error message to response
            respBean.setMessage("Parameter binding exception: " + be.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        // Fallback for unclassified exceptions
        return RespBean.error(RespBeanEnum.ERROR);
    }
}