# Phase 1 说明：骨架 / 数据库 / 用户登录 / JWT

本阶段已完成内容：

- 项目目录骨架（backend / frontend / database / docs）
- MySQL 建表与测试数据（`schema.sql` / `data.sql`）
- `docker-compose.yml`（本机若无 Docker，可手工导入 SQL）
- Spring Boot 3.2 + 用户登录 + JWT + 用户 CRUD（含角色权限）
- 统一响应体、统一错误码、全局异常处理

## 目录

```text
mall-admin-test/
├── backend/                 # Spring Boot
├── frontend/                # 第四阶段再实现
├── database/
│   ├── schema.sql
│   └── data.sql
├── docs/
│   └── error-codes.md
└── docker-compose.yml
```

## 1. 初始化数据库

### 方式 A：Docker（推荐，需安装 Docker Desktop）

```bash
cd d:\code\test-study\mall-admin-test
docker compose up -d
```

会启动 MySQL `3306`，库名 `mall_test`，账号 `root` / `123456`，并自动执行 schema + data。

### 方式 B：本机已有 MySQL

用客户端执行（按你的实际 root 密码调整）：

```bash
mysql -uroot -p < database/schema.sql
mysql -uroot -p < database/data.sql
```

然后确认 `backend/src/main/resources/application.yml` 中的：

```yaml
spring.datasource.username
spring.datasource.password
```

与本地一致（默认 `root` / `123456`）。

## 2. 启动后端

```bash
cd d:\code\test-study\mall-admin-test\backend
mvn spring-boot:run
```

或：

```bash
mvn -DskipTests package
java -jar target/mall-admin-backend-1.0.0-SNAPSHOT.jar
```

默认端口：`http://127.0.0.1:8080`

## 3. 测试账号

| 用户名 | 密码 | 角色 | 状态 |
|--------|------|------|------|
| admin | Admin@123 | ADMIN | 启用 |
| user01 | User@123 | USER | 启用 |
| disabled | User@123 | USER | 禁用 |

密码均为 BCrypt 存储，不是明文。

## 4. 第一阶段接口

### 登录（无需 Token）

`POST /api/auth/login`

```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

成功示例（HTTP 200）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "<jwt>",
    "tokenType": "Bearer",
    "expiresInHours": 24,
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "role": "ADMIN"
  }
}
```

后续请求头：

```text
Authorization: Bearer <token>
```

### 用户接口（需要 JWT）

| Method | URL | 权限 |
|--------|-----|------|
| GET | /api/users | ADMIN |
| GET | /api/users/{id} | ADMIN 或本人 |
| POST | /api/users | ADMIN |
| PUT | /api/users/{id} | ADMIN |
| DELETE | /api/users/{id} | ADMIN |

## 5. 建议验收用例（Postman / curl）

1. 正确登录 admin → 200 / code=200，拿到 token  
2. 错误密码 → 401 / code=10002  
3. 不存在用户 → 404 / code=10001  
4. 空用户名或空密码 → 400 / code=90001  
5. disabled 登录 → 403 / code=10003  
6. 无 Token 访问 `GET /api/users` → 401 / code=10006  
7. user01 Token 访问 `GET /api/users` → 403 / code=10008  
8. admin Token 访问 `GET /api/users` → 200，返回用户列表  
9. user01 访问 `GET /api/users/2` → 200；访问 `GET /api/users/1` → 403  

### curl 示例

```bash
curl -X POST http://127.0.0.1:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"Admin@123\"}"
```

```bash
curl http://127.0.0.1:8080/api/users ^
  -H "Authorization: Bearer <token>"
```

## 6. 本阶段尚未包含

- 商品分类 / 商品 / 库存（第二阶段）
- 购物车 / 订单（第三阶段）
- Vue 前端（第四阶段）
- Swagger 完善注解、操作日志落库、完整 README（第五阶段）

---

验收通过后回复：**继续**
