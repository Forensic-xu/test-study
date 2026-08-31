# 学习进度跟踪（全阶段）

> 打勾规则：自己做通并理解后，把 `[ ]` 改成 `[x]`。  
> 最后更新：2026-08-31 下班（Jenkins 01～02 ✅，03 待做）

> 跨对话续课请引用：[记忆.md](./记忆.md)

---

## 总览

| 阶段 | 主题 | 进度 | 状态 |
|------|------|------|------|
| 0 | 项目理解 | 1/4 | 补课中 |
| 1 | 测试基础 | 0/6 | 未开始 |
| 2 | Web 手工测试 | 0/5 | 未开始 |
| 3 | Postman | 10/10 | ✅ 入门课完成 |
| 4 | Python | 0/6（精简路线） | 与阶段 5 合并 |
| 5 | Pytest + Requests | 9/9 | ✅ 入门课完成 |
| 6 | Selenium | 5/5 | ✅ 入门课完成 |
| 7 | JMeter | 5/5 | **入门课完成** |
| 8 | MeterSphere | 0/— | 未开始 |
| 9 | Jenkins | 2/4 | **进行中** |
| 10 | 简历面试 | 0/— | 未开始 |

---

## 阶段 0 · 项目理解

笔记：[stage-00-project/00-overview.md](./stage-00-project/00-overview.md)

| 项 | 状态 |
|----|------|
| 能独立启动 MySQL / 后端 / 前端 | [ ] |
| 理解前后端 + DB 架构 | [ ] |
| 知道 JWT 作用 | [x]（Postman 已实践） |
| 能在 Navicat 看到 `mall_test`（端口 **3307**） | [ ] |
| 能打开 Swagger 对照接口 | [ ] |

---

## 阶段 1 · 软件测试基础

笔记：[stage-01-testing-basics/00-overview.md](./stage-01-testing-basics/00-overview.md)

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | 测试生命周期 + 测试分类 | [ ] 未开始 |
| 02 | 等价类划分 | [ ] 未开始 |
| 03 | 边界值分析 | [ ] 未开始 |
| 04 | 场景法 | [ ] 未开始 |
| 05 | 判定表 | [ ] 未开始 |
| 06 | 用 mall-admin-test 写第一份用例 | [ ] 未开始 |

---

## 阶段 2 · Web 功能测试

笔记：[stage-02-web-manual/00-overview.md](./stage-02-web-manual/00-overview.md)

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | 登录模块手工测试 | [ ] 未开始 |
| 02 | 商品管理模块测试 | [ ] 未开始 |
| 03 | 缺陷报告编写 | [ ] 未开始 |
| 04 | 订单流程手工测试 | [ ] 未开始 |
| 05 | 模块测试总结 | [ ] 未开始 |

---

## 阶段 3 · Postman 接口测试

笔记：[stage-03-postman/00-overview.md](./stage-03-postman/00-overview.md)

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | 登录 POST `/api/auth/login` | [x] 已完成 |
| 02 | 后置脚本存 Token + Bearer + GET `/api/users` | [x] 已完成 |
| 03 | Query / Path / 404（商品列表与详情） | [x] 已完成 |
| 04 | POST 创建分类 + 重复名 409 | [x] 已完成 |
| 05 | PUT/DELETE + 删除有商品分类 409 | [x] 已完成 |
| 06 | 库存增加/减少 + 超库存 409 | [x] 已完成 |
| 07 | 购物车 + 用户数据隔离 403 | [x] 已完成 |
| 08 | 下单 / 取消 / 状态机 / 重复取消 | [x] 已完成 |
| 09 | Collection Runner 串联整条业务流 | [x] 已完成 |
| 10 | 整理集合命名 + 导出备份（简历素材） | [x] 已完成 |

### 阶段 3 已掌握能力

