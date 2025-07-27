# xiaohongshu-microservices

## JWT 认证流程说明

### 1. 认证流程概述

本项目使用 JWT (JSON Web Token) 实现用户认证，主要流程如下：

1. 用户登录：用户通过 `/auth/login` 接口提供用户名和密码
2. 服务器验证：验证用户凭据后生成 JWT 令牌
3. 令牌使用：客户端在后续请求中通过 Authorization 请求头携带令牌
4. 请求验证：服务器验证令牌的有效性并授权访问

### 2. 接口调用示例

#### 登录获取令牌
```http
POST /auth/login
Content-Type: application/json

{
    "username": "your_username",
    "password": "your_password"
}
```

### 3. JWT 生成、传递及验证流程

#### 生成 JWT
- 用户通过登录接口提交凭据（如用户名和密码）。
- 服务器验证凭据是否正确。
- 验证通过后，服务器生成 JWT，包含以下部分：
  - **Header**: 指定签名算法（如 `HS256`）。
  - **Payload**: 包含用户信息（如用户 ID）及过期时间 `exp`。
  - **Signature**: 使用服务器的密钥对 Header 和 Payload 进行签名。
- 生成的 JWT 返回给客户端。

#### 传递 JWT
- 客户端在后续请求中通过 HTTP Header (`Authorization: Bearer <token>`) 发送 JWT。
- 服务器通过解析 Header 获取 JWT。

#### 验证 JWT
- 服务器接收到请求后，从 Header 中提取 JWT。
- 验证 JWT 的签名是否有效，确保其未被篡改。
- 检查 JWT 的过期时间等声明是否有效。
- 验证通过后，提取 JWT 中的用户信息，继续处理请求；否则返回 401 未授权错误。
- 
#### 退出登录
- 通过`/auth/logout` 来使相关的token退出登录。
- 通过黑名单来实现，把退出的 token 存入黑名单。
- 后续请求中如果发现该 token 在黑名单中，则拒绝访问。

## Docker
### 1. 启动 Redis 服务
- docker start redis7
- docker-compose -f docker-redis-compose.yml up -d redis
### 2. 查看 Redis 服务状态
- docker ps
### 3. 连接 Redis 服务
- redis-cli -h 127.0.0.1 -p 6379
### 4. 停止容器
- docker stop redis7
#### （可选）删除容器
- docker rm redis7


`