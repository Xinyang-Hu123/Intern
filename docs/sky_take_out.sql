mysqldump: [Warning] Using a password on the command line interface can be insecure.
Warning: A partial dump from a server that has GTIDs will by default include the GTIDs of all transactions, even those that changed suppressed parts of the database. If you don't want to restore GTIDs, pass --set-gtid-purged=OFF. To make a complete dump, pass --all-databases --triggers --routines --events. 
Warning: A dump from a server that has GTIDs enabled will by default include the GTIDs of all transactions, even those that were executed during its extraction and might not be represented in the dumped data. This might result in an inconsistent data dump. 
In order to ensure a consistent backup of the database, pass --single-transaction or --lock-all-tables or --source-data. 
-- MySQL dump 10.13  Distrib 9.5.0, for macos15 (arm64)
--
-- Host: localhost    Database: sky_take_out
-- ------------------------------------------------------
-- Server version	9.5.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '467736ca-c9f3-11f0-a482-59b2701b35b9:1-388';

--
-- Current Database: `sky_take_out`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `sky_take_out` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `sky_take_out`;

--
-- Table structure for table `address_book`
--

DROP TABLE IF EXISTS `address_book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address_book` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `consignee` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '收货人',
  `sex` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '性别',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '手机号',
  `province_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '省级区划编号',
  `province_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '省级名称',
  `city_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '市级区划编号',
  `city_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '市级名称',
  `district_code` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '区级区划编号',
  `district_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '区级名称',
  `detail` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '详细地址',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '标签',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '默认 0 否 1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='地址簿';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address_book`
--

