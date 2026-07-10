-- ============================================
-- 老宋速达 (sky-take-out) 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE demo;

-- 优惠券表
CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL COMMENT '优惠券名称',
    discount DECIMAL(3,1) NOT NULL COMMENT '折扣，8.5表示8.5折',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '截止时间',
    status INT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '修改人'
) COMMENT '优惠券';

-- 管理员表
CREATE TABLE IF NOT EXISTS admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    name VARCHAR(32) NOT NULL COMMENT '姓名',
    password VARCHAR(64) NOT NULL COMMENT '密码(MD5)',
    phone VARCHAR(20) COMMENT '手机号',
    sex VARCHAR(2) COMMENT '性别',
    status INT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '修改人',
    UNIQUE KEY idx_username (username)
) COMMENT '管理员';

-- 员工表
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    name VARCHAR(32) NOT NULL COMMENT '姓名',
    password VARCHAR(64) NOT NULL COMMENT '密码(MD5)',
    phone VARCHAR(20) COMMENT '手机号',
    sex VARCHAR(2) COMMENT '性别',
    id_number VARCHAR(18) COMMENT '身份证号',
    status INT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    create_user BIGINT COMMENT '创建人',
    update_user BIGINT COMMENT '修改人',
    UNIQUE KEY idx_username (username)
) COMMENT '员工';

-- 初始化后台登录账号 admin/123456 (MD5)，后台登录优先查 admin 表，查不到再查 employee 表
INSERT IGNORE INTO employee (username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user)
VALUES ('admin', '管理员', 'e10adc3949ba59abbe56e057f20f883e', '13800000000', '1', '110101199001010000', 1, NOW(), NOW(), 1, 1);

-- 菜品分类表
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

-- 套餐分类表
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

-- 初始化菜品分类
INSERT IGNORE INTO dish_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user) VALUES
(0, 1, '蜀味烤鱼', 1, 1, NOW(), NOW(), 1, 1),
(0, 1, '蜀味牛蛙', 2, 1, NOW(), NOW(), 1, 1),
(0, 1, '特色蒸菜', 3, 1, NOW(), NOW(), 1, 1),
(0, 1, '特色小炒', 4, 1, NOW(), NOW(), 1, 1),
(0, 1, '新鲜时蔬', 5, 1, NOW(), NOW(), 1, 1),
(0, 1, '水煮鱼', 6, 1, NOW(), NOW(), 1, 1),
(0, 1, '传统主食', 7, 1, NOW(), NOW(), 1, 1),
(0, 1, '酒水饮料', 8, 1, NOW(), NOW(), 1, 1);

-- 初始化套餐分类
INSERT IGNORE INTO setmeal_category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user) VALUES
(0, 2, '人气套餐', 1, 1, NOW(), NOW(), 1, 1),
(0, 2, '商务套餐', 2, 1, NOW(), NOW(), 1, 1);

-- 菜品表
CREATE TABLE IF NOT EXISTS dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) COMMENT '菜品名称',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10,2) COMMENT '价格',
    image VARCHAR(255) COMMENT '图片',
    detail_image VARCHAR(255) COMMENT '详情描述图',
    images VARCHAR(1000) COMMENT '商品多图，逗号分隔',
    description VARCHAR(255) COMMENT '描述',
    sort INT DEFAULT 0 COMMENT '商品排序',
    recommend INT DEFAULT 0 COMMENT '是否推荐首页 0否 1是',
    column_show INT DEFAULT 1 COMMENT '是否展示在栏目主页 0否 1是',
    status INT DEFAULT 1 COMMENT '状态 0停售 1起售',
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT
) COMMENT '菜品';

-- 菜品口味表
CREATE TABLE IF NOT EXISTS dish_flavor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dish_id BIGINT COMMENT '菜品ID',
    name VARCHAR(32) COMMENT '口味名称',
    value VARCHAR(255) COMMENT '口味数据'
) COMMENT '菜品口味';

-- 套餐表
CREATE TABLE IF NOT EXISTS setmeal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT COMMENT '分类ID',
    name VARCHAR(64) COMMENT '套餐名称',
    price DECIMAL(10,2) COMMENT '价格',
    status INT DEFAULT 1 COMMENT '状态 0停用 1启用',
    description VARCHAR(255) COMMENT '描述',
    image VARCHAR(255) COMMENT '图片',
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT
) COMMENT '套餐';

