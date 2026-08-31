# 第 8 课 · 下单 / 订单状态机 / 重复取消

**状态：✅ 已完成**  
**Collection：** `07-Orders`

---

## 学习目标

1. 理解订单状态机（PENDING → PAID → SHIPPED → COMPLETED / CANCELLED）
2. 学会从购物车下单（POST `/api/orders`）
3. 学会查看订单列表和详情
4. 学会取消订单（PENDING → CANCELLED）
5. 测试重复取消 → 409
6. 测试非法状态操作 → 409

---

## 课前准备

### 1. 新建文件夹 `07-Orders`

在 Postman 集合 `mall-admin-test API` 下，新建文件夹 **`07-Orders`**

### 2. 设置 `07-Orders` 文件夹的 Authorization

订单创建和查看用**普通用户**身份（user01），但支付/发货/完成需要 **ADMIN**。

本课的练习安排：

- 练习 1~3：用 user01（`{{user_token}}`）
- 练习 4~6：需要在 user 和 admin 之间切换

**先设为 user01：**

1. 点 `07-Orders` 文件夹
2. 授权标签 → 类型：**Bearer Token** → Token：`{{user_token}}`
3. 保存（Ctrl+S）

### 3. 确认购物车有商品

下单需要购物车里有商品。如果 user01 的购物车是空的，先回 `06-Cart` 发一次 `POST 加入购物车` 请求。

---

## 订单状态机（先理解这个！）

```
                    ┌──────────┐
                    │  PENDING  │  ← 下单后的初始状态
                    └────┬─────┘
                         │
              ┌──────────┼──────────┐
              ▼          ▼          ▼
       ┌──────────┐  ┌──────────┐  ┌──────────┐
       │   PAID   │  │ CANCELLED│  │  409 错误 │ ← 重复取消/非法操作
       └────┬─────┘  └──────────┘  └──────────┘
            │
            ▼
       ┌──────────┐
       │ SHIPPED  │
       └────┬─────┘
            │
            ▼
       ┌──────────┐
       │COMPLETED │
       └──────────┘
```

| 状态          | 含义   | 谁能操作       |
| ------------- | ------ | -------------- |
| **PENDING**   | 待支付 | 用户自己取消   |
| **PAID**      | 已支付 | ADMIN 发货     |
| **SHIPPED**   | 已发货 | ADMIN 完成     |
| **COMPLETED** | 已完成 | 终态，不能再变 |
| **CANCELLED** | 已取消 | 终态，不能再变 |

### 关键规则

1. **只能往前流转**，不能回退（PAID 不能变回 PENDING）
2. **PENDING → CANCELLED** 只有用户自己能操作
3. **PENDING → PAID → SHIPPED → COMPLETED** 只有 ADMIN 能操作
4. **重复取消**返回 409（幂等保护）
5. **非法状态操作**返回 409（如对已取消订单发货）

---

## 练习 1：POST 创建订单

在 `07-Orders` 文件夹里**新建请求**，命名为 `POST 创建订单`。

### 详细设置

| 位置                   | 操作                      |
| ---------------------- | ------------------------- |
| 方法                   | `POST`                    |
| URL                    | `{{base_url}}/api/orders` |
| Body → 原始数据 → JSON | 粘贴下方内容              |

**方式 A：从购物车结算（推荐）**

```json
{
  "cartItemIds": [1],
  "remark": "练习下单"
}
```

> 💡 `cartItemIds` 是购物车项 id。如果你的购物车项 id 不是 1，改成实际值。

