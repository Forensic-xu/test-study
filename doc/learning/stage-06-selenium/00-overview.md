# 阶段 6 · Selenium UI 自动化（概览）

**状态：第 1～5 课已完成**  
**对应总计划**：`doc/测试学习计划.md` 第十六～十七章

---

## 阶段目标

对 `mall-admin-test` 前端做 UI 自动化，项目已预留 `data-testid` 便于定位。

> Pytest 验接口「对不对」；Selenium 验页面「点得通不通」。

## 和前后阶段的关系

| 阶段 | 关系 |
|------|------|
| Postman / Pytest | 已会登录接口与断言；UI 测同一套账号 |
| JMeter（已完） | 性能；与 UI 自动化分开 |
| 手工测试 | 知道页面流程后再自动化 |

## 学习路线

```text
浏览器启动 → 元素定位 → 点击/输入 → 等待 → 截图
→ XPath/CSS → 显式等待 → Page Object Model
```

## 课时列表

| 课时 | 主题 | 笔记 | 状态 |
|------|------|------|------|
| 01 | 第一个 test_login | [lesson-01-first-login.md](./lesson-01-first-login.md) | ✅ |
| 02 | 元素定位与显式等待 | [lesson-02-locators-waits.md](./lesson-02-locators-waits.md) | ✅ |
| 03 | test_product 商品管理 | [lesson-03-products.md](./lesson-03-products.md) | ✅ |
| 04 | test_order 订单流程 | [lesson-04-orders.md](./lesson-04-orders.md) | ✅ |
| 05 | Page Object 封装 | [lesson-05-page-object.md](./lesson-05-page-object.md) | ✅ |

## 参考文档

- `mall-admin-test/docs/selenium-guide.md`

## 产出物

- `mall-admin-test/ui-test/` + 至少登录/商品/订单脚本

## 前置条件

- [x] 熟悉登录页与账号（Postman / 手工）
- [x] 前端 `5176` + 后端 `8080` 可访问
- [x] 本机已装 Chrome
- [x] `ui-test` 依赖已安装（第 1 课）
