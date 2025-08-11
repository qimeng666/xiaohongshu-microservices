package com.example.xiaohongshu_microservices.Utils;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * MDC追踪工具类
 * 用于管理请求链路追踪ID和用户信息
 */
public class MDCTraceUtils {
    
    public static final String TRACE_ID = "traceId";
    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";
    
    /**
     * 生成新的追踪ID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 设置追踪ID
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }
    
    /**
     * 获取当前追踪ID
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }
    
    /**
     * 设置用户ID
     */
    public static void setUserId(String userId) {
        MDC.put(USER_ID, userId);
    }
    
    /**
     * 获取当前用户ID
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }
    
    /**
     * 设置请求ID
     */
    public static void setRequestId(String requestId) {
        MDC.put(REQUEST_ID, requestId);
    }
    
    /**
     * 获取当前请求ID
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }
    
    /**
     * 清除所有MDC信息
     */
    public static void clear() {
        MDC.clear();
    }
    
    /**
     * 清除指定的MDC信息
     */
    public static void remove(String key) {
        MDC.remove(key);
    }
}