**方式 B：直接购买（不依赖购物车）**

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "remark": "直接购买"
}
```

### Tests 脚本

```javascript
const json = pm.response.json();
pm.test("创建订单成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.id).to.be.a("number");
  pm.expect(json.data.orderNo).to.be.a("string");
  pm.expect(json.data.status).to.eql("PENDING");
  pm.expect(json.data.items).to.be.an("array");
  pm.expect(json.data.items.length).to.be.at.least(1);
});
// 存订单 id，后续练习要用
pm.environment.set("order_id", json.data.id);
pm.environment.set("order_no", json.data.orderNo);
console.log("📦 订单ID:", json.data.id, "订单号:", json.data.orderNo);
console.log("💰 订单金额:", json.data.totalAmount);
console.log("📋 订单状态:", json.data.status);
```

### 发送前检查

- [x] 方法是 `POST`
- [x] URL 是 `{{base_url}}/api/orders`（根路径，没有 `/id`）
- [x] Body 里 `cartItemIds` 或 `items` 至少有一个
- [x] `07-Orders` Authorization = Bearer `{{user_token}}`

### 预期结果

```json
{
  "code": 200,
  "data": {
    "id": 7,
    "orderNo": "ORD202608221500001",
    "userId": 2,
    "totalAmount": 3998.0,
    "status": "PENDING",
    "remark": "练习下单",
    "items": [
      {
        "productId": 1,
        "productName": "测试手机A",
        "productPrice": 1999.0,
        "quantity": 2,
        "subtotal": 3998.0
      }
    ]
  }
}
```

### 教学：下单背后发生了什么

后端代码（[OrderServiceImpl.java:55-129](file:///d:/code/test-study/mall-admin-test/backend/src/main/java/com/mall/admin/service/impl/OrderServiceImpl.java#L55-L129)）：

1. **扣库存**：检查每个商品库存是否足够，扣减并写流水
2. **冻结价格**：下单时的商品价格快照到订单项里（商品降价不影响已下单）
3. **清空购物车**：如果用 `cartItemIds` 方式，下单后删除对应购物车项
4. **生成订单号**：自动生成唯一订单号
5. **事务保护**：整个操作在 `@Transactional` 里，任何一步失败全部回滚

### 教学：为什么忽略客户端金额？

注意请求里**没有传 `totalAmount`**——因为服务器不信任客户端计算的金额。用户可以改前端绕过价格校验。正确做法是**服务器从数据库读取商品现价重新计算**。

---

## 练习 2：GET 查看订单 ✅ 已完成

在 `07-Orders` 文件夹里新建请求，命名为 `GET 查看订单`。

| 位置 | 操作 |
|------|------|
| 方法 | `GET` |
| URL | `{{base_url}}/api/orders/{{order_id}}` |
| Body | 不需要 |

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("查看订单成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.id).to.eql(pm.environment.get("order_id") * 1);
  pm.expect(json.data.status).to.eql("PENDING");
  pm.expect(json.data.items).to.be.an("array");
});
```

**预期：** 返回订单详情，包含 `items` 数组。

---

## 练习 3：PUT 取消订单 ✅ 已完成

在 `07-Orders` 文件夹里新建请求，命名为 `PUT 取消订单`。

| 位置 | 操作 |
|------|------|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/cancel` |
| Body | 不需要 |

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("取消订单成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.status).to.eql("CANCELLED");
});
```

**预期：** 状态变为 CANCELLED，库存恢复。

**教学：** 取消用 PUT 而不是 DELETE——取消是状态流转（PENDING → CANCELLED），不是物理删除。

---

## 练习 4：PUT 重复取消订单 → 409 ✅ 已完成

在 `07-Orders` 文件夹里新建请求，命名为 `PUT 重复取消订单(409)`。

| 位置 | 操作 |
|------|------|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/cancel` |
| Body | 不需要 |

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("重复取消应被拒 (409)", function () {
  pm.response.to.have.status(409);
  pm.expect(json.code).to.eql(40005);
  pm.expect(json.message).to.include("不允许取消");
});
```

**预期：** 409 / 40005。

**教学：幂等保护**——后端用条件更新 `WHERE status='PENDING'` 保证并发安全。

---

## 练习 5：PUT 支付订单（切 ADMIN） ✅ 已完成

**前置：** 先创建一个新订单（PENDING 状态），因为已取消的订单不能支付。

