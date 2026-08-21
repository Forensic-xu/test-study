-- Phase 5: enrich operation_logs for audit fields (idempotent)

USE mall_test;

SET @col1 := (
  SELECT COUNT(1) FROM information_schema.columns
  WHERE table_schema='mall_test' AND table_name='operation_logs' AND column_name='request_params'
);
SET @sql1 := IF(@col1=0,
  'ALTER TABLE operation_logs ADD COLUMN request_params VARCHAR(2000) NULL COMMENT ''脱敏后的请求参数'' AFTER request_uri',
  'SELECT ''request_params exists''');
PREPARE s1 FROM @sql1; EXECUTE s1; DEALLOCATE PREPARE s1;

SET @col2 := (
  SELECT COUNT(1) FROM information_schema.columns
  WHERE table_schema='mall_test' AND table_name='operation_logs' AND column_name='http_status'
);
SET @sql2 := IF(@col2=0,
  'ALTER TABLE operation_logs ADD COLUMN http_status INT NULL COMMENT ''HTTP状态码'' AFTER request_params',
  'SELECT ''http_status exists''');
PREPARE s2 FROM @sql2; EXECUTE s2; DEALLOCATE PREPARE s2;
