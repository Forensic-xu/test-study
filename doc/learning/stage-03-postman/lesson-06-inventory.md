# 第 6 课 · 库存增加/减少 + 超库存 409

**状态：已完成**  
**Collection：** `05-Inventory`

---

## 学习目标

- 用 **PUT** 改库存（不是改商品，是独立库存接口）
- 理解**审计流水**：每次增减都会留痕（beforeStock / afterStock）
- 理解业务约束：库存不能为负 → 减太多返回 **409 / 50002**
- 区分：数量非法 **400 / 50001**（quantity ≤ 0 或缺字段）vs 库存不足 **409 / 50002**

---

## 接口速查（来自 `InventoryController`）

| 方法 | 路径                                  | 说明              |
| ---- | ------------------------------------- | ----------------- |
| GET  | `/api/inventory/{productId}`          | 查某商品当前库存  |
| PUT  | `/api/inventory/{productId}/increase` | 增加库存（ADMIN） |
| PUT  | `/api/inventory/{productId}/decrease` | 减少库存（ADMIN） |
| GET  | `/api/inventory/{productId}/records`  | 看流水            |

请求体（increase / decrease 通用）：

```json
{
  "quantity": 10,
  "remark": "练习-补货"
}
```

- `quantity` 必须 > 0，否则 **400 / 50001**（或 90001 校验失败）
- `remark` 选填

