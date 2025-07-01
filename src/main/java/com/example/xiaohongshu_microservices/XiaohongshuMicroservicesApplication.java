package com.example.xiaohongshu_microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;


@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class XiaohongshuMicroservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(XiaohongshuMicroservicesApplication.class, args);
	}

}
