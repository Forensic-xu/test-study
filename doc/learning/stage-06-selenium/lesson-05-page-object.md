# 第 5 课 · Page Object 封装

**状态：已完成**  
**代码目录**：`mall-admin-test/ui-test/`  
**前置**：第 1～4 课用例已绿

---

## 学习目标

1. 理解 **Page Object**：页面操作收进类，用例只写业务步骤  
2. 认识 `BasePage` + `LoginPage` / `LayoutPage` / `ProductsPage` / `OrdersPage`  
3. 重构后 `pytest` 仍然全绿（行为不变，结构变清晰）

---

## 第 0 步：为什么要 POM？

第 1～4 课用例里到处是：

```python
fill_by_testid(driver, "login-username", ...)
click_by_testid(driver, "menu-products")
wait_visible(driver, "product-table")
```

问题：testid 一改，很多用例都要改。

Page Object 做法：

```text
用例：page.search_by_name("低价零食")
页面类：知道要点哪个 testid
```

```text
测试用例（做什么）  ←→  页面对象（怎么点）
```

---

## 第 1 步：目录

```text
ui-test/
├── pages/
│   ├── base_page.py       # fill / click / visible
│   ├── login_page.py
│   ├── layout_page.py     # 侧栏菜单
│   ├── products_page.py
│   └── orders_page.py
├── common/
│   ├── ui.py              # 底层等待（页面类会调用）
│   └── flows.py           # login_as_admin → 返回 LayoutPage
└── tests/
    ├── test_login.py      # 已改成用 LoginPage
    ├── test_product.py
    └── test_order.py
```

还不是「大框架」：没有复杂 DriverManager，继续用 `conftest` 的 `driver` fixture。

---

## 第 2 步：对照读一段

**以前（第 3 课风格）：**

```python
login_as_admin(driver)
click_by_testid(driver, "menu-products")
fill_by_testid(driver, "product-search-name", "低价零食")
click_by_testid(driver, "product-search")
```

**现在（Page Object）：**

```python
page = login_as_admin(driver).open_products()
page.search_by_name("低价零食")
assert "低价零食" in page.table_text()
```

定位细节藏在 `ProductsPage.search_by_name` 里。

---

## 第 3 步：运行（确认重构没坏）

前后端开着：

```bash
cd d:\code\test-study\mall-admin-test\ui-test
pytest -v
```

或 PyCharm 右键 `tests` 文件夹 → 运行。

### 期望

```text
login 2 + product 3 + order 4  →  9 passed
```

若还有 `test_locators.py`，一并绿即可（定位小练习，不算坏重构）。

---

## 检查清单

- [x] 能用一句话说清：用例管「业务」，Page 管「怎么点」  
- [x] 知道 `BasePage` 和具体页面类的关系  
- [x] `pytest` 登录 / 商品 / 订单相关用例全绿（至少 9 passed）  
- [x] 不急着再加更多层框架  

> 2026-08-29 验收：PyCharm `tests` 全量 **11 passed**（含 locators）。阶段 6 入门课结束。

全部勾完 → **阶段 6 · Selenium 入门课结束**。

---

## 本课你要能说出来

1. POM 不是为了炫技，是为了 **改 UI 时少改用例**。  
2. 先有能跑的用例，再抽 Page（你们已经按总计划做对了）。  
3. `login_as_admin` 返回 `LayoutPage`，可以链式 `open_products()`。  
4. 阶段 6 Selenium 入门课到此收尾。  
