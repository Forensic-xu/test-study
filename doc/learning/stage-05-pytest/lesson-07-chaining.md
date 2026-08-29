# 第 7 课 · 接口关联（登录→创建→拿 id→下一步）

**状态：已完成**  
**代码目录**：`mall-admin-test/api-test/`  
**前置**：第 6 课 JSON 外置已绿

---

## 学习目标

1. 理解 **接口关联**：上一步返回的字段，当作下一步的入参  
2. 会写「创建 → 用 id → 删除/改状态」整条链  
3. 对照 Postman：相当于把 `{{category_id}}` / `{{order_id}}` 写进 Python 变量  

---

## 第 0 步：一句话

```text
create 响应里的 id
        ↓
   赋给变量 category_id / product_id / order_id
        ↓
delete / pay / increase 都用这个变量
```

不是魔法，就是 **普通 Python 变量**。

---

## 第 1 步：本课新增内容

| 文件 | 作用 |
|------|------|
| `api/category_api.py` | 创建 / 删除分类 |
| `api/product_api.py` | 增加 `create_product` / `delete_product` |
| `api/order_api.py` | 增加 `pay` / `ship` / `complete` |
| `tests/test_chaining.py` | **4 条关联用例** |

你之前 `test_order.py` 里「下单→取消」其实已经是关联，本课再练更长的链。

---

## 第 2 步：看懂一条链

```python
create_body = assert_api_success(create_category(admin_token, name=...))
category_id = create_body["data"]["id"]   # 关联点

assert_api_success(delete_category(admin_token, category_id))
```

更长的一条（商品）：

```text
建分类 → category_id
  → 建商品(category_id) → product_id
  → 加库存(product_id)
  → 删商品(product_id)
  → 删分类(category_id)
```

订单状态链：

```text
user 下单 → order_id
  → admin 支付 → 发货 → 完成
  → 再 get_order(order_id) 断言 COMPLETED
```

---

## 第 3 步：运行

后端 `8080` 开着：

```bat
cd /d D:\code\test-study\mall-admin-test\api-test
.\.venv1\Scripts\python.exe -m pytest tests/test_chaining.py -v
```

或 PyCharm 右键 `test_chaining.py` → 运行。

### 期望

```text
test_category_create_then_delete PASSED
test_product_create_inventory_then_cleanup PASSED
test_category_delete_blocked_when_has_product PASSED
test_order_pay_ship_complete_chain PASSED
```

全量：

```bat
.\.venv1\Scripts\python.exe -m pytest -v
```

大约 **32 passed**（原 28 + 本课 4）。

---

## 检查清单

- [x] 能指出代码里哪一行是「关联变量」  
- [x] 知道为什么删分类前要先删商品（20007）  
- [x] `pytest tests/test_chaining.py` 4 passed  
- [x] （可选）全量 pytest 仍绿  

> 2026-08-29 验收：第 7 课绿；`test_chaining.py` 注释已白话。下班存档。

全部勾完后进入第 8 课（日志与 HTML 报告）。

---

## 本课你要能说出来

1. 接口关联 = 响应字段当下一请求参数。  
2. Pytest 里用变量即可，不必上复杂框架。  
3. 长链路要记得 **清理数据**（或用唯一名称），避免脏数据。  
4. 面试可举：分类→商品→库存，或订单支付发货完成。  
