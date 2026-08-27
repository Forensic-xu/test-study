# 第 6 课 · 测试数据 JSON 外置

**状态：进行中**  
**代码目录**：`mall-admin-test/api-test/`  

---

## 学习目标

1. 知道为什么要把用例数据从 `.py` 挪到 `.json`
2. 会用 `load_json()` 读 `data/json/` 下的文件
3. 改 JSON 就能加场景，不用改测试逻辑

---

## 为什么要 JSON 外置？

第 4 课数据还在 `data/accounts.py` 这类 Python 文件里，能用，但有几个麻烦：

| 问题 | 说明 |
|------|------|
| 非开发不好改 | 测试同学改数据怕弄坏语法 |
| 和代码绑死 | 加一组登录失败场景还要动 `.py` |
| 不好给别人看 | Excel/JSON 更像「用例表」 |

企业里常见做法：

```text
用例逻辑（test_*.py）  +  用例数据（json/yaml/excel）
```

本课用 **JSON**（简单、Python 自带、不用多装库）。

---

## 本课改了什么

### 1. 新增 JSON 文件

```text
data/json/
├── login_error_cases.json      # 登录失败 4 组
└── inventory_cases.json        # 库存非法数量 + 增减成功
```

打开 `login_error_cases.json` 看一眼，就是一个数组，每项一组场景。

### 2. 加载工具 `common/case_loader.py`

```python
from common.case_loader import load_json

LOGIN_ERROR_CASES = load_json("login_error_cases.json")
```

### 3. `data/accounts.py` / `data/inventory_cases.py` 变成「入口」

测试文件 **不用改 import**：

```python
from data.accounts import LOGIN_ERROR_CASES  # 照旧
```

里面其实已经是从 JSON 读出来的了。

---

## 你怎么验证

### 1. 后端开着，跑全部

```bat
cd /d D:\code\test-study\mall-admin-test\api-test
.\.venv1\Scripts\activate
pytest -v
```

期望：和之前一样 **28 passed**（数据源换了，条数不变）。

### 2. 动手改 JSON（本课练习）

在 `login_error_cases.json` 里加一组（或先改密码期望，看会不会 FAILED）：

例如把 `wrong_password` 的 `expected_code` 故意改成 `99999`，再跑：

```bat
pytest tests/test_auth.py::test_login_error_cases -v
```

应看到 FAILED → 改回 `10002` → 再 PASSED。

这就证明：**数据在 JSON 里管，代码逻辑没动。**

---

## 和 Postman 的对应

| Postman | 本课 |
|---------|------|
| 环境变量 / 多个请求 Body | `data/json/*.json` |
| 集合里复制 N 个失败请求 | 参数化 + JSON 多行 |
| 改 Body 再 Send | 改 JSON 再 `pytest` |

---

## 本课记住

1. **逻辑和数据分开**：`test_*.py` 管怎么测，JSON 管测什么数据  
2. **`load_json`**：统一从 `data/json/` 读  
3. **加场景**：优先改 JSON，不要复制一整段测试函数  

---

## 检查清单

- [ ] 知道 `data/json/` 里放什么
- [ ] 能解释 `accounts.py` 为什么还要保留（兼容 import）
- [ ] `pytest` 仍全部通过
- [ ] 故意改错一个 JSON 期望，能看到 FAILED，再改回

全部勾完后进入第 7 课（接口关联：登录→创建→删除）。
