package com.example.xiaohongshu_microservices.Utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP客户端工具类
 * 支持自动传递traceId，便于微服务间调用追踪
 */
public class HttpClientUtils {
    
    private static final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * GET请求
     */
    public static <T> ResponseEntity<T> get(String url, Class<T> responseType) {
        HttpHeaders headers = createHeadersWithTraceId();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
    }
    
    /**
     * POST请求
     */
    public static <T> ResponseEntity<T> post(String url, Object body, Class<T> responseType) {
        HttpHeaders headers = createHeadersWithTraceId();
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
    }
    
    /**
     * PUT请求
     */
    public static <T> ResponseEntity<T> put(String url, Object body, Class<T> responseType) {
        HttpHeaders headers = createHeadersWithTraceId();
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
    }
    
    /**
     * DELETE请求
     */
    public static <T> ResponseEntity<T> delete(String url, Class<T> responseType) {
        HttpHeaders headers = createHeadersWithTraceId();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType);
    }
    
    /**
     * 创建包含traceId的请求头
     */
    private static HttpHeaders createHeadersWithTraceId() {
        HttpHeaders headers = new HttpHeaders();
        String traceId = MDCTraceUtils.getTraceId();
        if (traceId != null && !traceId.isEmpty()) {
            headers.set("X-Trace-Id", traceId);
        }
        return headers;
    }
    
    /**
     * 获取RestTemplate实例
     */
    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
