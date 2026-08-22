# 阶段 0 · 项目理解（概览）

**状态：补课中**  
**被测系统**：`mall-admin-test`

> 全阶段索引见 [../README.md](../README.md) · 进度见 [../progress.md](../progress.md)

## 你要搞懂的架构

```text
浏览器 / Postman / Apifox
        ↓
   Vue :5176  或  直接打 API
        ↓
 Spring Boot :8080
        ↓
   MySQL（本机多为 :3307）
```

## 必记地址

| 服务 | 地址 |
|------|------|
| 前端 | http://127.0.0.1:5176 |
| 后端 | http://127.0.0.1:8080 |
| Swagger | http://127.0.0.1:8080/swagger-ui.html |
| 库 | mall_test @ 127.0.0.1:**3307** |

## 账号

- admin / Admin@123（ADMIN）
- user01 / User@123（USER）
- disabled / User@123（禁用，测登录失败）

## 相关文档

- 项目 README：`mall-admin-test/README.md`
- 错误码：`mall-admin-test/docs/error-codes.md`
