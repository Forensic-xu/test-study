# 第 1 课 · Jenkins 安装与第一个 Job

**状态：✅ 已完成**  
**目标**：本机跑起 Jenkins，创建第一个 Job，自动执行 `api-test` 冒烟测试

---

## 学习目标

1. 理解 **CI** 是什么：代码/脚本固定步骤自动跑，不用每次手敲命令
2. 在 Windows 安装 **Jenkins LTS**
3. 创建 **Freestyle Job**，一键执行 `python run.py smoke`
4. 知道 Jenkins 与 mall-admin-test **后端端口冲突**怎么避

---

## 先理解：Jenkins 在你项目里干什么

```text
你点「立即构建」或 Git 提交触发
        ↓
Jenkins 按步骤执行：
  ① 进入 api-test 目录
  ② 用虚拟环境 Python
  ③ python run.py smoke（或 report）
        ↓
  成功 → 绿球  /  失败 → 红球
```

对应你已经在 PyCharm 里做的：

```powershell
cd D:\code\test-study\mall-admin-test\api-test
.\.venv1\Scripts\python.exe run.py smoke
```

Jenkins 就是把这条命令**交给机器定时/自动跑**。

---

## ⚠️ 端口冲突（必读）

| 服务 | 默认端口 |
|------|----------|
| mall-admin-test **后端** | **8080** |
| **Jenkins** | **8080**（默认） |

**不能两个同时占 8080。** 本课把 Jenkins 装到 **8081**。

- 测接口：`http://127.0.0.1:8080`（后端，照旧）
- 开 Jenkins：`http://127.0.0.1:8081`（本课）

---

## 课前准备

- [ ] **MySQL** 已启动（端口 **3307**）
- [ ] **后端** 已启动：`http://127.0.0.1:8080`
- [ ] 本地已能绿：`api-test` 下 `python run.py smoke` → **3 passed**
- [ ] 已安装 **JDK 17 或 21**（Jenkins 需要 Java）  
  - 终端执行 `java -version` 能看到版本号即可

---

## Step 1：安装 Jenkins（Windows）

### 方式 A：官方安装包（推荐）

1. 打开 https://www.jenkins.io/download/
2. 下载 **Windows** 安装包（LTS）
3. 安装过程中或安装后，把 HTTP 端口改成 **8081**  
   - 安装包若没提示：编辑  
     `C:\Program Files\Jenkins\jenkins.xml`  
     找到 `<arguments>` 里的 `--httpPort=8080`，改成 `--httpPort=8081`
4. 重启 Jenkins 服务：  
   - `Win + R` → `services.msc` → 找到 **Jenkins** → **重新启动**

### 方式 B：war 包（不用安装服务时）

```powershell
# 先下载 jenkins.war，再执行（端口 8081）
java -jar jenkins.war --httpPort=8081
```

---

## Step 2：解锁 Jenkins

1. 浏览器打开：**http://127.0.0.1:8081**
2. 按页面提示，打开初始密码文件（Windows 常见路径）：

```text
C:\Program Files\Jenkins\secrets\initialAdminPassword
```

3. 粘贴密码 → **继续**
4. 选 **Install suggested plugins**（安装推荐插件），等装完
5. 创建管理员账号（自己记牢）
6. 实例 URL 保持 `http://127.0.0.1:8081/` → **Save and Finish**

---

## Step 3：确认 Python 路径

Jenkins 构建时要能找到你的虚拟环境 Python。

在 PowerShell 里执行（按你实际路径）：

```powershell
D:\code\test-study\mall-admin-test\api-test\.venv1\Scripts\python.exe --version
D:\code\test-study\mall-admin-test\api-test\.venv1\Scripts\python.exe run.py smoke
```

两条都成功再往下。**记下这个 python.exe 的完整路径**，下面 Job 里要用。

> 若没有 `.venv1`，用 `.venv` 或先 `python -m venv .venv1` + `pip install -r requirements.txt`。

