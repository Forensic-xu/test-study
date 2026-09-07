# 第 1 课 · MeterSphere 安装与项目创建

**状态：进行中**  
**目标**：本机跑起 MeterSphere（社区版 All-in-One），创建 `mall-admin-test` 项目，为后续导入 Postman 做准备

---

## 学习目标

1. 理解 MeterSphere 在测试体系里的位置（**平台**，不是替代 Postman/Pytest）
2. 用 Docker 一键启动 MeterSphere 社区版
3. 登录并创建第一个项目
4. 记住本机端口分工，避免和 Jenkins / 后端打架

---

## 先理解：MeterSphere 干什么

你已经会：

| 工具 | 你在干什么 |
|------|------------|
| Postman | 手写/调试接口用例 |
| Pytest | 代码化自动化 + 报告 |
| JMeter | 压测 |
| Jenkins | 定时/流水线跑 Pytest |

MeterSphere 把这些能力**集中到一个 Web 平台**里：

```text
接口定义 / 导入 Postman
        ↓
场景编排（多步关联）
        ↓
测试计划 / 定时执行
        ↓
报告 + 团队协作
```

本课只做一件事：**把平台装起来，建好项目空壳**。

---

## ⚠️ 端口分工（必读）

| 服务 | 本机端口 |
|------|----------|
| mall-admin-test **后端** | **8080** |
| **Jenkins** | **8081** |
| **MeterSphere** | **8082**（本课映射） |

官方示例常用 `8081:8081`。你已经把 Jenkins 放在 8081，所以本课用：

```text
主机 8082 → 容器 8081
```

- 测接口：`http://127.0.0.1:8080`
- 开 Jenkins：`http://127.0.0.1:8081`
- 开 MeterSphere：`http://127.0.0.1:8082`

---

## 课前准备

- [ ] 机器建议 **≥ 8GB 内存**（MeterSphere All-in-One 较吃资源；官方最低约 2C4G，学习机尽量再留余量）
- [ ] 能访问外网（拉 Docker 镜像）
- [ ] 管理员权限（装 WSL / Docker 时需要）
- [ ] （可选）Jenkins 可先关掉，省内存：`services.msc` → Jenkins → 停止

---

## Step 1：启用 WSL2（Windows 必需）

MeterSphere 跑在 Docker 里；Windows 上 Docker Desktop 依赖 **WSL2**。

### 1.1 用管理员打开 PowerShell，执行

```powershell
wsl --install
```

若提示需要重启，**先重启电脑**，再继续。

### 1.2 重启后检查

```powershell
wsl -l -v
```

期望能看到至少一个发行版（常见为 Ubuntu），`VERSION` 为 **2**。

若还没有发行版：

```powershell
wsl --install -d Ubuntu
```

首次进入 Ubuntu 会要求设 Linux 用户名/密码，设好即可（和 Windows 账号无关）。

### 1.3 本课检查点

- [ ] `wsl -l -v` 有发行版且 VERSION=2

---

## Step 2：安装 Docker Desktop

1. 打开 https://www.docker.com/products/docker-desktop/
2. 下载 **Docker Desktop for Windows** 并安装
3. 安装完成后启动 Docker Desktop，等到状态为 **Running**
4. 设置建议：
   - **Settings → General**：勾选 *Use the WSL 2 based engine*
   - **Settings → Resources → WSL Integration**：打开你的 Ubuntu 发行版
5. 新开 **PowerShell**（普通用户即可），验证：

```powershell
docker version
docker run --rm hello-world
```

能看到 Client/Server 版本，且 `hello-world` 打印成功信息，即通过。

### 本课检查点

- [ ] `docker version` 有 Server 段（说明守护进程在跑）
- [ ] `hello-world` 成功

---

## Step 3：启动 MeterSphere（All-in-One）

官方在线安装（社区版一键镜像）：  
文档：https://metersphere.io/docs/v3.x/installation/online_installation/

### 3.1 拉镜像并启动（注意端口 8082）

在 **PowerShell** 执行：

```powershell
# 数据目录：放在用户目录，重启 Docker 也不丢
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.metersphere\data" | Out-Null

docker pull metersphere/metersphere-ce-allinone

docker run -d `
  -p 8082:8081 `
  --name=metersphere `
  -v "${env:USERPROFILE}\.metersphere\data:/opt/metersphere/data" `
  metersphere/metersphere-ce-allinone
```

