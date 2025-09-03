package com.example.blitzbuy.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha验证码配置类
 * 配置验证码的样式、字体、颜色等属性
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // 验证码图片宽度
        properties.setProperty("kaptcha.image.width", "160");
        // 验证码图片高度
        properties.setProperty("kaptcha.image.height", "50");
        // 验证码字符长度
        properties.setProperty("kaptcha.textproducer.char.length", "5");
        // 验证码字符范围
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 验证码字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        // 验证码字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // 验证码背景颜色
        properties.setProperty("kaptcha.background.clear.from", "white");
        properties.setProperty("kaptcha.background.clear.to", "white");
        // 验证码边框
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.border.thickness", "1");
        // 验证码干扰线
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        // 验证码文字渲染器
        properties.setProperty("kaptcha.textproducer.impl", "com.google.code.kaptcha.text.impl.DefaultTextCreator");
        // 验证码图片样式
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
}
