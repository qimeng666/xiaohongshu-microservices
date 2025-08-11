package com.example.xiaohongshu_microservices.config;

import com.example.xiaohongshu_microservices.Utils.MDCTraceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求追踪拦截器
 * 自动为每个请求设置追踪ID
 */
@Component
public class TraceInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取追踪ID，如果没有则生成新的
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = MDCTraceUtils.generateTraceId();
        }
        
        // 设置追踪ID到MDC
        MDCTraceUtils.setTraceId(traceId);
        MDCTraceUtils.setRequestId(traceId);
        
        // 将追踪ID添加到响应头，便于前端或其他服务使用
        response.addHeader("X-Trace-Id", traceId);
        
        logger.info("Request started - Method: {}, URI: {}, TraceId: {}", 
                   request.getMethod(), request.getRequestURI(), traceId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String traceId = MDCTraceUtils.getTraceId();
        
        if (ex != null) {
            logger.error("Request failed - Method: {}, URI: {}, TraceId: {}, Error: {}", 
                        request.getMethod(), request.getRequestURI(), traceId, ex.getMessage(), ex);
        } else {
            logger.info("Request completed - Method: {}, URI: {}, TraceId: {}, Status: {}", 
                       request.getMethod(), request.getRequestURI(), traceId, response.getStatus());
        }
        
        // 清理MDC
        MDCTraceUtils.clear();
    }
}
