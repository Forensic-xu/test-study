# 第 7 课 · 购物车 + 用户数据隔离 403

**状态：已完成**  
**Collection：** `06-Cart`

---

## 学习目标

- 用 **POST** 添加商品到购物车
- 用 **GET** 查看我的购物车列表
- 用 **PUT** 修改购物车中商品的数量
- 用 **DELETE** 删除购物车项 / 清空购物车
- 理解**用户数据隔离**：每个用户只能操作自己的购物车，越权 → **403 / 30004**
- 理解购物车与库存的关系：数量不能超过库存

---

## 接口速查（来自 `CartController`）

| 方法   | 路径              | 说明           | 权限               |
| ------ | ----------------- | -------------- | ------------------ |
| GET    | `/api/cart`       | 查看我的购物车 | 任意登录用户       |
| POST   | `/api/cart`       | 加入购物车     | 任意登录用户       |
| PUT    | `/api/cart/{id}`  | 修改数量       | **仅购物车所有者** |
| DELETE | `/api/cart/{id}`  | 删除某项       | **仅购物车所有者** |
| DELETE | `/api/cart/clear` | 清空我的购物车 | 任意登录用户       |

请求体：

**POST 加入购物车：**

```json
{
  "productId": 1,
  "quantity": 2
}
```

**PUT 修改数量：**

```json
{
  "quantity": 5
}
```

响应 `CartItemVO`：

```json
{
  "id": 1,
  "userId": 2,
  "productId": 1,
  "productName": "测试手机A",
  "productPrice": 1999.0,
  "productStock": 10,
  "productStatus": "ON_SALE",
  "quantity": 2,
  "createdAt": "...",
  "updatedAt": "..."
}
```

---

## 核心概念：用户数据隔离

### 什么是数据隔离？

在多用户系统中，**每个用户只能看到和操作自己的数据**。你的购物车是你的，别人看不到也改不了。

### 后端怎么实现隔离？

打开 [CartServiceImpl.java](file:///d:/code/test-study/mall-admin-test/backend/src/main/java/com/mall/admin/service/impl/CartServiceImpl.java#L110-L121)，看这段关键代码：

```java
// 110行: 私有方法，检查购物车项是否属于当前用户
private CartItem requireOwnCartItem(Long id, Long userId) {
    CartItem item = cartMapper.selectById(id);
    if (item == null) {
        throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
    }
    // 115行: 如果 userId 不匹配，抛出 403
    if (!Objects.equals(item.getUserId(), userId)) {
        throw new BusinessException(ErrorCode.CART_FORBIDDEN);  // 403 / 30004
    }
    return item;
}
```

工作流程：

1. 用户 A 的请求到达 → JWT 解析出 `userId=2` → 存入 `UserContext`（ThreadLocal）
2. 查询购物车项 `id=1` → 发现 `userId=2`（属于 A）→ 允许操作
3. 用户 B 的请求到达 → JWT 解析出 `userId=4` → 存入 `UserContext`
4. 查询同一个购物车项 `id=1` → 发现 `userId=2`（不属于 B）→ **抛出 403**

### 对比：不同错误码的含义

| 场景                 | 状态码  | 业务码    | 意思                               |
| -------------------- | ------- | --------- | ---------------------------------- |
| 购物车项不存在       | 404     | 30001     | 这项购物车记录压根没有             |
| 你去操作别人的购物车 | **403** | **30004** | 有这项，但**不是你的**，你无权操作 |
| 未登录               | 401     | 10006     | 连身份都不知道，更别说权限了       |

---

## 课前准备

### 1. 新建文件夹 `06-Cart`

在 Postman 集合 `mall-admin-test API` 下，新建文件夹 `06-Cart`

### 2. 切换到普通用户

购物车是普通用户用的功能（不需要 ADMIN 权限）。我们用 `user01` 账号。

**新建登录请求：**

```text
POST  {{base_url}}/api/auth/login
```

Body：

```json
{
  "username": "user01",
  "password": "User@123"
}
```

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("user01 登录成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
});
// 用不同的环境变量存储，避免覆盖 admin 的 token
pm.environment.set("user_token", json.data.token);
// 记录 user01 的 userId
pm.environment.set("user_id", json.data.userId);
console.log("user01 userId:", json.data.userId);
```

### 3. 购物车文件夹的 Authorization 设置

`06-Cart` 文件夹的 Authorization 标签：

- 类型：**Bearer Token**
- Token：`{{user_token}}`

> ⚠️ 关键：这一步覆盖了集合级的 `admin_token` 继承！因为 `06-Cart` 里放的是普通用户的请求，必须用 `user_token`。

### 4. 准备 product_id

环境里应该已有 `product_id`（= 1，测试手机A，库存 10）。如果没有，先设上。

---

## 练习 1：POST 添加商品到购物车

在 `06-Cart` 文件夹里新建请求：

```text
POST  {{base_url}}/api/cart
```

Body → raw → JSON：

```json
{
  "productId": 1,
  "quantity": 3
}
```

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("加入购物车成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.productId).to.eql(1);
  pm.expect(json.data.quantity).to.eql(3);
});
// 存购物车项 id，后面删除要用
pm.environment.set("cart_item_id", json.data.id);
console.log("购物车项ID:", json.data.id);
```

