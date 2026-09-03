# 第 3 课 · Jenkinsfile Pipeline

**状态：✅ 已完成（构建 #2 SUCCESS）**  
**目标**：把构建步骤写进 **Jenkinsfile**，用 Pipeline 任务跑通全量测试 + 归档报告

---

## 学习目标

1. 理解 **Freestyle** vs **Pipeline**（配置在界面 vs 配置在代码里）
2. 会写最简 **Jenkinsfile**（Windows `bat` 步骤）
3. 新建 **Pipeline** 类型 Job 并跑绿
4. 知道 Jenkinsfile 可以提交进 Git，团队共享同一条流水线

---

## Freestyle vs Pipeline

| | Freestyle（第 1～2 课） | Pipeline（本课） |
|---|------------------------|------------------|
| 配置存在哪 | Jenkins 网页里点 | **Jenkinsfile** 文件 |
| 版本管理 | 难备份 | 跟代码一起 commit |
| 阶段展示 | 一整块日志 | **分 stage**（Test / Archive） |
| 简历说法 | 会配 Jenkins Job | 会写 **Jenkins Pipeline** |

---

## 课前准备

- [ ] 第 2 课已绿（32 passed + report.html）
- [ ] **MySQL + 后端 8080** 已启动
- [ ] Python 路径仍是：  
  `C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe`

---

## Step 1：认识仓库里的 Jenkinsfile

本课已在项目中添加：

```text
mall-admin-test/api-test/Jenkinsfile
```

内容大意：

```text
stage('API Test')     → run_ci.bat report
stage('Archive Report') → 归档 report.html
```

---

## Step 2：新建 Pipeline Job（不用改旧 Job）

1. Jenkins 首页 → **新建 Item**
2. 名称：`mall-api-test-pipeline`
3. 类型选 **流水线**（Pipeline）→ 确定
4. 往下滚到 **流水线**（Pipeline）区域

### 方式 A：先粘贴脚本（推荐入门，不依赖 GitHub）

**定义** 选：**Pipeline script**

把下面整段粘进 **脚本** 框：

```groovy
pipeline {
    agent any

    environment {
        CI_PYTHON = 'C:\\Users\\Administrator\\AppData\\Local\\Programs\\Python\\Python312\\python.exe'
        API_TEST  = 'D:\\code\\test-study\\mall-admin-test\\api-test'
    }

    stages {
        stage('API Test') {
            steps {
                bat """
                    chcp 65001 >nul
                    cd /d ${env.API_TEST}
                    call run_ci.bat report
                    if not exist "${env.WORKSPACE}\\htmlreport" mkdir "${env.WORKSPACE}\\htmlreport"
                    copy /Y "${env.API_TEST}\\htmlreport\\report.html" "${env.WORKSPACE}\\htmlreport\\report.html"
                """
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'htmlreport/report.html', fingerprint: true
        }
    }
}
```

> 仍用本机路径 `API_TEST`，和你在第 2 课方案 B 一致，避开 GitHub 不稳定。

5. **保存** → **立即构建**

### 方式 B：从 Git 读 Jenkinsfile（GitHub 稳定后用）

**定义** 选：**Pipeline script from SCM**

| 项 | 值 |
|----|-----|
| SCM | Git |
| URL | `https://github.com/Forensic-xu/test-study.git` |
| 分支 | `*/main` |
| Script Path | `mall-admin-test/api-test/Jenkinsfile` |

需先把 `Jenkinsfile` push 到 GitHub。

---

## Step 3：看 Pipeline 界面

构建开始后，点 **#1** → 左侧 **流水线**（Pipeline Steps / Stage View）

应看到两个阶段：

```text
API Test  ──绿──▶  (post) Archive
```

点 **API Test** 可展开看 `bat` 日志。

---

## Step 4：验收

| 项 | 期望 |
|----|------|
| 构建结果 | **SUCCESS**（蓝球） |
| 控制台 | `32 passed` |
| 构建产物 | `htmlreport/report.html` 可下载 |
| Stage View | `API Test` 绿色 |

---

## Jenkinsfile 逐行说明（仓库版）

打开 `mall-admin-test/api-test/Jenkinsfile`：

| 关键字 | 含义 |
|--------|------|
| `pipeline { }` | 声明式流水线 |
| `agent any` | 任意可用节点执行（本机就一个） |
| `environment { }` | 环境变量，`CI_PYTHON` 给 `run_ci.bat` |
| `stage('API Test')` | 一个阶段，界面上单独显示 |
| `bat """ ... """` | Windows 批处理（Linux 用 `sh`） |
| `archiveArtifacts` | 归档报告，等同第 2 课「构建后操作」 |

---

## 常见问题

### Pipeline 语法报错

- 引号用英文 `'` `"`
- 路径里的 `\` 在 Groovy 字符串里写成 `\\`

### 和 Freestyle Job 的关系

- 旧的 **`mall-api-test-smoke`** 可以保留作对比
- 新任务 **`mall-api-test-pipeline`** 专门跑 Pipeline
- 熟练后可删 Freestyle，只留 Pipeline

### GitHub 又连不上

- 继续用 **方式 A**（脚本粘贴在 Jenkins 里）
- 或 push Jenkinsfile 后等网络恢复再用 **方式 B**

---

## 检查清单

- [ ] 新建 Pipeline Job `mall-api-test-pipeline`
- [ ] 粘贴 Pipeline 脚本或配置 SCM + Jenkinsfile
- [ ] 构建 SUCCESS，32 passed
- [ ] 能下载 `report.html`
- [ ] 能说出 Freestyle 和 Pipeline 的区别

全部勾完后回复 **「Jenkins 03 绿了」**，进入第 4 课（定时构建 / 可选通知）。

---

## 简历加一句

> 编写 Jenkins Pipeline（Jenkinsfile），分阶段执行 Pytest 回归并自动归档 HTML 测试报告。