说明：

- `-p 8082:8081`：浏览器访问 **8082**
- `-v ...`：数据持久化到 `%USERPROFILE%\.metersphere\data`
- 首次启动可能要 **几分钟**（内部还要起 MySQL/Redis 等）

若官方镜像拉取慢，可试飞致云仓库（文档升级章节同款）：

```powershell
docker pull cr2.fit2cloud.com/metersphere/metersphere-ce-allinone
docker run -d -p 8082:8081 --name=metersphere `
  -v "${env:USERPROFILE}\.metersphere\data:/opt/metersphere/data" `
  cr2.fit2cloud.com/metersphere/metersphere-ce-allinone
```

### 3.2 看是否起来

```powershell
docker ps --filter name=metersphere
docker logs -f metersphere
```

容器 `STATUS` 为 `Up`；日志里出现可访问 / 启动完成类信息后，用浏览器打开：

**http://127.0.0.1:8082**

默认账号（社区版）：

| 项 | 值 |
|----|-----|
| 用户名 | `admin` |
| 密码 | `metersphere` |

登录后建议立刻改密码（若界面提示）。

### 3.3 常用运维命令

```powershell
docker stop metersphere      # 停
docker start metersphere     # 再开
docker rm -f metersphere     # 删容器（数据还在挂载目录里）
```

### 本课检查点

- [ ] 浏览器能打开 MeterSphere 登录页
- [ ] `admin` / `metersphere` 登录成功

---

## Step 4：创建项目 `mall-admin-test`

界面文案随版本可能略有差异，按「意思」找菜单即可：

1. 登录后进入 **工作台 / 项目** 相关入口
2. **创建项目**（或「新建项目」）
3. 填写：
   - **项目名称**：`mall-admin-test`
   - **描述**（可选）：电商后台被测系统，对接 Postman / Pytest / JMeter 资产
4. 保存，进入该项目

你已经有的资产（下一课会用）：

| 资产 | 路径 |
|------|------|
| Postman Collection | `mall-admin-test/postman/mall-admin-test API.postman_collection.json` |
| Postman Environment | `mall-admin-test/postman/mall-admin-test-local.postman_environment.json` |

### 本课检查点

- [ ] 项目列表里能看到 `mall-admin-test`
- [ ] 能进入项目工作台（看见接口/用例/测试计划等菜单入口即可，本课不用点完）

---

## Step 5：和已有工具对齐（口述一遍）

用自己的话确认（能答出来即可）：

1. MeterSphere 装在哪、端口多少？
2. 为什么不用 8081？
3. 停掉容器会不会丢项目数据？（答：一般不会，因为挂了 `~\.metersphere\data`）
4. 下一课准备导入哪个文件？

---

## 验收清单（全勾才算本课完成）

- [ ] WSL2 可用
- [ ] Docker Desktop Running，`docker version` 正常
- [ ] MeterSphere 容器 Up，浏览器 **http://127.0.0.1:8082** 可登录
- [ ] 已创建项目 **mall-admin-test**
- [ ] 知道端口：后端 8080 / Jenkins 8081 / MS 8082

---

## 常见问题

| 现象 | 处理 |
|------|------|
| `docker` 不是内部命令 | Docker Desktop 未装好或未启动；装完**重开**终端 |
| 8082 打不开 | `docker ps` 看容器是否 Up；`docker logs metersphere` 看是否还在初始化 |
| 内存不够 / 很卡 | 先停 Jenkins；Docker Desktop → Resources 适当加内存 |
| `port is already allocated` | 本机 8082 被占：改映射如 `-p 8083:8081`，记住改访问地址 |
| 镜像拉取超时 | 换 `cr2.fit2cloud.com/metersphere/metersphere-ce-allinone`，或配 Docker 镜像加速 |
| 与后端无关 | MeterSphere **不替代** 8080 后端；跑接口测试时后端仍要启动 |

---

## 完成后

1. 把本文顶部状态改成 **✅ 已完成**
2. 在 [`progress.md`](../progress.md) 勾选阶段 8 第 01 课
3. 下一课：**导入 mall-admin-test 接口（Postman 集合）**

续课口令：`MeterSphere 第 2 课` 或 `@doc/learning/记忆.md 继续 MeterSphere`
