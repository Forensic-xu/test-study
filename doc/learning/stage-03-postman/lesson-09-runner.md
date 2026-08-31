# 第 9 课 · Collection Runner 串联业务流

**状态：✅ 已完成**  
**Collection：** 新建 `08-Smoke-Flow`  
**工具：** Apifox「批量运行」≈ Postman「Collection Runner」

---

## 学习目标

1. 理解 **Runner**：按顺序自动执行多个请求，环境变量在步骤间传递
2. 新建一条 **冒烟业务流**：登录 → 下单 → 支付 → 发货 → 完成
3. 处理 **多用户 Token**（user 下单、admin 履约）——在**单个请求**上覆盖 Authorization
4. 一键跑通，看到 **全部 Tests 通过**

---

## 核心概念

### Runner 是什么？

前面 8 课你是**一个一个点发送**。Runner 的作用是：

```text
请求 1 → 后置脚本存变量 → 请求 2 用变量 → … → 最后看通过/失败统计
```

这和 Pytest 的 `test_chaining.py`、JMeter 的 setUp + 业务线程组是同一思路。

### 为什么不能直接跑整个 `07-Orders`？

`07-Orders` 里既有「取消订单」又有「支付订单」，用的是**同一个** `{{order_id}}`，顺序跑会互相打架（已取消的订单不能支付）。

所以要单独建 **`08-Smoke-Flow`**：只放**一条能从头跑到尾**的正向流程。

### Apifox 怎么开 Runner？

1. 左侧选中文件夹 **`08-Smoke-Flow`**
2. 点右上角 **「批量运行」**（或「运行」）
3. 环境选 **`mall-admin-test-local`**
4. 确认请求顺序 → **开始运行**
5. 结束后看：**测试结果** 全部绿色

> Postman：选中 Collection/文件夹 → **Run** → Collection Runner，操作类似。

---

## 课前准备

- [ ] 后端已启动：`http://127.0.0.1:8080`
- [ ] 环境变量已有：`base_url` = `http://127.0.0.1:8080`
- [ ] 商品 id=1 存在且库存充足（默认种子数据一般有）

---

## Step 1：新建文件夹 `08-Smoke-Flow`

在集合 `mall-admin-test API` 下，新建文件夹 **`08-Smoke-Flow`**。

**文件夹 Authorization：** 选 **No Auth**（每个请求自己带 Token，避免 admin/user 混用）。

---

## Step 2：按顺序添加 7 个请求

> ⚠️ **顺序很重要！** 在文件夹里用拖拽调整，从上到下必须是 1→7。

---

### 请求 1：`POST 登录 admin`

| 项 | 值 |
|----|-----|
| 方法 | `POST` |
| URL | `{{base_url}}/api/auth/login` |
| Authorization | No Auth |
| Body | 见下 |

```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

**Tests：**

```javascript
const json = pm.response.json();
pm.test("admin 登录成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.token).to.be.a("string");
});
pm.environment.set("token", json.data.token);
```

---

### 请求 2：`POST 登录 user01`

| 项 | 值 |
|----|-----|
| 方法 | `POST` |
| URL | `{{base_url}}/api/auth/login` |
| Authorization | No Auth |

```json
{
  "username": "user01",
  "password": "User@123"
}
```

**Tests：**

```javascript
const json = pm.response.json();
pm.test("user01 登录成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
});
pm.environment.set("user_token", json.data.token);
```

---

### 请求 3：`POST 创建订单（直接购买）`

| 项 | 值 |
|----|-----|
| 方法 | `POST` |
| URL | `{{base_url}}/api/orders` |
| Authorization | Bearer Token → `{{user_token}}` |

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 1
    }
  ],
  "remark": "Runner 冒烟"
}
```

**Tests：**

```javascript
const json = pm.response.json();
pm.test("创建订单成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.status).to.eql("PENDING");
  pm.expect(json.data.id).to.be.a("number");
});
pm.environment.set("order_id", json.data.id);
console.log("📦 smoke order_id =", json.data.id);
```

> 用「直接购买」而不是购物车，Runner 每次都能独立跑，不依赖购物车里有东西。

---

### 请求 4：`PUT 支付订单`

