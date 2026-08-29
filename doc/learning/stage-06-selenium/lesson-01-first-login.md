# 第 1 课 · 第一个 test_login（打开页→输入→登录→验证）

**状态：已完成**  
**代码目录**：`mall-admin-test/ui-test/`  
**参考**：`mall-admin-test/docs/selenium-guide.md`

---

## 学习目标

1. 安装 Selenium，用 Chrome 打开前端登录页  
2. 用 `data-testid` 定位输入框和按钮  
3. 完成：输入账号密码 → 点登录 → 断言进入 Dashboard  
4. 再写一条：错误密码 → 看到 `login-error`

> Pytest 验接口「对不对」；Selenium 验页面「点得通不通」。

---

## 第 0 步：先理解在干什么

```text
你手工：打开浏览器 → 输入账号 → 点登录 → 看有没有 Dashboard
第一课：用代码驱动同一浏览器，自动做完并 assert
```

定位优先用项目预留的 **`data-testid`**，不要抄 Element Plus 的动态 class。

---

## 第 1 步：确认前后端都在跑（必须）

| 服务 | 地址 | 怎么确认 |
|------|------|----------|
| 后端 | http://127.0.0.1:8080 | Swagger 能开 |
| 前端 | http://127.0.0.1:5176 | 浏览器能打开登录页 |

前端没起 → Selenium 会连不上；后端没起 → 登录接口失败。

---

## 第 2 步：打开项目目录

```text
mall-admin-test/
└── ui-test/                 ← 本课代码
    ├── config.py
    ├── pytest.ini
    ├── requirements.txt
    ├── README.md
    └── tests/
        └── test_login.py
```

本机需已安装 **Google Chrome**（Selenium 4.6+ 会自动下载匹配的 ChromeDriver）。

---

## 第 3 步：安装依赖（只做一次）

在终端执行：

```bash
cd mall-admin-test/ui-test
pip install -r requirements.txt
```

| 库 | 作用 |
|----|------|
| `selenium` | 控制浏览器 |
| `pytest` | 跑测试 |

---

## 第 4 步：看懂关键几行

### ① 打开浏览器 + 打开登录页

```python
driver = webdriver.Chrome()
driver.get("http://127.0.0.1:5176/login")
```

### ② 用 `data-testid` 找输入框

```python
# 当前项目：testid 已在原生 input 上
driver.find_element(By.CSS_SELECTOR, '[data-testid="login-username"]')
```

不要写成 `[data-testid="login-username"] input`（会超时找不到）。

### ③ 显式等待（不要瞎 sleep）

```python
WebDriverWait(driver, 10).until(
    EC.visibility_of_element_located((By.CSS_SELECTOR, '[data-testid="dashboard-page"]'))
)
```

### ④ 用完关掉浏览器

```python
driver.quit()
```

---

## 第 5 步：运行

```bash
cd mall-admin-test/ui-test
pytest
```

### 成功时你会看到

- 弹出 Chrome，自动填表、点登录、关掉  
- 终端大致：

```text
tests/test_login.py::test_login_success PASSED
tests/test_login.py::test_login_wrong_password PASSED
```

### 失败常见原因

| 现象 | 处理 |
|------|------|
| 连不上 5176 | 先 `npm run dev` 起前端 |
| 登录后超时等不到 Dashboard | 后端 8080 没起 / 账号错 |
| ChromeDriver 报错 | 升级 Chrome；或 `pip install -U selenium` |
| 找不到输入框 / Timeout | 不要多写 ` input`；确认前端 5176 已开 |

---

## 检查清单

- [x] 前端 5176 + 后端 8080 已启动  
- [x] `pip install -r requirements.txt` 成功  
- [x] `pytest` 两条用例都 PASSED  
- [x] 能说出：为什么用 `data-testid`，以及输入框选择器怎么写  

> 2026-08-29 验收：`test_login_success` / `test_login_wrong_password` 均 PASSED。

全部勾完后进入第 2 课（定位与显式等待整理）。

---

## 本课你要能说出来

1. Selenium 驱动的是**真实浏览器**，不是发 HTTP。  
2. 本项目定位首选 **`data-testid`**。  
3. 本项目 `el-input`：testid 在原生 **`input`** 上，直接点它。  
4. 等待用 **WebDriverWait**，别一上来就大框架 / Page Object。  
