-- Local management module schema migration.
-- This script is safe to run repeatedly against the sky_take_out database.

CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    discount DECIMAL(3,1) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT
);

CREATE TABLE IF NOT EXISTS setmeal_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    type INT DEFAULT 2,
    name VARCHAR(32),
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT,
    UNIQUE KEY idx_type_name (type, name)
);

INSERT IGNORE INTO setmeal_category
    (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
VALUES
    (0, 2, 'Popular', 1, 1, NOW(), NOW(), 1, 1),
    (0, 2, 'Business', 2, 1, NOW(), NOW(), 1, 1);
