# 第 8 课 · 日志与 HTML 报告

**状态：已完成**  
**代码目录**：`mall-admin-test/api-test/`  
**前置**：第 7 课接口关联已绿

---

## 学习目标

1. 跑测试时 **有日志文件** 可查（`logs/pytest.log`）  
2. 会生成 **HTML 报告** 给同事/自己看结果  
3. 知道日志和报告各解决什么问题（调试 vs 留档）

---

## 第 0 步：为什么要日志 + 报告？

| 东西 | 解决什么 | 本课产出 |
|------|----------|----------|
| **日志** | 跑的时候发生了什么、哪条请求返回几 | `logs/pytest.log` |
| **HTML 报告** | 一眼看通过/失败、点开看详情 | `htmlreport/report.html` |

终端里 pytest 滚完就没了；日志和报告能 **事后翻**。

---

## 第 1 步：本课改了什么

```text
api-test/
├── common/log_config.py     ← 统一配置日志（文件 + 控制台）
├── conftest.py              ← 启动时 setup_logging；每条用例 PASSED/FAILED 记一笔
├── common/http_client.py    ← ApiClient 每个请求记一行 HTTP 状态码
├── run_tests.bat            ← 一键 pytest + 出 HTML 报告
└── logs/pytest.log          ← 跑完自动生成（已 gitignore）
```

日常 `pytest` **不会** 每次都出 HTML（避免变慢）；要报告时加参数或双击 bat。

---

## 第 2 步：看日志

后端开着，在 `api-test` 目录：

```bat
cd /d D:\code\test-study\mall-admin-test\api-test
.\.venv1\Scripts\python.exe -m pytest -q
```

跑完后打开 **`logs/pytest.log`**，应能看到类似：

```text
检查后端: http://127.0.0.1:8080
后端可达 HTTP 200
PASSED tests/test_auth.py::test_login_success
GET /api/products -> HTTP 200
...
```

---

## 第 3 步：生成 HTML 报告

**方式 A（推荐）**：双击 `api-test/run_tests.bat`

**方式 B**：命令行

```bat
.\.venv1\Scripts\python.exe -m pytest --html=htmlreport/report.html --self-contained-html
```

用浏览器打开 **`htmlreport/report.html`**：

- 绿色 = 通过  
- 红色 = 失败，可点进去看堆栈  

`--self-contained-html`：报告一个文件就能发，不依赖外部 css。

---

## 第 4 步：冒烟 + 报告（可选）

```bat
.\.venv1\Scripts\python.exe -m pytest -m smoke --html=htmlreport/smoke.html --self-contained-html
```

只跑带 `@pytest.mark.smoke` 的用例，适合「快速验环境」。

---

## 检查清单

- [x] 跑完能在 `logs/pytest.log` 里看到 PASSED/FAILED  
- [x] 能打开 `htmlreport/report.html` 并看懂通过数  
- [x] 知道日常 `pytest` 和「带 `--html`」的区别  
- [x] 全量仍约 **32 passed**  

全部勾完后进入第 9 课（框架封装收尾）。

---

## 本课你要能说出来

1. 日志给 **开发/自己排错**；HTML 报告给 **留档、汇报**。  
2. 日志在 `conftest` 里统一开，用例不用每条手写 `print`。  
3. HTML 报告用 `pytest-html`，不是 pytest 自带的。  
4. `logs/`、`htmlreport/` 已在 `.gitignore`，别提交到 Git。  
