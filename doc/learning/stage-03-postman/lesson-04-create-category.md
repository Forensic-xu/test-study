# 第 4 课 · POST 创建分类 + 重复名称 409

**状态：已完成**  
**Collection：** `03-Categories`

---

## 学习目标

- 用 POST + JSON Body 创建资源  
- 区分 200（成功）与 409（业务冲突）  
- 把新建资源的 `id` 存进环境变量（接口关联）

---

## 练习 1：正常创建

```text
POST {{base_url}}/api/categories
```

Body：

```json
{
  "name": "Postman练习分类A",
  "status": 1
}
```

期望：HTTP 200，code 200，返回带 `id` 的分类。

后置脚本（保存 id）：

```javascript
const json = pm.response.json();
pm.test("创建分类成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
});
if (json.data && json.data.id) {
  pm.environment.set("categoryId", json.data.id);
}
```

---

## 练习 2：重复名称

再用**相同 name** 发一次 POST。

期望：

| 项 | 值 |
|----|-----|
| HTTP | 409 |
| code | 20006 |
| message | 商品分类名称已存在 |

---

## 练习 3：空名称

```json
{ "name": "", "status": 1 }
```

期望：HTTP 400，code 90001

---

## 检查清单

- [x] 创建成功 200  
- [x] 环境变量有 `categoryId`  
- [x] 重复名 409 / 20006  
- [x] 空名称 400 / 90001  

全部勾完后进入 [lesson-05-update-delete-category.md](./lesson-05-update-delete-category.md)

