package com.example.blitzbuy.controller;
import com.example.blitzbuy.pojo.User;
import com.example.blitzbuy.service.GoodsService;
import com.example.blitzbuy.vo.GoodsVo;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

/**
 * @author Heather
 * @version 1.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    // Template resolver for manual rendering
    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * Enter goods list page - optimized with Redis caching
     * @param model: Used to pass data to frontend template
     * @param user: User object obtained through WebMvcConfigurer parameter resolution
     * @return: String, complete HTML page content
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toList(Model model, 
                        User user, 
                        HttpServletRequest request, 
                        HttpServletResponse response){

        // If user is null, login was not successful
        if(null == user){
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", "/login/toLogin");
            return "";
        }

        // Get goods list HTML from Redis
        String goodsListKey = "goodsList";
        String goodsListHtml = (String) redisTemplate.opsForValue().get(goodsListKey);

        // If HTML exists in Redis, return HTML content directly
        if (StringUtils.hasText(goodsListHtml)) {
            return goodsListHtml;
        }

        // If HTML does not exist in Redis, query database and render template
        // Add user and goods data to model for template rendering
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.getGoodsVo());
        
        // Manually render HTML page using Thymeleaf
        try {
            Context context = new Context(request.getLocale(), model.asMap());
            goodsListHtml = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
            // If rendering successful
            if(StringUtils.hasText(goodsListHtml)){
                // Store complete HTML page in Redis with expiration time (60 seconds)
                redisTemplate.opsForValue().set(goodsListKey, goodsListHtml, 60, TimeUnit.SECONDS);
            } else {
                // If rendering failed, return error page
                return "error";
            }
        } catch (Exception e) {
            // Log the error and return error page
            System.err.println("Template rendering error: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
        
        // Return goods list page
        return goodsListHtml;
    }

    /**
     * Enter goods detail page - based on goods ID - optimized with Redis
     * @param model: Used to pass data to frontend
     * @param user: User object obtained through WebMvcConfigurer parameter resolution
     * @param goodsId: Goods ID, path variable, obtained from request path
     * @return: String, corresponding HTML page name
     */
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String toDetail(Model model, 
                            User user, 
                            HttpServletRequest request,
                            HttpServletResponse response,
                            @PathVariable Long goodsId){
        // If user is null, login was not successful
        if(null == user){
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", "/login/toLogin");
            return "";
        }

        // Get goods detail HTML from Redis
        String goodsDetailKey = "goodsDetail:" + goodsId;
        String goodsDetailHtml = (String) redisTemplate.opsForValue().get(goodsDetailKey);

        // If HTML exists in Redis, return HTML content directly
        if (StringUtils.hasText(goodsDetailHtml)) {
            return goodsDetailHtml;
        }

        // Add user object to model
        model.addAttribute("user", user);

        // Add queried goods details（from database） to model
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        // Add queried goods details to model
        model.addAttribute("goods", goodsVo);
        // Return flash sale goods detail page with flash sale status
        // Get flash sale status: 0-not started, 1-in progress, 2-ended
        int status = 0;
        // Remaining time: seconds
        long remainSeconds = 0;
        // Get flash sale start time
        Date startTime = goodsVo.getStartTime();
        // Get flash sale end time
        Date endTime = goodsVo.getEndTime();
        // Get current time
        Date now = new Date();

        if(now.before(startTime)){
            // Flash sale not started
            status = 0;
            // Seconds until flash sale starts
            remainSeconds = (startTime.getTime() - now.getTime()) / 1000;
        }else if(now.after(startTime) && now.before(endTime)){
            // Flash sale in progress
            status = 1;
            // Seconds until flash sale ends
            remainSeconds = (endTime.getTime() - now.getTime()) / 1000;
        }else{
            status = 2;
            remainSeconds = -1;
        }

        // Add status and remainSeconds to model
        model.addAttribute("flashSaleStatus", status);
        model.addAttribute("remainSeconds", remainSeconds);

         // If not in Redis, get from database and cache HTML
         // Manually render HTML page
         try {
             Context context = new Context(request.getLocale(), model.asMap());
             goodsDetailHtml = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
             // If rendering successful
             if(StringUtils.hasText(goodsDetailHtml)){
                 // Store complete HTML page in Redis with expiration time (60 seconds)
                 redisTemplate.opsForValue().set(goodsDetailKey, goodsDetailHtml, 60, TimeUnit.SECONDS);
             } else {
                 // If rendering failed, return error page
                 return "error";
             }
         } catch (Exception e) {
             // Log the error and return error page
             System.err.println("Template rendering error: " + e.getMessage());
             e.printStackTrace();
             return "error";
         }

        // Return flash sale goods detail page
        return goodsDetailHtml;
    }
}
