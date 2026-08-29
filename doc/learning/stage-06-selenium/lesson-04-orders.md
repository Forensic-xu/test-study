# 第 4 课 · test_order 订单流程

**状态：已完成**  
**代码目录**：`mall-admin-test/ui-test/`  
**前置**：第 1～3 课已绿

---

## 学习目标

1. 登录后进入 **订单管理** 列表  
2. 按订单号搜索（前端过滤）  
3. 进入订单 **详情页**，断言订单号与明细  
4. 理解状态流转：有「支付」就点一次；已付过也能再跑（不崩）

---

## 第 0 步：本课流程

```text
登录 admin
  → menu-orders → 订单列表（当前页有 ORD…）
  → 用当前页第一条订单号搜索 → 表格仍含该号
  → 点第一条详情 → 详情页 + 明细表
  →（可选）有「支付」按钮就点；没有也能绿（可重复跑）
```

> 注意：学习库里常有 Pytest 等历史订单，**种子单可能在第 2 页**。  
> 本课用例故意用「当前页第一条」，不写死 `ORD202601010002`。

种子订单（`database/data.sql`，仅作对照）：

| 订单号 | 初始状态 |
|--------|----------|
| `ORD202601010001` | PENDING |
| `ORD202601010002` | PAID |
| `ORD202601010003` | COMPLETED |
| `ORD202601010004` | CANCELLED |

---

## 第 1 步：关键 data-testid

| testid | 作用 |
|--------|------|
| `menu-orders` | 侧栏订单 |
| `orders-page` / `order-table` | 列表页 |
| `order-search-no` / `order-search` | 订单号搜索 |
| `order-detail-{id}` | 进详情 |
| `order-pay-{id}` | 列表上支付（仅 PENDING + admin） |
| `order-detail-page` / `order-detail-info` / `order-detail-items` | 详情 |

---

## 第 2 步：新增文件

```text
tests/test_order.py
```

继续复用 `login_as_admin`，不新造框架。

---

## 第 3 步：运行

```bash
cd d:\code\test-study\mall-admin-test\ui-test
pytest tests/test_order.py -v
```

或 PyCharm 右键 `test_order.py` → **运行**。

### 期望

```text
test_open_orders_page PASSED
test_search_order_by_no PASSED
test_open_order_detail PASSED
test_pay_pending_order_when_available PASSED
```

约 4 条；全量：

```bash
pytest
```

登录 2 + 商品 3 + 订单 4 ≈ 9 passed。

---

## 注意：支付会改库 & 列表分页

- 支付用例：**有按钮才点**，第二次通常没有按钮，仍应 PASSED。  
- 订单号搜索是**当前页数据的前端过滤**；写死很久以前的种子单号可能「No Data」。  
- 要恢复种子：重新导入 `data.sql`。

---

## 检查清单

- [x] 能说出列表 vs 详情页的 testid 区别  
- [x] 理解订单号搜索是前端过滤（填了就要点查询或依赖过滤逻辑）  
- [x] `pytest tests/test_order.py` 4 passed  
- [x] 知道支付用例为何「第二次还能绿」  

> 2026-08-29 验收：PyCharm `test_order.py` 4 passed（约 22s）。

全部勾完后进入第 5 课（Page Object 封装）。

---

## 本课你要能说出来

1. UI 订单流 = 列表定位 + 状态按钮 + 详情页断言。  
2. 会改数据的用例要设计成 **可重复跑**（有按钮才点）。  
3. admin 才能看到支付/发货等操作按钮。  
4. 下一课才把这些步骤收成 Page Object。  
