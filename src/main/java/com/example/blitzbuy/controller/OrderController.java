package com.example.blitzbuy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Heather
 * @version 1.0
 * Order Controller: Handle order detail page requests
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    /**
     * Show order detail demo page
     * @param model model
     * @param orderId order ID from URL parameter
     * @return order detail page
     */
    @RequestMapping("/detail")
    public String orderDetail(Model model, @RequestParam(required = false) String orderId) {
        // Add order ID to model for display
        model.addAttribute("orderId", orderId != null ? orderId : "12345");
        
        // Return order detail page
        return "orderDetail";
    }
}
