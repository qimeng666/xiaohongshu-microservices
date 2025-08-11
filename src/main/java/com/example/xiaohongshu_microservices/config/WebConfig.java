package com.example.xiaohongshu_microservices.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册拦截器和其他Web相关配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private TraceInterceptor traceInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册追踪拦截器，应用到所有请求
        registry.addInterceptor(traceInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/**", "/error", "/favicon.ico");
    }
}
