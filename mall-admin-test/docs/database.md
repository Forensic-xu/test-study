# 数据库说明

## 表

`users` · `categories` · `products` · `inventory_records` · `cart` · `orders` · `order_items` · `operation_logs`

## 初始化

- 全新环境：`docker compose up -d` 或执行 `schema.sql` + `data.sql`
- 已有库升级：
  - `migration_phase2.sql`（商品名唯一索引、流水类型归一）
  - `migration_phase5.sql`（操作日志 `request_params` / `http_status`）

## 学习环境账号（库）

- MySQL：`root` / `123456`，库 `mall_test`
- 业务账号见 README

## 注意

- 商品 `status`：库内 `1/0`，API 为 `ON_SALE/OFF_SALE`
- 订单明细保存价格/名称快照
- 操作日志中密码与 Token 已脱敏为 `******`
