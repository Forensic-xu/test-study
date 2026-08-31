# 第 2 课 · Git 拉代码 + 全量报告 + 归档

**状态：✅ 已完成**  
**目标**：Jenkins 从 GitHub 拉代码 → 跑全量 Pytest → 生成并归档 HTML 报告

---

## 学习目标

1. 给 Job 配置 **Git 源码管理**（不再写死 `D:\code\...` 路径）
2. 构建命令改为 **`run_ci.bat report`**（约 32 条用例 + HTML）
3. **构建后操作** 归档 `report.html`，在 Jenkins 里下载查看
4. 理解 `%WORKSPACE%` 环境变量

---

## 和上一课的区别

| 项目 | 第 1 课 | 第 2 课（本课） |
|------|---------|----------------|
| 代码来源 | 本机固定路径 | **Git 拉取** |
| 测试范围 | `smoke`（3 条） | **`report`（全量 + HTML）** |
| 产物 | 无 | **归档 HTML 报告** |
| 路径 | `D:\code\test-study\...` | `%WORKSPACE%\mall-admin-test\api-test` |

---

## 课前准备

- [ ] 第 1 课 Job 已能绿（smoke）
- [ ] **MySQL + 后端 8080** 已启动
- [ ] 本地改动已 **push 到 GitHub**（Jenkins 拉的是远程仓库，不是你家 D 盘那份）  
  - 远程：`https://github.com/Forensic-xu/test-study.git`  
  - 分支：`main`

> 若还没 push：在项目根目录执行 `git add` → `git commit` → `git push`。  
> 至少要有 `mall-admin-test/api-test/run_ci.bat` 和测试代码在远程。

---

## Step 1：配置 Git 源码管理

1. 打开 Job **`mall-api-test-smoke`**（或新建 **`mall-api-test-report`** 也行）
2. 点 **配置**（Configure）
3. 找到 **源码管理**（Source Code Management）
4. 选 **Git**
5. 填写：

| 项 | 值 |
|----|-----|
| Repository URL | `https://github.com/Forensic-xu/test-study.git` |
| Branches to build | `*/main` |
| Credentials | 公开仓库可留空；私有仓库选 GitHub 账号 |

6. **先不要保存**，继续改构建步骤（下面 Step 2 一起保存）

### 私有仓库怎么办？

GitHub → Settings → Developer settings → Personal access token，在 Jenkins **Manage Credentials** 里添加 Username + Token。

---

## Step 2：改构建命令（用 WORKSPACE）

删掉第 1 课里写死的 `D:\code\test-study\...`，换成：

**构建** → **Execute Windows batch command**：

```bat
@echo off
chcp 65001 >nul
echo [CI] WORKSPACE=%WORKSPACE%
cd /d "%WORKSPACE%\mall-admin-test\api-test"

REM Jenkins 服务账号 SYSTEM 默认找不到 python，必须写完整路径
set CI_PYTHON=C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe

call run_ci.bat report
```

说明：

- `%WORKSPACE%` = Jenkins 本次构建的工作目录（拉代码的位置）
- `report` = 全量 pytest + 生成 `htmlreport/report.html`

---

## Step 3：构建后归档 HTML 报告

往下滚到 **构建后操作**（Post-build Actions）→ **增加构建后操作** → **Archive the artifacts**

| 项 | 值 |
|----|-----|
| Files to archive | `mall-admin-test/api-test/htmlreport/report.html` |

勾选 **Fingerprint** 可不要（单文件无所谓）。

> 可选插件：**HTML Publisher**（装完后可在 Jenkins 页面内嵌打开报告）。本课用「归档 + 下载」就够。

---

## Step 4：保存并构建

1. **保存**
2. **立即构建**
3. 看 **Console Output**

### 预期控制台末尾

```text
32 passed
Finished: SUCCESS
```

（具体条数随用例增减，关键是全绿 + SUCCESS）

### 预期产物

构建页左侧 **Build Artifacts** 或 **上一次构建** 里能看到 **`report.html`**，点下载用浏览器打开。

---

## Step 5：验证 Git 真的拉到了代码

在 Console Output 前几行应看到类似：

```text
Cloning repository https://github.com/Forensic-xu/test-study.git
```

若报错 `Couldn't find any revision`：检查分支是不是 `main`，或远程有没有 push。

---

## 本课要记住的

| 概念 | 说明 |
|------|------|
| `%WORKSPACE%` | Jenkins 当前 Job 的工作目录，Git 代码拉在这里 |
| `report` 模式 | `run.py report` → 全量 + `htmlreport/report.html` |
| Archive artifacts | 把报告从工作区拷到 Jenkins 构建记录，可下载 |
| CI 前提 | 测接口 Job **仍需本机 MySQL + 后端**（下节课可再优化） |

---

## 常见问题

### 构建失败：SYSTEM 找不到 python（`report.html` 不存在）

Jenkins 以 **SYSTEM** 运行，PATH 里往往没有 `python`，`venv` 创建失败 → 测试没跑 → 归档报错。

**修复**：构建脚本里指定 Python 完整路径：

```bat
set CI_PYTHON=C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe
```

（你机器上可用 `where python` 查看；Jenkins Job 里用 **Execute Windows batch command** 写在 `call run_ci.bat` 之前。）

### 构建失败：找不到 `run_ci.bat`

- 远程仓库没有该文件 → 本地 commit + push
- 路径错 → 确认是 `%WORKSPACE%\mall-admin-test\api-test`

### 构建失败：pip / python 找不到

`run_ci.bat` 会自动用 `.venv1`，没有则用系统 `python` 并 `pip install`。  
Jenkins 服务账号的 PATH 里要有 Python（你本机已装 3.14 一般没问题）。

### 测试全红：连不上后端

Jenkins 和你在同一台机器 → 后端仍要 `http://127.0.0.1:8080` 先启动。

### 没有 Build Artifacts

- 构建必须 **SUCCESS** 且测试生成了 `htmlreport/report.html`
- 检查归档路径是否写对（相对 Job 工作区根目录）

### 想改 Job 名字

可把 `mall-api-test-smoke` 改成 `mall-api-test-ci`，或新建一个 Job 专门跑 report。

---

## 检查清单

- [x] 构建用 `run_ci.bat report` + `CI_PYTHON`（本机路径方案亦可）
- [x] 构建后归档 `htmlreport/report.html`
- [x] 构建 SUCCESS，能下载 HTML 报告（**32 passed**）
- [x] 理解 Git 拉取失败多为网络问题，可先用本机路径完成 CI

全部勾完后回复 **「Jenkins 02 绿了」**，进入第 3 课：**Jenkinsfile Pipeline**。

---

## 简历加一句

> 使用 Jenkins 关联 GitHub 仓库，自动执行 Pytest 接口回归并归档 HTML 测试报告。
