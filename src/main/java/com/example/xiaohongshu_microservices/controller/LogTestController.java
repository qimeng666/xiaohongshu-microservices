package com.example.xiaohongshu_microservices.controller;

import com.example.xiaohongshu_microservices.Utils.MDCTraceUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志测试Controller
 * 用于验证日志配置和链路追踪功能
 */
@RestController
@RequestMapping("/log-test")
@Tag(name = "日志测试", description = "测试日志配置和链路追踪")
public class LogTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(LogTestController.class);
    
    @Operation(summary = "测试不同级别的日志")
    @GetMapping("/levels")
    public ResponseEntity<Map<String, String>> testLogLevels() {
        logger.trace("这是一条TRACE级别的日志");
        logger.debug("这是一条DEBUG级别的日志");
        logger.info("这是一条INFO级别的日志");
        logger.warn("这是一条WARN级别的日志");
        logger.error("这是一条ERROR级别的日志");
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "日志测试完成，请查看控制台和日志文件");
        result.put("traceId", MDCTraceUtils.getTraceId());
        
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "测试异常日志")
    @GetMapping("/exception")
    public ResponseEntity<Map<String, String>> testExceptionLog() {
        try {
            // 模拟异常
            throw new RuntimeException("这是一个测试异常");
        } catch (Exception e) {
            logger.error("捕获到异常 - 错误信息: {}", e.getMessage(), e);
            
            Map<String, String> result = new HashMap<>();
            result.put("message", "异常日志测试完成");
            result.put("traceId", MDCTraceUtils.getTraceId());
            result.put("error", e.getMessage());
            
            return ResponseEntity.ok(result);
        }
    }
    
    @Operation(summary = "测试MDC追踪")
    @GetMapping("/trace/{userId}")
    public ResponseEntity<Map<String, String>> testTrace(@PathVariable String userId) {
        // 设置用户ID到MDC
        MDCTraceUtils.setUserId(userId);
        
        logger.info("开始处理用户请求 - 用户ID: {}", userId);
        
        // 模拟业务处理
        try {
            Thread.sleep(100); // 模拟处理时间
            logger.debug("业务处理中 - 用户ID: {}", userId);
            
            // 模拟调用其他服务
            logger.info("调用用户服务 - 用户ID: {}", userId);
            
            Map<String, String> result = new HashMap<>();
            result.put("message", "MDC追踪测试完成");
            result.put("traceId", MDCTraceUtils.getTraceId());
            result.put("userId", userId);
            result.put("requestId", MDCTraceUtils.getRequestId());
            
            logger.info("请求处理完成 - 用户ID: {}, 结果: {}", userId, result);
            
            return ResponseEntity.ok(result);
        } catch (InterruptedException e) {
            logger.error("处理被中断 - 用户ID: {}", userId, e);
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(summary = "测试自定义traceId")
    @GetMapping("/custom-trace")
    public ResponseEntity<Map<String, String>> testCustomTrace(@RequestHeader(value = "X-Trace-Id", required = false) String customTraceId) {
        String currentTraceId = MDCTraceUtils.getTraceId();
        
        logger.info("当前追踪ID: {}, 自定义追踪ID: {}", currentTraceId, customTraceId);
        
        Map<String, String> result = new HashMap<>();
        result.put("currentTraceId", currentTraceId);
        result.put("customTraceId", customTraceId);
        result.put("message", "自定义traceId测试完成");
        
        return ResponseEntity.ok(result);
    }
    
    @Operation(summary = "测试微服务间调用")
    @GetMapping("/service-call")
    public ResponseEntity<Map<String, String>> testServiceCall() {
        logger.info("开始测试微服务间调用");
        
        // 模拟调用其他微服务
        String traceId = MDCTraceUtils.getTraceId();
        logger.info("准备调用其他服务，当前traceId: {}", traceId);
        
        // 这里可以实际调用HttpClientUtils来测试
        logger.info("模拟调用用户服务");
        logger.info("模拟调用订单服务");
        logger.info("模拟调用支付服务");
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "微服务调用测试完成");
        result.put("traceId", traceId);
        result.put("services", "user,order,payment");
        
        logger.info("所有服务调用完成");
        
        return ResponseEntity.ok(result);
    }
}