**Step 1：切换 Authorization**
- 点 `07-Orders` 文件夹 → 授权标签
- Token 改为 `{{token}}`（admin）
- 保存

**Step 2：新建请求**

| 位置 | 操作 |
|------|------|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/pay` |
| Body | 不需要 |

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("支付订单成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.status).to.eql("PAID");
});
```

**预期：** 状态变为 PAID。

**教学：** 支付/发货/完成只有 ADMIN 能操作，模拟后台管理系统。

---

## 练习 6：PUT 发货（ADMIN）

在 `07-Orders` 文件夹里新建请求，命名为 `PUT 发货`。

**确保 Authorization 还是 `{{token}}`（ADMIN）。**

| 位置 | 操作 |
|------|------|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/ship` |
| Body | 不需要 |

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("发货成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.status).to.eql("SHIPPED");
});
```

**预期：** 状态变为 SHIPPED。

---

## 练习 7：PUT 完成订单（ADMIN）

在 `07-Orders` 文件夹里新建请求，命名为 `PUT 完成订单`。

| 位置 | 操作 |
|------|------|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/complete` |
| Body | 不需要 |

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("完成订单成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.status).to.eql("COMPLETED");
});
```

**预期：** 状态变为 COMPLETED（终态）。

---

## 练习 8（可选）：非法状态操作 → 409

对已取消/已完成的订单发货或支付，应该被拒绝。

**示例：对已完成的订单再发货**

| 位置 | 操作 |
|------|------|
| 方法 | `PUT` |
| URL | `{{base_url}}/api/orders/{{order_id}}/ship` |
| Body | 不需要 |

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("非法状态操作应被拒 (409)", function () {
  pm.response.to.have.status(409);
  // 40007 = 订单状态不允许发货
  pm.expect(json.code).to.eql(40007);
});
```

**预期：** 409 / 40007。

---

## 本课要记住的

### 订单状态机

```
PENDING → PAID → SHIPPED → COMPLETED
  ↓
CANCELLED（终态）
```

### 接口清单

| 操作 | 方法 | URL | 权限 |
|------|------|-----|------|
| 创建订单 | POST | `/api/orders` | 任意用户 |
| 查看订单 | GET | `/api/orders/{id}` | 本人 + ADMIN |
| 取消订单 | PUT | `/api/orders/{id}/cancel` | 本人 + ADMIN |
| 支付订单 | PUT | `/api/orders/{id}/pay` | **仅 ADMIN** |
| 发货 | PUT | `/api/orders/{id}/ship` | **仅 ADMIN** |
| 完成订单 | PUT | `/api/orders/{id}/complete` | **仅 ADMIN** |

### 错误码速查

| 场景 | code | HTTP |
|------|------|------|
| 订单不存在 | 40001 | 404 |
| 重复取消 | 40005 | 409 |
| 非法状态操作 | 40006/40007/40008 | 409 |
| 无权操作订单 | 40009 | 403 |
| 订单参数无效 | 40010 | 400 |

### 关键认知

1. **下单扣库存**——事务保护，失败回滚
2. **价格冻结**——下单时的价格快照，不随商品变动
3. **状态单向流转**——不能回退
4. **幂等保护**——重复取消返回 409，不会创建新订单
5. **服务器不信任客户端**——金额由服务端计算

---

## 检查清单

- [x] POST 创建订单 → 200，状态 PENDING
- [x] GET 查看订单 → 200，包含 items
- [x] PUT 取消订单 → 200，状态 CANCELLED
- [x] PUT 重复取消 → **409 / 40005**
- [x] PUT 支付订单（ADMIN）→ 200，状态 PAID
- [x] PUT 发货（ADMIN）→ 200，状态 SHIPPED
- [x] PUT 完成订单（ADMIN）→ 200，状态 COMPLETED
- [x] （可选）非法状态操作 → 409 / 40007

全部勾完后，回去更新 [progress.md](../progress.md) 与 [记忆.md](../记忆.md)。
