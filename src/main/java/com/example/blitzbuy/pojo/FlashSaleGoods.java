package com.example.blitzbuy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Heather
 * @version 1.0
 */
@Data
@TableName("flash_sale_goods")
public class FlashSaleGoods implements Serializable {

    @Serial
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    private BigDecimal flashSalePrice;

    private Integer flashSaleStock;

    private Date startTime;

    private Date endTime;

    @TableField("is_active")
    private boolean active;
}
