# Selenium UI 自动化准备说明

前端地址：http://127.0.0.1:5176

重要交互元素使用稳定的 `data-testid`，避免依赖动态 CSS class。

## Element Plus 输入框定位

`el-input` 的 `data-testid` 在外层容器上，定位原生输入建议：

```text
[data-testid="login-username"] input
[data-testid="login-password"] input
```

按钮一般可直接：

```text
[data-testid="login-submit"]
```

## 常用 data-testid

### 登录

- `login-username` / `login-password` / `login-submit` / `login-error`

### 布局

- `admin-layout` / `side-menu` / `menu-dashboard` / `menu-products` / `logout-btn` / `header-username`

### 商品

- `product-search` / `product-create` / `product-edit-{id}` / `product-delete-{id}` / `product-detail-{id}`
- `product-form-name` / `product-form-price` / `product-form-submit`

### 库存

- `inventory-product-id` / `inventory-search` / `inventory-increase` / `inventory-decrease` / `inventory-records`

### 订单

- `order-search` / `order-detail-{id}` / `order-pay-{id}` / `order-ship-{id}` / `order-complete-{id}` / `order-cancel-{id}`

## 建议自动化流程（暂不写代码）

### 流程 1

登录 → 商品管理 → 搜索商品 → 查看详情

### 流程 2

管理员登录 → 创建商品 → 库存管理增加库存 → 再查商品

### 流程 3

登录 → 订单管理 → 查看订单 → 支付 → 发货 → 完成

### 流程 4（权限）

user01 登录 → 侧边栏无用户/商品菜单 → 直接访问 `/users` 应回到 Dashboard
