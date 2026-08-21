# Phase 5 说明

本阶段完成：

- Swagger 注解完善（Tag / Operation / ApiResponses / Security）
- 统一异常增强（校验 / 业务 / DB / 404 / 系统）+ TraceId
- 操作日志 AOP（`@OpLog`）+ 敏感字段脱敏
- `operation_logs` 增加 `request_params` / `http_status`（migration_phase5）
- Docker Compose（MySQL；可选 profile `app` 启动 backend）
- 配置环境变量化
- 测试准备文档：Postman / Pytest / Selenium / JMeter
- README 最终整理

**未实现（刻意留给你练习）：** Pytest 代码、Selenium 代码、JMeter 脚本、Jenkins Pipeline。
