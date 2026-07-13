-- ============================================
-- 座位容量与就餐会话参与者 - 数据库迁移脚本
-- ============================================

CREATE TABLE IF NOT EXISTS dining_session_participant (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  dining_session_id BIGINT NOT NULL COMMENT '用餐会话ID',
  user_id BIGINT NOT NULL COMMENT '小程序用户ID',
  create_time DATETIME NOT NULL COMMENT '确认加入时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_session_user (dining_session_id, user_id),
  KEY idx_session_id (dining_session_id),
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用餐会话参与者';
