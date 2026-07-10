-- ============================================
-- 座位管理与扫码点餐 - 数据库迁移脚本
-- 版本: V1
-- 日期: 2026-07-10
-- ============================================

-- 1. 座位表 seat
CREATE TABLE IF NOT EXISTS seat (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  seat_code VARCHAR(16) NOT NULL COMMENT '座位编码全局唯一',
  seat_name VARCHAR(64) NOT NULL COMMENT '展示名称',
  area_name VARCHAR(32) NOT NULL COMMENT '区域名称',
  capacity INT NOT NULL DEFAULT 4 COMMENT '建议容纳人数',
  position_x DECIMAL(5,2) NOT NULL DEFAULT 50.00 COMMENT '布局横坐标0-100',
  position_y DECIMAL(5,2) NOT NULL DEFAULT 50.00 COMMENT '布局纵坐标0-100',
  status VARCHAR(16) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态AVAILABLE空闲OCCUPIED使用中DISABLED停用',
  qr_version INT NOT NULL DEFAULT 1 COMMENT '二维码版本',
  qr_sign VARCHAR(64) DEFAULT NULL COMMENT '二维码签名',
  sort INT NOT NULL DEFAULT 0 COMMENT '同区域排序',
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  create_user BIGINT DEFAULT NULL,
  update_user BIGINT DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_seat_code (seat_code),
  KEY idx_area_name (area_name),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='座位表';

-- 2. 用餐会话表
CREATE TABLE IF NOT EXISTS dining_session (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  seat_id BIGINT NOT NULL COMMENT '座位ID',
  status VARCHAR(16) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN开放CLOSED已关闭',
  start_time DATETIME NOT NULL,
  close_time DATETIME DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  update_time DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_seat_id (seat_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用餐会话表';

-- 3. 订单表新增字段
ALTER TABLE orders ADD COLUMN order_type INT NOT NULL DEFAULT 1 COMMENT '订单类型1外卖2自取3堂食' AFTER status;
ALTER TABLE orders ADD COLUMN seat_id BIGINT DEFAULT NULL COMMENT '座位ID堂食' AFTER order_type;
ALTER TABLE orders ADD COLUMN dining_session_id BIGINT DEFAULT NULL COMMENT '用餐会话ID' AFTER seat_id;
ALTER TABLE orders ADD COLUMN delivery_method VARCHAR(16) DEFAULT 'DELIVERY' COMMENT 'DELIVERY配送PICKUP自取DINING堂食' AFTER dining_session_id;

-- 4. 购物车新增字段
ALTER TABLE shopping_cart ADD COLUMN dining_session_id BIGINT DEFAULT NULL COMMENT '用餐会话ID' AFTER user_id;

-- 回滚说明:
-- ALTER TABLE shopping_cart DROP COLUMN dining_session_id;
-- ALTER TABLE orders DROP COLUMN delivery_method, dining_session_id, seat_id, order_type;
-- DROP TABLE IF EXISTS dining_session;
-- DROP TABLE IF EXISTS seat;