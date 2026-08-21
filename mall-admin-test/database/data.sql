-- mall-admin-test seed data (idempotent via ON DUPLICATE KEY UPDATE)
-- Passwords (BCrypt):
--   admin / Admin@123
--   user01~user10, disabled / User@123

USE mall_test;

SET NAMES utf8mb4;

-- =========================
-- users (10+ accounts)
-- =========================
INSERT INTO users (id, username, password, nickname, email, phone, role, status) VALUES
(1,  'admin',    '$2b$10$nZoatxAwL/KBmuiT9eQ.d.v/8gq60m9cWtGBmvr8aMCQZXFi6sZJ6', '系统管理员', 'admin@mall.test',    '13800000001', 'ADMIN', 1),
(2,  'user01',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户01', 'user01@mall.test',   '13800000002', 'USER',  1),
(3,  'disabled', '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '禁用用户',   'disabled@mall.test', '13800000003', 'USER',  0),
(4,  'user02',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户02', 'user02@mall.test',   '13800000004', 'USER',  1),
(5,  'user03',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户03', 'user03@mall.test',   '13800000005', 'USER',  1),
(6,  'user04',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户04', 'user04@mall.test',   '13800000006', 'USER',  1),
(7,  'user05',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户05', 'user05@mall.test',   '13800000007', 'USER',  1),
(8,  'user06',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户06', 'user06@mall.test',   '13800000008', 'USER',  1),
(9,  'user07',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户07', 'user07@mall.test',   '13800000009', 'USER',  1),
(10, 'user08',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户08', 'user08@mall.test',   '13800000010', 'USER',  1),
(11, 'user09',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户09', 'user09@mall.test',   '13800000011', 'USER',  1),
(12, 'user10',   '$2b$10$auzI3kFwvdGmVVUojb/vaeM81tvCIiHC1/TNMl8sor99yiYxPkAba', '测试用户10', 'user10@mall.test',   '13800000012', 'USER',  1)
ON DUPLICATE KEY UPDATE
  password = VALUES(password),
  nickname = VALUES(nickname),
  email = VALUES(email),
  phone = VALUES(phone),
  role = VALUES(role),
  status = VALUES(status);

-- =========================
-- categories (5)
-- =========================
INSERT INTO categories (id, name, parent_id, sort_order, status) VALUES
(1, '手机数码', 0, 1, 1),
(2, '电脑办公', 0, 2, 1),
(3, '家用电器', 0, 3, 1),
(4, '服饰鞋包', 0, 4, 1),
(5, '食品饮料', 0, 5, 1)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = VALUES(parent_id),
  sort_order = VALUES(sort_order),
  status = VALUES(status);

-- =========================
-- products (30) — Phase2+ will use these
-- stock designed for boundary tests: 0, 1, 5, 10, 100, etc.
-- =========================
INSERT INTO products (id, category_id, name, description, price, stock, status) VALUES
(1,  1, '测试手机A', '库存10，适合边界测试', 1999.00, 10, 1),
(2,  1, '测试手机B', '库存1，适合超库存测试', 2999.00, 1, 1),
(3,  1, '测试手机C', '已下架商品', 3999.00, 50, 0),
(4,  1, '测试手机D', '库存0', 1599.00, 0, 1),
(5,  1, '测试耳机', '普通商品', 199.00, 100, 1),
(6,  2, '测试笔记本A', '办公本', 5999.00, 20, 1),
(7,  2, '测试笔记本B', '游戏本', 8999.00, 8, 1),
(8,  2, '测试键盘', '机械键盘', 399.00, 50, 1),
(9,  2, '测试鼠标', '无线鼠标', 99.00, 80, 1),
(10, 2, '测试显示器', '27寸显示器', 1299.00, 15, 1),
(11, 3, '测试冰箱', '双开门冰箱', 3499.00, 6, 1),
(12, 3, '测试洗衣机', '滚筒洗衣机', 2499.00, 12, 1),
(13, 3, '测试空调', '变频空调', 3299.00, 9, 1),
(14, 3, '测试微波炉', '家用微波炉', 499.00, 30, 1),
(15, 3, '测试电饭煲', '智能电饭煲', 299.00, 40, 1),
(16, 4, '测试T恤', '纯棉T恤', 79.00, 200, 1),
(17, 4, '测试牛仔裤', '修身牛仔裤', 199.00, 60, 1),
(18, 4, '测试运动鞋', '跑步鞋', 399.00, 45, 1),
(19, 4, '测试双肩包', '通勤背包', 159.00, 35, 1),
(20, 4, '测试羽绒服', '冬季羽绒服', 699.00, 25, 1),
(21, 5, '测试咖啡豆', '中度烘焙', 68.00, 100, 1),
(22, 5, '测试绿茶', '明前绿茶', 128.00, 70, 1),
(23, 5, '测试坚果礼盒', '混合坚果', 88.00, 90, 1),
(24, 5, '测试矿泉水', '箱装矿泉水', 29.90, 500, 1),
(25, 5, '测试巧克力', '黑巧克力', 39.90, 120, 1),
(26, 1, '边界商品-库存5', '库存5边界', 100.00, 5, 1),
(27, 1, '边界商品-库存10', '库存10边界', 100.00, 10, 1),
(28, 2, '下架办公椅', '已下架', 599.00, 20, 0),
(29, 3, '高价家电', '高价格测试', 19999.00, 3, 1),
(30, 5, '低价零食', '低价格测试', 1.00, 9999, 1)
ON DUPLICATE KEY UPDATE
  category_id = VALUES(category_id),
  name = VALUES(name),
  description = VALUES(description),
  price = VALUES(price),
  stock = VALUES(stock),
  status = VALUES(status);

-- =========================
-- sample cart (user01)
-- =========================
INSERT INTO cart (id, user_id, product_id, quantity) VALUES
(1, 2, 1, 2),
(2, 2, 5, 1),
(3, 4, 6, 1)
ON DUPLICATE KEY UPDATE
  quantity = VALUES(quantity);

-- =========================
-- sample orders
-- =========================
INSERT INTO orders (id, order_no, user_id, total_amount, status, remark) VALUES
(1, 'ORD202601010001', 2, 2198.00, 'PENDING', '待支付测试订单'),
(2, 'ORD202601010002', 2, 399.00,  'PAID', '已支付测试订单'),
(3, 'ORD202601010003', 4, 99.00,   'COMPLETED', '已完成测试订单'),
(4, 'ORD202601010004', 5, 1999.00, 'CANCELLED', '已取消测试订单')
ON DUPLICATE KEY UPDATE
  total_amount = VALUES(total_amount),
  status = VALUES(status),
  remark = VALUES(remark);

INSERT INTO order_items (id, order_id, product_id, product_name, product_price, quantity, subtotal) VALUES
(1, 1, 1, '测试手机A', 1999.00, 1, 1999.00),
(2, 1, 5, '测试耳机', 199.00, 1, 199.00),
(3, 2, 8, '测试键盘', 399.00, 1, 399.00),
(4, 3, 9, '测试鼠标', 99.00, 1, 99.00),
(5, 4, 1, '测试手机A', 1999.00, 1, 1999.00)
ON DUPLICATE KEY UPDATE
  product_name = VALUES(product_name),
  product_price = VALUES(product_price),
  quantity = VALUES(quantity),
  subtotal = VALUES(subtotal);

-- =========================
-- sample inventory records
-- =========================
INSERT INTO inventory_records (id, product_id, before_stock, change_quantity, after_stock, operation_type, operator_id, remark) VALUES
(1, 1, 0, 10, 10, 'INCREASE', 1, '初始化库存记录'),
(2, 26, 0, 5, 5, 'INCREASE', 1, '边界库存初始化'),
(3, 2, 0, 1, 1, 'INCREASE', 1, '库存1边界初始化'),
(4, 4, 0, 0, 0, 'INCREASE', 1, '库存0边界初始化'),
(5, 27, 0, 10, 10, 'INCREASE', 1, '库存10边界初始化')
ON DUPLICATE KEY UPDATE
  remark = VALUES(remark),
  operation_type = VALUES(operation_type);

-- =========================
-- sample operation logs
-- =========================
INSERT INTO operation_logs (id, user_id, username, module, action, method, request_uri, ip, detail, status) VALUES
(1, 1, 'admin', 'AUTH', 'LOGIN', 'POST', '/api/auth/login', '127.0.0.1', '管理员登录成功', 1),
(2, 2, 'user01', 'AUTH', 'LOGIN', 'POST', '/api/auth/login', '127.0.0.1', '普通用户登录成功', 1)
ON DUPLICATE KEY UPDATE
  detail = VALUES(detail);
