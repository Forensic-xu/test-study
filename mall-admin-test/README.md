# mall-admin-test

电商后台管理系统练习项目（被测系统 / SUT），用于学习接口测试、UI 自动化、性能测试与 Jenkins CI/CD。

> 当前进度：**第五阶段已完成**（Swagger / 异常 / 日志 / Docker / 文档）。  
> 本仓库**不包含** Pytest / Selenium / JMeter / Jenkins 自动化脚本实现，留给你后续练习。

## 1. 项目介绍

前后端分离的电商后台练习系统，覆盖登录鉴权、用户、分类、商品、库存、购物车、订单状态机与操作日志，接口 HTTP Status + 业务错误码统一，便于 Postman / Pytest / Selenium / JMeter 练习。

## 2. 技术栈

- 后端：Java 17、Spring Boot 3.2、MyBatis-Plus、MySQL 8、JWT、Springdoc OpenAPI
- 前端：Vue 3、Vite、TypeScript、Element Plus、Pinia、Axios
- 数据库：MySQL 8

## 3. 访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://127.0.0.1:5176 |
| 后端 | http://127.0.0.1:8080 |
| Swagger | http://127.0.0.1:8080/swagger-ui.html |
| OpenAPI JSON | http://127.0.0.1:8080/v3/api-docs |

## 4. 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | Admin@123 | ADMIN |
| user01 | User@123 | USER |
| user02 | User@123 | USER |
| disabled | User@123 | 禁用（测登录） |

密码均为 BCrypt，非明文。

## 5. 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 18+ / npm
- MySQL 8（或 Docker Desktop）

## 6. 数据库启动

### 方式 A：Docker（全新环境）

```bash
cd d:\code\test-study\mall-admin-test
docker compose up -d
```

自动执行 `database/schema.sql` + `database/data.sql`。  
默认：`root` / `123456`，库名 `mall_test`，端口 `3306`。

可选同时启动后端容器：

```bash
docker compose --profile app up -d --build
```

### 方式 B：本机已有 MySQL

```bash
mysql -uroot -p123456 < database/schema.sql
mysql -uroot -p123456 < database/data.sql
```

若库已存在且从旧版本升级，按需执行：

```bash
mysql -uroot -p123456 < database/migration_phase2.sql
mysql -uroot -p123456 < database/migration_phase5.sql
```

## 7. 后端启动

```bash
cd backend
mvn spring-boot:run
```

可通过环境变量覆盖：

```text
DB_HOST / DB_PORT / DB_NAME / DB_USERNAME / DB_PASSWORD
MALL_JWT_SECRET / MALL_JWT_EXPIRE_HOURS
```

## 8. 前端启动

```bash
cd frontend
npm install
npm run dev
```

或双击 / 运行一键脚本：

```bash
frontend\start.bat
```

开发代理：`/api` → `http://127.0.0.1:8080`，端口固定 **5176**。

## 9. 项目目录

```text
mall-admin-test/
├── backend/
├── frontend/
├── database/
│   ├── schema.sql
│   ├── data.sql
│   ├── migration_phase2.sql
│   └── migration_phase5.sql
├── docs/
├── docker-compose.yml
└── README.md
```

## 10. 文档索引

- [API 概览](docs/api.md)
- [错误码](docs/error-codes.md)
- [数据库](docs/database.md)
- [测试场景](docs/test-cases.md)
- [Postman 指南](docs/postman-guide.md)
- [Pytest 指南](docs/pytest-guide.md)
- [Selenium 指南](docs/selenium-guide.md)
- [JMeter 指南](docs/jmeter-guide.md)
- [phase1~phase4](docs/)

## 11. 测试建议（后续你来做）

1. Postman 手工接口探索  
2. Python Requests → Pytest 接口自动化  
3. Selenium + `data-testid` UI 自动化  
4. JMeter 登录/商品列表/下单压测  
5. Jenkins 串联以上流水线  

## 12. 常见问题

- **Can't connect / 10061**：本机 MySQL 8.4 实际端口多为 **3307**（见服务 `MySQL84` 的 `my.ini`），Navicat 与 `DB_PORT` 需一致，不要写死 3306。  
- **密码连不上库**：本地默认 `123456`，可用 `DB_PASSWORD` 覆盖。  
- **401**：检查 `Authorization: Bearer <token>`。  
- **Docker 未安装**：用本机 MySQL 手工导入 SQL 即可。
