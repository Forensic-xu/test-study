# 学习笔记总目录

> **被测系统（SUT）**：`mall-admin-test`  
> **总路线**：[`doc/测试学习计划.md`](../测试学习计划.md)  
> **AI 续课记忆**：[@记忆.md](./记忆.md)（新对话请引用）  
> **进度打卡**：[`progress.md`](./progress.md)

---

## 使用方式

1. 新对话开始 → 引用 `@doc/learning/记忆.md`，说「继续下一节课」
2. 查看当前阶段 → 打开对应 `stage-XX-*/00-overview.md`
3. 按 `lesson-XX-*.md` 一课一课做（有课时的阶段）
4. 做完在 [`progress.md`](./progress.md) 打勾
5. 重要节点更新 [`记忆.md`](./记忆.md)

---

## 目录结构（为后续阶段预留）

```text
doc/learning/
├── README.md              ← 本文件（总索引）
├── 记忆.md                ← AI 续课上下文（权威进度摘要）
├── progress.md            ← 全阶段详细打卡
│
├── stage-00-project/      ← 阶段 0：环境与项目理解
├── stage-01-testing-basics/   ← 阶段 1：软件测试基础（待开课）
├── stage-02-web-manual/       ← 阶段 2：Web 功能测试（待开课）
├── stage-03-postman/          ← 阶段 3：Postman 接口测试（进行中）
├── stage-04-python/           ← 阶段 4：Python 基础（待开课）
├── stage-05-pytest/           ← 阶段 5：Pytest + Requests（待开课）
├── stage-06-selenium/         ← 阶段 6：Selenium UI 自动化（待建课时）
├── stage-07-jmeter/           ← 阶段 7：JMeter 性能测试（待建课时）
├── stage-08-metersphere/      ← 阶段 8：MeterSphere 平台（待建课时）
├── stage-09-jenkins/          ← 阶段 9：Jenkins CI/CD（待建课时）
└── stage-10-resume/           ← 阶段 10：简历与面试（待建课时）
```

每个阶段文件夹约定：

| 文件 | 用途 |
|------|------|
| `00-overview.md` | 阶段目标、课时列表、产出物 |
| `lesson-XX-*.md` | 具体课时（随学随建） |

---

## 十阶段路线一览

| 阶段 | 主题 | 笔记目录 | 状态 | 最终产出 |
|------|------|----------|------|----------|
| 0 | 测试环境与项目理解 | [stage-00-project](./stage-00-project/00-overview.md) | 补课中 | 能独立运行项目 |
| 1 | 软件测试基础 | [stage-01-testing-basics](./stage-01-testing-basics/00-overview.md) | 未开始 | 能设计测试用例 |
| 2 | Web 功能测试 | [stage-02-web-manual](./stage-02-web-manual/00-overview.md) | 未开始 | 完整测一个模块 |
| 3 | Postman 接口测试 | [stage-03-postman](./stage-03-postman/00-overview.md) | **进行中** | 接口测试集合 |
| 4 | Python | [stage-04-python](./stage-04-python/00-overview.md) | 待开课 | 能写测试脚本 |
| 5 | Pytest + Requests | [stage-05-pytest](./stage-05-pytest/00-overview.md) | **进行中（第 6 课待开）** | 接口自动化框架 |
| 6 | Selenium | [stage-06-selenium](./stage-06-selenium/00-overview.md) | 未开始 | UI 自动化框架 |
| 7 | JMeter | [stage-07-jmeter](./stage-07-jmeter/00-overview.md) | 未开始 | 性能测试项目 |
| 8 | MeterSphere | [stage-08-metersphere](./stage-08-metersphere/00-overview.md) | 未开始 | 企业测试平台实践 |
| 9 | Jenkins | [stage-09-jenkins](./stage-09-jenkins/00-overview.md) | 未开始 | CI/CD 自动化 |
| 10 | 项目包装 + 面试 | [stage-10-resume](./stage-10-resume/00-overview.md) | 未开始 | 简历 + 面试话术 |

> **顺序不要乱**：Postman → Python → Pytest → Selenium → JMeter → MeterSphere → Jenkins

---

## 当前进度（摘要）

| 项 | 内容 |
|----|------|
| **Postman** | 第 1～7 课完成；第 8 课进行中 |
| **Pytest** | 第 1～5 课完成；**28 passed**；下次第 6 课 |
| **Git 备份** | `push-backup.bat` 已推 GitHub |
| **续课** | `@doc/learning/记忆.md` |

详见 → [`progress.md`](./progress.md) · [`记忆.md`](./记忆.md)

---

## 项目快捷信息

| 项 | 值 |
|----|-----|
| 前端 | http://127.0.0.1:5176 |
| 后端 | http://127.0.0.1:8080 |
| Swagger | http://127.0.0.1:8080/swagger-ui.html |
| MySQL | `127.0.0.1:3307` / root / 123456 / `mall_test` |
| ADMIN | admin / Admin@123 |
| USER | user01 / User@123 |
| Python（本机） | 3.8.10 |

### 项目文档索引

| 文档 | 路径 |
|------|------|
| API 概览 | `mall-admin-test/docs/api.md` |
| 错误码 | `mall-admin-test/docs/error-codes.md` |
| Postman 指南 | `mall-admin-test/docs/postman-guide.md` |
| Pytest 指南 | `mall-admin-test/docs/pytest-guide.md` |
| Selenium 指南 | `mall-admin-test/docs/selenium-guide.md` |
| 测试用例参考 | `mall-admin-test/docs/test-cases.md` |

---

## 学习方式（核心原则）

> **学一个知识 → 在 mall-admin-test 上实际做 → 产生测试资产 → 总结 → 面试表达**

Postman 用例 → Python 重写 → Pytest 参数化 / Fixture → 一键 `pytest` 执行。
