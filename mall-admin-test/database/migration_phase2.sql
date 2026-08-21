-- Phase 2 migration for existing mall_test databases
-- Safe to re-run: uses information_schema checks where needed.

USE mall_test;

-- Add unique product name if missing
SET @idx_exists := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = 'mall_test' AND table_name = 'products' AND index_name = 'uk_products_name'
);
SET @sql := IF(@idx_exists = 0,
  'ALTER TABLE products ADD UNIQUE KEY uk_products_name (name)',
  'SELECT ''uk_products_name already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop old non-unique name index if it still exists alongside unique
SET @old_idx := (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = 'mall_test' AND table_name = 'products' AND index_name = 'idx_products_name'
);
SET @sql2 := IF(@old_idx > 0,
  'ALTER TABLE products DROP INDEX idx_products_name',
  'SELECT ''idx_products_name already dropped''');
PREPARE stmt2 FROM @sql2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;

-- Normalize legacy inventory operation type names
UPDATE inventory_records SET operation_type = 'INCREASE'
WHERE operation_type = 'ADJUST' AND change_quantity >= 0;

UPDATE inventory_records SET operation_type = 'DECREASE'
WHERE operation_type = 'ADJUST' AND change_quantity < 0;

UPDATE inventory_records SET operation_type = 'ORDER_CANCEL_RESTORE'
WHERE operation_type = 'ORDER_RESTORE';