响应 `InventoryVO`：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productId": 1,
    "productName": "...",
    "stock": 110,
    "status": "ON_SALE"
  }
}
```

---

## 课前确认

1. 已登录 ADMIN，`{{token}}` 有效（库存写操作要 ADMIN）
2. 环境里需要一个 `{{product_id}}`：
   - 如果第 3 课用过商品且还记得 id，直接填进环境；
   - 否则先 `GET {{base_url}}/api/products?page=1&size=5` 找一个 `id` 填进环境变量 `product_id`
3. 把 `product_id` 在环境里设好（注意全程统一用下划线命名）

> 没有商品可练？管理员可 POST `/api/products` 创建一个，Body 里带 `stock: 10`。

---

## 练习 1：GET 查询库存（先摸底）

```text
GET  {{base_url}}/api/inventory/{{product_id}}
```

无 Body。

期望：

| 项             | 值                                         |
| -------------- | ------------------------------------------ |
| HTTP           | 200                                        |
| code           | 200                                        |
| data.productId | 与 `{{product_id}}` 一致                   |
| data.stock     | 一个整数，**记下来当作 N**（后面要拿来算） |

脚本：

```javascript
const json = pm.response.json();
pm.test("查询库存成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.productId).to.eql(pm.environment.get("product_id") * 1);
});
// 把当前库存存到环境，后面断言要用
pm.environment.set("stock_before", json.data.stock);
```

要点：**先知道现在有多少，再算增减后的期望值。**

---

## 练习 2：PUT increase 增加库存

```text
PUT  {{base_url}}/api/inventory/{{product_id}}/increase
```

Body → raw → JSON：

```json
{
  "quantity": 10,
  "remark": "练习-补货10件"
}
```

期望：

| 项         | 值                      |
| ---------- | ----------------------- |
| HTTP       | 200                     |
| code       | 200                     |
| data.stock | `{{stock_before}} + 10` |

脚本：

```javascript
const json = pm.response.json();
const before = pm.environment.get("stock_before") * 1;
pm.test("增加库存成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.stock).to.eql(before + 10);
});
// 更新环境里的当前库存，给下一步用
pm.environment.set("stock_before", json.data.stock);
```

---

## 练习 3：PUT decrease 减少库存

```text
PUT  {{base_url}}/api/inventory/{{product_id}}/decrease
```

Body：

```json
{
  "quantity": 5,
  "remark": "练习-卖出5件"
}
```

期望：

| 项         | 值                     |
| ---------- | ---------------------- |
| HTTP       | 200                    |
| code       | 200                    |
| data.stock | `{{stock_before}} - 5` |

脚本：

```javascript
const json = pm.response.json();
const before = pm.environment.get("stock_before") * 1;
pm.test("减少库存成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.stock).to.eql(before - 5);
});
pm.environment.set("stock_before", json.data.stock);
```

要点：**库存接口不是覆盖式更新，是基于当前值做增减。**

---

## 练习 4：GET records 看流水（审计追溯）

```text
GET  {{base_url}}/api/inventory/{{product_id}}/records
```

期望：

| 项   | 值                                                                   |
| ---- | -------------------------------------------------------------------- |
| HTTP | 200                                                                  |
| code | 200                                                                  |
| data | 数组，至少有刚才 2 条（increase + decrease）                         |
| 每条 | 含 `beforeStock` / `changeQuantity` / `afterStock` / `operationType` |

脚本：

```javascript
const json = pm.response.json();
pm.test("库存流水存在", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data).to.be.an("array");
  pm.expect(json.data.length).to.be.at.least(2);
});
// 打印最近一条，人工核对流水
console.log("最近一条流水:", JSON.stringify(json.data[0]));
```

要点：**流水是审计证据，能回答"谁、什么时候、改了多少、改前改后是多少"。**

---

## 练习 5：超库存减少 → 409 / 50002

先看一下当前 `{{stock_before}}` 是多少（假设是 N）。Body 里 quantity 写一个明显大于 N 的值：

```text
PUT  {{base_url}}/api/inventory/{{product_id}}/decrease
```

Body：

```json
{
  "quantity": 999999,
  "remark": "练习-超库存减少"
}
```

期望：

| 项      | 值                 |
| ------- | ------------------ |
| HTTP    | **409**            |
| code    | **50002**          |
| message | 库存不足，无法减少 |

脚本：

```javascript
const json = pm.response.json();
pm.test("超库存减少被拒", function () {
  pm.response.to.have.status(409);
  pm.expect(json.code).to.eql(50002);
});
```

要点：**这是业务规则冲突，不是服务器错误。库存没变。**

可选验证：再 GET 一次库存，确认 stock 没变（仍是 N）。

---

## 练习 6（可选）：非法数量 → 400

```text
PUT  {{base_url}}/api/inventory/{{product_id}}/increase
```

Body（quantity = 0）：

```json
{
  "quantity": 0,
  "remark": "练习-非法数量"
}
```

期望：

| 项      | 值                                         |
| ------- | ------------------------------------------ |
| HTTP    | **400**                                    |
| code    | **50001** 或 **90001**（看校验层先拦哪个） |
| message | 库存调整数量非法 / 参数校验失败            |

脚本：

```javascript
const json = pm.response.json();
pm.test("非法数量被拒", function () {
  pm.response.to.have.status(400);
  // 50001 = 业务层校验；90001 = 框架参数校验，二选一都算对
  pm.expect([50001, 90001]).to.include(json.code);
});
```

---

## 本课要记住的

| 场景     | 方法              | 期望                     |
| -------- | ----------------- | ------------------------ |
| 查库存   | GET               | 200                      |
| 加库存   | PUT `/increase`   | 200，stock 增加          |
| 减库存   | PUT `/decrease`   | 200，stock 减少          |
| 看流水   | GET `/records`    | 200，数组                |
| 库存不足 | PUT decrease 超量 | **409 / 50002**          |
| 数量非法 | quantity ≤ 0      | **400 / 50001 或 90001** |

关键认知：

1. **库存是独立资源**，不是商品字段；改库存走 `/api/inventory/*`，不要 PUT 商品
2. **每次增减有流水**，可追溯（beforeStock → afterStock）
3. **409 不一定是 20006/20007**，库存类冲突是 **50002**

---

## 检查清单

- [x] GET 库存 200，记下 N
- [x] PUT increase +10 → stock = N+10
- [x] PUT decrease -5 → stock = N+5
- [x] GET records → 至少 2 条流水
- [x] PUT decrease 超量 → 409 / 50002
- [x] （可选）quantity=0 → 400

全部勾完。下次进入第 7 课（购物车）——见 [记忆.md](../记忆.md)。
