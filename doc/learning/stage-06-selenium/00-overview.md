# 阶段 6 · Selenium UI 自动化（概览）

**状态：未开始**  
**对应总计划**：`doc/测试学习计划.md` 第十六～十七章

---

## 阶段目标

对 `mall-admin-test` 前端做 UI 自动化，项目已预留 `data-testid` 便于定位。

## 学习路线

```text
浏览器启动 → 元素定位 → 点击/输入 → 等待 → 截图
→ XPath/CSS → 显式等待 → Page Object Model
```

## 计划课时（待建）

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | 第一个 test_login（打开页→输入→登录→验证） | 待建 |
| 02 | 元素定位与显式等待 | 待建 |
| 03 | test_product 商品管理 | 待建 |
| 04 | test_order 订单流程 | 待建 |
| 05 | Page Object 封装 | 待建 |

## 参考文档

- `mall-admin-test/docs/selenium-guide.md`

## 产出物

- `ui-test/` 目录 + 至少登录/商品/订单 3 个自动化脚本

## 前置条件

- 阶段 2 手工测试经验（知道页面流程）
- 阶段 4 Python 基础
