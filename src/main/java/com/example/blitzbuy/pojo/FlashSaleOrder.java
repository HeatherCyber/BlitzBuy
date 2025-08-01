package com.example.blitzbuy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * @author Heather
 * @version 1.0
 * 秒杀订单实体类
 */
@Data
@TableName("flash_sale_order")
public class FlashSaleOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;

} 