-- 套餐菜品关系表
CREATE TABLE IF NOT EXISTS setmeal_dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setmeal_id BIGINT COMMENT '套餐ID',
    dish_id BIGINT COMMENT '菜品ID',
    name VARCHAR(64) COMMENT '菜品名称',
    price DECIMAL(10,2) COMMENT '菜品原价',
    copies INT COMMENT '份数'
) COMMENT '套餐菜品关系';

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    openid VARCHAR(64) COMMENT '微信openid',
    name VARCHAR(32) COMMENT '姓名',
    phone VARCHAR(20) COMMENT '手机号',
    sex VARCHAR(2) COMMENT '性别',
    id_number VARCHAR(18) COMMENT '身份证号',
    avatar VARCHAR(255) COMMENT '头像',
    status INT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    total_order_count INT DEFAULT 0 COMMENT '累计订单数',
    total_amount DECIMAL(10,2) DEFAULT 0 COMMENT '累计消费金额',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME COMMENT '注册时间'
) COMMENT '用户';

-- 会员评论表
CREATE TABLE IF NOT EXISTS member_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '会员ID',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    rating INT DEFAULT 5 COMMENT '评分 1-5',
    images VARCHAR(1000) COMMENT '评论图片，逗号分隔',
    status INT DEFAULT 1 COMMENT '状态 0隐藏 1展示',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间'
) COMMENT '会员评论';

-- 会员收藏表
CREATE TABLE IF NOT EXISTS member_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '会员ID',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    create_time DATETIME COMMENT '收藏时间',
    UNIQUE KEY idx_member_favorite_dish (user_id, dish_id),
    UNIQUE KEY idx_member_favorite_setmeal (user_id, setmeal_id)
) COMMENT '会员收藏';

-- 地址簿表
CREATE TABLE IF NOT EXISTS address_book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID',
    consignee VARCHAR(32) COMMENT '收货人',
    phone VARCHAR(20) COMMENT '手机号',
    sex VARCHAR(2) COMMENT '性别 0女 1男',
    province_code VARCHAR(16) COMMENT '省级区划编号',
    province_name VARCHAR(32) COMMENT '省级名称',
    city_code VARCHAR(16) COMMENT '市级区划编号',
    city_name VARCHAR(32) COMMENT '市级名称',
    district_code VARCHAR(16) COMMENT '区级区划编号',
    district_name VARCHAR(32) COMMENT '区级名称',
    detail VARCHAR(255) COMMENT '详细地址',
    label VARCHAR(32) COMMENT '标签',
    is_default INT DEFAULT 0 COMMENT '是否默认 0否 1是'
) COMMENT '地址簿';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(64) COMMENT '订单号',
    status INT DEFAULT 1 COMMENT '订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消',
    user_id BIGINT COMMENT '用户ID',
    address_book_id BIGINT COMMENT '地址簿ID',
    order_time DATETIME COMMENT '下单时间',
    checkout_time DATETIME COMMENT '结账时间',
    pay_method INT COMMENT '支付方式 1微信 2支付宝',
    pay_status INT DEFAULT 0 COMMENT '支付状态 0未支付 1已支付 2退款',
    amount DECIMAL(10,2) COMMENT '实收金额',
    remark VARCHAR(255) COMMENT '备注',
    user_name VARCHAR(32) COMMENT '用户名',
    phone VARCHAR(20) COMMENT '手机号',
    address VARCHAR(255) COMMENT '地址',
    consignee VARCHAR(32) COMMENT '收货人',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    rejection_reason VARCHAR(255) COMMENT '拒绝原因',
    cancel_time DATETIME COMMENT '取消时间',
    estimated_delivery_time DATETIME COMMENT '预计送达时间',
    delivery_status INT COMMENT '配送状态 1立即送出 0选择具体时间',
    delivery_time DATETIME COMMENT '送达时间',
    pack_amount INT DEFAULT 0 COMMENT '打包费',
    tableware_number INT DEFAULT 0 COMMENT '餐具数量',
    tableware_status INT COMMENT '餐具数量状态 1按餐量提供 0选择具体数量'
) COMMENT '订单';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) COMMENT '名称',
    order_id BIGINT COMMENT '订单ID',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    dish_flavor VARCHAR(255) COMMENT '口味',
    number INT COMMENT '数量',
    amount DECIMAL(10,2) COMMENT '金额',
    image VARCHAR(255) COMMENT '图片'
) COMMENT '订单明细';

-- 购物车表
CREATE TABLE IF NOT EXISTS shopping_cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) COMMENT '名称',
    user_id BIGINT COMMENT '用户ID',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    dish_flavor VARCHAR(255) COMMENT '口味',
    number INT NOT NULL DEFAULT 1 COMMENT '数量',
    amount DECIMAL(10,2) COMMENT '金额',
    image VARCHAR(255) COMMENT '图片',
    create_time DATETIME COMMENT '创建时间'
) COMMENT '购物车';

-- ============================================
-- 座位管理与扫码点餐 - 新增表结构
-- ============================================

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