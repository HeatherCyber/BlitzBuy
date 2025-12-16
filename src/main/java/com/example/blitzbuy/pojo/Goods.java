package com.example.blitzbuy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("goods")
public class Goods implements Serializable {

    @Serial
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String imageUrl;

    private BigDecimal price;

    private String description;

    private Integer stock;

    private boolean status;

    private Date createTime;

    private Date updateTime;

}
