# 第 4 课 · 定时触发与通知

**状态：🔄 进行中**  
**目标**：让 Pipeline **自动定时跑**，并在构建完成后输出状态通知

---

## 学习目标

1. 理解 **cron 定时表达式**（Jenkins Pipeline 用的语法）
2. 会在 Jenkinsfile 里加 `triggers { cron(...) }`
3. 会扩展 `post { }` 块（success / failure / always 各做一件事）
4. 知道邮件/钉钉通知需要 SMTP / Webhook，本地 Jenkins 可能配不了

---

## 先理解：为什么需要定时 + 通知

| 场景 | 说明 |
|------|------|
| **夜间回归** | 晚上 22:00 自动跑全量，第二天早上看报告 |
| **代码入库触发** | GitHub/Gitee Webhook 一 push 就触发（需要外网可访问的 Jenkins） |
| **质量门禁** | 每次构建后自动邮件通知结果，不等人去点 Jenkins |

本课重点练 **cron 定时**（最简单，不需要任何外部服务）。

---

## Step 1：在 Jenkinsfile 里加 cron 触发

打开仓库里的 `mall-admin-test/api-test/Jenkinsfile`，在 `agent any` 后面加一行 `triggers`：

```groovy
pipeline {
    agent any

    // ===== 新增：定时触发（每 5 分钟跑一次，方便验证）=====
    triggers {
        cron('H/5 * * * *')
    }

    environment {
        // ... 不变 ...
    }
    // ... 后面不变 ...
}
```

### cron 表达式图解（5 位）

```
H/5  *  *  *  *
 │    │  │  │  │
 │    │  │  │  └─ 星期几（0=周日, 6=周六, * = 每天）
 │    │  │  └─── 月份（* = 每月）
 │    │  └────── 日期（1-31, * = 每天）
 │    └───────── 小时（0-23, * = 每小时）
 └────────────── 分钟（H/5 = 每 5 分钟）
```

### 常用 cron 表达式速查

| 你想什么时候跑 | 表达式 |
|---------------|--------|
| **每 5 分钟**（测试用） | `H/5 * * * *` |
| 每小时整点 | `H * * * *` |
| 每天早上 9:00 | `H 9 * * *` |
| 每天晚上 22:00 | `H 22 * * *` |
| 工作日早上 9 点 | `H 9 * * 1-5` |
| 每周一早上 9 点 | `H 9 * * 1` |

> **`H` 是什么？** Jenkins 推荐用 `H`（Hash）代替具体数字。Jenkins 会给每个 Job 算一个哈希值，这样多个 Job 不会在同一秒同时触发。你可以暂时理解为"某个随机但稳定的分钟数"。

---

## Step 2：扩展 post 块（构建后通知）

把当前的 `post { }` 块扩展一下：

```groovy
    post {
        always {
            echo "=== 构建完成：${currentBuild.result} ==="
            echo "报告地址：${env.BUILD_URL}artifact/mall-admin-test/api-test/htmlreport/report.html"
        }
        success {
            echo '✅ SUCCESS — 全部用例通过！'
        }
        failure {
            echo '❌ FAILURE — 请检查控制台日志！'
        }
        unstable {
            echo '⚠️ UNSTABLE — 有用例失败但构建没挂'
        }
    }
```

### post 各块什么时候执行

| 块 | 什么时候触发 | 典型用途 |
|----|-------------|---------|
| `always` | **永远执行**（不管成失败） | 打印总结、清理临时文件 |
| `success` | 构建成功 | 绿色 ✅ |
| `failure` | 构建失败（pytest 挂了 / bat 报错） | 红色 ❌ |
| `unstable` | 有用例失败但进程没退出 | pytest 里某个 assert 挂了但 pytest 本身跑完了 |
| `aborted` | 人工点"停止" | — |

---

## Step 3：邮件通知（可选 · 需要 SMTP）

Jenkins 自带 **邮件扩展插件**（Email Extension Plugin），但需要 **SMTP 服务器**。

### 检查你有没有 SMTP

- 公司内网一般有 SMTP 服务器 → **能配**
- 个人电脑 → **大概率没有**，跳过这步

### 如果有 SMTP，配置路径

**Jenkins 首页 → 管理 Jenkins → 系统 → 邮件通知**：

| 项 | 示例（QQ 邮箱） |
|----|----------------|
| SMTP 服务器 | `smtp.qq.com` |
| 用户默认邮箱后缀 | `@qq.com` |
| 使用 SMTP 认证 | ✅ 勾上 |
| 用户名 | `你的QQ号` |
| 密码 | **授权码**（不是 QQ 登录密码！QQ 设置→账户→开启 SMTP→拿授权码） |
| 使用 SSL | ✅ |
| SMTP 端口 | `465` |

然后在 Jenkinsfile 里加（在 `post` 块里）：

```groovy
success {
    echo '✅ SUCCESS'
    emailext(
        to: 'yourname@qq.com',
        subject: "✅ Jenkins SUCCESS - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        body: "报告地址：${env.BUILD_URL}artifact/mall-admin-test/api-test/htmlreport/report.html"
    )
}
failure {
    echo '❌ FAILURE'
    emailext(
        to: 'yourname@qq.com',
        subject: "❌ Jenkins FAILURE - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        body: "控制台日志：${env.BUILD_URL}console"
    )
}
```

### 钉钉/飞书 Webhook（进阶）

也可以用 Webhook 插件发钉钉/飞书群通知，**但需要装额外插件**。本节课先不做，简历里可以提"了解 Webhook 通知机制"。

---

## Step 4：改完 Jenkinsfile → commit → 等定时触发

```powershell
cd D:\code\test-study
git add mall-admin-test/api-test/Jenkinsfile
git commit -m "ci: add cron trigger + extended post block"
```

然后回到 Jenkins：

1. 打开 `mall-api-test-pipeline` Job
2. 点 **立即构建**（手动跑一次，确认 cron 没报错）
3. 跑完后看 **控制台**，应该能看到：
   ```
   ✅ SUCCESS — 全部用例通过！
   === 构建完成：SUCCESS ===
   报告地址：...
   ```
4. 然后等 **5 分钟**，Job 应该**自动触发**一次

> 💡 如果等不及 5 分钟，可以手动改 cron 表达式为 `H/1 * * * *`（每分钟），验证完再改回 `H/5`。

---

## 验收

| 项 | 期望 |
|----|------|
| Jenkinsfile 语法检查 | 无 Groovy 报错 |
| 手动构建 | SUCCESS，控制台打印 ✅ 消息 |
| 定时触发 | 5 分钟后自动出现新构建 |
| post 块 | 控制台末尾看到 always + success 的 echo |

---

## 检查清单

- [ ] Jenkinsfile 加了 `triggers { cron('H/5 * * * *') }`
- [ ] Jenkinsfile 加了扩展的 `post` 块（always / success / failure）
- [ ] commit 到 Git（让 SCM 拉到最新 Jenkinsfile）
- [ ] 手动构建 SUCCESS，控制台看到 ✅ 消息
- [ ] 等到定时触发自动跑了一次（或改成 H/1 验证）
- [ ] 能说出 cron 5 位表达式的含义
- [ ] 能说出 post 块各分支的区别
- [ ] 知道邮件通知需要 SMTP 服务器

---

## 简历加一句

> 配置 Jenkins Pipeline 定时触发（cron），每次构建自动归档 HTML 报告并输出构建状态通知。

---

## 下一步

Jenkins 01～04 全过后，阶段 9 完成。可以：
1. 跳到 **阶段 10 · 简历面试**
2. 或学 **MeterSphere**（阶段 8）做平台化测试管理
