package com.example.blitzbuy.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Heather
 * @version 1.0
 */
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

//  response message for general use
    SUCCESS(200,"SUCCESS"),
    ERROR(500, "Internal Server Error."),

//  response message related to login
    LOGIN_ERROR(500210,"Invalid user ID or password."),
    BING_ERROR(500213,"Invalid request parameters"),
    MOBILE_ERROR(500211,"Invalid mobile number format."),
    MOBILE_NOT_EXIST(500212,"Mobile number not found."),

// 秒杀模块返回的信息
    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "该商品每人限购一件");

    private final Integer code;
    private final String message;

}
