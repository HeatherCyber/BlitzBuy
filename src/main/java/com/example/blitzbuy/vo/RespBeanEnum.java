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
    USER_NOT_EXIST(500212,"User does not exist."),

// Flash sale module response messages
    INSUFFICIENT_STOCK(500500, "Insufficient stock"),
    REPEAT_ERROR(500501, "This item is limited to one per person"),
    PASSWORD_UPDATE_FAIL(500502, "Password update failed."),
    QUEUE_SUCCESS(500503, "Queueing, please wait...");

    private final Integer code;
    private final String message;

}
