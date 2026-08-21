# JMeter 性能测试准备指南

> 本阶段**不执行**压测，仅列出适合压测的接口与注意点。

## 建议场景

| 场景 | 接口 | 说明 |
|------|------|------|
| 登录 | `POST /api/auth/login` | 获取 token；注意 BCrypt CPU |
| 商品列表 | `GET /api/products` | 读多写少 |
| 商品详情 | `GET /api/products/{id}` | 热点 |
| 分类列表 | `GET /api/categories` | 轻量 |
| 订单列表 | `GET /api/orders` | 需 Token |
| 创建订单 | `POST /api/orders` | 含事务与库存条件更新 |

## 建议线程组思路

1. setUp：登录拿 token → 写入变量  
2. 读场景：商品列表 + 详情  
3. 写场景（低并发）：创建订单（注意库存被测数据）  

## 注意

- 下单压测前准备足够库存或专用商品  
- 观察是否出现负库存（应不会）  
- 对比 HTTP 错误率与业务 code  

前端不参与 JMeter 压测；直接打 `8080`。
