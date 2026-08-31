# 第 10 课 · 整理集合 + 导出备份（阶段 3 收尾）

**状态：✅ 已完成**  
**工具**：Postman  
**目标**：把 Postman Collection 整理成**可备份、可写简历**的正式产出

---

## 学习目标

1. 检查集合**命名、文件夹结构、环境变量**是否统一
2. 从 Postman **Export** 集合与环境 JSON，放进项目仓库
3. 能用自己的话描述这套接口测试集合（面试/简历一句话）

---

## 你现在的集合（对照检查）

```text
mall-admin-test API
├── 01-Auth
├── 02-Users
├── 03-Categories
├── 04-Products
├── 05-Inventory
├── 06-Cart
├── 07-Orders          ← 单接口 + 异常场景
└── 08-Smoke-Flow      ← Runner 正向冒烟链
```

### 命名规范（快速扫一眼）

| 规则 | 示例 |
|------|------|
| 文件夹用 `序号-模块` | `06-Cart` |
| 请求写清 **方法 + 动作** | `POST 创建订单`、`PUT 重复取消订单(409)` |
| 异常用例标 HTTP/code | `(409)`、`(403)` |
| Runner 单独文件夹 | 不和 `07-Orders` 混 |

有不清楚的旧名字，顺手改一下（改完 Ctrl+S 保存）。

---

## Step 1：环境变量清单（写下来）

打开环境 **`mall-admin-test-local`**，确认有这些变量：

| 变量 | 初始值（可空） | 谁写入 |
|------|----------------|--------|
| `base_url` | `http://127.0.0.1:8080` | 你手动配 |
| `token` | 空 | 登录 admin 后置脚本 |
| `user_token` | 空 | 登录 user01 后置脚本 |
| `category_id` | 空 | 创建分类 Tests |
| `product_id` | 空 | 创建商品 Tests（如有） |
| `cart_item_id` | 空 | 加购物车 Tests（如有） |
| `order_id` | 空 | 创建订单 Tests |
| `order_no` | 空 | 创建订单 Tests（如有） |

> Token 类变量**导出时保持空值**，不要把真实 JWT 提交进 Git。

---

## Step 2：Postman 导出 Collection

1. 左侧选中集合 **`mall-admin-test API`**
2. 右侧 **`···`** → **Export**
3. 格式选 **Collection v2.1**
4. 保存到：

```text
d:\code\test-study\mall-admin-test\postman\mall-admin-test-api.postman_collection.json
```

---

## Step 3：Postman 导出 Environment

1. 左侧点 **Environments**（环境图标）
2. 找到 **`mall-admin-test-local`** → **`···`** → **Export**
3. 保存到：

```text
d:\code\test-study\mall-admin-test\postman\mall-admin-test-local.postman_environment.json
```

导出前检查：在 Postman 环境里把 **`token`、`user_token` 的 Current Value 清空**（Initial Value 可保留空字符串）。

---

## Step 4：在仓库里验收

导出完成后，本地应有：

```text
mall-admin-test/postman/
├── README.md
├── mall-admin-test-api.postman_collection.json      ← 你导出的
└── mall-admin-test-local.postman_environment.json   ← 你导出的
```

**自检：**

- [ ] 两个 JSON 文件都存在
- [ ] 用记事本打开，能看到 `08-Smoke-Flow`、`07-Orders` 等文件夹名
- [ ] 环境里 **没有** 长长的 JWT 字符串

---

## Step 5：可选 — 再跑一遍 Runner

导出前最后确认：选中 **`08-Smoke-Flow`** → **Run**（Collection Runner）→ **7/7 绿**。

---

## 简历 / 面试怎么说

### 项目描述（可直接改）

> 基于 **Postman** 对电商后台 `mall-admin-test` 搭建接口测试集合，覆盖认证、商品、库存、购物车、订单状态机等模块；包含 **正常流、409 冲突、403 越权** 等场景；使用 Tests 后置脚本做 **Token 与 order_id 关联**；通过 **Collection Runner** 实现登录→下单→履约端到端冒烟。

### 面试官可能追问

| 问题 | 你怎么答 |
|------|----------|
| 断言什么？ | HTTP Status + 业务 `code` + 关键字段（如 `status`） |
| 怎么做多用户？ | admin / user 分变量；履约接口请求级 Bearer `{{token}}` |
| Runner 失败怎么查？ | 看第几步红、状态码、是否 Token 用错角色 |
| 和自动化关系？ | Postman 探索设计用例，Pytest 固化回归（`api-test`） |

---

## 阶段 3 能力清单（全部应会）

- [x] GET / POST / PUT / DELETE
- [x] 环境变量 + Bearer Token
- [x] Tests 后置脚本 `pm.environment.set`
- [x] 404 / 409 / 403 异常场景
- [x] 订单状态机 + 幂等
- [x] Collection Runner 串联
- [ ] 集合 + 环境导出进仓库

---

## 检查清单

- [x] 集合文件夹 01～08 命名整齐
- [x] 环境变量表与文档一致
- [x] 阶段 3 全部课时完成
- [x] `08-Smoke-Flow` 批量运行 7/7 通过
- [x] 能口述本课「简历一段话」

全部勾完后回复 **「10 绿了」** 或 **「Postman 阶段完成」**。

**下一阶段建议**：**Jenkins**（把 `python run.py report` 接 CI）或 **MeterSphere**（导入本 JSON）。
