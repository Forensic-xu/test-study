# 第 1 课 · 登录接口（POST + Body）

**状态：已完成**  
**对应接口：** `POST /api/auth/login`  
**Collection：** `01-Auth / Login - Success`

---

## 学习目标

- 知道登录要用 **POST**，不是 GET  
- 会发 JSON Body  
- 会看统一响应：`code` / `message` / `data`  
- 知道正确账号与错误场景的期望结果

---

## 正确请求

```text
POST {{base_url}}/api/auth/login
Content-Type: application/json
```

```json
{
  "username": "admin",
  "password": "Admin@123"
}
```

### 成功期望

| 项 | 期望 |
|----|------|
| HTTP | 200 |
| code | 200 |
| data.token | 字符串（JWT） |
| data.role | ADMIN |

---

## 常见错误（你踩过的坑）

| 错误写法 | 正确写法 |
|----------|----------|
| GET | POST |
| `/api/users/login` | `/api/auth/login` |
| users / Users@123 | admin / Admin@123 |

错误示例响应（打到需鉴权接口）：

```json
{ "code": 10006, "message": "未登录或缺少 Token" }
```

---

## 练习题（建议再练一遍）

| 场景 | Body | HTTP | code |
|------|------|------|------|
| 正确登录 | admin / Admin@123 | 200 | 200 |
| 错误密码 | admin / wrong | 401 | 10002 |
| 空用户名 | "" / Admin@123 | 400 | 90001 |
| 禁用用户 | disabled / User@123 | 403 | 10003 |

错误码详见：`mall-admin-test/docs/error-codes.md`

---

## 检查清单

- [x] 能独立发通登录  
- [x] 能从响应里找到 token  
- [ ] 能独立测出密码错误 / 空用户名（建议补做）
