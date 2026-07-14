-- Runtime schema compatibility for existing sky_take_out databases.
USE sky_take_out;

CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    name VARCHAR(32) NOT NULL,
    password VARCHAR(64) NOT NULL,
    phone VARCHAR(20),
    sex VARCHAR(2),
    status INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT,
    UNIQUE KEY idx_username (username)
);

INSERT IGNORE INTO admin (username, name, password, phone, sex, status, create_time, update_time, create_user, update_user)
VALUES ('admin', 'Administrator', 'e10adc3949ba59abbe56e057f20f883e', '13800000000', '1', 1, NOW(), NOW(), 1, 1);

SET @sql = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'status') = 0, 'ALTER TABLE user ADD COLUMN status INT DEFAULT 1', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'total_order_count') = 0, 'ALTER TABLE user ADD COLUMN total_order_count INT DEFAULT 0', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'total_amount') = 0, 'ALTER TABLE user ADD COLUMN total_amount DECIMAL(10,2) DEFAULT 0', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'last_login_time') = 0, 'ALTER TABLE user ADD COLUMN last_login_time DATETIME', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