| 项 | 值 |
|----|-----|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/pay` |
| Authorization | Bearer Token → `{{token}}`（**admin**，不是 user_token） |
| Body | 无 |

**Tests：**

```javascript
const json = pm.response.json();
pm.test("支付成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.status).to.eql("PAID");
});
```

> 💡 **关键**：支付必须 admin。在**本请求**的 Authorization 里写 `{{token}}`，不要继承文件夹或 user 的 token。

---

### 请求 5：`PUT 发货`

| 项 | 值 |
|----|-----|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/ship` |
| Authorization | Bearer `{{token}}` |

**Tests：**

```javascript
const json = pm.response.json();
pm.test("发货成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.status).to.eql("SHIPPED");
});
```

---

### 请求 6：`PUT 完成订单`

| 项 | 值 |
|----|-----|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/complete` |
| Authorization | Bearer `{{token}}` |

**Tests：**

```javascript
const json = pm.response.json();
pm.test("完成订单成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.status).to.eql("COMPLETED");
});
```

---

### 请求 7（可选加强）：`GET 确认订单终态`

| 项 | 值 |
|----|-----|
| 方法 | `GET` |
| URL | `{{base_url}}/api/orders/{{order_id}}` |
| Authorization | Bearer `{{user_token}}` |

**Tests：**

```javascript
const json = pm.response.json();
pm.test("订单终态为 COMPLETED", function () {
  pm.response.to.have.status(200);
  pm.expect(json.data.status).to.eql("COMPLETED");
  pm.expect(json.data.id).to.eql(pm.environment.get("order_id") * 1);
});
```

---

## Step 3：批量运行

1. 选中 **`08-Smoke-Flow`**
2. 点 **批量运行**
3. 环境：**mall-admin-test-local**
4. 延迟：默认 0 即可（本地够用）
5. **开始运行**

### 预期结果

| 指标 | 期望 |
|------|------|
| 请求数 | 6～7 个全部 200 |
| Tests | **全部通过**（约 6～7 条） |
| 最后订单状态 | `COMPLETED` |

### 流程图

```text
登录 admin ──→ token
登录 user01 ──→ user_token
POST 下单 ──→ order_id (PENDING)
PUT 支付 (admin) ──→ PAID
PUT 发货 (admin) ──→ SHIPPED
PUT 完成 (admin) ──→ COMPLETED
GET 确认 (可选)
```

---

## 常见问题

### 1. 第 4 步支付报 403

- 检查请求 4 的 Token 是不是 `{{token}}`（admin），误用了 `{{user_token}}`

### 2. 第 4 步支付报 409 / 40006

- 订单不是 PENDING（可能上一步没创建成功，或 `order_id` 是旧值）
- 重新从请求 1 整链跑一遍

### 3. 第 3 步下单报库存不足

- 商品 1 库存被测空了 → 换 `productId: 2` 或先给商品 1 加库存（`05-Inventory`）

### 4. 变量没传下去

- 检查每一步 Tests 里有没有 `pm.environment.set(...)`
- Runner 必须选对环境 `mall-admin-test-local`

### 5. Apifox 和 Postman 脚本语法

- 本课脚本用 `pm.*`，Apifox 兼容 Postman 语法时可直接粘贴

---

## 进阶（可选，本课不要求）

| 方向 | 说明 |
|------|------|
| 再建 `08-Negative-Flow` | 专门放 409/403 异常用例，和正向流分开 |
| CLI | Postman 用 **Newman**；Apifox 有命令行导出，可接 Jenkins |
| 迭代次数 | Runner 设 Iterations=3，观察库存/订单号是否每次都正确 |

---

## 本课要记住的

1. **Runner = 手工点的自动化版**，变量串联靠后置脚本
2. **正向流和异常流分开建文件夹**，不要混在一个 Runner 里
3. **多角色接口**：在单个请求上指定 Token，不要只靠文件夹级 Auth
4. 对应 Pytest：`python run.py smoke` 跑 3 条；本课 Apifox 跑 6～7 条接口链

---

## 检查清单

- [x] 新建 `08-Smoke-Flow` 文件夹
- [x] 7 个请求顺序正确
- [x] admin / user Token 在对应请求上配置正确
- [x] 批量运行 **全部 Tests 通过**
- [x] 理解为什么不能直接跑整个 `07-Orders`

全部勾完后回复 **「09 绿了」**，进入第 10 课（整理命名 + 导出备份）。