**预期：**

| 项             | 值                |
| -------------- | ----------------- |
| HTTP           | 200               |
| code           | 200               |
| data.productId | 1                 |
| data.quantity  | 3                 |
| data.userId    | 2（user01 的 id） |

### 教学：同商品合并逻辑

后端代码（[CartServiceImpl.java:55-75](file:///d:/code/test-study/mall-admin-test/backend/src/main/java/com/mall/admin/service/impl/CartServiceImpl.java#L55-L75)）：

```java
// 55行: 先查这个用户的购物车里是否已有此商品
CartItem existing = cartMapper.selectOne(...
    .eq(CartItem::getUserId, user.getUserId())      // 属于当前用户
    .eq(CartItem::getProductId, request.getProductId())); // 商品ID相同

if (existing == null) {
    // 新商品 → 直接插入
    cartMapper.insert(item);
} else {
    // 已有商品 → 合并数量（累加）
    int newQty = existing.getQuantity() + request.getQuantity();
    existing.setQuantity(newQty);
    cartMapper.updateById(existing);
}
```

> 💡 如果你重复发同一个请求（productId=1, quantity=3），数量会**累加**变成 6，而不是创建第二条记录。

---

## 练习 2：GET 查看购物车列表

```text
GET  {{base_url}}/api/cart
```

无 Body。

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("查看购物车成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data).to.be.an("array");
  // 至少有 1 条（刚才加的）
  pm.expect(json.data.length).to.be.at.least(1);
});
// 打印购物车内容
console.log("🛒 我的购物车:", JSON.stringify(json.data, null, 2));
```

**预期：** 返回该用户（user01）的所有购物车项。

> 注意：种子数据中 user01（userId=2）已有 2 条购物车记录（product_id=1 qty=2, product_id=5 qty=1），加上刚才新加的，应该至少 3 条。

---

## 练习 3：PUT 修改购物车数量

```text
PUT  {{base_url}}/api/cart/{{cart_item_id}}
```

Body：

```json
{
  "quantity": 5
}
```

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("修改购物车数量成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.quantity).to.eql(5);
});
```

**预期：** 数量被改成 5。

### 教学：数量校验

后端会校验：

1. `quantity > 0`（否则 400 / 30002）
2. `quantity ≤ 商品库存`（否则 409 / 20003）

比如商品 A 库存只有 10，你不能把数量改成 11。

---

## 练习 4：DELETE 删除购物车项

```text
DELETE  {{base_url}}/api/cart/{{cart_item_id}}
```

