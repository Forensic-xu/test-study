# 第 4 课 · 参数化 + 测试数据外置

**状态：已完成**  
**代码目录**：`mall-admin-test/api-test/`  
**对应 Postman**：阶段 3 · lesson-06-inventory

---

## 学习目标

1. 用 `@pytest.mark.parametrize` **一条逻辑测多组数据**
2. 把测试数据放到 `data/`，与用例代码分离
3. 用 `case_id` + `case_ids()` 让报告可读
4. 新增 `test_inventory.py`（库存增减 + 非法数量 + 超库存）

---

## 第 1 步：为什么参数化？

不用参数化（重复代码）：

```python
def test_wrong_password(): ...
def test_empty_username(): ...
def test_disabled_user(): ...
```

用参数化（一条逻辑）：

```python
@pytest.mark.parametrize("case", LOGIN_ERROR_CASES, ids=case_ids(LOGIN_ERROR_CASES))
def test_login_error_cases(case):
    resp = login(case["username"], case["password"])
    assert_api_error(resp, case["expected_http"], case["expected_code"])
```

**4 组数据 → 4 条测试报告**，代码只写一遍。

---

## 第 2 步：数据外置（data/）

`data/accounts.py`：

```python
LOGIN_ERROR_CASES = [
    {
        "case_id": "wrong_password",
        "username": "admin",
        "password": "wrong",
        "expected_http": 401,
        "expected_code": 10002,
    },
    # ...
]
```

`data/inventory_cases.py`：

```python
INVALID_QUANTITY_CASES = [...]
INVENTORY_CHANGE_SUCCESS_CASES = [...]
```

**原则**：改数据不动代码；非开发人员也能维护用例表。

---

## 第 3 步：case_ids 让报告可读

`common/case_loader.py`：

```python
def case_ids(cases, key="case_id"):
    return [case[key] for case in cases]
```

pytest 输出：

```text
test_login_error_cases[wrong_password] PASSED
test_inventory_invalid_quantity[zero_quantity] PASSED
```

而不是无意义的 `case0`、`case1`。

---

## 第 4 步：库存用例（test_inventory.py）

| 用例 | 方式 | 期望 |
|------|------|------|
| 查询库存 | 普通用例 | 200 |
| 增加/减少 | 参数化 2 组 | stock 前后计算正确 |
| quantity=0/-1 | 参数化 | 400 / **90001** |
| 减超库存 | 单用例 | 409 / **50002** |
| 流水列表 | 单用例 | data 为非空 list |

> 注意：quantity≤0 走 **参数校验 90001**；库存不够走 **业务码 50002**。

---

## 第 5 步：运行

```bash
cd mall-admin-test/api-test
pytest tests/test_inventory.py -v
pytest   # 全部
```

期望：**21 passed**

---

## 本课要记住的

| 概念 | 说明 |
|------|------|
| `@pytest.mark.parametrize` | 多组输入 → 多条测试 |
| `ids=` | 报告里显示有意义的名称 |
| `data/*.py` | 数据与代码分离 |
| `case_id` | 每条数据的唯一标识 |

---

## 检查清单

- [ ] 能说出参数化相比复制粘贴的好处
- [ ] `data/accounts.py` 和 `test_auth.py` 分工清楚
- [ ] `pytest tests/test_inventory.py -v` 全部 PASSED
- [ ] `pytest` 全部 **21 passed**

全部勾完后进入第 5 课（conftest 与项目结构完善）。
