# 第 5 课 · conftest 与项目结构

**状态：已完成**  
**代码目录**：`mall-admin-test/api-test/`  
**对应 Postman**：阶段 3 · lesson-08-orders（创建/取消流程）

---

## 学习目标

1. 理解 **根目录 `conftest.py`** 与 `tests/conftest.py` 的分工
2. 用 **`pytest_sessionstart`** 跑用例前检查后端
3. 封装 **`ApiClient`**，用 `admin_client` / `user_client` fixture 注入
4. 新增 `test_smoke.py` + `test_order.py`，完成框架骨架

---

## 第 1 步：conftest 放哪？

```text
api-test/
├── conftest.py          ← 全局：token、client、运行前检查
└── tests/
    ├── conftest.py      ← 仅 tests 目录补充（可为空）
    ├── test_auth.py
    ├── test_order.py
    └── test_smoke.py
```

pytest 规则：

- 子目录用例**自动继承**父目录 conftest 里的 fixture
- 根 `conftest.py` = 整个项目的「公共准备」

---

## 第 2 步：运行前检查后端

```python
def pytest_sessionstart(session):
    # 连不上后端 → 直接退出，避免一堆 ConnectionError
    requests.get(f"{BASE_URL}/swagger-ui.html", timeout=5)
```

= 跑测试前先确认 Spring Boot 已启动。

---

## 第 3 步：ApiClient + client fixture

`common/http_client.py`：

```python
class ApiClient:
    def get(self, path, **kwargs): ...
    def post(self, path, **kwargs): ...
```

`conftest.py`：

```python
@pytest.fixture(scope="session")
def admin_client(admin_token, http_session):
    return ApiClient(admin_token, session=http_session)
```

用例里：

```python
def test_user_list_own_orders(user_client):
    resp = user_client.get("/api/orders", params={"page": 1, "size": 5})
```

比每次 `auth_headers(token)` 更简洁。

---

## 第 4 步：完整项目结构（当前）

```text
api-test/
├── conftest.py              # 全局 fixture + 后端检查
├── config/settings.py       # 环境配置
├── common/
│   ├── assertions.py        # 断言
│   ├── auth_helper.py       # 登录取 token
│   ├── case_loader.py       # 参数化 ids
│   └── http_client.py       # ApiClient
├── api/                     # 接口封装（按模块）
│   ├── auth_api.py
│   ├── product_api.py
│   ├── inventory_api.py
│   ├── cart_api.py
│   └── order_api.py
├── data/                    # 测试数据
├── tests/                   # 用例（按模块 test_*.py）
├── pytest.ini
└── requirements.txt
```

---

## 第 5 步：运行

```bash
cd mall-admin-test/api-test
pytest                    # 全部
pytest -m smoke           # 只跑冒烟
pytest tests/test_order.py -v
```

期望：**28 passed**

---

## 本课要记住的

| 概念 | 说明 |
|------|------|
| 根 `conftest.py` | 全局 fixture，所有用例可用 |
| `pytest_sessionstart` | 会话开始钩子，适合做环境检查 |
| `ApiClient` | 封装带 Token 的请求，减少重复 |
| `http_session` | 复用连接，session 级只建一次 |
| `-m smoke` | 按标记筛选用例 |

---

## 检查清单

- [ ] 知道根 `conftest.py` 和 `tests/conftest.py` 区别
- [ ] `pytest -m smoke` 3 条 PASSED
- [ ] `pytest tests/test_order.py -v` 4 条 PASSED
- [ ] `pytest` 全部 **28 passed**

全部勾完后进入第 6 课（测试数据管理 JSON/YAML）。
