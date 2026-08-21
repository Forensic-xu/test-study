# Phase 3 说明：购物车 / 订单 / 事务 / 状态流转

## 新增 API

### 购物车 `/api/cart`

| Method | URL | 说明 |
|--------|-----|------|
| GET | /api/cart | 我的购物车 |
| POST | /api/cart | 加入（合并同商品） |
| PUT | /api/cart/{id} | 改数量 |
| DELETE | /api/cart/clear | 清空我的 |
| DELETE | /api/cart/{id} | 删除一项 |

### 订单 `/api/orders`

| Method | URL | 权限 |
|--------|-----|------|
| POST | /api/orders | 登录用户 |
| GET | /api/orders | USER=自己 / ADMIN=全部 |
| GET | /api/orders/{id} | 本人或 ADMIN |
| PUT | /api/orders/{id}/cancel | 本人或 ADMIN；仅 PENDING |
| PUT | /api/orders/{id}/pay | ADMIN；PENDING→PAID |
| PUT | /api/orders/{id}/ship | ADMIN；PAID→SHIPPED |
| PUT | /api/orders/{id}/complete | ADMIN；SHIPPED→COMPLETED |

## 创建订单示例

购物车结算：

```json
{ "cartItemIds": [1, 2] }
```

全部结算：

```json
{ "checkoutAll": true }
```

直接下单：

```json
{
  "items": [
    { "productId": 1, "quantity": 2 }
  ]
}
```

金额由服务端按数据库商品价格计算，忽略客户端金额字段。

## 事务与并发

- `@Transactional`：校验 → 建单 → 明细 → 条件扣库存 → 流水 → 删购物车；任一步失败整单回滚
- 扣库存：`UPDATE ... SET stock = stock - ? WHERE id=? AND stock >= ?`
- 取消：`UPDATE orders SET status=CANCELLED WHERE id=? AND status=PENDING`，成功后再恢复库存（防重复取消）

## 数据库

沿用已有 `cart` / `orders` / `order_items`，无破坏性变更。明细含商品名称与价格快照。

详见 [test-cases.md](test-cases.md)、[error-codes.md](error-codes.md)。

---

验收通过后回复：**继续**（进入第四阶段 Vue）
