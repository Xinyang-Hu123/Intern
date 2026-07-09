-- ============================================
-- 小程序点餐菜单 / 后台分类管理 数据对齐脚本
-- 目标：后台菜品分类、后台套餐分类、小程序菜单共用 demo 库，
--      菜品分类放 dish_category，套餐分类放 setmeal_category。
-- ============================================

USE demo;

CREATE TABLE IF NOT EXISTS dish_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID 0表示一级分类',
    type INT DEFAULT 1 COMMENT '类型 1菜品分类',
    name VARCHAR(32) COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT,
    UNIQUE KEY idx_type_name (type, name)
) COMMENT '菜品分类';

CREATE TABLE IF NOT EXISTS setmeal_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID 0表示一级分类',
    type INT DEFAULT 2 COMMENT '类型 2套餐分类',
    name VARCHAR(32) COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT,
    UNIQUE KEY idx_type_name (type, name)
) COMMENT '套餐分类';

INSERT IGNORE INTO dish_category (id, parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT id, parent_id, 1, name, sort, 1, create_time, update_time, create_user, update_user
FROM category
WHERE type = 1;

INSERT IGNORE INTO setmeal_category (id, parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT id, parent_id, 2, name, sort, 1, create_time, update_time, create_user, update_user
FROM category
WHERE type = 2;

INSERT INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 1, '蜀味烤鱼', 1, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM dish_category WHERE name = '蜀味烤鱼');

INSERT INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 1, '蜀味牛蛙', 2, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM dish_category WHERE name = '蜀味牛蛙');

INSERT INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 1, '特色蒸菜', 3, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM dish_category WHERE name = '特色蒸菜');

INSERT INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 1, '特色小炒', 4, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM dish_category WHERE name = '特色小炒');

INSERT INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 1, '新鲜时蔬', 5, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM dish_category WHERE name = '新鲜时蔬');

INSERT INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 1, '水煮鱼', 6, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM dish_category WHERE name = '水煮鱼');

INSERT INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 1, '传统主食', 7, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM dish_category WHERE name = '传统主食');

INSERT INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 1, '酒水饮料', 8, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM dish_category WHERE name = '酒水饮料');

INSERT INTO setmeal_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 2, '人气套餐', 1, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM setmeal_category WHERE name = '人气套餐');

INSERT INTO setmeal_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT 0, 2, '商务套餐', 2, 1, NOW(), NOW(), 1, 1
WHERE NOT EXISTS (SELECT 1 FROM setmeal_category WHERE name = '商务套餐');

UPDATE dish_category SET type = 1, sort = 1, status = 1, update_time = NOW(), update_user = 1 WHERE name = '蜀味烤鱼';
UPDATE dish_category SET type = 1, sort = 2, status = 1, update_time = NOW(), update_user = 1 WHERE name = '蜀味牛蛙';
UPDATE dish_category SET type = 1, sort = 3, status = 1, update_time = NOW(), update_user = 1 WHERE name = '特色蒸菜';
UPDATE dish_category SET type = 1, sort = 4, status = 1, update_time = NOW(), update_user = 1 WHERE name = '特色小炒';
UPDATE dish_category SET type = 1, sort = 5, status = 1, update_time = NOW(), update_user = 1 WHERE name = '新鲜时蔬';
UPDATE dish_category SET type = 1, sort = 6, status = 1, update_time = NOW(), update_user = 1 WHERE name = '水煮鱼';
UPDATE dish_category SET type = 1, sort = 7, status = 1, update_time = NOW(), update_user = 1 WHERE name = '传统主食';
UPDATE dish_category SET type = 1, sort = 8, status = 1, update_time = NOW(), update_user = 1 WHERE name = '酒水饮料';
UPDATE setmeal_category SET type = 2, sort = 1, status = 1, update_time = NOW(), update_user = 1 WHERE name = '人气套餐';
UPDATE setmeal_category SET type = 2, sort = 2, status = 1, update_time = NOW(), update_user = 1 WHERE name = '商务套餐';
