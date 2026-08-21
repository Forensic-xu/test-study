# Phase 4：Vue 3 管理后台

## 访问地址

- 前端：http://127.0.0.1:5176
- 后端：http://127.0.0.1:8080
- Swagger：http://127.0.0.1:8080/swagger-ui.html

## 启动

```bash
# 后端
cd backend
mvn spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```

## 默认账号

- admin / Admin@123（ADMIN）
- user01 / User@123（USER）

## 路由

| 路径 | 角色 |
|------|------|
| /login | 公开 |
| /dashboard | ADMIN / USER |
| /users | ADMIN |
| /categories | ADMIN |
| /products | ADMIN |
| /inventory | ADMIN |
| /orders | ADMIN / USER |
| /orders/:id | ADMIN / USER |
| /logs | ADMIN |

## Axios / JWT

- `src/utils/request.ts`：`baseURL=/api`，开发态经 Vite 代理到 `8080`
- Request 拦截器自动加 `Authorization: Bearer <token>`
- 401：清 Token 并跳转 `/login`

## 后端最小改动（本阶段）

新增只读接口：`GET /api/operation-logs`（ADMIN），供日志页查询。不改变既有购物车/订单/库存行为。

详见 [selenium-guide.md](selenium-guide.md)。

---

验收通过后回复：**继续**（进入第五阶段）
