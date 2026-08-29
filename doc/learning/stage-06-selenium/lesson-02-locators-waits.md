# 第 2 课 · 元素定位与显式等待

**状态：已完成**  
**代码目录**：`mall-admin-test/ui-test/`  
**前置**：第 1 课登录两条用例已 PASSED

---

## 学习目标

1. 分清常见定位方式，本项目优先 **`data-testid` + CSS**  
2. 理解 **显式等待**（`WebDriverWait`）vs 隐式等待  
3. 用 pytest **`driver` fixture** 去掉每个用例里的 `try/finally`  
4. 把找元素 / 填表 / 点击抽到 `common/ui.py`（还不是 Page Object）

---

## 第 0 步：和第 1 课比，改了什么

```text
第 1 课：每个 test 里自己 new Chrome → try → quit
第 2 课：conftest 提供 driver → 用例只写业务步骤
         common/ui.py 统一：等待可见 / 可点 / 填写
```

用例变短，出错时也好改一处。

---

## 第 1 步：定位怎么选（先记结论）

| 方式 | 例子 | 本项目建议 |
|------|------|------------|
| CSS + data-testid | `[data-testid="login-submit"]` | **首选** |
| CSS class | `.el-button--primary` | 易碎，Element Plus 常变 |
| XPath | `//button[text()='登录']` | 文本改了就挂，少用 |
| id / name | `#username` | 本前端几乎没有 |

本课封装：

```python
by_testid("login-username")
# → (By.CSS_SELECTOR, '[data-testid="login-username"]')
```

登录页输入框：**testid 在原生 `<input>` 上**，直接点它，不要再套 ` input`。

---

## 第 2 步：显式等待（必会）

页面是异步的：点登录后 Dashboard **稍后才出现**。  
`find_element` 立刻找 → 经常失败。

```python
WebDriverWait(driver, 10).until(
    EC.visibility_of_element_located((By.CSS_SELECTOR, '[data-testid="dashboard-page"]'))
)
```

| 条件 | 含义 | 本课封装 |
|------|------|----------|
| `visibility_of_element_located` | 出现且可见 | `wait_visible` |
| `element_to_be_clickable` | 可见且可点 | `wait_clickable` |

**不要**再开 `implicitly_wait` 和显式等待混用（本项目 `driver` fixture 里已设为 `0`）。

---

## 第 3 步：看三个新/改文件

```text
ui-test/
├── conftest.py          ← driver fixture（开浏览器 / 关浏览器）
├── common/
│   └── ui.py            ← by_testid / wait_* / fill_* / click_*
└── tests/
    └── test_login.py    ← 用例变短，参数里写 driver
```

### `conftest.py` 核心

```python
@pytest.fixture
def driver():
    drv = webdriver.Chrome(...)
    yield drv      # 把浏览器交给用例
    drv.quit()     # 用例结束（成功或失败）都关掉
```

用例写成：

```python
def test_login_success(driver):   # 参数名 = fixture 名
    driver.get(...)
```

### `common/ui.py` 常用函数

| 函数 | 作用 |
|------|------|
| `fill_by_testid(driver, "login-username", "admin")` | 等可见 → Ctrl+A 清空 → 输入 |
| `click_by_testid(driver, "login-submit")` | 等可点 → 点击 |
| `wait_visible(driver, "dashboard-page")` | 等到元素出现 |

---

## 第 4 步：运行（确认重构没坏）

前后端仍要开着，然后：

```bash
cd d:\code\test-study\mall-admin-test\ui-test
pytest
```

期望：

```text
tests/test_login.py::test_login_success PASSED
tests/test_login.py::test_login_wrong_password PASSED
```

---

## 检查清单

- [x] 知道为什么优先 `data-testid`，而不是 class / 按钮文字  
- [x] 能说出显式等待解决什么问题  
- [x] 理解 `def test_xxx(driver):` 里的 `driver` 从哪来  
- [x] `pytest` 仍 2 passed  

> 2026-08-29 验收：PyCharm / 命令行均可 2 passed。

全部勾完后进入第 3 课（商品管理页面）。

---

## 本课你要能说出来

1. UI 自动化定位首选稳定属性（本项目是 `data-testid`）。  
2. 异步页面用 **WebDriverWait + EC**，别瞎 `sleep`。  
3. `driver` fixture = 准备浏览器 + 用完关掉（对标 Pytest 里的 token fixture）。  
4. 公共操作进 `common/ui.py`；**完整 Page Object 留到第 5 课**。  
