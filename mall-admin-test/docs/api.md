# API 概览

Base URL：`http://127.0.0.1:8080`

统一响应：

```json
{ "code": 200, "message": "success", "data": {} }
```

认证：除 `POST /api/auth/login` 外，均需：

```text
Authorization: Bearer <token>
```

完整交互说明见 Swagger：http://127.0.0.1:8080/swagger-ui.html

## 模块一览

| 模块 | 前缀 | 说明 |
|------|------|------|
| 认证 | `/api/auth` | 登录 |
| 用户 | `/api/users` | 用户 CRUD（写=ADMIN） |
| 分类 | `/api/categories` | 分类 CRUD |
| 商品 | `/api/products` | 商品分页 CRUD |
| 库存 | `/api/inventory` | 增减库存 + 流水 |
| 购物车 | `/api/cart` | 当前用户购物车 |
| 订单 | `/api/orders` | 下单/状态机 |
| 日志 | `/api/operation-logs` | 操作审计（ADMIN） |

错误码见 [error-codes.md](error-codes.md)。
