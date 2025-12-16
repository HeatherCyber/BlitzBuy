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
    MOBILE_ERROR(500211,"Invalid mobile number format."),
    USER_NOT_EXIST(500212,"User does not exist."),
    BING_ERROR(500213,"Invalid request parameters."),
    PASSWORD_UPDATE_FAIL(500214, "Password update failed."),

//  response messages for flash sale module
    NO_STOCK(500500, "No stock."),
    REPEAT_PURCHASE(500501, "This product is limited to 1 per customer."),
    IN_QUEUE(500503, "In queue, please wait..."),
    REQUEST_ILLEGAL(500504, "Request illegal."),
    SESSION_ERROR(500505, "Session error."),
    CAPTCHA_ERROR(500506, "Captcha error."),
    REQUEST_TOO_FREQUENT(500507, "Request too frequent, please try again later."),
    TRY_AGAIN(500508, "Flash sale failed, please try again."),
    GOODS_NOT_EXIST(500509, "Goods not found."),
    ORDER_NOT_EXIST(500510, "Order not found."),
    ACCESS_DENIED(500511, "Access denied.");



    private final Integer code;
    private final String message;

}
