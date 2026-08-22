# 第 3 课 · fixture 深入 + 多用户 token

**状态：已完成**  
**代码目录**：`mall-admin-test/api-test/`  
**对应 Postman**：阶段 3 · lesson-07-cart（用户隔离 403）

---

## 学习目标

1. 理解 **fixture** 是什么、为什么用 `scope="session"`
2. 配置多个 token fixture：`admin_token` / `user_token` / `user02_token`
3. 用 fixture 注入参数，避免每个用例重复登录
4. 用购物车越权场景验证 **403 / 30004**

---

## 第 1 步：什么是 fixture？

fixture = **测试前的准备工作**，pytest 自动注入到用例参数里。

```python
# conftest.py
@pytest.fixture(scope="session")
def user_token():
    return fetch_token("user01", "User@123")

# test_cart.py
def test_user_token_can_list_cart(user_token):  # ← 参数名 = fixture 名
    resp = list_cart(user_token)
```

= Postman 里切换环境变量 `{{token_user}}`，但 **pytest 自动帮你切换**。

### scope 含义

| scope | 含义 | 本课用法 |
|-------|------|----------|
| `function` | 每个用例执行一次（默认） | — |
| `session` | 整个 pytest 只执行一次 | 登录 token（省时间） |

---

## 第 2 步：看 conftest.py

```text
tests/conftest.py
├── base_url          # 后端地址
├── admin_token       # admin 登录
├── user_token        # user01 登录
├── user02_token      # user02 登录（越权测试用）
└── tokens            # 字典，一次拿三个 token
```

登录逻辑抽到 `common/auth_helper.py` 的 `fetch_token()`，避免重复代码。

---

## 第 3 步：多用户隔离测试

种子数据：`cart.id=1` 属于 **user01**。

| 操作者 | 动作 | 期望 |
|--------|------|------|
| user01 | 查看自己的购物车 | 200 |
| user02 | 删 user01 的 cart_item_id=1 | **403 / 30004** |
| admin | 删 user01 的 cart_item_id=1 | **403 / 30004** |

购物车是**用户私有数据**，即使是 ADMIN 也不能操作别人的购物车项。

---

## 第 4 步：运行

```bash
cd mall-admin-test/api-test
pytest tests/test_cart.py -v
pytest   # 全部用例
```

期望：**14 passed**（8 原有 + 6 cart）

---

## 本课要记住的

- fixture 写在 `conftest.py`，用例通过**同名参数**自动获取
- `session` 级 token：全文件只登录一次
- 多用户测试 = 多个 token fixture + 越权场景断言
- `request.getfixturevalue("user02_token")` 用于参数化时动态取 fixture

---

## 检查清单

- [ ] 能说出 fixture 和 `conftest.py` 的作用
- [ ] `pytest tests/test_cart.py -v` 全部 PASSED
- [ ] 理解为什么 admin 删 user01 购物车也是 403
- [ ] `pytest` 全部 PASSED

全部勾完后进入第 4 课（参数化 `@pytest.mark.parametrize` 进阶）。
