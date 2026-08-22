# 阶段 4 · Python 基础（概览）

**状态：待开课（精简路线：与阶段 5 第 1 课合并推进）**  
**对应总计划**：`doc/测试学习计划.md` 第十一～十二章

---

## 阶段目标

> **用 Python 写测试**，不是为了转 Python 开发。

掌握足够写接口自动化脚本的 Python 基础，并能用 `requests` 发 HTTP 请求。

## 学习策略（按用户情况调整）

**用户画像**：Python 会一点；习惯 **AI 辅助编程** 提效。

| 路线 | 适用 | 做法 |
|------|------|------|
| **A · 精简（推荐）** | 会一点 + 有 Postman 基础 | 跳过 01～05，直接 requests + 进 Pytest；语法只在报错时补 |
| B · 完整 | 完全零基础 | 按下面 6 课顺序系统学 |

精简路线下，阶段 4 与阶段 5 第 1 课 **合并推进**，目标：半天内跑通第一个 `test_login.py`。

### AI 辅助学测试的正确姿势

1. **你定场景**：例如「测错误密码登录 → 401 / 10002」（Postman 已会）
2. **AI 生成骨架**：requests + assert 代码
3. **你必须做**：运行、看报错、改断言、对照 `error-codes.md` 核对 code
4. **面试要能讲**：为什么这样断言、fixture 干什么、失败怎么排查

---

Django、Flask、异步、爬虫、机器学习

## 计划课时

| 课时 | 主题 | 笔记 | 状态 |
|------|------|------|------|
| 01 | 变量、字符串、数字 | `lesson-01-basics.md` | 待建 |
| 02 | 列表、字典 | `lesson-02-collections.md` | 待建 |
| 03 | if / for / while | `lesson-03-control-flow.md` | 待建 |
| 04 | 函数与异常 | `lesson-04-functions.md` | 待建 |
| 05 | JSON、模块、文件读写 | `lesson-05-json-modules.md` | 待建 |
| 06 | requests 发 HTTP（对标 Postman） | `lesson-06-requests.md` | 待建 |

## 环境与前置

- 本机 Python：**3.8.10**
- 建议虚拟环境建在：`mall-admin-test/api-test/`
- 后端需启动：`http://127.0.0.1:8080`

## Postman → Python 对照

| Postman | Python |
|---------|--------|
| `{{base_url}}` | `BASE_URL = "http://127.0.0.1:8080"` |
| Body JSON | `requests.post(url, json={...})` |
| Bearer Token | `headers={"Authorization": f"Bearer {token}"}` |
| Tests 断言 | `assert resp.status_code == 200` |

## 产出物

- 能独立写脚本调用 `POST /api/auth/login` 并打印 token
- 理解 assert 与异常处理

## 与后续阶段关系

→ 阶段 5 Pytest：把脚本组织成可批量运行的测试用例
