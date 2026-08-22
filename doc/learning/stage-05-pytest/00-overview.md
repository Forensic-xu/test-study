# 阶段 5 · Pytest + Requests 接口自动化（概览）

**状态：第 1 课进行中**  
**对应总计划**：`doc/测试学习计划.md` 第十三～十五章

---

## 阶段目标

搭建针对 `mall-admin-test` 的接口自动化框架，简历可写：

> **Python + Pytest + Requests 接口自动化测试框架**

## 学习路线

```text
Pytest 基础 → assert → fixture → 参数化 → conftest
→ 测试数据 → 接口关联 → 日志/报告 → 框架封装
```

## 计划课时

| 课时 | 主题 | 笔记 | 状态 |
|------|------|------|------|
| 01 | Pytest 入门 + 第一个 `test_login.py` | `lesson-01-first-test.md` | 待建 |
| 02 | assert 与响应断言模式 | `lesson-02-assertions.md` | 待建 |
| 03 | fixture（登录拿 token） | `lesson-03-fixture.md` | 待建 |
| 04 | 参数化多组数据 | `lesson-04-parametrize.md` | 待建 |
| 05 | conftest + 目录结构 | `lesson-05-conftest.md` | 待建 |
| 06 | 测试数据管理 | `lesson-06-test-data.md` | 待建 |
| 07 | 接口关联（登录→创建→删除） | `lesson-07-chaining.md` | 待建 |
| 08 | 日志与 HTML 报告 | `lesson-08-report.md` | 待建 |
| 09 | 框架封装 + `pytest` 一键执行 | `lesson-09-framework.md` | 待建 |

## 目标项目结构

建在 `mall-admin-test/api-test/`：

```text
api-test/
├── config/           # base_url、账号
├── common/           # 公共断言、工具
├── api/              # 接口封装（auth、product…）
├── data/             # 测试数据
├── tests/
│   ├── test_auth.py
│   ├── test_product.py
│   ├── test_inventory.py
│   ├── test_cart.py
│   └── test_order.py
├── conftest.py
├── pytest.ini
└── requirements.txt
```

## 用例迁移来源

- Postman 集合（阶段 3 已练接口）
- `mall-admin-test/docs/pytest-guide.md`

## 断言模板

```python
assert resp.status_code == 409
assert resp.json()["code"] == 20003
```

## 产出物

- `pytest` 一键执行全部用例
- 覆盖 auth / product / inventory / cart / order 核心场景

## 与后续阶段关系

→ 阶段 9 Jenkins：CI 中自动跑 Pytest  
→ 阶段 8 MeterSphere：平台化管理用例
