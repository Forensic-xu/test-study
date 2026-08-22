# 第 2 课 · Token 存储与 Bearer 鉴权

**状态：已完成**  
**关键能力：** 接口关联的第一步（登录 → 拿 token → 访问受保护接口）

---

## 学习目标

- 用后置脚本把 `token` 写入环境变量  
- 在集合级配置 Bearer Token  
- 用 `GET /api/users` 验证鉴权生效  
- 理解 401（没 token）与 403（有 token 但无权限）

---

## A. 登录后置脚本（Tests / 后置操作）

放在 `Login - Success`：

```javascript
const json = pm.response.json();

pm.test("登录成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.token).to.be.a("string");
});

if (json.data && json.data.token) {
  pm.environment.set("token", json.data.token);
}
```

发送后：环境 `mall-admin-test-local` 中应有 `token`。

你已做到：**测试结果 1/1**。

---

## B. 集合授权

1. 选中集合 `mall-admin-test API`
2. 授权类型：Bearer Token  
3. Token：`{{token}}`  
4. 子请求选择「继承父级」

---

## C. 验证请求

```text
GET {{base_url}}/api/users
Authorization: Bearer {{token}}   // 继承即可
```

### 成功期望（你已做到）

- HTTP 200  
- code 200  
- data 为用户数组（含 admin、user01…）

### 对比实验

| 操作 | 期望 |
|------|------|
| 去掉 Token | 401 / code=10006 |
| user01 登录后再 GET /api/users | 403 / code=10008 |

---

## 检查清单

- [x] token 自动写入环境变量  
- [x] 集合 Bearer 继承  
- [x] GET /api/users 返回 200  
- [ ] 做过「无 Token → 401」对比（建议补做）  
- [ ] 做过「USER 越权 → 403」对比（建议补做）
