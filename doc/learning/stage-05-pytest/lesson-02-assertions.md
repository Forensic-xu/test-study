# 第 2 课 · 公共断言 + 商品接口用例

**状态：已完成**  
**代码目录**：`mall-admin-test/api-test/`  
**对应 Postman**：阶段 3 · lesson-03-query-path-404

---

## 学习目标

1. 把重复的 `assert status_code + assert code` 抽成公共函数
2. 学会 **成功断言** 与 **失败断言** 两种模式
3. 用 `admin_token` fixture 发需要鉴权的 GET 请求
4. 新增 `test_product.py`（列表 / 详情 / 404）

---

## 第 1 步：理解两种断言模式

本项目每个接口都要看两层：

| 层 | 检查什么 | 成功函数 | 失败函数 |
|----|----------|----------|----------|
| HTTP | `resp.status_code` | `assert_api_success` | `assert_api_error` |
| 业务 | `body["code"]` | 同上 | 同上 |

### 成功场景

```python
body = assert_api_success(resp)
# 可选：继续断言 data 结构
assert_page_records(body, min_count=1)
```

### 失败场景

```python
body = assert_api_error(resp, expected_http=404, expected_code=20001)
```

**好处**：失败时 pytest 会打印「期望 vs 实际」，比手写两行 assert 清晰。

---

## 第 2 步：看公共断言文件

`common/assertions.py` 核心函数：

| 函数 | 用途 |
|------|------|
| `assert_api_success(resp)` | HTTP 200 + code 200 |
| `assert_api_error(resp, http, code)` | 错误场景 |
| `assert_page_records(body)` | 分页 `data.records` |
| `assert_data_id(body, id)` | 详情 `data.id` |
| `assert_data_has_keys(body, keys)` | data 含指定字段 |
| `auth_headers(token)` | Bearer 请求头 |

---

## 第 3 步：三层代码分工

```text
api/product_api.py     → 只负责发请求（list_products / get_product）
common/assertions.py   → 只负责断言
tests/test_product.py  → 组合「请求 + 断言 + 业务检查」
```

### 为什么要分层？

- 改 URL 只改 `api/`
- 改断言规则只改 `common/`
- 用例文件保持短、可读

---

## 第 4 步：fixture 复用 token

`tests/conftest.py` 里：

```python
@pytest.fixture(scope="session")
def admin_token():
    # 整个 pytest 会话只登录一次
    ...
```

用例里直接写参数 `admin_token`，pytest 自动注入：

```python
def test_product_list_success(admin_token):
    resp = list_products(admin_token, page=1, size=5, status="ON_SALE")
```

= Postman 里 Collection 继承 Bearer `{{token}}`。

---

## 第 5 步：运行

```bash
cd mall-admin-test/api-test
pytest tests/test_product.py -v
pytest   # 跑全部（auth + product）
```

期望：**8 passed**（5 条 auth + 3 条 product）

---

## 本课新增用例

| 用例 | 接口 | 期望 |
|------|------|------|
| 商品列表 | GET `/api/products?page=1&size=5&status=ON_SALE` | 200 / 200，records 非空 |
| 商品详情 | GET `/api/products/1` | 200 / 200，data.id=1 |
| 不存在 | GET `/api/products/999999` | 404 / 20001 |

---

## 检查清单

- [ ] 能说出 `assert_api_success` 和 `assert_api_error` 区别
- [ ] 知道 `admin_token` 从哪来（conftest 登录一次）
- [ ] `pytest tests/test_product.py -v` 3 条 PASSED
- [ ] `pytest` 全部 PASSED

全部勾完后进入第 3 课（fixture 深入 + 多用户 token）。
