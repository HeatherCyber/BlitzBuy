package com.example.blitzbuy.vo;

import com.example.blitzbuy.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Heather
 * @version 1.0
 * GoodsVo: Encapsulates goods information participating in flash sale activities for frontend page display
 * fields: Need to integrate attributes from Goods and FlashSaleGoods pojo classes, so extends Goods, and copy FlashSaleGoods attributes
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {

    private BigDecimal flashSalePrice;

    private Integer flashSaleStock;

    private Date startTime;

    private Date endTime;

}
