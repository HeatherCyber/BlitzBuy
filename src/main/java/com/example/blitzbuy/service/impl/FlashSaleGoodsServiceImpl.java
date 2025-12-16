package com.example.blitzbuy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blitzbuy.mapper.FlashSaleGoodsMapper;
import com.example.blitzbuy.pojo.FlashSaleGoods;
import com.example.blitzbuy.service.FlashSaleGoodsService;
import org.springframework.stereotype.Service;

/**
 * @author Heather
 * @version 1.0
 */
@Service
public class FlashSaleGoodsServiceImpl
        extends ServiceImpl<FlashSaleGoodsMapper, FlashSaleGoods>
        implements FlashSaleGoodsService {
}
