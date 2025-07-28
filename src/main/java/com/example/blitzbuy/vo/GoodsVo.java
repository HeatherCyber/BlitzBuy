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
 * GoodVo： 封装前端页面需要展示的参与秒杀活动的商品信息
 * fields: 需要整合Goods和sFlashSaleGood两个pojo类的属性，所以要extends Goods，同时把FlashSaleGood的属性复制过来
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
