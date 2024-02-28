package com.example.yiyan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.example.yiyan.mapper")
public class YiYanApplication {

	public static void main(String[] args) {
		SpringApplication.run(YiYanApplication.class, args);
	}

}
