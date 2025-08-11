# 日志配置说明

## 概述

本项目使用 Logback 作为日志框架，配置了结构化 JSON 格式日志输出，支持请求链路追踪。

## 配置特性

### 1. 结构化 JSON 日志

- 使用`logstash-logback-encoder`输出 JSON 格式日志
- 包含时间戳、日志级别、线程名、logger 名等标准字段
- 支持自定义业务字段（traceId、userId、requestId）

### 2. 请求链路追踪

- 通过 MDC（Mapped Diagnostic Context）实现
- 每个请求自动生成唯一的 traceId
- 支持从请求头传递 traceId，便于微服务间调用追踪

### 3. 多输出目标

- 控制台输出：开发环境查看
- 文件输出：生产环境持久化
- 错误日志单独输出：便于问题排查

### 4. 异步日志

- 使用 AsyncAppender 避免日志输出阻塞业务线程
- 配置队列大小和丢弃策略

## 日志格式示例

```json
{
  "@timestamp": "2024-01-15T10:30:45.123Z",
  "level": "INFO",
  "thread": "http-nio-8080-exec-1",
  "logger": "com.example.xiaohongshu_microservices.controller.userController",
  "message": "用户创建成功 - 用户ID: 123",
  "traceId": "abc123def456",
  "userId": "123",
  "requestId": "abc123def456",
  "app": "xiaohongshu-microservices",
  "version": "1.0.0",
  "host": "server-01"
}
```

## 使用方法

### 1. 在 Controller 中使用

```java
@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        // 设置用户ID到MDC
        MDCTraceUtils.setUserId(userId.toString());

        logger.info("查询用户信息 - 用户ID: {}", userId);

        // 业务逻辑...

        logger.info("用户信息查询成功 - 用户ID: {}, 用户名: {}", userId, user.getUsername());
        return ResponseEntity.ok(user);
    }
}
```

### 2. 在 Service 中使用

```java
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User findById(Long userId) {
        logger.debug("开始查询用户 - 用户ID: {}", userId);

        // 业务逻辑...

        logger.debug("用户查询完成 - 用户ID: {}", userId);
        return user;
    }
}
```

### 3. 异常处理

```java
try {
    // 业务逻辑
} catch (Exception e) {
    logger.error("操作失败 - 用户ID: {}, 错误: {}", userId, e.getMessage(), e);
    throw e;
}
```

## 配置说明

### 1. logback-spring.xml

- **控制台输出**：开发环境使用，便于调试
- **文件输出**：生产环境使用，按日期滚动
- **错误日志**：单独输出 ERROR 级别日志
- **异步输出**：避免阻塞业务线程

### 2. 日志级别

- **开发环境**：DEBUG 级别，包含 SQL 日志
- **生产环境**：INFO 级别，降低第三方库日志级别

### 3. 文件管理

- 日志文件位置：`logs/`
- 滚动策略：按日期滚动，保留 30 天
- 大小限制：总大小不超过 3GB

## 微服务间调用

### 1. 传递 traceId

```java
// 在HTTP客户端中设置请求头
HttpHeaders headers = new HttpHeaders();
headers.set("X-Trace-Id", MDCTraceUtils.getTraceId());
```

### 2. 接收 traceId

拦截器会自动从请求头`X-Trace-Id`中获取 traceId，如果没有则生成新的。

## 监控和告警

### 1. 错误日志监控

错误日志单独输出到`logs/error.json`，便于设置告警规则。

### 2. 性能监控

通过 traceId 可以追踪请求的完整调用链路，便于性能分析。

### 3. 业务监控

通过 userId 等业务字段，可以分析用户行为和使用情况。

## 最佳实践

1. **合理使用日志级别**

   - ERROR：系统错误，需要立即处理
   - WARN：警告信息，需要关注
   - INFO：重要业务信息
   - DEBUG：调试信息，开发环境使用

2. **避免敏感信息**

   - 不要在日志中输出密码、token 等敏感信息
   - 使用脱敏处理

3. **性能考虑**

   - 使用占位符而不是字符串拼接
   - 避免在循环中输出大量日志

4. **结构化日志**
   - 使用 JSON 格式便于解析
   - 包含必要的上下文信息

## 故障排查

### 1. 日志文件位置

- 应用日志：`logs/application.json`
- 错误日志：`logs/error.json`

### 2. 常见问题

- 日志文件过大：检查滚动配置和保留策略
- 日志丢失：检查异步配置和队列大小
- 性能问题：检查日志级别和输出频率
