package com.example.blitzbuy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * This class is used to store the key information of the flash sale request, 
 * and provide a data carrier for subsequent asynchronous processing
 * 
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlashSaleMessage {
    
    // The user who made the request
    private User user;

    // The id of the goods  
    private Long goodsId;
}
