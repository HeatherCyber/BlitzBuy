package com.example.blitzbuy.exception;

import com.example.blitzbuy.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Heather
 * @version 1.0
 * Custom global exception class for consistent error handling across the application.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException{

    /**
     * Standardized response enum containing:
     * - Error code
     * - Error message
     * - HTTP status reference
     */
    private RespBeanEnum respBeanEnum;
}
