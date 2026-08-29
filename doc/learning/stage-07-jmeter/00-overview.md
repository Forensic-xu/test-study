# 阶段 7 · JMeter 性能测试（概览）

**状态：第 1～5 课完成**  
**对应总计划**：`doc/测试学习计划.md` 第十八～二十章

---

## 阶段目标

对 `mall-admin-test` 核心接口做性能测试，理解并发、TPS、响应时间、错误率。

> Postman/Pytest 验「对不对」；JMeter 验「快不快、稳不稳」。

## 和前后阶段的关系

| 阶段 | 关系 |
|------|------|
| Postman（已会） | 接口路径、登录、Token 直接复用 |
| Pytest（进行中） | 可并行；功能自动化 ≠ 性能 |
| Selenium（未开） | UI 自动化，和压测分开 |

## 学习范围

- 线程组、HTTP 请求、请求头、断言、监听器
- 登录 + Token 提取（JSON Extractor）
- 商品列表等读场景并发
- 看懂聚合报告里的指标

## 课时列表

| 课时 | 主题 | 笔记 | 状态 |
|------|------|------|------|
| 01 | 安装 + 第一个登录脚本 | [lesson-01-first-script.md](./lesson-01-first-script.md) | ✅ |
| 02 | Token 提取 + 带鉴权压测 | [lesson-02-token-and-auth.md](./lesson-02-token-and-auth.md) | ✅ |
| 03 | 商品列表并发阶梯 | [lesson-03-products-load.md](./lesson-03-products-load.md) | ✅ |
| 04 | 指标解读与简单报告 | [lesson-04-metrics-report.md](./lesson-04-metrics-report.md) | ✅ |
| 05 | setUp 只登录一次再压读接口 | [lesson-05-setup-once-login.md](./lesson-05-setup-once-login.md) | ✅ |

## 参考文档

- `mall-admin-test/docs/jmeter-guide.md`

## 建议压测接口（来自项目指南）

| 场景 | 接口 | 说明 |
|------|------|------|
| 登录 | `POST /api/auth/login` | 第 1～2 课 |
| 商品列表 | `GET /api/products` | 读多写少，适合并发 |
| 分类列表 | `GET /api/categories` | 轻量 |
| 创建订单 | `POST /api/orders` | 低并发，注意库存 |

## 产出物

- `mall-admin-test/jmeter/*.jmx`
- 简单性能测试结论（并发、响应时间、错误率）

## 前置条件

- [x] 熟悉登录等接口（Postman）
- [ ] 后端稳定运行
- [ ] 本机已装 JMeter（第 1 课完成）
