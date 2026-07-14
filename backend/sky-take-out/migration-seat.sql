-- ============================================
-- 座位管理 (seat) 数据迁移脚本
-- Issue #30: feat(database): 新增座位管理数据模型
-- ============================================

USE sky_take_out;

-- 座位表
CREATE TABLE IF NOT EXISTS seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seat_number VARCHAR(32) NOT NULL COMMENT '桌号/座位编号',
    name VARCHAR(64) COMMENT '座位名称',
    capacity INT DEFAULT 1 COMMENT '容纳人数',
    area VARCHAR(64) COMMENT '区域（大厅/包间等）',
    qr_code VARCHAR(255) COMMENT '二维码路径',
    status INT DEFAULT 0 COMMENT '状态: 0空闲 1使用中 2已预订 3清洁中',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '修改人',
    UNIQUE KEY idx_seat_number (seat_number)
) COMMENT '座位（桌台）';

-- 初始化示例座位
INSERT INTO seat (seat_number, name, capacity, area, status, create_time, update_time, create_user, update_user) VALUES
('A1', 'A1桌', 4, '大厅', 0, NOW(), NOW(), 1, 1),
('A2', 'A2桌', 4, '大厅', 0, NOW(), NOW(), 1, 1),
('A3', 'A3桌', 2, '大厅', 0, NOW(), NOW(), 1, 1),
('B1', 'B1桌', 6, '包间', 0, NOW(), NOW(), 1, 1),
('B2', 'B2桌', 8, '包间', 0, NOW(), NOW(), 1, 1);

-- 订单表添加座位关联（如果不存在）
ALTER TABLE orders ADD COLUMN IF NOT EXISTS seat_id BIGINT COMMENT '关联座位ID' AFTER user_id;
