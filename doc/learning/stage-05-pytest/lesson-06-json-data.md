# 第 6 课学习笔记 · 测试数据放到 JSON 里（小白版）

> 适合：刚接触 Pytest，会一点 Postman 的人  
> 代码目录：`mall-admin-test/api-test/`  
> 状态：代码已改好，需启动后端后跑 `pytest` 验证

---

## 一句话说明白

以前：测试数据和测试代码写在一起（`.py` 文件里）。  
现在：测试数据放到 `.json` 文件里，测试代码只负责「怎么发请求、怎么断言」。

就像：

| 角色 | 对应 |
|------|------|
| 考卷上的题目内容 | JSON 数据 |
| 答题步骤 / 判分规则 | `test_*.py` 代码 |

改题目（改数据）不用重写答题步骤（不用大改代码）。

---

## 本课完成了什么

### 1. 新增了两个 JSON 文件

路径：

```text
api-test/data/json/
├── login_error_cases.json      ← 登录失败的 4 种情况
└── inventory_cases.json        ← 库存相关的测试数据
```

打开 `login_error_cases.json`，大概长这样（简化理解）：

```json
[
  {
    "case_id": "wrong_password",
    "username": "admin",
    "password": "wrong",
    "expected_http": 401,
    "expected_code": 10002
  }
]
```

每一组就是一条失败场景：用什么账号、期望 HTTP 多少、业务 code 多少。

### 2. 写了一个读 JSON 的小工具

文件：`common/case_loader.py`

核心就一句思路：

```python
load_json("login_error_cases.json")
# → 自动去 data/json/ 目录读文件，变成 Python 的 list/dict
```

你不用记复杂语法，知道「数据从 JSON 读进来」就行。

### 3. 原来的 `data/accounts.py` 变「入口」

测试文件还是这样写（没变）：

```python
from data.accounts import LOGIN_ERROR_CASES
```

但 `accounts.py` 里面不再手写一大坨列表，而是：

```python
LOGIN_ERROR_CASES = load_json("login_error_cases.json")
```

好处：测试代码不用大改，数据源悄悄换成 JSON 了。

---

## 和你之前学过的怎么对应

| 你已经会的 | 本课对应 |
|------------|----------|
| Postman 里改 Body 再 Send | 改 JSON 再跑 pytest |
| Postman 环境变量 | `config/settings.py` / `.env` |
| Postman Tests 断言 | `assert_api_success` / `assert_api_error` |
| 第 4 课参数化（一组逻辑测多组数据） | 多组数据现在住在 JSON 里 |

所以本课不是新技能「从零学 JSON」，而是：

> **把参数化用的那堆数据，搬家到 JSON 文件。**

---

## 项目里现在怎么分工（很重要）

```text
tests/test_auth.py     → 测什么流程（发登录、断言）
data/json/*.json       → 用什么数据（账号、期望码）
common/case_loader.py  → 怎么把 JSON 读进来
api/*.py               → 怎么发 HTTP 请求
```

小白记口诀：

1. **改数据** → 找 `data/json/`
2. **改断言/流程** → 找 `tests/`
3. **改请求地址/封装** → 找 `api/`

---

## 你怎么自己验证（动手）

### 步骤 1：启动后端

保证：http://127.0.0.1:8080 能打开（Swagger 也行）

### 步骤 2：跑测试

PyCharm：右键 `tests` → Run pytest  

或终端：

```bat
cd /d D:\code\test-study\mall-admin-test\api-test
.\.venv1\Scripts\python.exe -m pytest -v
```

期望：还是大概 **28 passed**（数据搬家了，用例条数不变）。

### 步骤 3：故意改错数据（本课必做）

1. 打开 `data/json/login_error_cases.json`
2. 找到 `wrong_password`，把 `expected_code` 从 `10002` 改成 `99999`
3. 再跑登录相关用例
4. 应该 **FAILED**（期望和实际对不上）
5. 改回 `10002`，再跑 → **PASSED**

如果你完成这一步，就真正理解「数据外置」了。

---

## 常见疑问

### Q：为什么不直接删掉 accounts.py？

因为测试里已经写了 `from data.accounts import ...`。  
保留它当「入口」，改动小、不容易把项目搞坏。以后熟练了，也可以测试里直接 `load_json(...)`。

### Q：JSON 和 Python 列表有什么区别？

- JSON：纯数据文件，给人和工具看/改都方便  
- Python 列表：写在代码里，改错一个逗号就可能语法报错  

企业里测试数据常用 JSON / YAML / Excel。

### Q：后端没开为什么跑不了？

`conftest.py` 开头会检查后端。没开就直接退出，避免一堆 `ConnectionError`。  
这是正常保护，不是你代码坏了。

---

## 本课你要能说出来的 3 句话（面试也用得上）

1. 我把接口自动化的测试数据和测试逻辑分开了。  
2. 数据放在 JSON 里，用参数化一批跑多组场景。  
3. 加失败用例时，优先改 JSON，不用复制一整段测试函数。

---

## 检查清单

- [ ] 知道 `data/json/` 是干什么的  
- [ ] 知道 `load_json` 大概干什么  
- [ ] 后端启动后 `pytest` 能过  
- [ ] 故意改错 JSON 能看到 FAILED，改回能过  

---

## 下一课预告

**第 7 课：接口关联**  
例如：登录 → 拿到 token → 创建资源 → 再用返回的 id 去删除。  
对应你在 Postman 里「上一步结果给下一步用」的那种串联。
