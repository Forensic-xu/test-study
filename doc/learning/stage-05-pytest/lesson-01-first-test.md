# 第 1 课 · Pytest 入门 + 第一个登录用例

**状态：已完成**  
**代码目录**：`mall-admin-test/api-test/`  
**对应 Postman**：阶段 3 · lesson-01-login

---

## 学习目标

1. 搭建 `api-test` 最小项目
2. 用 `requests` 发 POST 请求（对标 Postman）
3. 用 `assert` 断言 HTTP Status + 业务 `code`
4. 用 `pytest` 一键运行多条用例

---

## 第 0 步：先理解「第一课在干什么」

第一课只做一件事：**把 Postman 里手点的 4 个登录请求，改成 Python 代码，让 pytest 自动跑。**

```text
你在 Postman 里：点 Send → 看 Tests 是否通过
第一课在 Python 里：写 test_ 函数 → 终端输入 pytest → 看 PASSED/FAILED
```

背后都是同一件事：**发 HTTP 请求 → 检查返回是否符合预期**。

---

## 第 1 步：确认后端在跑（必须）

Pytest 测的是**真实接口**，不是 mock。后端没启动，用例会报错（ConnectionError）。

1. 确认 `mall-admin-test` 后端已启动
2. 浏览器打开：http://127.0.0.1:8080/swagger-ui.html  
   能打开 = 后端正常

---

## 第 2 步：打开项目文件夹

在 Cursor 左侧文件树找到：

```text
mall-admin-test/
└── api-test/          ← 第一课所有代码在这里
    ├── config.py
    ├── pytest.ini
    ├── requirements.txt
    ├── README.md
    └── tests/
        └── test_auth.py
```

---

## 第 3 步：安装 Python 依赖（只做一次）

在 Cursor 里打开终端（`` Ctrl+` `` 或菜单 Terminal → New Terminal），执行：

```bash
cd mall-admin-test/api-test
pip install -r requirements.txt
```

这会安装两个库：

| 库 | 作用 |
|----|------|
| `requests` | 发 HTTP 请求（= Postman 发请求） |
| `pytest` | 运行测试、统计 PASSED/FAILED |

看到 `Successfully installed` 即可。

---

## 第 4 步：看懂 3 个文件（不用背，知道干什么）

### ① `config.py` = Postman 环境变量

```python
BASE_URL = "http://127.0.0.1:8080"   # 相当于 {{base_url}}
ADMIN_USERNAME = "admin"              # 测试账号
ADMIN_PASSWORD = "Admin@123"
```

### ② `tests/test_auth.py` = 4 个测试用例

每个 `def test_xxx():` 就是一个用例，和 Postman 里一个请求 + Tests 脚本对应：

```python
def test_login_success():
    # 1. 发请求（= Postman 点 Send）
    resp = requests.post(LOGIN_URL, json={"username": "admin", "password": "Admin@123"})

    # 2. 断言（= Postman Tests 里的 pm.test）
    assert resp.status_code == 200
    assert resp.json()["code"] == 200
```

**规则**：文件名 `test_` 开头、函数名 `test_` 开头 → pytest 才会当测试执行。

### ③ `pytest.ini` = pytest 配置

告诉 pytest：去 `tests/` 找测试、`pythonpath = .` 让 `from config import ...` 能导入。

---

## 第 5 步：运行 pytest（核心操作）

终端仍在 `api-test` 目录下，输入：

```bash
pytest
```

### 成功时你会看到：

```text
tests/test_auth.py::test_login_success PASSED
tests/test_auth.py::test_login_wrong_password PASSED
tests/test_auth.py::test_login_empty_username PASSED
tests/test_auth.py::test_login_disabled_user PASSED

4 passed
```

**4 passed = 第一课跑通。**

### 只跑某一个用例：

```bash
pytest tests/test_auth.py::test_login_success -v
```

---

## 第 6 步：对照 Postman 理解（帮助记忆）

| Postman | Python（第一课） |
|---------|------------------|
| 环境变量 `{{base_url}}` | `config.py` 的 `BASE_URL` |
| 新建请求 POST `/api/auth/login` | `requests.post(LOGIN_URL, json={...})` |
| Body 填 JSON | `json={"username": "...", "password": "..."}` |
| Tests：`pm.response.to.have.status(200)` | `assert resp.status_code == 200` |
| Tests：`pm.expect(json.code).to.eql(200)` | `assert resp.json()["code"] == 200` |
| 点 Send，看 Tests 结果 | 终端输入 `pytest`，看 PASSED |

---

## 完整流程图

```text
① 启动后端 :8080
        ↓
