# Phase 2 说明：商品分类 / 商品 / 库存

## 本阶段新增 API

### 分类 `/api/categories`

| Method | URL | 权限 |
|--------|-----|------|
| GET | /api/categories | 登录 |
| GET | /api/categories/{id} | 登录 |
| POST | /api/categories | ADMIN |
| PUT | /api/categories/{id} | ADMIN |
| DELETE | /api/categories/{id} | ADMIN |

### 商品 `/api/products`

| Method | URL | 权限 |
|--------|-----|------|
| GET | /api/products?page&size&name&categoryId&status | 登录 |
| GET | /api/products/{id} | 登录 |
| POST | /api/products | ADMIN |
| PUT | /api/products/{id} | ADMIN |
| DELETE | /api/products/{id} | ADMIN |

商品状态 API 值：`ON_SALE` / `OFF_SALE`（库内仍为 1/0）。

### 库存 `/api/inventory`

| Method | URL | 权限 |
|--------|-----|------|
| GET | /api/inventory/{productId} | 登录 |
| PUT | /api/inventory/{productId}/increase | ADMIN |
| PUT | /api/inventory/{productId}/decrease | ADMIN |
| GET | /api/inventory/{productId}/records | 登录 |

## 并发安全说明

库存增减使用条件更新：

```sql
UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?
```

只有满足库存条件的更新才会成功，避免并发下出现负库存。无需分布式锁，适合本练习项目复杂度。

## 数据库变更

第一阶段已建 `categories` / `products` / `inventory_records`。

Phase 2 增量：

1. `products.name` 增加唯一索引 `uk_products_name`
2. 流水类型统一为：`INCREASE` / `DECREASE` / `ORDER_DEDUCT` / `ORDER_CANCEL_RESTORE`
3. 商品 #30 库存调整为 `9999`（边界数据）

已有库执行：

```bash
mysql -uroot -p123456 < database/migration_phase2.sql
```

## Swagger

启动后访问：

- http://127.0.0.1:8080/swagger-ui.html
- http://127.0.0.1:8080/v3/api-docs

右上角 Authorize 填入：`Bearer <token>`（或仅 token，视 UI 提示）。

## 关键测试数据

| 商品 | 用途 |
|------|------|
| id=1 库存10 | 边界购买 |
| id=2 库存1 | 超库存 |
| id=3 / 28 OFF_SALE | 下架 |
| id=4 库存0 | 零库存 |
| id=26 库存5 | 边界 |
| id=27 库存10 | 边界 |
| id=30 库存9999 | 大库存 |

---

验收通过后回复：**继续**
