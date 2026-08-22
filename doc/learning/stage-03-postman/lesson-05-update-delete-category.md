# 第 5 课 · PUT 修改 / DELETE 删除 + 有商品分类不能删

**状态：已完成**  
**Collection：** `03-Categories`

---

## 学习目标

- 用 **PUT** 修改已有资源（Path 里带 id + Body）  
- 用 **DELETE** 删除资源  
- 理解业务约束：分类下有商品时禁止删除 → **409 / 20007**  
- 区分两类 409：名字重复 **20006** vs 有商品不能删 **20007**

---

## 课前确认

1. 已登录，`{{token}}` 有效（ADMIN）  
2. 第 4 课创建过分类，环境里有 `{{categoryId}}`（或你自己记下的 id）  
3. 若没有 `categoryId`：先再发一次 POST 创建，名称用新的，例如 `Postman练习分类B`

---

## 练习 1：PUT 修改分类名称

新建请求：

```text
PUT  {{base_url}}/api/categories/{{categoryId}}
```

Body → raw → JSON：

```json
{
  "name": "Postman练习分类A-已改名",
  "status": 1
}
```

期望：

| 项 | 值 |
|----|-----|
| HTTP | 200 |
| code | 200 |
| data.name | `Postman练习分类A-已改名` |
| data.id | 与 `{{categoryId}}` 一致 |

脚本示例：

```javascript
const json = pm.response.json();
pm.test("修改分类成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.name).to.eql("Postman练习分类A-已改名");
});
```

要点：**Path 定位要改谁，Body 写改成什么。**

---

## 练习 2：DELETE 删除「空」分类（你刚创建的）

```text
DELETE  {{base_url}}/api/categories/{{categoryId}}
```

无 Body。

期望：HTTP **200**，`code: 200`（空分类可删）

脚本：

```javascript
const json = pm.response.json();
pm.test("删除空分类成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
});
```

可选验证：再 `GET {{base_url}}/api/categories/{{categoryId}}` → 期望 **404 / 20005**（已删不存在）。

---

## 练习 3：删除「有商品」的分类 → 409

种子数据里分类 `id=1` 下通常挂有商品。发：

```text
DELETE  {{base_url}}/api/categories/1
```

期望：

| 项 | 值 |
|----|-----|
| HTTP | **409** |
| code | **20007** |
| message | 分类下存在商品，不允许删除 |

脚本：

```javascript
const json = pm.response.json();
pm.test("有商品分类不可删", function () {
  pm.response.to.have.status(409);
  pm.expect(json.code).to.eql(20007);
});
```

这才是「业务规则导致的冲突」，不是服务器坏了。

---

## 本课要记住的

| 方法 | 含义 | 典型用法 |
|------|------|----------|
| PUT | 更新 | `/resource/{{id}}` + Body |
| DELETE | 删除 | `/resource/{{id}}`，通常无 Body |
| 409/20006 | 名称冲突 | 创建/改名撞名 |
| 409/20007 | 关联约束 | 分类下还有商品 |

---

## 检查清单

- [x] PUT 改名 200  
- [x] DELETE 空分类 200  
- [x] （可选）删后再 GET → 404/20005  
- [x] DELETE `/categories/1` → 409/20007  

全部勾完。下次进入第 6 课（库存增减）——见 [记忆.md](../记忆.md)。
