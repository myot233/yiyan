package com.example.yiyan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.example.yiyan.mapper")
public class YiYanApplication implements WebMvcConfigurer {



	public static void main(String[] args) {
		SpringApplication.run(YiYanApplication.class, args);
	}

}
