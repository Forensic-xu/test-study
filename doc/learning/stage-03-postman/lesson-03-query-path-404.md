# 第 3 课 · Query 参数、Path 参数与 404

**状态：已完成**  
**Collection：** `04-Products`

---

## 学习目标

- 会用 Query Params（分页、过滤）  
- 会用 Path 参数访问单个资源  
- 会主动测「资源不存在」→ HTTP 404 + 业务 code  

---

## 练习 1：商品列表（Query）

```text
GET {{base_url}}/api/products
```

Params：

| Key | Value |
|-----|-------|
| page | 1 |
| size | 5 |
| status | ON_SALE |

期望：

- HTTP 200，code 200  
- `data.records` 为数组  

后置脚本示例：

```javascript
const json = pm.response.json();
pm.test("商品列表成功", function () {
  pm.response.to.have.status(200);
  pm.expect(json.code).to.eql(200);
  pm.expect(json.data.records).to.be.an("array");
});
```

---

## 练习 2：商品详情（Path）

```text
GET {{base_url}}/api/products/1
```

期望：HTTP 200，`data.id === 1`

---

## 练习 3：不存在的商品（404）

```text
GET {{base_url}}/api/products/999999
```

期望：

| 项 | 值 |
|----|-----|
| HTTP | 404 |
| code | 20001 |
| message | 商品不存在 |

```javascript
const json = pm.response.json();
pm.test("商品不存在", function () {
  pm.response.to.have.status(404);
  pm.expect(json.code).to.eql(20001);
});
```

---

## 概念小结

| 类型 | 例子 | 用途 |
|------|------|------|
| Query | `?page=1&size=5` | 过滤、分页 |
| Path | `/products/1` | 指定资源 |
| Header | Authorization | 鉴权 |
| Body | JSON | 提交数据 |

---

## 检查清单

- [x] 列表 200 且有 records  
- [x] `/products/1` 200  
- [x] `/products/999999` 404 / 20001  

全部勾完后进入 [lesson-04-create-category.md](./lesson-04-create-category.md)