② cd mall-admin-test/api-test
        ↓
③ pip install -r requirements.txt   （首次）
        ↓
④ pytest
        ↓
⑤ pytest 自动执行 tests/test_auth.py 里 4 个 test_ 函数
        ↓
⑥ 每个函数：requests 发请求 → assert 检查 → PASSED 或 FAILED
        ↓
⑦ 终端显示 4 passed
```

---

## 常见报错

| 报错 | 原因 | 处理 |
|------|------|------|
| `ConnectionError` / 连接被拒绝 | 后端没启动 | 先启动 Spring Boot |
| `ModuleNotFoundError: No module named 'requests'` | 没装依赖 | `pip install -r requirements.txt` |
| `ModuleNotFoundError: No module named 'config'` | 不在 api-test 目录跑 | `cd mall-admin-test/api-test` 再 pytest |
| `AssertionError` | 断言失败（返回和预期不符） | 看 FAILED 那行，对照 error-codes.md |

---

## 建议你自己做一遍（5 分钟）

1. 打开终端，`cd mall-admin-test/api-test`
2. 输入 `pytest`
3. 确认看到 **4 passed**
4. 打开 `test_auth.py`，把第 41 行 `10002` 改成 `99999`
5. 再 `pytest` → 应看到 1 FAILED
6. 改回 `10002`，再 `pytest` → 恢复 4 passed

做完你就真正理解第一课了。

---


```text
api-test/
├── config.py           # BASE_URL、账号（像 Postman 环境变量）
├── tests/
│   └── test_auth.py    # 测试文件，函数名 test_ 开头
├── pytest.ini          # pytest 配置
└── requirements.txt
```

### Postman → Python 对照

| Postman | Python |
|---------|--------|
| `POST {{base_url}}/api/auth/login` | `requests.post(LOGIN_URL, json={...})` |
| Tests: `pm.response.to.have.status(200)` | `assert resp.status_code == 200` |
| Tests: `pm.expect(json.code).to.eql(200)` | `assert body["code"] == 200` |
| 环境变量 `base_url` | `config.py` 里的 `BASE_URL` |

---

## 第 4 步：阅读 test_auth.py

四个用例（和 Postman 第 1 课一致）：

| 用例 | 期望 HTTP | 期望 code |
|------|-----------|-----------|
| 正确登录 | 200 | 200 |
| 错误密码 | 401 | 10002 |
| 空用户名 | 400 | 90001 |
| 禁用用户 | 403 | 10003 |

核心代码模式：

```python
resp = requests.post(LOGIN_URL, json={"username": "...", "password": "..."})
assert resp.status_code == 200
assert resp.json()["code"] == 200
```

---

## 第 5 步：运行

```bash
pytest
```

期望输出类似：

```text
tests/test_auth.py::test_login_success PASSED
tests/test_auth.py::test_login_wrong_password PASSED
tests/test_auth.py::test_login_empty_username PASSED
tests/test_auth.py::test_login_disabled_user PASSED
4 passed
```

只看某一个用例：

```bash
pytest tests/test_auth.py::test_login_success -v
```

---

## 本课要记住的

- **测试文件**：`test_` 开头；**测试函数**：`test_` 开头
- **assert**：断言失败 → 用例 FAILED，pytest 会显示期望值和实际值
- **同时断言** HTTP Status 和 body 里的 `code`（本项目约定）
- AI 可以帮你写代码，但**断言值要对照** `mall-admin-test/docs/error-codes.md`

---

## 检查清单

- [ ] `pip install -r requirements.txt` 成功
- [ ] `pytest` 4 条全部 PASSED
- [ ] 能说出 `assert resp.status_code` 和 `assert body["code"]` 的区别
- [ ] 故意改错一个 code，看到 pytest 报 FAILED

全部勾完后进入第 2 课（断言模式与公共封装）。
