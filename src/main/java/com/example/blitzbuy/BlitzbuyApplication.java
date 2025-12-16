package com.example.blitzbuy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.blitzbuy.mapper")
public class BlitzbuyApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlitzbuyApplication.class, args);
    }

}