LOCK TABLES `address_book` WRITE;
/*!40000 ALTER TABLE `address_book` DISABLE KEYS */;
INSERT INTO `address_book` VALUES (2,4,'12','0','19999999999','11','北京市','1101','市辖区','110102','西城区','北京北京','3',1);
/*!40000 ALTER TABLE `address_book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int DEFAULT NULL COMMENT '类型   1 菜品分类 2 套餐分类',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '顺序',
  `status` int DEFAULT NULL COMMENT '分类状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_category_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='菜品及套餐分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (11,1,'酒水饮料',10,1,'2022-06-09 22:09:18','2022-06-09 22:09:18',1,1),(12,1,'传统主食',9,1,'2022-06-09 22:09:32','2022-06-09 22:18:53',1,1),(13,2,'人气套餐',12,1,'2022-06-09 22:11:38','2022-06-10 11:04:40',1,1),(15,2,'商务套餐',13,1,'2022-06-09 22:14:10','2022-06-10 11:04:48',1,1),(16,1,'蜀味烤鱼',4,1,'2022-06-09 22:15:37','2022-08-31 14:27:25',1,1),(17,1,'蜀味牛蛙',5,1,'2022-06-09 22:16:14','2022-08-31 14:39:44',1,1),(18,1,'特色蒸菜',6,1,'2022-06-09 22:17:42','2022-06-09 22:17:42',1,1),(19,1,'新鲜时蔬',7,1,'2022-06-09 22:18:12','2022-06-09 22:18:28',1,1),(20,1,'水煮鱼',8,1,'2022-06-09 22:22:29','2022-06-09 22:23:45',1,1),(21,1,'汤类',11,1,'2022-06-10 10:51:47','2022-06-10 10:51:47',1,1);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dish`
--

DROP TABLE IF EXISTS `dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '菜品名称',
  `category_id` bigint NOT NULL COMMENT '菜品分类id',
  `price` decimal(10,2) DEFAULT NULL COMMENT '菜品价格',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '描述信息',
  `status` int DEFAULT '1' COMMENT '0 停售 1 起售',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_dish_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='菜品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish`
--

LOCK TABLES `dish` WRITE;
/*!40000 ALTER TABLE `dish` DISABLE KEYS */;
INSERT INTO `dish` VALUES (46,'王老吉',11,6.00,'1.png','',1,'2022-06-09 22:40:47','2022-06-09 22:40:47',1,1),(47,'北冰洋',11,4.00,'2.png','还是小时候的味道',1,'2022-06-10 09:18:49','2022-06-10 09:18:49',1,1),(48,'雪花啤酒',11,4.00,'3.png','',1,'2022-06-10 09:22:54','2022-06-10 09:22:54',1,1),(49,'米饭',12,2.00,'4.png','精选五常大米',1,'2022-06-10 09:30:17','2022-06-10 09:30:17',1,1),(50,'馒头',12,1.00,'5.png','优质面粉',1,'2022-06-10 09:34:28','2022-06-10 09:34:28',1,1),(51,'老坛酸菜鱼',20,56.00,'6.png','原料：汤，草鱼，酸菜',1,'2022-06-10 09:40:51','2022-06-10 09:40:51',1,1),(52,'经典酸菜鮰鱼',20,66.00,'7.png','原料：酸菜，江团，鮰鱼',1,'2022-06-10 09:46:02','2022-06-10 09:46:02',1,1),(53,'蜀味水煮草鱼',20,38.00,'8.png','原料：草鱼，汤',1,'2022-06-10 09:48:37','2022-06-10 09:48:37',1,1),(54,'清炒小油菜',19,18.00,'9.png','原料：小油菜',1,'2022-06-10 09:51:46','2022-06-10 09:51:46',1,1),(55,'蒜蓉娃娃菜',19,18.00,'10.png','原料：蒜，娃娃菜',1,'2022-06-10 09:53:37','2022-06-10 09:53:37',1,1),(56,'清炒西兰花',19,18.00,'11.png','原料：西兰花',1,'2022-06-10 09:55:44','2022-06-10 09:55:44',1,1),(57,'炝炒圆白菜',19,18.00,'12.png','原料：圆白菜',1,'2022-06-10 09:58:35','2022-06-10 09:58:35',1,1),(58,'清蒸鲈鱼',18,98.00,'13.png','原料：鲈鱼',1,'2022-06-10 10:12:28','2022-06-10 10:12:28',1,1),(59,'东坡肘子',18,138.00,'14.png','原料：猪肘棒',1,'2022-06-10 10:24:03','2022-06-10 10:24:03',1,1),(60,'梅菜扣肉',18,58.00,'15.png','原料：猪肉，梅菜',1,'2022-06-10 10:26:03','2022-06-10 10:26:03',1,1),(61,'剁椒鱼头',18,66.00,'16.png','原料：鲢鱼，剁椒',1,'2022-06-10 10:28:54','2022-06-10 10:28:54',1,1),(62,'金汤酸菜牛蛙',17,88.00,'17.png','原料：鲜活牛蛙，酸菜',1,'2022-06-10 10:33:05','2022-06-10 10:33:05',1,1),(63,'香锅牛蛙',17,88.00,'18.png','配料：鲜活牛蛙，莲藕，青笋',1,'2022-06-10 10:35:40','2022-06-10 10:35:40',1,1),(64,'馋嘴牛蛙',17,88.00,'19.png','配料：鲜活牛蛙，丝瓜，黄豆芽',1,'2022-06-10 10:37:52','2022-06-10 10:37:52',1,1),(65,'草鱼2斤',16,68.00,'20.png','原料：草鱼，黄豆芽，莲藕',1,'2022-06-10 10:41:08','2022-06-10 10:41:08',1,1),(66,'江团鱼2斤',16,119.00,'21.png','配料：江团鱼，黄豆芽，莲藕',1,'2022-06-10 10:42:42','2022-06-10 10:42:42',1,1),(67,'鮰鱼2斤',16,72.00,'22.png','原料：鮰鱼，黄豆芽，莲藕',1,'2022-06-10 10:43:56','2022-06-10 10:43:56',1,1),(68,'鸡蛋汤',21,4.00,'23.png','配料：鸡蛋，紫菜',1,'2022-06-10 10:54:25','2022-06-10 10:54:25',1,1),(69,'平菇豆腐汤',21,6.00,'24.png','配料：豆腐，平菇',1,'2022-06-10 10:55:02','2026-06-30 21:25:33',1,1),(71,'测试',12,10.00,'25.png','测试产品',1,'2026-03-11 11:05:16','2026-03-11 11:05:16',1,1);
/*!40000 ALTER TABLE `dish` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dish_flavor`
--

DROP TABLE IF EXISTS `dish_flavor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish_flavor` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dish_id` bigint NOT NULL COMMENT '菜品',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '口味名称',
  `value` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '口味数据list',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='菜品口味关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish_flavor`
--

LOCK TABLES `dish_flavor` WRITE;
/*!40000 ALTER TABLE `dish_flavor` DISABLE KEYS */;
INSERT INTO `dish_flavor` VALUES (40,10,'甜味','[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]'),(41,7,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(42,7,'温度','[\"热饮\",\"常温\",\"去冰\",\"少冰\",\"多冰\"]'),(45,6,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(46,6,'辣度','[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),(47,5,'辣度','[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),(48,5,'甜味','[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]'),(49,2,'甜味','[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]'),(50,4,'甜味','[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]'),(51,3,'甜味','[\"无糖\",\"少糖\",\"半糖\",\"多糖\",\"全糖\"]'),(52,3,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(86,52,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(87,52,'辣度','[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),(88,51,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(89,51,'辣度','[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),(92,53,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(93,53,'辣度','[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),(94,54,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\"]'),(95,56,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(96,57,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(97,60,'忌口','[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]'),(101,66,'辣度','[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),(102,67,'辣度','[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]'),(103,65,'辣度','[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]');
/*!40000 ALTER TABLE `dish_flavor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '姓名',
  `username` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '密码',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '身份证号',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='员工信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'管理员','admin','e10adc3949ba59abbe56e057f20f883e','13812312312','1','110101199001010047',1,'2022-02-15 15:51:20','2022-02-17 09:16:20',10,1),(2,'武','樊国琴','e10adc3949ba59abbe56e057f20f883e','17616032560','女','110114333333332222',1,'2026-02-02 19:25:18','2026-02-06 16:22:58',10,1),(3,'核心','123','e10adc3949ba59abbe56e057f20f883e','13324569876','1','138229193847580968',1,'2026-02-03 11:25:47','2026-02-03 11:25:47',10,10),(6,'李四我','lisi','e10adc3949ba59abbe56e057f20f883e','13748592041','1','110114255687580921',1,'2026-02-03 15:13:12','2026-02-03 15:13:12',10,10),(7,'王五','zhangwu','e10adc3949ba59abbe56e057f20f883e','13859577449','1','111222333344445555',1,'2026-02-03 15:45:01','2026-02-03 15:45:01',1,1),(8,'嘿嘿','wangwu','e10adc3949ba59abbe56e057f20f883e','13748478817','1','111888222288884395',1,'2026-02-03 15:47:20','2026-03-05 14:31:54',1,1);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_detail`
--

DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '名字',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
  `order_id` bigint NOT NULL COMMENT '订单id',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '口味',
  `number` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_detail`
--

LOCK TABLES `order_detail` WRITE;
/*!40000 ALTER TABLE `order_detail` DISABLE KEYS */;
INSERT INTO `order_detail` VALUES (5,'北冰洋','https://sky-itcast.oss-cn-beijing.aliyuncs.com/4451d4be-89a2-4939-9c69-3a87151cb979.png',4,47,NULL,NULL,1,4.00),(6,'王老吉','https://sky-itcast.oss-cn-beijing.aliyuncs.com/41bfcacf-7ad4-4927-8b26-df366553a94c.png',4,46,NULL,NULL,1,6.00),(7,'鮰鱼2斤','https://sky-itcast.oss-cn-beijing.aliyuncs.com/8cfcc576-4b66-4a09-ac68-ad5b273c2590.png',5,67,NULL,'重辣',1,72.00),(8,'江团鱼2斤','https://sky-itcast.oss-cn-beijing.aliyuncs.com/a101a1e9-8f8b-47b2-afa4-1abd47ea0a87.png',6,66,NULL,'重辣',1,119.00),(9,'江团鱼2斤','https://sky-itcast.oss-cn-beijing.aliyuncs.com/a101a1e9-8f8b-47b2-afa4-1abd47ea0a87.png',7,66,NULL,'重辣',1,119.00),(10,'蜀味水煮草鱼','8.png',8,53,NULL,'不要香菜,重辣',1,38.00),(11,'wejfh','https://sky-heima-xinyang.oss-cn-beijing.aliyuncs.com/e6f0f2fe-e25a-408f-ad0b-3b2dbcb5f9eb.png',8,NULL,36,NULL,1,888.00),(12,'经典酸菜鮰鱼','7.png',9,52,NULL,'不辣,不要香菜',1,66.00),(13,'鮰鱼2斤','22.png',10,67,NULL,'重辣',1,72.00),(14,'江团鱼2斤','21.png',11,66,NULL,'不辣',1,119.00);
/*!40000 ALTER TABLE `order_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `number` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '订单号',
  `status` int NOT NULL DEFAULT '1' COMMENT '订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款',
  `user_id` bigint NOT NULL COMMENT '下单用户',
  `address_book_id` bigint NOT NULL COMMENT '地址id',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `checkout_time` datetime DEFAULT NULL COMMENT '结账时间',
  `pay_method` int NOT NULL DEFAULT '1' COMMENT '支付方式 1微信,2支付宝',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态 0未支付 1已支付 2退款',
  `amount` decimal(10,2) NOT NULL COMMENT '实收金额',
  `remark` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '备注',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '手机号',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '地址',
  `user_name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '用户名称',
  `consignee` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '收货人',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '订单取消原因',
  `rejection_reason` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '订单拒绝原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '订单取消时间',
  `estimated_delivery_time` datetime DEFAULT NULL COMMENT '预计送达时间',
  `delivery_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '配送状态  1立即送出  0选择具体时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `pack_amount` int DEFAULT NULL COMMENT '打包费',
  `tableware_number` int DEFAULT NULL COMMENT '餐具数量',
  `tableware_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '餐具数量状态  1按餐量提供  0选择具体数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (4,'1782822905790',5,4,2,'2026-06-30 20:35:06','2026-06-30 20:35:08',1,1,18.00,'','19999999999','北京北京',NULL,'12',NULL,NULL,NULL,'2026-06-30 21:35:00',0,'2026-06-30 20:51:05',2,0,0),(5,'1782823207172',5,4,2,'2026-06-30 20:40:07','2026-06-30 20:40:08',1,1,79.00,'','19999999999','北京北京',NULL,'12',NULL,NULL,NULL,'2026-06-30 21:40:00',0,'2026-06-30 20:51:05',1,0,0),(6,'1782823519086',5,4,2,'2026-06-30 20:45:19','2026-06-30 20:45:21',1,1,126.00,'','19999999999','北京北京',NULL,'12',NULL,NULL,NULL,'2026-06-30 21:45:00',0,'2026-06-30 20:51:04',1,0,0),(7,'1782824497820',5,4,2,'2026-06-30 21:01:38','2026-06-30 21:01:39',1,1,126.00,'','19999999999','北京北京',NULL,'12',NULL,NULL,NULL,'2026-06-30 22:01:00',0,'2026-06-30 21:02:07',1,0,0),(8,'1782827049008',5,4,2,'2026-06-30 21:44:09','2026-06-30 21:44:11',1,1,934.00,'','19999999999','北京北京',NULL,'12',NULL,NULL,NULL,'2026-06-30 22:44:00',0,'2026-06-30 21:44:42',2,0,0),(9,'1782830211097',2,4,2,'2026-06-30 22:36:51','2026-06-30 22:36:53',1,1,73.00,'','19999999999','北京北京',NULL,'12',NULL,NULL,NULL,'2026-06-30 23:36:00',0,NULL,1,0,0),(10,'1782830236199',2,4,2,'2026-06-30 22:37:16','2026-06-30 22:37:18',1,1,79.00,'','19999999999','北京北京',NULL,'12',NULL,NULL,NULL,'2026-06-30 23:37:00',0,NULL,1,0,0),(11,'1782830317114',2,4,2,'2026-06-30 22:38:37','2026-06-30 22:38:39',1,1,126.00,'','19999999999','北京北京',NULL,'12',NULL,NULL,NULL,'2026-06-30 23:38:00',0,NULL,1,0,0);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `setmeal`
--

DROP TABLE IF EXISTS `setmeal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setmeal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint NOT NULL COMMENT '菜品分类id',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '套餐名称',
  `price` decimal(10,2) NOT NULL COMMENT '套餐价格',
  `status` int DEFAULT '1' COMMENT '售卖状态 0:停售 1:起售',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '描述信息',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` bigint DEFAULT NULL COMMENT '创建人',
  `update_user` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_setmeal_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='套餐';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `setmeal`
--

LOCK TABLES `setmeal` WRITE;
/*!40000 ALTER TABLE `setmeal` DISABLE KEYS */;
INSERT INTO `setmeal` VALUES (35,13,'qq',123.00,0,'','https://sky-heima-xinyang.oss-cn-beijing.aliyuncs.com/28bd637b-2cba-41bc-afe3-8e464c0220bb.png','2026-03-15 19:54:12','2026-03-15 19:54:12',1,1),(36,15,'wejfh',888.00,1,'','https://sky-heima-xinyang.oss-cn-beijing.aliyuncs.com/e6f0f2fe-e25a-408f-ad0b-3b2dbcb5f9eb.png','2026-03-15 19:54:35','2026-03-15 20:44:04',1,1);
/*!40000 ALTER TABLE `setmeal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `setmeal_dish`
--

DROP TABLE IF EXISTS `setmeal_dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `setmeal_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '菜品名称 （冗余字段）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '菜品单价（冗余字段）',
  `copies` int DEFAULT NULL COMMENT '菜品份数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='套餐菜品关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `setmeal_dish`
--

LOCK TABLES `setmeal_dish` WRITE;
/*!40000 ALTER TABLE `setmeal_dish` DISABLE KEYS */;
INSERT INTO `setmeal_dish` VALUES (47,36,65,'草鱼2斤',68.00,1),(48,36,66,'江团鱼2斤',119.00,1),(49,36,67,'鮰鱼2斤',72.00,1);
/*!40000 ALTER TABLE `setmeal_dish` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shopping_cart`
--

DROP TABLE IF EXISTS `shopping_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shopping_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '商品名称',
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '图片',
  `user_id` bigint NOT NULL COMMENT '主键',
  `dish_id` bigint DEFAULT NULL COMMENT '菜品id',
  `setmeal_id` bigint DEFAULT NULL COMMENT '套餐id',
  `dish_flavor` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '口味',
  `number` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='购物车';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shopping_cart`
--

LOCK TABLES `shopping_cart` WRITE;
/*!40000 ALTER TABLE `shopping_cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `shopping_cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid` varchar(45) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '微信用户唯一标识',
  `name` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '姓名',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '手机号',
  `sex` varchar(2) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '性别',
  `id_number` varchar(18) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '身份证号',
  `avatar` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL COMMENT '头像',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='用户信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (4,'ouy1a3Yx1r6W5FFuYki6-C6bShHI',NULL,NULL,NULL,NULL,NULL,'2026-06-30 16:41:58');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'sky_take_out'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 22:42:17
