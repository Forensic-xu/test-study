# 阶段 3 · Postman 接口测试（概览）

**状态：进行中（第 8 课收尾）**  
**对应总计划**：`doc/测试学习计划.md` 第七～十章

---

## 阶段目标

从「会发请求」做到「会设计接口测试」，最终产出：

> 一套针对 `mall-admin-test` 的 Postman/Apifox 接口测试集合（可写进简历）。

## 学习顺序

| # | 课时 | 笔记 | 状态 |
|---|------|------|------|
| 01 | 登录 | [lesson-01-login.md](./lesson-01-login.md) | ✅ |
| 02 | Token 与鉴权 | [lesson-02-token-and-auth.md](./lesson-02-token-and-auth.md) | ✅ |
| 03 | Query / Path / 404 | [lesson-03-query-path-404.md](./lesson-03-query-path-404.md) | ✅ |
| 04 | POST 创建分类 + 409 | [lesson-04-create-category.md](./lesson-04-create-category.md) | ✅ |
| 05 | PUT / DELETE + 20007 | [lesson-05-update-delete-category.md](./lesson-05-update-delete-category.md) | ✅ |
| 06 | 库存增减 + 超库存 | [lesson-06-inventory.md](./lesson-06-inventory.md) | ✅ |
| 07 | 购物车 + 403 隔离 | [lesson-07-cart.md](./lesson-07-cart.md) | ✅ |
| 08 | 订单状态机 | [lesson-08-orders.md](./lesson-08-orders.md) | 🔄 |
| 09 | Collection Runner 串联 | `lesson-09-runner.md` | 待建 |
| 10 | 整理导出（简历素材） | `lesson-10-export.md` | 待建 |

## 工具说明

Postman 与 Apifox 概念一致（环境变量、后置脚本、Bearer）。  
笔记里的 `pm.*` 脚本在 Apifox 兼容 Postman 语法时同样可用。

## 推荐集合结构

```text
mall-admin-test API
├── 01-Auth
├── 02-Users
├── 03-Categories
├── 04-Products
├── 05-Inventory
├── 06-Cart
└── 07-Orders
```

## 环境变量约定

| 变量 | 含义 |
|------|------|
| `base_url` | `http://127.0.0.1:8080` |
| `token` | 登录后 JWT |
| `category_id` | 创建分类后保存（推荐下划线） |
| `token_admin` / `token_user` | 多用户场景 |

## 每个请求都要看的四件事

1. HTTP Status  
2. 业务 `code`  
3. `message`  
4. `data`

## 产出物

- 完整 Postman/Apifox 集合（正常 + 异常 + 边界 + 权限）
- 可导出 JSON 备份，后续导入 MeterSphere

## 与后续阶段关系

→ 阶段 5 Pytest：将本阶段用例逐条改写成自动化脚本
