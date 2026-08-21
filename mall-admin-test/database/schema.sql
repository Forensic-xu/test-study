-- mall-admin-test schema (idempotent)
-- MySQL 8.x | charset utf8mb4

CREATE DATABASE IF NOT EXISTS mall_test
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE mall_test;

SET NAMES utf8mb4;

-- =========================
-- users
-- =========================
CREATE TABLE IF NOT EXISTS users (
  id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username      VARCHAR(50)  NOT NULL COMMENT '用户名',
  password      VARCHAR(100) NOT NULL COMMENT 'BCrypt密码哈希',
  nickname      VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  email         VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  phone         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  role          VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN/USER',
  status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_username (username),
  KEY idx_users_role (role),
  KEY idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =========================
-- categories
-- =========================
CREATE TABLE IF NOT EXISTS categories (
  id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  name          VARCHAR(50)  NOT NULL COMMENT '分类名称',
  parent_id     BIGINT       DEFAULT 0 COMMENT '父分类ID，0表示顶级',
  sort_order    INT          NOT NULL DEFAULT 0 COMMENT '排序',
  status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_categories_name (name),
  KEY idx_categories_parent (parent_id),
  KEY idx_categories_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类';

-- =========================
-- products
-- =========================
CREATE TABLE IF NOT EXISTS products (
  id            BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  category_id   BIGINT         NOT NULL COMMENT '分类ID',
  name          VARCHAR(100)   NOT NULL COMMENT '商品名称',
  description   VARCHAR(1000)  DEFAULT NULL COMMENT '商品描述',
  price         DECIMAL(12,2)  NOT NULL COMMENT '价格',
  stock         INT            NOT NULL DEFAULT 0 COMMENT '库存',
  status        TINYINT        NOT NULL DEFAULT 1 COMMENT '状态: 1上架 0下架',
  created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_products_name (name),
  KEY idx_products_category (category_id),
  KEY idx_products_status (status),
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- =========================
-- inventory_records
-- =========================
CREATE TABLE IF NOT EXISTS inventory_records (
  id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  product_id       BIGINT       NOT NULL COMMENT '商品ID',
  before_stock     INT          NOT NULL COMMENT '变更前库存',
  change_quantity  INT          NOT NULL COMMENT '变更数量(可正可负)',
  after_stock      INT          NOT NULL COMMENT '变更后库存',
  operation_type   VARCHAR(30)  NOT NULL COMMENT '操作类型: INCREASE/DECREASE/ORDER_DEDUCT/ORDER_CANCEL_RESTORE',
  operator_id      BIGINT       DEFAULT NULL COMMENT '操作人ID',
  remark           VARCHAR(255) DEFAULT NULL COMMENT '备注',
  created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_inv_product (product_id),
  KEY idx_inv_operator (operator_id),
  KEY idx_inv_type (operation_type),
  CONSTRAINT fk_inv_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存变更记录';

-- =========================
-- cart
-- =========================
CREATE TABLE IF NOT EXISTS cart (
  id            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  user_id       BIGINT   NOT NULL COMMENT '用户ID',
  product_id    BIGINT   NOT NULL COMMENT '商品ID',
  quantity      INT      NOT NULL COMMENT '数量',
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_cart_user_product (user_id, product_id),
  KEY idx_cart_user (user_id),
  KEY idx_cart_product (product_id),
  CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id),
  CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车';

-- =========================
-- orders
-- =========================
CREATE TABLE IF NOT EXISTS orders (
  id            BIGINT         NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  order_no      VARCHAR(64)    NOT NULL COMMENT '订单号',
  user_id       BIGINT         NOT NULL COMMENT '用户ID',
  total_amount  DECIMAL(12,2)  NOT NULL COMMENT '订单总金额',
  status        VARCHAR(20)    NOT NULL COMMENT '状态: PENDING/PAID/SHIPPED/COMPLETED/CANCELLED',
  remark        VARCHAR(255)   DEFAULT NULL COMMENT '备注',
  created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_orders_order_no (order_no),
  KEY idx_orders_user (user_id),
  KEY idx_orders_status (status),
  KEY idx_orders_created (created_at),
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =========================
-- order_items
-- =========================
CREATE TABLE IF NOT EXISTS order_items (
  id            BIGINT         NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  order_id      BIGINT         NOT NULL COMMENT '订单ID',
  product_id    BIGINT         NOT NULL COMMENT '商品ID',
  product_name  VARCHAR(100)   NOT NULL COMMENT '商品名称快照',
  product_price DECIMAL(12,2)  NOT NULL COMMENT '商品单价快照',
  quantity      INT            NOT NULL COMMENT '购买数量',
  subtotal      DECIMAL(12,2)  NOT NULL COMMENT '小计',
  created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_order_items_order (order_id),
  KEY idx_order_items_product (product_id),
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id),
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细';

-- =========================
-- operation_logs
-- =========================
CREATE TABLE IF NOT EXISTS operation_logs (
  id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  user_id       BIGINT       DEFAULT NULL COMMENT '操作用户ID',
  username      VARCHAR(50)  DEFAULT NULL COMMENT '操作用户名',
  module        VARCHAR(50)  NOT NULL COMMENT '模块',
  action        VARCHAR(50)  NOT NULL COMMENT '动作',
  method        VARCHAR(10)  DEFAULT NULL COMMENT 'HTTP方法',
  request_uri   VARCHAR(255) DEFAULT NULL COMMENT '请求路径',
  request_params VARCHAR(2000) DEFAULT NULL COMMENT '脱敏后的请求参数',
  http_status   INT          DEFAULT NULL COMMENT 'HTTP状态码',
  ip            VARCHAR(50)  DEFAULT NULL COMMENT 'IP',
  detail        VARCHAR(1000) DEFAULT NULL COMMENT '详情',
  status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1成功 0失败',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_oplog_user (user_id),
  KEY idx_oplog_module (module),
  KEY idx_oplog_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志';
