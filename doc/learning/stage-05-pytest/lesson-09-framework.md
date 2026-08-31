# 第 9 课 · 框架封装 + 一键执行（阶段 5 收尾）

**状态：已完成**  
**代码目录**：`mall-admin-test/api-test/`  
**前置**：第 1～8 课已完成

---

## 学习目标

1. 能 **说清** `api-test` 分层：config / common / api / data / tests  
2. 会用 **一条命令** 跑全量、冒烟、带报告  
3. 知道阶段 5 收尾后简历上怎么写这条成果

---

## 第 0 步：框架长什么样（背一张图）

```text
tests/          ← 用例：测什么、断言什么（不写 URL 细节）
  ↑ 调用
api/            ← 接口层：每个 API 一个函数（login、create_order…）
  ↑ 调用
common/         ← 公共：断言、登录、ApiClient、日志、读 JSON
  ↑ 读
config/         ← 地址、账号、超时（.env 可覆盖）
data/           ← 测试数据（JSON 外置）
conftest.py     ← fixture：token、client、后端检查
```

**口诀**：改数据找 `data/`，改请求找 `api/`，改用例找 `tests/`。

---

## 第 1 步：本课新增「一键入口」

| 方式 | 命令 | 干什么 |
|------|------|--------|
| 全量 | `python run.py` | 约 32 条 |
| 冒烟 | `python run.py smoke` | 3 条，验环境 |
| 报告 | `python run.py report` | 全量 + `htmlreport/report.html` |
| 双击 | `run_tests.bat` | 同 report |
| 冒烟 bat | `run_smoke.bat` | 同 smoke |

底层都是 **pytest**，只是不用记一长串参数。

---

## 第 2 步：你怎么跑

后端 `8080` 开着，在 `api-test` 目录：

```powershell
# PowerShell（已在 api-test 且 .venv1 已激活时）
python run.py smoke
python run.py
python run.py report
```

或：

```powershell
.\.venv1\Scripts\python.exe run.py smoke
```

---

## 第 3 步：阶段 5 你学会了什么（简历用）

> 基于 Pytest + Requests 搭建 mall-admin 接口自动化框架：分层封装 API、conftest 管理多用户 token、JSON 外置测试数据、接口关联与冒烟/全量/报告一键执行，覆盖登录、商品、库存、购物车、订单等核心场景。

---

## 检查清单

- [x] 能指着目录说出 config / common / api / data / tests 各干什么  
- [x] `python run.py smoke` → 3 passed  
- [x] `python run.py report` → 生成 `htmlreport/report.html`  
- [x] `python run.py` → 全量约 32 passed  

> 2026-08-31 验收：阶段 5 · Pytest 入门 9 课全部完成。

全部勾完 → **阶段 5 · Pytest 入门课结束**。

---

## 本课你要能说出来

1. 框架不是炫技，是 **好维护**：改接口只动 `api/`，改数据只动 `data/json/`。  
2. 一键执行 = `run.py` / bat 包一层 pytest，方便自己和以后 Jenkins。  
3. 阶段 5 做完，后面 Jenkins 只要把 `python run.py report` 写进流水线即可。  
