# 第 2 课 · 提取 Token + 带鉴权请求商品列表（中文界面）

**状态：已完成**  
**在第 1 课 `01-login.jmx` 上继续改**（或另存为 `02-login-token-products.jmx`）

---

## 本课要解决什么问题？

商品列表接口需要登录：

```text
GET /api/products
Authorization: Bearer <上一步登录返回的 token>
```

Postman 里你会：登录 → 把 token 存环境变量 → 下一个请求带上。  
JMeter 里同样做，工具叫：**JSON提取器**。

流程：

```text
登录（拿到 token）
   ↓
JSON提取器（把 token 存成变量）
   ↓
商品列表（请求头带 Bearer ${token}）
```

---

## 课前确认

- 第 1 课登录能绿  
- 后端开着  
- 打开你的 `01-login.jmx`

---

## 步骤 1：在「登录」下面加 JSON提取器

1. 右键左侧的 **登录**（HTTP请求）
2. **添加 → 后置处理器 → JSON提取器**
3. 点刚加出来的 **JSON提取器**，右侧这样填：

| 中文项 | 填什么 | 说明 |
|--------|--------|------|
| 名称 | `提取token` | 随便起 |
| 变量名称 | `token` | 后面用 `${token}` 引用 |
| JSON Path表达式 | `$.data.token` | 从响应里取 data 下面的 token |
| 匹配数字 | `1` | 取第 1 个匹配 |
| 默认值 | `TOKEN_NOT_FOUND` | 没取到时的值，方便排查 |

> 你第 1 课响应结构是：`data.token`，所以表达式是 `$.data.token`。  
> 点开头的 `$` 表示「从根开始」。

**顺序很重要**：JSON提取器必须挂在「登录」下面，并且在「登录」执行完之后才会跑。

此时左侧大概是：

```text
登录压测
└── 登录
    ├── HTTP信息头管理器
    └── 提取token（JSON提取器）
```

（查看结果树可以仍挂在线程组下）

---

## 步骤 2：新建「商品列表」HTTP请求

1. 右键 **登录压测**（线程组）
2. **添加 → 取样器 → HTTP请求**
3. 名称改成：`商品列表`
4. **基本** 页填写：

| 中文项 | 填什么 |
|--------|--------|
| 协议 | `http` |
| 服务器名称或IP | `127.0.0.1` |
| 端口号 | `8080` |
| 方法 | **GET** |
| 路径 | `  ` |

可选：在「参数」页加分页（和 Postman 一样）：

| 名称 | 值 |
|------|-----|
| page | 1 |
| size | 5 |

没有参数也能通，只是默认分页。

---

## 步骤 3：给「商品列表」加鉴权头

商品列表要带 Token，登录时的 Content-Type 头不够用。

1. 右键 **商品列表**
2. **添加 → 配置元件 → HTTP信息头管理器**
3. **添加** 一行：

| 名称 | 值 |
|------|-----|
| `Authorization` | `Bearer ${token}` |

注意：

- `Bearer` 后面有一个空格  
- `${token}` 就是步骤 1 提取出来的变量  
- 不要写成 `Bearer ${TOKEN}`（大小写要和变量名称一致，我们用的是小写 `token`）

---

## 步骤 4：确认左侧结构

最终建议长这样：

```text
测试计划
└── 登录压测（线程组）
    ├── 登录（HTTP请求 POST）
    │   ├── HTTP信息头管理器（Content-Type）
    │   └── 提取token（JSON提取器）
    ├── 商品列表（HTTP请求 GET）
    │   └── HTTP信息头管理器（Authorization）
    └── 查看结果树
```

**执行顺序**：从上到下。先登录提取 token，再请求商品列表。

---

## 步骤 5：运行并检查

1. 点绿色 **启动**
2. 打开 **查看结果树**
3. 应看到两条：

| 请求 | 期望 |
|------|------|
| 登录 | 绿色，`code:200`，有 token |
| 商品列表 | 绿色，`code:200`，有 `data.records` |

### 如果商品列表是红的

点红的那条，看响应：

| 现象 | 原因 | 处理 |
|------|------|------|
| `10006` 未登录 / 缺 Token | `${token}` 没带上或提取失败 | 检查 JSON Path 是否 `$.data.token`；变量名是否一致 |
| `TOKEN_NOT_FOUND` 出现在请求头里 | 提取失败 | 先看登录是否绿；再核对 JSON Path |
| Connection refused | 后端没开 | 启动后端 |

想确认 token 有没有提出来：可临时在线程组下加 **调试取样器**（添加 → 取样器 → 调试取样器），跑完在结果树里能看到 `token=...`。用完可禁用或删掉。

---

## 步骤 6：另存一份（推荐）

**文件 → 另存测试计划为…**

```text
D:\code\test-study\mall-admin-test\jmeter\02-login-token-products.jmx
```

第 1 课脚本留着当备份。

---

## 本课要记住的

1. **JSON提取器** = Postman 后置脚本里的 `pm.environment.set("token", ...)`
2. **`${token}`** = Postman 的 `{{token}}`
3. 鉴权头格式：`Authorization: Bearer ${token}`
4. 提取器要挂在「产生数据的那个请求」下面

---

## 检查清单

- [x] 登录下有 JSON提取器，`$.data.token` → 变量 `token`
- [x] 有商品列表 GET `/api/products`
- [x] 商品列表有头：`Authorization: Bearer ${token}`
- [x] 结果树里两条都是绿色
- [x] 已另存为 `02-login-token-products.jmx`

全部勾完。下一课：[lesson-03-products-load.md](./lesson-03-products-load.md)
