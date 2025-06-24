package com.example.xiaohongshu_microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@MapperScan("com.example.xiaohongshu_microservices.mapper")
public class XiaohongshuMicroservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(XiaohongshuMicroservicesApplication.class, args);
	}

}
