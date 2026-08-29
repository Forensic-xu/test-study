# 第 3 课 · test_product 商品管理

**状态：已完成**  
**代码目录**：`mall-admin-test/ui-test/`  
**前置**：第 1～2 课登录用例已绿

---

## 学习目标

1. 登录后通过侧栏进入 **商品管理**  
2. 断言列表页 `products-page` / `product-table`  
3. 按名称搜索种子商品，验证表格内容  
4. 抽出可复用的 `login_as_admin`（还不是 Page Object）

---

## 第 0 步：本课流程

```text
登录 admin
  → 点侧栏 menu-products
  → 看到商品列表
  →（可选）输入名称 → 查询 → 表格含「低价零食」
  →（可选）点详情 → 弹窗里名称只读
```

对应手工：你自己点浏览器做的同一套操作。

---

## 第 1 步：关键 data-testid

| testid | 作用 |
|--------|------|
| `menu-products` | 侧栏「商品管理」 |
| `products-page` | 商品页容器 |
| `product-search-name` | 名称搜索框 |
| `product-search` | 查询按钮 |
| `product-table` | 商品表格 |
| `product-detail-{id}` | 某行「详情」 |
| `product-form-name` | 弹窗里的名称 |

种子商品名：**低价零食**（`database/data.sql`）。

---

## 第 2 步：新增文件

```text
ui-test/
├── common/
│   ├── ui.py
│   └── flows.py          ← login_as_admin（本课新增）
└── tests/
    ├── test_login.py
    └── test_product.py   ← 本课 3 条用例
```

`login_as_admin(driver)`：打开登录页 → 填 admin → 等到 `admin-layout`。  
商品用例开头都先调它，避免每条用例复制登录代码。

---

## 第 3 步：运行

前后端要开着，然后：

```bash
cd d:\code\test-study\mall-admin-test\ui-test
pytest tests/test_product.py -v
```

或 PyCharm：右键 `test_product.py` → **运行 'Python 测试'**。

### 期望

```text
test_open_products_page PASSED
test_search_product_by_name PASSED
test_open_product_detail PASSED
```

全部用例：

```bash
pytest
```

应看到登录 2 条 + 商品 3 条都绿。

---

## 失败常见原因

| 现象 | 处理 |
|------|------|
| 侧栏没有商品管理 | 确认用的是 **admin**（普通用户看不到） |
| 表格为空 / 搜不到 | 后端 + MySQL 种子数据；先手工打开页面看有没有数据 |
| 点详情超时 | 列表还没加载完；或数据库空 |

---

## 检查清单

- [x] 理解为什么商品用例要先 `login_as_admin`  
- [x] 能说出 `menu-products` → `products-page` 这条路径  
- [x] `pytest tests/test_product.py` 3 passed  
- [x] （可选）`pytest` 全绿  

> 2026-08-29 验收：`test_product` 3 passed（约 32s）。

全部勾完后进入第 4 课（订单流程）。

---

## 本课你要能说出来

1. UI 用例常跨多个页面：登录只是前置，业务在后面。  
2. 公共「登录」抽到 `flows.py`，用例保持短。  
3. 列表断言可以看 URL、testid，也可以看表格 `text`。  
4. 完整 Page Object 仍留到第 5 课。  