无 Body。

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("删除购物车项成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
});
```

**预期：** 删除成功，返回 200。

> 可选：再 GET 一次购物车列表，确认该项已不存在。

---

## 练习 5：用户数据隔离测试（核心！403）

这是本课**最重要的练习**——验证"别人不能操作你的购物车"。

### 思路

1. 用户 A（user01）有一个购物车项，`cart_item_id` 已记住
2. **切换到用户 B（admin）** 去操作这个购物车项
3. 后端应该拒绝 → **403 / 30004**

### 步骤

#### 5a. 用 user01 先确保有购物车项

发练习 1 的 POST 请求（加商品到 user01 购物车），记下返回的 `id`（比如是 `X`）。

#### 5b. 切换 Collection Authorization 到 admin

`06-Cart` 文件夹的 Authorization 标签：

- 类型：**Bearer Token**
- Token：`{{token}}`（这是之前登录 admin 存的 token）

> ⚠️ 这里故意让 admin 去操作 user01 的购物车项！

#### 5c. 发 DELETE 请求

```text
DELETE  {{base_url}}/api/cart/X
```

（`X` 换成练习 5a 记下的 cart_item_id）

**Tests 脚本：**

```javascript
const json = pm.response.json();
pm.test("越权删除应被拒绝 (403)", function () {
  // HTTP 必须是 403
  pm.response.to.have.status(403);
  // 业务码必须是 30004
  pm.expect(json.code).to.eql(30004);
});
```

**预期：**

| 项             | 值                 |
| -------------- | ------------------ |
| HTTP Status    | **403 Forbidden**  |
| Body `code`    | **30004**          |
| Body `message` | 无权操作该购物车项 |

#### 5d. 切换回 user01 的 token

`06-Cart` 文件夹的 Authorization 标签改回：

- 类型：**Bearer Token**
- Token：`{{user_token}}`

> 这样就恢复到 user01 的身份了。

---

## 练习 6（可选）：库存不足的数量校验

用 user01 的 token，尝试把购物车数量改得超过库存。

```text
PUT  {{base_url}}/api/cart/{{cart_item_id}}
```

Body（假设商品库存只有 10，这里写 999）：

```json
{
  "quantity": 999
}
```

**预期：**

| 项      | 值           |
| ------- | ------------ |
| HTTP    | **409**      |
| code    | **20003**    |
| message | 商品库存不足 |

**脚本：**

```javascript
const json = pm.response.json();
pm.test("超库存数量应被拒 (409)", function () {
  pm.response.to.have.status(409);
  pm.expect(json.code).to.eql(20003);
});
```

---

## 本课要记住的

| 场景           | 方法            | 期望            | 权限         |
| -------------- | --------------- | --------------- | ------------ |
| 加入购物车     | POST            | 200             | 任意登录用户 |
| 查看我的购物车 | GET             | 200，数组       | 任意登录用户 |
| 修改数量       | PUT `/{id}`     | 200             | **仅所有者** |
| 删除某项       | DELETE `/{id}`  | 200             | **仅所有者** |
| 清空购物车     | DELETE `/clear` | 200             | 任意登录用户 |
| 越权操作别人的 | 任何写操作      | **403 / 30004** | —            |
| 数量超库存     | PUT 超量        | **409 / 20003** | —            |

关键认知：

1. **购物车不需要 ADMIN**——普通用户也能用
2. **数据隔离是后端强制的**——不是靠前端隐藏按钮
3. **403 是"有但不给你"**，404 是"真没有"
4. **同商品加入会合并**（累加数量，不是创建多条）
5. **购物车数量受库存约束**——不能超

---

## 检查清单

- [x] 登录 user01，存 `user_token`
- [x] POST 添加商品 → 200
- [x] GET 查看购物车 → 200，数组
- [x] PUT 修改数量 → 200
- [x] DELETE 删除购物车项 → 200
- [x] 切换 admin token，操作 user01 购物车 → **403 / 30004**
- [x] （可选）超库存修改 → 409 / 20003

全部勾完。下次进入第 8 课（下单/订单状态机）——见 [记忆.md](../记忆.md)。
