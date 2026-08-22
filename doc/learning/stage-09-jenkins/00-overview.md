# 阶段 9 · Jenkins CI/CD（概览）

**状态：未开始**  
**对应总计划**：`doc/测试学习计划.md` 第二十二章

---

## 阶段目标

搭建 Jenkins 流水线，代码提交或定时触发后自动执行 Pytest（及可选 Selenium / JMeter）。

## 学习范围

- Jenkins 安装与插件
- Pipeline / Freestyle 任务
- 拉取代码 → 安装依赖 → `pytest` → 报告归档
- 邮件/通知（可选）

## 计划课时（待建）

| 课时 | 主题 | 状态 |
|------|------|------|
| 01 | Jenkins 安装与第一个 Job | 待建 |
| 02 | 自动执行 Pytest | 待建 |
| 03 | 测试报告集成 | 待建 |
| 04 | 定时触发与通知 | 待建 |

## 产出物

- Jenkins Pipeline 配置文件
- 一键 CI 跑通接口自动化

## 前置条件

- 阶段 5 Pytest 框架完整可运行
