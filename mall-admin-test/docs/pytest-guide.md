# Pytest 接口自动化准备指南

> 本阶段**不编写** Pytest 代码，仅列出后续可落地的用例方向。

推荐技术：`Python 3` + `requests` + `pytest`

## 建议目录（你后续自建）

```text
tests/
  conftest.py      # base_url / token fixture
  test_auth.py
  test_product.py
  test_inventory.py
  test_cart.py
  test_order.py
```

## Auth

- 正确登录 → 200 / code=200，拿到 token  
- 错误密码 → 401 / 10002  
- 不存在用户 → 404 / 10001  
- 禁用用户 → 403 / 10003  
- 空用户名/空密码 → 400 / 90001  

## Product

- 正常查询 / 详情  
- 不存在商品 → 404 / 20001  
- 非法价格 0/-1 → 400 / 90001  
- 非法库存 -1 → 400 / 90001  
- 不存在分类创建商品 → 404 / 20005  

## Inventory

- 增加 / 减少成功  
- 超库存减少 → 409 / 50002  
- 减到 0 成功  
- quantity 非法 → 400  

## Cart

- 正常添加、重复添加合并数量  
- 库存不足 / 下架 / 不存在  
- user02 操作 user01 购物车 → 403 / 30004  

## Order

- 正常创建（items / cartItemIds）  
- 库存不足整单失败并回滚  
- 取消恢复库存；重复取消 409 / 40005  
- 非法状态转换 409  
- user02 看 user01 订单 → 403 / 40009  
- 价格快照不随商品改价变化  

断言模板：

```python
assert resp.status_code == 409
assert resp.json()["code"] == 20003
```
