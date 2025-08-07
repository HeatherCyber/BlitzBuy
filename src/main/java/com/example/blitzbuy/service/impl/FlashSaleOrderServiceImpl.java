package com.example.blitzbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blitzbuy.mapper.FlashSaleOrderMapper;
import com.example.blitzbuy.pojo.FlashSaleOrder;
import com.example.blitzbuy.service.FlashSaleOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Heather
 * @version 1.0
 * Flash Sale Order Service Implementation Class
 */
@Service
public class FlashSaleOrderServiceImpl
        extends ServiceImpl<FlashSaleOrderMapper, FlashSaleOrder>
        implements FlashSaleOrderService {

} 