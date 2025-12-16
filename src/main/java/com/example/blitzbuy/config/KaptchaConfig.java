package com.example.blitzbuy.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha Captcha Configuration Class
 * Configure captcha style, font, color and other properties
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha defaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        
        // Captcha image width
        properties.setProperty("kaptcha.image.width", "160");
        // Captcha image height
        properties.setProperty("kaptcha.image.height", "50");
        // Captcha character length
        properties.setProperty("kaptcha.textproducer.char.length", "5");
        // Captcha character range
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // Captcha font size
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        // Captcha font color
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        // Captcha background color
        properties.setProperty("kaptcha.background.clear.from", "white");
        properties.setProperty("kaptcha.background.clear.to", "white");
        // Captcha border
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.border.thickness", "1");
        // Captcha noise lines
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        // Captcha text renderer
        properties.setProperty("kaptcha.textproducer.impl", "com.google.code.kaptcha.text.impl.DefaultTextCreator");
        // Captcha image style
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.WaterRipple");
        
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        
        return defaultKaptcha;
    }
}
