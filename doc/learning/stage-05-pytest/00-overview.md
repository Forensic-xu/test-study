# 阶段 5 · Pytest + Requests 接口自动化（概览）

**状态：第 1～9 课已完成（阶段 5 收尾）**  
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
| 01 | Pytest 入门 + 第一个登录用例 | [lesson-01-first-test.md](./lesson-01-first-test.md) | ✅ |
| 02 | assert 与响应断言模式 | [lesson-02-assertions.md](./lesson-02-assertions.md) | ✅ |
| 03 | fixture（登录拿 token） | [lesson-03-fixture.md](./lesson-03-fixture.md) | ✅ |
| 04 | 参数化多组数据 | [lesson-04-parametrize.md](./lesson-04-parametrize.md) | ✅ |
| 05 | conftest + 目录结构 | [lesson-05-conftest.md](./lesson-05-conftest.md) | ✅ |
| 06 | 测试数据 JSON 外置 | [lesson-06-json-data.md](./lesson-06-json-data.md) | ✅ |
| 07 | 接口关联（登录→创建→删除） | [lesson-07-chaining.md](./lesson-07-chaining.md) | ✅ |
| 08 | 日志与 HTML 报告 | [lesson-08-report.md](./lesson-08-report.md) | ✅ |
| 09 | 框架封装 + 一键执行 | [lesson-09-framework.md](./lesson-09-framework.md) | ✅ |

## 产出物

- `mall-admin-test/api-test/` + `pytest` 一键跑通
- 覆盖 auth / product / inventory / cart / order + 关联链

## 与后续阶段关系

→ 阶段 9 Jenkins：CI 中自动跑 Pytest  
→ 阶段 8 MeterSphere：平台化管理用例
