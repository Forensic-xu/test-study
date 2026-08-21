# Postman 测试指南

## 准备

1. 启动后端 `8080`
2. 新建 Environment：
   - `baseUrl` = `http://127.0.0.1:8080`
   - `token` = （登录后写入）

## 建议顺序

```text
登录 → 商品查询 → 库存 → 购物车 → 创建订单 → 取消/支付/发货/完成
```

## 1. 登录拿 Token

`POST {{baseUrl}}/api/auth/login`

```json
{ "username": "admin", "password": "Admin@123" }
```

Tests 脚本示例：

```javascript
const json = pm.response.json();
pm.environment.set("token", json.data.token);
```

## 2. 设置 Authorization

Collection Auth：Bearer Token → `{{token}}`

## 3. 常用请求

| 目的 | Method | URL |
|------|--------|-----|
| 商品列表 | GET | `/api/products?page=1&size=10` |
| 加库存 | PUT | `/api/inventory/{id}/increase` body `{"quantity":5}` |
| 加购物车 | POST | `/api/cart` body `{"productId":27,"quantity":1}` |
| 直接下单 | POST | `/api/orders` body `{"items":[{"productId":27,"quantity":1}]}` |
| 取消 | PUT | `/api/orders/{id}/cancel` |
| 支付 | PUT | `/api/orders/{id}/pay` |
| 发货 | PUT | `/api/orders/{id}/ship` |
| 完成 | PUT | `/api/orders/{id}/complete` |

## 断言建议

同时断言：

1. HTTP Status  
2. `json.code`  
3. `json.message`  
4. 关键字段（如库存、订单状态）

错误码见 [error-codes.md](error-codes.md)。
