package com.example.blitzbuy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Heather
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespBean {

    private long code;
    private String message;
    private Object object;

    // if success,response 'success' with data
    public static RespBean success(Object data){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),
                RespBeanEnum.SUCCESS.getMessage(),
                data
        );
    }

    // if success,response 'success' without data
    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),
                RespBeanEnum.SUCCESS.getMessage(),
                null
        );
    }

    // if error,response 'error' with data
    public static RespBean error(RespBeanEnum respBeanEnum, Object data){
        return new RespBean(respBeanEnum.getCode(),
                respBeanEnum.getMessage(),
                data
        );
    }

    // if error,response 'error' without data
    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(),
                respBeanEnum.getMessage(),
                null
        );
    }

}