- [x] GET / POST / PUT / DELETE
- [x] 环境变量 `{{base_url}}`、Bearer Token
- [x] JSON Body、HTTP Status + code/message/data
- [x] Tests 后置脚本、`pm.environment.set`
- [x] Query Params、Path Params
- [x] 404 / 409 异常场景（20006 / 20007 / 50002 等）
- [x] 库存增减、购物车 CRUD、用户隔离 403
- [x] 订单创建/取消/支付/发货/完成、状态机、重复取消与非法状态 409
- [x] Collection Runner 串联（`08-Smoke-Flow` 7 步全绿）
- [x] 集合导出与简历整理

---

## 阶段 4 · Python

笔记：[stage-04-python/00-overview.md](./stage-04-python/00-overview.md)

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | 变量、字符串、数字 | [ ] 未开始 |
| 02 | 列表、字典 | [ ] 未开始 |
| 03 | if / for / while | [ ] 未开始 |
| 04 | 函数与异常 | [ ] 未开始 |
| 05 | JSON、模块、文件 | [x] 与 Pytest 第 6 课合并 |
| 06 | requests 发 HTTP 请求 | [x] 与 Pytest 合并完成 |

---

## 阶段 5 · Pytest + Requests

笔记：[stage-05-pytest/00-overview.md](./stage-05-pytest/00-overview.md)

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | Pytest 入门 + 第一个登录用例 | [x] 已完成 |
| 02 | assert 与响应断言模式 | [x] 已完成 |
| 03 | fixture（登录 token + 多用户） | [x] 已完成 |
| 04 | 参数化 @pytest.mark.parametrize | [x] 已完成 |
| 05 | conftest + 目录结构 | [x] 已完成 |
| 06 | 测试数据管理（JSON 外置） | [x] 已完成 |
| 07 | 接口关联（登录→创建→删除） | [x] 已完成 |
| 08 | 日志与测试报告 | [x] 已完成 |
| 09 | 框架封装 + 一键 pytest | [x] 已完成 |

---

## 阶段 7 · JMeter 性能测试

笔记：[stage-07-jmeter/00-overview.md](./stage-07-jmeter/00-overview.md)

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | 安装 + 第一个登录脚本 | [x] 已完成 |
| 02 | Token 提取 + 带鉴权压测 | [x] 已完成 |
| 03 | 商品列表并发阶梯 | [x] 已完成 |
| 04 | 指标解读与简单报告 | [x] 已完成 |
| 05 | setUp 只登录一次再压读接口 | [x] 已完成 |

---

## 阶段 6 · Selenium UI 自动化

笔记：[stage-06-selenium/00-overview.md](./stage-06-selenium/00-overview.md)

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | 第一个 test_login | [x] 已完成 |
| 02 | 元素定位与显式等待 | [x] 已完成 |
| 03 | test_product 商品管理 | [x] 已完成 |
| 04 | test_order 订单流程 | [x] 已完成 |
| 05 | Page Object 封装 | [x] 已完成 |

---

## 阶段 9 · Jenkins CI/CD

笔记：[stage-09-jenkins/lesson-01-install-first-job.md](./stage-09-jenkins/lesson-01-install-first-job.md)

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | 安装 + 第一个 Job（smoke） | [x] 已完成 |
| 02 | Git + 全量 report + 归档 | [x] 已完成 |
| 03 | Pipeline + Jenkinsfile | [ ] 进行中 |
| 04 | 定时/通知（可选） | [ ] 未开始 |

---

## 阶段 8～10 · 其他

| 阶段 | 笔记 | 状态 |
|------|------|------|
| 8 MeterSphere | [stage-08-metersphere/00-overview.md](./stage-08-metersphere/00-overview.md) | 未开始 |
| 9 Jenkins | [stage-09-jenkins/00-overview.md](./stage-09-jenkins/00-overview.md) | 进行中 |
| 10 简历面试 | [stage-10-resume/00-overview.md](./stage-10-resume/00-overview.md) | 未开始 |
