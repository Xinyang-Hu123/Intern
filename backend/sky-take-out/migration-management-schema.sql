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

CREATE TABLE IF NOT EXISTS dish_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    type INT DEFAULT 1,
    name VARCHAR(32),
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT,
    UNIQUE KEY idx_type_name (type, name)
);

-- Delivery orders require an address, while seat-bound dine-in orders do not.
SET @sql = (
    SELECT IF(is_nullable = 'NO',
        'ALTER TABLE orders MODIFY COLUMN address_book_id BIGINT NULL',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'orders' AND column_name = 'address_book_id'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE dish ADD COLUMN detail_image VARCHAR(255) NULL',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'dish' AND column_name = 'detail_image'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE dish ADD COLUMN images VARCHAR(1024) NULL',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'dish' AND column_name = 'images'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE dish ADD COLUMN sort INT NOT NULL DEFAULT 0',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'dish' AND column_name = 'sort'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE dish ADD COLUMN recommend INT NOT NULL DEFAULT 0',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'dish' AND column_name = 'recommend'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE dish ADD COLUMN column_show INT NOT NULL DEFAULT 0',
        'SELECT 1')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'dish' AND column_name = 'column_show'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

-- Preserve legacy category IDs so existing dish.category_id and
-- setmeal.category_id values remain valid after the mapper split.
INSERT IGNORE INTO dish_category
    (id, parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT id, 0, 1, name, sort, status, create_time, update_time, create_user, update_user
FROM category
WHERE type = 1;

INSERT IGNORE INTO setmeal_category
    (id, parent_id, type, name, sort, status, create_time, update_time, create_user, update_user)
SELECT id, 0, 2, name, sort, status, create_time, update_time, create_user, update_user
FROM category
WHERE type = 2;

-- Remove the temporary seed rows created by the first revision of this
-- migration. Real categories are copied from the legacy category table above.
DELETE FROM setmeal_category
WHERE id IN (1, 2) AND name IN ('Popular', 'Business');
