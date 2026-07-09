-- ============================================
-- 老宋速达 (sky-take-out) 数据库初始化脚本
-- ============================================

USE sky_take_out;

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
CREATE TABLE admin (
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

-- 初始化管理员账号 admin/123456 (MD5)
INSERT INTO admin (username, name, password, phone, sex, status, create_time, update_time, create_user, update_user)
VALUES ('admin', '管理员', 'e10adc3949ba59abbe56e057f20f883e', '13800000000', '1', 1, NOW(), NOW(), 1, 1);

-- 员工表
CREATE TABLE employee (
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

-- 分类表
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID 0表示一级分类',
    type INT COMMENT '类型 1菜品分类 2套餐分类',
    name VARCHAR(32) COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_user BIGINT,
    update_user BIGINT,
    UNIQUE KEY idx_type_name (type, name)
) COMMENT '分类';

-- 初始化菜品分类
INSERT IGNORE INTO category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user) VALUES
(0, 1, '热销推荐', 1, 1, NOW(), NOW(), 1, 1),
(0, 1, '主食盖饭', 2, 1, NOW(), NOW(), 1, 1),
(0, 1, '特色小炒', 3, 1, NOW(), NOW(), 1, 1),
(0, 1, '面食粉类', 4, 1, NOW(), NOW(), 1, 1),
(0, 1, '汉堡炸鸡', 5, 1, NOW(), NOW(), 1, 1),
(0, 1, '烧烤夜宵', 6, 1, NOW(), NOW(), 1, 1),
(0, 1, '凉菜卤味', 7, 1, NOW(), NOW(), 1, 1),
(0, 1, '汤粥炖品', 8, 1, NOW(), NOW(), 1, 1),
(0, 1, '甜品小吃', 9, 1, NOW(), NOW(), 1, 1),
(0, 1, '饮品奶茶', 10, 1, NOW(), NOW(), 1, 1);

-- 初始化套餐分类
INSERT IGNORE INTO category (parent_id, type, name, sort, status, create_time, update_time, create_user, update_user) VALUES
(0, 2, '人气套餐', 11, 1, NOW(), NOW(), 1, 1),
(0, 2, '商务套餐', 12, 1, NOW(), NOW(), 1, 1),
(0, 2, '单人套餐', 13, 1, NOW(), NOW(), 1, 1),
(0, 2, '双人套餐', 14, 1, NOW(), NOW(), 1, 1),
(0, 2, '家庭套餐', 15, 1, NOW(), NOW(), 1, 1);

-- 菜品表
CREATE TABLE dish (
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
CREATE TABLE dish_flavor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dish_id BIGINT COMMENT '菜品ID',
    name VARCHAR(32) COMMENT '口味名称',
    value VARCHAR(255) COMMENT '口味数据'
) COMMENT '菜品口味';

-- 套餐表
CREATE TABLE setmeal (
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
CREATE TABLE setmeal_dish (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setmeal_id BIGINT COMMENT '套餐ID',
    dish_id BIGINT COMMENT '菜品ID',
    name VARCHAR(64) COMMENT '菜品名称',
    price DECIMAL(10,2) COMMENT '菜品原价',
    copies INT COMMENT '份数'
) COMMENT '套餐菜品关系';

-- 用户表
CREATE TABLE user (
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
CREATE TABLE member_comment (
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
CREATE TABLE member_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '会员ID',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    create_time DATETIME COMMENT '收藏时间',
    UNIQUE KEY idx_member_favorite_dish (user_id, dish_id),
    UNIQUE KEY idx_member_favorite_setmeal (user_id, setmeal_id)
) COMMENT '会员收藏';

-- 地址簿表
CREATE TABLE address_book (
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
CREATE TABLE orders (
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
CREATE TABLE order_detail (
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
CREATE TABLE shopping_cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) COMMENT '名称',
    user_id BIGINT COMMENT '用户ID',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    dish_flavor VARCHAR(255) COMMENT '口味',
    number INT COMMENT '数量',
    amount DECIMAL(10,2) COMMENT '金额',
    image VARCHAR(255) COMMENT '图片',
    create_time DATETIME COMMENT '创建时间'
) COMMENT '购物车';