---

## Step 4：创建第一个 Freestyle Job

### 4.1 新建任务

1. Jenkins 首页 → **新建任务**（New Item）
2. 名称：`mall-api-test-smoke`
3. 选 **Freestyle project** → **确定**

### 4.2 配置构建步骤

往下滚到 **构建**（Build Steps）→ **增加构建步骤** → **Execute Windows batch command**

粘贴（**把 `PY` 路径改成你的**）：

```bat
@echo off
chcp 65001 >nul
set API_TEST=D:\code\test-study\mall-admin-test\api-test
set PY=%API_TEST%\.venv1\Scripts\python.exe

cd /d %API_TEST%

echo ===== mall-admin-test api-test smoke =====
"%PY%" -m pip install -r requirements.txt -q
"%PY%" run.py smoke
```

> 也可用仓库里的 **`run_ci.bat smoke`**（第 1 课已加），构建命令改为：
>
> ```bat
> cd /d D:\code\test-study\mall-admin-test\api-test
> call run_ci.bat smoke
> ```

### 4.3 保存并构建

1. 点 **保存**
2. 左侧 **立即构建**（Build Now）
3. 点构建号 **#1** → **Console Output**（控制台输出）

### 4.4 预期结果

控制台末尾类似：

```text
3 passed
Finished: SUCCESS
```

Jenkins 首页该 Job 显示 **蓝球**（成功）。

---

## Step 5：故意失败一次（建议做）

1. **停掉后端**（或改错 `BASE_URL`）
2. 再点 **立即构建**
3. 应看到 **红球** + 控制台里 `连不上后端` / `pytest.exit`

**教学点**：CI 的价值就是——环境坏了立刻能发现，不用等到你手动跑才发现。

测完记得 **重新启动后端**。

---

## 本课要记住的

| 概念 | 一句话 |
|------|--------|
| CI | 固定命令自动执行，结果成功/失败可见 |
| Freestyle Job | 最简单任务类型，适合入门 |
| 构建步骤 | Windows 用 **batch command**，不是 Linux shell |
| 端口 | Jenkins **8081**，后端 **8080** |
| 前置条件 | Jenkins 跑 Pytest 前，**MySQL + 后端必须已启动** |

---

## 常见问题

### Jenkins 打不开 8081

- 检查 Jenkins 服务是否运行
- 检查 `jenkins.xml` 里 `--httpPort` 是否为 8081
- 防火墙是否拦截

### 构建失败：`python不是内部或外部命令`

- 必须用 **虚拟环境完整路径**，不要写裸 `python`
- 检查 `set PY=...` 路径是否存在

### 构建失败：连不上后端

- 先在本机手动 `python run.py smoke` 能绿
- Jenkins 和你在同一台机器，后端地址仍是 `http://127.0.0.1:8080`

### 中文乱码

- batch 里已有 `chcp 65001`；控制台仍乱码可忽略，看 `passed/failed` 即可

---

## 检查清单

- [x] Jenkins 在 **http://127.0.0.1:8081** 能登录
- [x] 创建 Job `mall-api-test-smoke`
- [x] 立即构建 → **SUCCESS**，3 passed
- [ ] （可选）停后端再构建 → **FAILURE**，理解 CI 报错
- [ ] 知道 Jenkins 8081 与后端 8080 的区别

全部勾完后回复 **「Jenkins 01 绿了」**，进入第 2 课：拉 Git 代码 + 全量 `run.py report` + 归档 HTML 报告。

---

## 和后续课的关系

| 课时 | 内容 |
|------|------|
| **01（本课）** | 安装 + Freestyle + smoke |
| 02 | 从 Git 拉代码 + 全量 pytest |
| 03 | HTML 报告归档 + Pipeline（Jenkinsfile） |
| 04 | 定时构建 / 构建后通知（可选） |
