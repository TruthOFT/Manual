/*
 Navicat Premium Dump SQL

 Source Server         : MyDB
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : manual_mall

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 27/04/2026 11:50:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '购物车id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cart_userId`(`userId` ASC) USING BTREE,
  CONSTRAINT `fk_cart_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (3100000000000007001, 3100000000000001004, '2026-04-12 19:10:00', '2026-04-13 09:00:00', 0);
INSERT INTO `cart` VALUES (3100000000000007002, 3100000000000001005, '2026-04-12 20:20:00', '2026-04-13 09:05:00', 0);

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '购物车明细id',
  `cartId` bigint UNSIGNED NOT NULL COMMENT '购物车id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `skuId` bigint UNSIGNED NOT NULL COMMENT 'sku id',
  `quantity` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '购买数量',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '加入购物车时价格',
  `selected` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否选中',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cart_item_cartId_skuId`(`cartId` ASC, `skuId` ASC) USING BTREE,
  INDEX `idx_cart_item_userId`(`userId` ASC) USING BTREE,
  INDEX `idx_cart_item_productId`(`productId` ASC) USING BTREE,
  INDEX `fk_cart_item_skuId`(`skuId` ASC) USING BTREE,
  CONSTRAINT `fk_cart_item_cartId` FOREIGN KEY (`cartId`) REFERENCES `cart` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_cart_item_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_cart_item_skuId` FOREIGN KEY (`skuId`) REFERENCES `product_sku` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_cart_item_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '购物车明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES (3100000000000007101, 3100000000000007001, 3100000000000001004, 3100000000000004003, 3100000000000005005, 1, 168.00, 1, '2026-04-12 19:20:00', '2026-04-13 09:00:00', 0);
INSERT INTO `cart_item` VALUES (3100000000000007102, 3100000000000007001, 3100000000000001004, 3100000000000004004, 3100000000000005008, 2, 128.00, 1, '2026-04-12 19:25:00', '2026-04-13 09:00:00', 0);
INSERT INTO `cart_item` VALUES (3100000000000007103, 3100000000000007002, 3100000000000001005, 3100000000000004001, 3100000000000005002, 1, 129.00, 1, '2026-04-12 20:30:00', '2026-04-13 09:05:00', 0);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '分类id',
  `parentId` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父级分类id',
  `categoryName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `categoryIcon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类图标',
  `categoryDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `categoryLevel` tinyint NOT NULL DEFAULT 1 COMMENT '分类层级',
  `sortOrder` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `isEnable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_parentId`(`parentId` ASC) USING BTREE,
  INDEX `idx_category_sortOrder`(`sortOrder` ASC) USING BTREE,
  CONSTRAINT `fk_category_parentId` FOREIGN KEY (`parentId`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (2045427688540012546, NULL, 'ttt', NULL, NULL, 1, 0, 0, '2026-04-18 17:02:17', '2026-04-18 17:02:17', 0);
INSERT INTO `category` VALUES (3100000000000003001, NULL, '陶艺', 'https://example.com/category/pottery.png', '手作陶杯、茶器与摆件', 1, 1, 1, '2026-04-01 09:00:00', '2026-04-01 09:00:00', 0);
INSERT INTO `category` VALUES (3100000000000003002, NULL, '木作', '/upload/category/7bc787600cc642edb3d02daa31030242.png', '木盒、木盘与家居小件', 1, 2, 1, '2026-04-01 09:05:00', '2026-04-01 09:05:00', 0);
INSERT INTO `category` VALUES (3100000000000003003, NULL, '刺绣', 'https://example.com/category/embroidery.png', '团扇、挂饰与布艺刺绣', 1, 3, 1, '2026-04-01 09:10:00', '2026-04-01 09:10:00', 0);
INSERT INTO `category` VALUES (3100000000000003004, NULL, '编织', 'https://example.com/category/weaving.png', '藤编、草编与软装器具', 1, 4, 1, '2026-04-01 09:15:00', '2026-04-01 09:15:00', 0);
INSERT INTO `category` VALUES (3100000000000003005, 3100000000000003001, '茶器杯具', 'https://example.com/category/tea_ware.png', '适合日常饮茶和咖啡场景', 2, 10, 1, '2026-04-01 09:20:00', '2026-04-01 09:20:00', 0);
INSERT INTO `category` VALUES (3100000000000003006, 3100000000000003002, '木盒收纳', 'https://example.com/category/storage_box.png', '礼盒、首饰盒与桌面收纳', 2, 20, 1, '2026-04-01 09:25:00', '2026-04-01 09:25:00', 0);
INSERT INTO `category` VALUES (3100000000000003007, 3100000000000003003, '团扇配饰', 'https://example.com/category/fan.png', '刺绣团扇与节令礼物', 2, 30, 1, '2026-04-01 09:30:00', '2026-04-01 09:30:00', 0);
INSERT INTO `category` VALUES (3100000000000003008, 3100000000000003004, '藤编收纳', 'https://example.com/category/rattan.png', '藤编托盘与居家收纳', 2, 40, 1, '2026-04-01 09:35:00', '2026-04-01 09:35:00', 0);

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '优惠券id',
  `couponName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '优惠券名称',
  `couponType` tinyint NOT NULL DEFAULT 1 COMMENT '类型：1满减 2折扣',
  `thresholdAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '使用门槛金额',
  `discountAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `discountRate` decimal(5, 2) NULL DEFAULT NULL COMMENT '折扣比例',
  `totalCount` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '发行数量',
  `receiveCount` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '已领取数量',
  `usedCount` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '已使用数量',
  `startTime` datetime NOT NULL COMMENT '开始时间',
  `endTime` datetime NOT NULL COMMENT '结束时间',
  `couponStatus` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coupon_status`(`couponStatus` ASC) USING BTREE,
  INDEX `idx_coupon_time`(`startTime` ASC, `endTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '优惠券表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (2047616968699961346, 'testc', 2, 0.00, 100.00, 2.00, 100, 2, 1, '2026-04-24 00:00:00', '2027-04-24 00:00:00', 1, '2026-04-24 18:01:42', '2026-04-25 18:25:21', 1);
INSERT INTO `coupon` VALUES (2047985449119309826, '测试优惠券', 2, 1.00, 1000.00, 1.00, 100, 2, 2, '2026-04-22 18:25:42', '2026-04-30 18:25:50', 1, '2026-04-25 18:25:55', '2026-04-25 18:54:36', 0);
INSERT INTO `coupon` VALUES (2047988929322143746, '测试优惠券1', 2, 100.00, 0.00, 1.00, 100, 1, 1, '2026-04-24 18:39:36', '2026-04-30 18:39:40', 1, '2026-04-25 18:39:45', '2026-04-26 18:00:25', 0);
INSERT INTO `coupon` VALUES (2047989447633260545, '满减测试', 1, 100.00, 5.00, NULL, 100, 1, 1, '2026-04-15 18:39:45', '2026-04-30 18:39:45', 1, '2026-04-25 18:41:48', '2026-04-25 18:42:22', 0);

-- ----------------------------
-- Table structure for coupon_receive
-- ----------------------------
DROP TABLE IF EXISTS `coupon_receive`;
CREATE TABLE `coupon_receive`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '优惠券领取id',
  `couponId` bigint UNSIGNED NOT NULL COMMENT '优惠券id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '顾客用户id',
  `receiveTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `useStatus` tinyint NOT NULL DEFAULT 0 COMMENT '使用状态：0未使用 1已使用 2已过期',
  `useTime` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `orderId` bigint UNSIGNED NULL DEFAULT NULL COMMENT '订单id',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coupon_receive_coupon_id`(`couponId` ASC) USING BTREE,
  INDEX `idx_coupon_receive_user_id`(`userId` ASC) USING BTREE,
  INDEX `idx_coupon_receive_order_id`(`orderId` ASC) USING BTREE,
  CONSTRAINT `fk_coupon_receive_coupon_id` FOREIGN KEY (`couponId`) REFERENCES `coupon` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_coupon_receive_order_id` FOREIGN KEY (`orderId`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_coupon_receive_user_id` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '优惠券领取表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon_receive
-- ----------------------------
INSERT INTO `coupon_receive` VALUES (2047617310112112642, 2047616968699961346, 3100000000000001005, '2026-04-24 18:03:04', 0, NULL, NULL, '2026-04-24 18:03:04', '2026-04-24 18:03:04', 0);
INSERT INTO `coupon_receive` VALUES (2047623247531110402, 2047616968699961346, 2044038386325221377, '2026-04-24 18:26:39', 1, '2026-04-24 18:26:58', 2047623327503904770, '2026-04-24 18:26:39', '2026-04-24 18:26:58', 0);
INSERT INTO `coupon_receive` VALUES (2047985524088299521, 2047985449119309826, 2044038386325221377, '2026-04-25 18:26:13', 1, '2026-04-25 18:35:59', 2047987984651636737, '2026-04-25 18:26:13', '2026-04-25 18:35:59', 0);
INSERT INTO `coupon_receive` VALUES (2047989001225097217, 2047988929322143746, 2044038386325221377, '2026-04-25 18:40:02', 1, '2026-04-26 18:00:25', 2048341421398499330, '2026-04-25 18:40:02', '2026-04-26 18:00:25', 0);
INSERT INTO `coupon_receive` VALUES (2047989007596244994, 2047985449119309826, 2044038386325221377, '2026-04-25 18:40:03', 1, '2026-04-25 18:54:36', 2047992666899943426, '2026-04-25 18:40:03', '2026-04-25 18:54:36', 0);
INSERT INTO `coupon_receive` VALUES (2047989479593857026, 2047989447633260545, 2044038386325221377, '2026-04-25 18:41:56', 1, '2026-04-25 18:42:22', 2047989590973599745, '2026-04-25 18:41:56', '2026-04-25 18:42:22', 0);

-- ----------------------------
-- Table structure for customer_profile
-- ----------------------------
DROP TABLE IF EXISTS `customer_profile`;
CREATE TABLE `customer_profile`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '顾客资料id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `customerLevel` tinyint NOT NULL DEFAULT 1 COMMENT '顾客等级',
  `points` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '积分',
  `totalAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计消费金额',
  `orderCount` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '累计订单数',
  `preferenceTags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '偏好标签',
  `lastPurchaseTime` datetime NULL DEFAULT NULL COMMENT '最近购买时间',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_customer_profile_user_id`(`userId` ASC) USING BTREE,
  CONSTRAINT `fk_customer_profile_user_id` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '顾客资料表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of customer_profile
-- ----------------------------

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '订单明细id',
  `orderId` bigint UNSIGNED NOT NULL COMMENT '订单id',
  `orderNo` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `skuId` bigint UNSIGNED NOT NULL COMMENT 'sku id',
  `productName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称快照',
  `skuName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'sku名称快照',
  `productCover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品主图快照',
  `specText` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规格描述快照',
  `quantity` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '购买数量',
  `salePrice` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '成交单价',
  `totalAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '成交总额',
  `refundStatus` tinyint NOT NULL DEFAULT 0 COMMENT '退款状态：0-无 1-申请中 2-已退款',
  `reviewStatus` tinyint NOT NULL DEFAULT 0 COMMENT '评价状态：0-未评 1-已评',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_item_orderId`(`orderId` ASC) USING BTREE,
  INDEX `idx_order_item_userId`(`userId` ASC) USING BTREE,
  INDEX `idx_order_item_productId`(`productId` ASC) USING BTREE,
  INDEX `fk_order_item_skuId`(`skuId` ASC) USING BTREE,
  CONSTRAINT `fk_order_item_orderId` FOREIGN KEY (`orderId`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_item_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_item_skuId` FOREIGN KEY (`skuId`) REFERENCES `product_sku` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_order_item_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (2046858049341927426, 2046858049341927425, 'mo20260422154602927425', 2044038386325221377, 3100000000000004001, 3100000000000005002, '柴烧釉陶马克杯', '礼盒装双杯组', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '330ml*2', 10, 129.00, 1290.00, 0, 0, '2026-04-22 15:46:02', '2026-04-22 15:46:02', 0);
INSERT INTO `order_item` VALUES (2046933954940698626, 2046933954940698625, 'mo20260422204739698625', 2044038386325221377, 3100000000000004002, 3100000000000005004, '黑胡桃榫卯首饰盒', '刻字定制款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '18cm x 12cm，支持盒盖刻字', 1, 299.00, 299.00, 0, 0, '2026-04-22 20:47:39', '2026-04-22 20:47:39', 0);
INSERT INTO `order_item` VALUES (2046934165289238531, 2046934165289238530, 'mo20260422204829238530', 2044038386325221377, 3100000000000004001, 3100000000000005002, '柴烧釉陶马克杯', '礼盒装双杯组', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '330ml*2', 1, 129.00, 129.00, 0, 0, '2026-04-22 20:48:29', '2026-04-22 20:48:29', 0);
INSERT INTO `order_item` VALUES (2046935098484129794, 2046935098484129793, 'mo20260422205212129793', 2044038386325221377, 3100000000000004003, 3100000000000005006, '苏绣花影真丝团扇', '题字定制款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm，支持题字', 6, 228.00, 1368.00, 0, 0, '2026-04-22 20:52:12', '2026-04-22 20:52:12', 0);
INSERT INTO `order_item` VALUES (2046937661564583938, 2046937661564583937, 'mo20260422210223583937', 2044038386325221377, 3100000000000004003, 3100000000000005005, '苏绣花影真丝团扇', '月白花影款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm', 5, 168.00, 840.00, 0, 0, '2026-04-22 21:02:23', '2026-04-22 21:02:23', 0);
INSERT INTO `order_item` VALUES (2046940980848713731, 2046940980848713730, 'mo20260422211534713730', 2044038386325221377, 3100000000000004003, 3100000000000005005, '苏绣花影真丝团扇', '月白花影款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm', 1, 168.00, 168.00, 0, 0, '2026-04-22 21:15:34', '2026-04-22 21:15:34', 0);
INSERT INTO `order_item` VALUES (2047617355460927490, 2047617355460927489, 'mo20260424180315927489', 3100000000000001005, 3100000000000004002, 3100000000000005003, '黑胡桃榫卯首饰盒', '原木标准款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '18cm x 12cm', 1, 239.00, 239.00, 0, 0, '2026-04-24 18:03:15', '2026-04-24 18:03:15', 0);
INSERT INTO `order_item` VALUES (2047623327503904771, 2047623327503904770, 'mo20260424182658904770', 2044038386325221377, 3100000000000004003, 3100000000000005006, '苏绣花影真丝团扇', '题字定制款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm，支持题字', 5, 228.00, 1140.00, 0, 0, '2026-04-24 18:26:58', '2026-04-24 18:26:58', 0);
INSERT INTO `order_item` VALUES (2047968447038353411, 2047968447038353410, 'mo20260425171821353410', 2044038386325221377, 2047962529810669570, 2047966038203424770, 'a', '123', '123', '123', 8, 123123.00, 984984.00, 0, 0, '2026-04-25 17:18:21', '2026-04-25 17:18:21', 0);
INSERT INTO `order_item` VALUES (2047979192136359938, 2047979192136359937, 'mo20260425180103359937', 2044038386325221377, 2047978956726853633, 2047978956726853635, 'test_goods_name', '默认规格2', '/upload/product/3a173f91a72c447e868d0453400cee33.jpg', '默认规格2', 15, 10.00, 150.00, 0, 0, '2026-04-25 18:01:03', '2026-04-25 18:01:03', 0);
INSERT INTO `order_item` VALUES (2047980368705740802, 2047980368705740801, 'mo20260425180544740801', 2044038386325221377, 2047978956726853633, 2047978956726853634, 'test_goods_name', '默认规格1', '/upload/product/3a173f91a72c447e868d0453400cee33.jpg', '默认规格描述', 4, 1000.00, 4000.00, 0, 0, '2026-04-25 18:05:44', '2026-04-25 18:05:44', 0);
INSERT INTO `order_item` VALUES (2047981998557683715, 2047981998557683714, 'mo20260425181212683714', 2044038386325221377, 2047962529810669570, 2047976835948343298, 'a', '123', '/upload/product/5ad18a5a51f545408a68ae85daf9c26e.png', '123', 7, 123123.00, 861861.00, 0, 0, '2026-04-25 18:12:12', '2026-04-25 18:12:12', 0);
INSERT INTO `order_item` VALUES (2047987984651636738, 2047987984651636737, 'mo20260425183559636737', 2044038386325221377, 2047978956726853633, 2047978956726853635, 'test_goods_name', '默认规格2', '/upload/product/b076a69fc9184bd0a51d50b1becab3fa.jpg', '默认规格2', 100, 10.00, 1000.00, 0, 0, '2026-04-25 18:35:59', '2026-04-25 18:35:59', 0);
INSERT INTO `order_item` VALUES (2047989590973599746, 2047989590973599745, 'mo20260425184222599745', 2044038386325221377, 2047962529810669570, 2047976835948343299, 'a', 't', '/upload/product/1cd86901fa0749dd922d408387cf91c3.jpg', 't', 10, 123.00, 1230.00, 0, 0, '2026-04-25 18:42:22', '2026-04-25 18:42:22', 0);
INSERT INTO `order_item` VALUES (2047992666899943427, 2047992666899943426, 'mo20260425185436943426', 2044038386325221377, 2047978956726853633, 2047978956726853635, 'test_goods_name', '默认规格2', '/upload/product/b076a69fc9184bd0a51d50b1becab3fa.jpg', '默认规格2', 100, 10.00, 1000.00, 0, 0, '2026-04-25 18:54:36', '2026-04-25 18:54:36', 0);
INSERT INTO `order_item` VALUES (2048341421398499331, 2048341421398499330, 'mo20260426180025499330', 2044038386325221377, 2047978956726853633, 2047978956726853635, 'test_goods_name', '默认规格2', '/upload/product/b076a69fc9184bd0a51d50b1becab3fa.jpg', '默认规格2', 20, 10.00, 200.00, 0, 0, '2026-04-26 18:00:25', '2026-04-26 18:00:25', 0);
INSERT INTO `order_item` VALUES (2048352800243261442, 2048352800243261441, 'mo20260426184538261441', 2044038386325221377, 3100000000000004003, 2047978071208615937, '苏绣花影真丝团扇', '月白花影款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm', 5, 168.00, 840.00, 0, 0, '2026-04-26 18:45:38', '2026-04-26 18:45:38', 0);
INSERT INTO `order_item` VALUES (3100000000000009001, 3100000000000008001, 'mo202604130001', 3100000000000001004, 3100000000000004001, 3100000000000005002, '柴烧釉陶马克杯', '礼盒装双杯组', 'https://example.com/product/mug_cover.png', '330ml*2', 1, 129.00, 129.00, 0, 1, '2026-04-10 10:00:30', '2026-04-12 20:00:00', 0);
INSERT INTO `order_item` VALUES (3100000000000009002, 3100000000000008002, 'mo202604130002', 3100000000000001005, 3100000000000004002, 3100000000000005004, '黑胡桃榫卯首饰盒', '刻字定制款', 'https://example.com/product/box_cover.png', '18cm x 12cm，支持盒盖刻字', 1, 299.00, 299.00, 0, 0, '2026-04-11 13:10:30', '2026-04-12 16:40:00', 0);
INSERT INTO `order_item` VALUES (3100000000000009003, 3100000000000008003, 'mo202604130003', 3100000000000001004, 3100000000000004003, 3100000000000005006, '苏绣花影真丝团扇', '题字定制款', 'https://example.com/product/fan_cover.png', '圆扇 21cm，支持题字', 1, 228.00, 228.00, 0, 0, '2026-04-13 08:35:30', '2026-04-13 08:40:00', 0);

-- ----------------------------
-- Table structure for order_logistics
-- ----------------------------
DROP TABLE IF EXISTS `order_logistics`;
CREATE TABLE `order_logistics`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '物流记录id',
  `orderId` bigint UNSIGNED NOT NULL COMMENT '订单id',
  `orderNo` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `companyName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物流公司',
  `trackingNo` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物流单号',
  `senderName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '寄件人',
  `senderPhone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '寄件电话',
  `receiverName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收件人',
  `receiverPhone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收件电话',
  `logisticsRemark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物流备注',
  `shipTime` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `signTime` datetime NULL DEFAULT NULL COMMENT '签收时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待发货 1-运输中 2-已签收',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_logistics_orderId`(`orderId` ASC) USING BTREE,
  INDEX `idx_order_logistics_trackingNo`(`trackingNo` ASC) USING BTREE,
  CONSTRAINT `fk_order_logistics_orderId` FOREIGN KEY (`orderId`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单物流表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_logistics
-- ----------------------------
INSERT INTO `order_logistics` VALUES (2045413065753444354, 3100000000000008003, 'mo202604130003', '11', '11', '11', '111', '林知夏', '13800000004', '11', '2026-04-18 16:04:11', NULL, 1, '2026-04-18 16:04:11', '2026-04-18 16:04:11', 0);
INSERT INTO `order_logistics` VALUES (3100000000000011001, 3100000000000008001, 'mo202604130001', '顺丰速运', 'SF202604100001', '青禾陶作', '13800000002', '林知夏', '13800000004', '易碎品已加厚泡沫包装', '2026-04-10 17:30:00', '2026-04-12 14:20:00', 2, '2026-04-10 17:35:00', '2026-04-12 14:20:00', 0);
INSERT INTO `order_logistics` VALUES (3100000000000011002, 3100000000000008002, 'mo202604130002', '京东物流', 'JD202604120001', '木心造物', '13800000003', '周聿风', '13800000005', '定制刻字款，木盒四角已做防撞处理', '2026-04-12 16:40:00', NULL, 1, '2026-04-12 16:45:00', '2026-04-12 16:45:00', 0);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '订单id',
  `orderNo` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `addressId` bigint UNSIGNED NULL DEFAULT NULL COMMENT '收货地址id',
  `orderStatus` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消',
  `payStatus` tinyint NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付 1-已支付 2-已退款',
  `payType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式',
  `deliveryStatus` tinyint NOT NULL DEFAULT 0 COMMENT '发货状态：0-未发货 1-已发货 2-已签收',
  `productAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品总额',
  `discountAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `freightAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '运费',
  `payAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  `buyerRemark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '买家备注',
  `receiverName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人',
  `receiverPhone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货手机号',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区县',
  `detailAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `payTime` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `deliveryTime` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `receiveTime` datetime NULL DEFAULT NULL COMMENT '收货时间',
  `finishTime` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancelTime` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_orders_orderNo`(`orderNo` ASC) USING BTREE,
  INDEX `idx_orders_userId`(`userId` ASC) USING BTREE,
  INDEX `idx_orders_orderStatus`(`orderStatus` ASC) USING BTREE,
  INDEX `fk_orders_addressId`(`addressId` ASC) USING BTREE,
  CONSTRAINT `fk_orders_addressId` FOREIGN KEY (`addressId`) REFERENCES `user_address` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_orders_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单主表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (2046858049341927425, 'mo20260422154602927425', 2044038386325221377, 2046857939480522753, 4, 0, NULL, 0, 1290.00, 0.00, 0.00, 1290.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', NULL, NULL, NULL, NULL, '2026-04-22 16:01:02', '2026-04-22 15:46:02', '2026-04-22 16:01:02', 0);
INSERT INTO `orders` VALUES (2046933954940698625, 'mo20260422204739698625', 2044038386325221377, 2046857939480522753, 4, 0, NULL, 0, 299.00, 0.00, 0.00, 299.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', NULL, NULL, NULL, NULL, '2026-04-22 21:02:39', '2026-04-22 20:47:39', '2026-04-22 21:02:39', 0);
INSERT INTO `orders` VALUES (2046934165289238530, 'mo20260422204829238530', 2044038386325221377, 2046857939480522753, 4, 0, NULL, 0, 129.00, 0.00, 0.00, 129.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', NULL, NULL, NULL, NULL, '2026-04-22 20:48:49', '2026-04-22 20:48:29', '2026-04-22 20:48:49', 0);
INSERT INTO `orders` VALUES (2046935098484129793, 'mo20260422205212129793', 2044038386325221377, 2046934986638819330, 4, 0, NULL, 0, 1368.00, 0.00, 0.00, 1368.00, NULL, 'Barry Allen', '11111', 'Smyrna', 'Smyrna', 'a', 'asdasdad', NULL, NULL, NULL, NULL, '2026-04-22 21:06:37', '2026-04-22 20:52:12', '2026-04-22 21:06:37', 0);
INSERT INTO `orders` VALUES (2046937661564583937, 'mo20260422210223583937', 2044038386325221377, 2046857939480522753, 4, 0, NULL, 0, 840.00, 0.00, 0.00, 840.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', NULL, NULL, NULL, NULL, '2026-04-22 21:06:36', '2026-04-22 21:02:23', '2026-04-22 21:06:36', 0);
INSERT INTO `orders` VALUES (2046940980848713730, 'mo20260422211534713730', 2044038386325221377, 2046857939480522753, 4, 0, NULL, 0, 168.00, 0.00, 0.00, 168.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', NULL, NULL, NULL, NULL, '2026-04-22 21:16:34', '2026-04-22 21:15:34', '2026-04-22 21:16:34', 0);
INSERT INTO `orders` VALUES (2047617355460927489, 'mo20260424180315927489', 3100000000000001005, 3100000000000006003, 1, 1, 'balance', 0, 239.00, 0.00, 0.00, 239.00, NULL, '周聿风', '13800000005', '浙江省', '杭州市', '西湖区', '文三路 188 号 9 幢 902', '2026-04-24 18:03:21', NULL, NULL, NULL, NULL, '2026-04-24 18:03:15', '2026-04-24 18:03:21', 0);
INSERT INTO `orders` VALUES (2047623327503904770, 'mo20260424182658904770', 2044038386325221377, 2046857939480522753, 1, 1, 'balance', 0, 1140.00, 912.00, 0.00, 228.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', '2026-04-24 18:27:31', NULL, NULL, NULL, NULL, '2026-04-24 18:26:58', '2026-04-24 18:27:31', 0);
INSERT INTO `orders` VALUES (2047968447038353410, 'mo20260425171821353410', 2044038386325221377, 2046934986638819330, 1, 1, 'balance', 0, 984984.00, 0.00, 0.00, 984984.00, NULL, 'Barry Allen', '11111', 'Smyrna', 'Smyrna', 'a', 'asdasdad', '2026-04-25 17:18:38', NULL, NULL, NULL, NULL, '2026-04-25 17:18:21', '2026-04-25 17:18:38', 0);
INSERT INTO `orders` VALUES (2047979192136359937, 'mo20260425180103359937', 2044038386325221377, 2046934986638819330, 4, 0, NULL, 0, 150.00, 0.00, 0.00, 150.00, NULL, 'Barry Allen', '11111', 'Smyrna', 'Smyrna', 'a', 'asdasdad', NULL, NULL, NULL, NULL, '2026-04-25 18:05:18', '2026-04-25 18:01:03', '2026-04-25 18:05:18', 0);
INSERT INTO `orders` VALUES (2047980368705740801, 'mo20260425180544740801', 2044038386325221377, 2046857939480522753, 1, 1, 'balance', 0, 4000.00, 0.00, 0.00, 4000.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', '2026-04-25 18:11:59', NULL, NULL, NULL, NULL, '2026-04-25 18:05:44', '2026-04-25 18:11:59', 0);
INSERT INTO `orders` VALUES (2047981998557683714, 'mo20260425181212683714', 2044038386325221377, 2046934986638819330, 1, 1, 'balance', 0, 861861.00, 0.00, 0.00, 861861.00, NULL, 'Barry Allen', '11111', 'Smyrna', 'Smyrna', 'a', 'asdasdad', '2026-04-25 18:12:14', NULL, NULL, NULL, NULL, '2026-04-25 18:12:12', '2026-04-25 18:12:14', 0);
INSERT INTO `orders` VALUES (2047987984651636737, 'mo20260425183559636737', 2044038386325221377, 2046934986638819330, 1, 1, 'balance', 0, 1000.00, 900.00, 0.00, 100.00, NULL, 'Barry Allen', '11111', 'Smyrna', 'Smyrna', 'a', 'asdasdad', '2026-04-25 18:36:04', NULL, NULL, NULL, NULL, '2026-04-25 18:35:59', '2026-04-25 18:36:04', 0);
INSERT INTO `orders` VALUES (2047989590973599745, 'mo20260425184222599745', 2044038386325221377, 2046857939480522753, 1, 1, 'balance', 0, 1230.00, 5.00, 0.00, 1225.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', '2026-04-25 18:42:24', NULL, NULL, NULL, NULL, '2026-04-25 18:42:22', '2026-04-25 18:42:24', 0);
INSERT INTO `orders` VALUES (2047992666899943426, 'mo20260425185436943426', 2044038386325221377, 2046857939480522753, 1, 1, 'balance', 0, 1000.00, 900.00, 0.00, 100.00, NULL, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', '2026-04-25 18:54:37', NULL, NULL, NULL, NULL, '2026-04-25 18:54:36', '2026-04-25 18:54:37', 0);
INSERT INTO `orders` VALUES (2048341421398499330, 'mo20260426180025499330', 2044038386325221377, 2046934986638819330, 1, 1, 'balance', 0, 200.00, 180.00, 0.00, 20.00, NULL, 'Barry Allen', '11111', 'Smyrna', 'Smyrna', 'a', 'asdasdad', '2026-04-26 18:00:26', NULL, NULL, NULL, NULL, '2026-04-26 18:00:25', '2026-04-26 18:00:26', 0);
INSERT INTO `orders` VALUES (2048352800243261441, 'mo20260426184538261441', 2044038386325221377, 2046934986638819330, 1, 1, 'balance', 0, 840.00, 0.00, 0.00, 840.00, NULL, 'Barry Allen', '11111', 'Smyrna', 'Smyrna', 'a', 'asdasdad', '2026-04-26 18:45:39', NULL, NULL, NULL, NULL, '2026-04-26 18:45:38', '2026-04-26 18:45:39', 0);
INSERT INTO `orders` VALUES (3100000000000008001, 'mo202604130001', 3100000000000001004, 3100000000000006001, 3, 1, 'wechat', 2, 129.00, 0.00, 12.00, 141.00, '杯子要包装好，避免磕碰', '林知夏', '13800000004', '上海市', '上海市', '徐汇区', '漕溪北路 88 号 8 幢 1602', '2026-04-10 10:06:00', '2026-04-10 17:30:00', '2026-04-12 14:20:00', '2026-04-12 20:00:00', NULL, '2026-04-10 10:00:00', '2026-04-12 20:00:00', 0);
INSERT INTO `orders` VALUES (3100000000000008002, 'mo202604130002', 3100000000000001005, 3100000000000006003, 2, 1, 'alipay', 1, 299.00, 0.00, 18.00, 317.00, '盒盖刻字：赠予春日', '周聿风', '13800000005', '浙江省', '杭州市', '西湖区', '文三路 188 号 9 幢 902', '2026-04-11 13:15:00', '2026-04-12 16:40:00', NULL, NULL, NULL, '2026-04-11 13:10:00', '2026-04-12 16:40:00', 0);
INSERT INTO `orders` VALUES (3100000000000008003, 'mo202604130003', 3100000000000001004, 3100000000000006001, 2, 1, 'wechat', 1, 228.00, 20.00, 12.00, 220.00, '题字内容：夏日有风', '林知夏', '13800000004', '上海市', '上海市', '徐汇区', '漕溪北路 88 号 8 幢 1602', '2026-04-13 08:40:00', '2026-04-18 16:04:11', NULL, NULL, NULL, '2026-04-13 08:35:00', '2026-04-18 16:04:11', 0);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `categoryId` bigint UNSIGNED NOT NULL COMMENT '分类id',
  `productName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `productSubtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品副标题',
  `productCover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品主图',
  `productDesc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品描述',
  `craftType` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工艺类型',
  `materialDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '材质说明',
  `originPlace` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '产地',
  `handmadeCycleDays` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '制作周期天数',
  `supportCustom` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否支持定制',
  `inventory` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '总库存',
  `soldQuantity` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '销量',
  `favoriteCount` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '收藏数',
  `reviewCount` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '评价数',
  `minPrice` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最低售价',
  `maxPrice` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最高售价',
  `auditStatus` tinyint NOT NULL DEFAULT 1 COMMENT '审核状态：0-待审核 1-通过 2-驳回',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '商品状态：0-草稿 1-上架 2-下架',
  `sortOrder` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_categoryId`(`categoryId` ASC) USING BTREE,
  INDEX `idx_product_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_product_categoryId` FOREIGN KEY (`categoryId`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '手工制品商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (2045438415971672066, 3100000000000003005, '123123', '123123123123213', '123213', '12312', '123123', '1232131', '1231231', 12313, 1, 1, 0, 0, 0, 123.00, 123.00, 1, 0, 123, '2026-04-18 17:44:55', '2026-04-25 17:57:19', 1);
INSERT INTO `product` VALUES (2046044813835182081, 3100000000000003008, '123123213213', '123123', '/upload/product/feac01e14c9b405db46a91114522aed9.png', '123', '123123', '1231', '12312313', 1231, 1, 123123, 0, 0, 0, 123.00, 123.00, 1, 0, 12313, '2026-04-20 09:54:31', '2026-04-25 17:57:19', 1);
INSERT INTO `product` VALUES (2046047708286623745, 3100000000000003005, '111', '1111111111', '/upload/product/2cc857a553a646c79a5593bc0ea830cd.png', '111', '111111', '111', '111', 111, 1, 111, 0, 0, 0, 111.00, 111.00, 1, 0, 111, '2026-04-20 10:06:02', '2026-04-25 17:57:19', 1);
INSERT INTO `product` VALUES (2047962529810669570, 3100000000000003002, 'a', 'a', '/upload/product/ba6153a985ff41a59e5246ee045bb69a.png', '123', '123', '123', '123', 123, 0, 1329, 25, 0, 0, 123.00, 123123.00, 1, 1, 111, '2026-04-25 16:54:51', '2026-04-25 18:42:24', 0);
INSERT INTO `product` VALUES (2047978956726853633, 3100000000000003001, 'test_goods_name', 'test_goods_name_subtitle', '/upload/product/3a173f91a72c447e868d0453400cee33.jpg', '测试商品描述', '工艺类型测试数据', '材质测试数据', '产地测试数据', 1, 0, 2776, 224, 0, 0, 10.00, 1000.00, 1, 1, 0, '2026-04-25 18:00:07', '2026-04-26 18:00:26', 0);
INSERT INTO `product` VALUES (3100000000000004001, 3100000000000003001, '柴烧釉陶马克杯', '景德镇手作，杯沿圆润，适合日常咖啡与热茶', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '杯身保留手工拉坯纹理，釉面呈现自然窑变，每一只纹路略有差异。', '拉坯柴烧', '高白泥、木灰釉', '景德镇', 5, 1, 1, 18, 1, 1, 89.00, 129.00, 1, 0, 100, '2026-04-04 10:00:00', '2026-04-25 17:57:19', 0);
INSERT INTO `product` VALUES (3100000000000004002, 3100000000000003006, '黑胡桃榫卯首饰盒', '可加刻字的双层收纳盒，适合作为礼物', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '采用黑胡桃木手工打磨与天然木蜡油封层，盒盖与盒身为榫卯拼接结构。', '榫卯木作', '黑胡桃木、黄铜扣件', '杭州', 7, 1, 17, 7, 1, 0, 239.00, 299.00, 1, 0, 95, '2026-04-04 11:00:00', '2026-04-25 17:57:19', 0);
INSERT INTO `product` VALUES (3100000000000004003, 3100000000000003007, '苏绣花影真丝团扇', '双面刺绣，扇骨轻巧，适合夏日穿搭与送礼', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '选用真丝绢面与竹制扇骨，双面绣花影图案，支持题字与包装定制。', '双面苏绣', '真丝、竹骨', '苏州', 10, 1, 2, 14, 2, 0, 168.00, 228.00, 1, 1, 90, '2026-04-04 12:00:00', '2026-04-26 19:00:55', 0);
INSERT INTO `product` VALUES (3100000000000004004, 3100000000000003008, '藤编居家收纳托盘', '餐桌与玄关两用，轻便耐看', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '手工藤编结构紧密，适合水果、面包与玄关小物收纳。', '手工藤编', '藤条、棉绳', '安吉', 4, 0, 1, 9, 0, 0, 96.00, 128.00, 1, 0, 85, '2026-04-04 13:00:00', '2026-04-25 17:57:19', 0);

-- ----------------------------
-- Table structure for product_browse_log
-- ----------------------------
DROP TABLE IF EXISTS `product_browse_log`;
CREATE TABLE `product_browse_log`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '浏览记录id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `sourcePage` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源页面',
  `staySeconds` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '停留秒数',
  `deviceType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备类型',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_browse_log_userId`(`userId` ASC) USING BTREE,
  INDEX `idx_product_browse_log_productId`(`productId` ASC) USING BTREE,
  CONSTRAINT `fk_product_browse_log_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_product_browse_log_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品浏览日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_browse_log
-- ----------------------------
INSERT INTO `product_browse_log` VALUES (2048341384387960833, 2044038386325221377, 2047978956726853633, 'product_detail', 0, 'desktop', '2026-04-26 18:00:17', '2026-04-26 18:00:17', 0);
INSERT INTO `product_browse_log` VALUES (2048352736913465345, 2044038386325221377, 2047978956726853633, 'product_detail', 0, 'desktop', '2026-04-26 18:45:23', '2026-04-26 18:45:23', 0);
INSERT INTO `product_browse_log` VALUES (2048352762985259010, 2044038386325221377, 3100000000000004003, 'product_detail', 0, 'desktop', '2026-04-26 18:45:29', '2026-04-26 18:45:29', 0);
INSERT INTO `product_browse_log` VALUES (2048353819199086594, 2044038386325221377, 2047962529810669570, 'product_detail', 0, 'desktop', '2026-04-26 18:49:41', '2026-04-26 18:49:41', 0);
INSERT INTO `product_browse_log` VALUES (2048356594154831874, 2044038386325221377, 3100000000000004003, 'product_detail', 0, 'desktop', '2026-04-26 19:00:43', '2026-04-26 19:00:43', 0);
INSERT INTO `product_browse_log` VALUES (2048356679978680322, 2044038386325221377, 3100000000000004003, 'product_detail', 0, 'desktop', '2026-04-26 19:01:03', '2026-04-26 19:01:03', 0);
INSERT INTO `product_browse_log` VALUES (3100000000000014001, 3100000000000001004, 3100000000000004001, 'home', 36, 'h5', '2026-04-09 20:30:00', '2026-04-09 20:30:00', 0);
INSERT INTO `product_browse_log` VALUES (3100000000000014002, 3100000000000001004, 3100000000000004003, 'detail', 92, 'h5', '2026-04-12 20:05:00', '2026-04-12 20:05:00', 0);
INSERT INTO `product_browse_log` VALUES (3100000000000014003, 3100000000000001004, 3100000000000004004, 'search', 28, 'pc', '2026-04-12 20:12:00', '2026-04-12 20:12:00', 0);
INSERT INTO `product_browse_log` VALUES (3100000000000014004, 3100000000000001005, 3100000000000004002, 'detail', 145, 'pc', '2026-04-10 21:50:00', '2026-04-10 21:50:00', 0);
INSERT INTO `product_browse_log` VALUES (3100000000000014005, 3100000000000001005, 3100000000000004001, 'home', 22, 'pc', '2026-04-12 19:40:00', '2026-04-12 19:40:00', 0);
INSERT INTO `product_browse_log` VALUES (3100000000000014006, 3100000000000001005, 3100000000000004003, 'recommend', 51, 'pc', '2026-04-12 19:48:00', '2026-04-12 19:48:00', 0);

-- ----------------------------
-- Table structure for product_favorite
-- ----------------------------
DROP TABLE IF EXISTS `product_favorite`;
CREATE TABLE `product_favorite`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '收藏记录id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_product_favorite_userId_productId`(`userId` ASC, `productId` ASC) USING BTREE,
  INDEX `idx_product_favorite_productId`(`productId` ASC) USING BTREE,
  CONSTRAINT `fk_product_favorite_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_product_favorite_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_favorite
-- ----------------------------
INSERT INTO `product_favorite` VALUES (2048356629923856385, 2044038386325221377, 3100000000000004003, '2026-04-26 19:00:51', '2026-04-26 19:00:55', 0);
INSERT INTO `product_favorite` VALUES (3100000000000012001, 3100000000000001004, 3100000000000004001, '2026-04-09 21:00:00', '2026-04-09 21:00:00', 0);
INSERT INTO `product_favorite` VALUES (3100000000000012002, 3100000000000001004, 3100000000000004003, '2026-04-12 20:10:00', '2026-04-12 20:10:00', 0);
INSERT INTO `product_favorite` VALUES (3100000000000012003, 3100000000000001005, 3100000000000004002, '2026-04-10 22:15:00', '2026-04-10 22:15:00', 0);

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '商品图片id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `imageUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片地址',
  `imageType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'detail' COMMENT '图片类型：cover/detail',
  `sortOrder` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_image_productId`(`productId` ASC) USING BTREE,
  CONSTRAINT `fk_product_image_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品图片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_image
-- ----------------------------
INSERT INTO `product_image` VALUES (2045438416009420801, 2045438415971672066, '123121', 'detail', 1, '2026-04-18 17:44:55', '2026-04-18 17:45:07', 1);
INSERT INTO `product_image` VALUES (2045438416009420802, 2045438415971672066, '123121123', 'detail', 2, '2026-04-18 17:44:55', '2026-04-18 17:45:07', 1);
INSERT INTO `product_image` VALUES (2045438467100237825, 2045438415971672066, '123121', 'detail', 1, '2026-04-18 17:45:07', '2026-04-18 17:45:10', 1);
INSERT INTO `product_image` VALUES (2045438467167346690, 2045438415971672066, '123121123', 'detail', 2, '2026-04-18 17:45:07', '2026-04-18 17:45:10', 1);
INSERT INTO `product_image` VALUES (2045438479301468162, 2045438415971672066, '123121', 'detail', 1, '2026-04-18 17:45:10', '2026-04-18 17:55:18', 1);
INSERT INTO `product_image` VALUES (2045438479301468163, 2045438415971672066, '123121123', 'detail', 2, '2026-04-18 17:45:10', '2026-04-18 17:55:18', 1);
INSERT INTO `product_image` VALUES (2045441029517938689, 2045438415971672066, '123121', 'detail', 1, '2026-04-18 17:55:18', '2026-04-18 17:55:24', 1);
INSERT INTO `product_image` VALUES (2045441029530521601, 2045438415971672066, '123121123', 'detail', 2, '2026-04-18 17:55:18', '2026-04-18 17:55:24', 1);
INSERT INTO `product_image` VALUES (2045441053144453122, 2045438415971672066, '123121', 'detail', 1, '2026-04-18 17:55:24', '2026-04-18 18:00:48', 1);
INSERT INTO `product_image` VALUES (2045441053144453123, 2045438415971672066, '123121123', 'detail', 2, '2026-04-18 17:55:24', '2026-04-18 18:00:48', 1);
INSERT INTO `product_image` VALUES (2045442412413579266, 2045438415971672066, '123121', 'detail', 1, '2026-04-18 18:00:48', '2026-04-25 17:40:55', 1);
INSERT INTO `product_image` VALUES (2045442412413579267, 2045438415971672066, '123121123', 'detail', 2, '2026-04-18 18:00:48', '2026-04-25 17:40:55', 1);
INSERT INTO `product_image` VALUES (2046044813835182082, 2046044813835182081, '/upload/product/e5a4ef04a0d04390bddfb66db3d3de8f.png', '12313', 1, '2026-04-20 09:54:31', '2026-04-25 17:40:52', 1);
INSERT INTO `product_image` VALUES (2046047708286623746, 2046047708286623745, '/upload/product/49e6afbf49db492389189e417e0bba0a.png', 'detail', 1, '2026-04-20 10:06:02', '2026-04-25 17:40:57', 1);
INSERT INTO `product_image` VALUES (3100000000000004101, 3100000000000004001, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 'cover', 1, '2026-04-04 10:05:00', '2026-04-15 22:07:53', 0);
INSERT INTO `product_image` VALUES (3100000000000004102, 3100000000000004001, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 'detail', 2, '2026-04-04 10:05:00', '2026-04-15 22:07:53', 0);
INSERT INTO `product_image` VALUES (3100000000000004103, 3100000000000004002, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 'cover', 1, '2026-04-04 11:05:00', '2026-04-15 22:07:53', 0);
INSERT INTO `product_image` VALUES (3100000000000004104, 3100000000000004002, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 'detail', 2, '2026-04-04 11:05:00', '2026-04-15 22:07:53', 0);
INSERT INTO `product_image` VALUES (3100000000000004105, 3100000000000004003, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 'cover', 1, '2026-04-04 12:05:00', '2026-04-15 22:07:53', 0);
INSERT INTO `product_image` VALUES (3100000000000004106, 3100000000000004003, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 'detail', 2, '2026-04-04 12:05:00', '2026-04-15 22:07:53', 0);
INSERT INTO `product_image` VALUES (3100000000000004107, 3100000000000004004, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 'cover', 1, '2026-04-04 13:05:00', '2026-04-15 22:07:53', 0);
INSERT INTO `product_image` VALUES (3100000000000004108, 3100000000000004004, 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 'detail', 2, '2026-04-04 13:05:00', '2026-04-15 22:07:53', 0);

-- ----------------------------
-- Table structure for product_material
-- ----------------------------
DROP TABLE IF EXISTS `product_material`;
CREATE TABLE `product_material`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '商品材质明细id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `materialName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '材质名称',
  `materialOrigin` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '材质来源',
  `materialRatio` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '材质占比说明',
  `sortOrder` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_material_productId`(`productId` ASC) USING BTREE,
  CONSTRAINT `fk_product_material_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品材质明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_material
-- ----------------------------
INSERT INTO `product_material` VALUES (2045438416009420803, 2045438415971672066, '1231231', '123123', '1231231', 1, '2026-04-18 17:44:55', '2026-04-18 17:45:07', 1);
INSERT INTO `product_material` VALUES (2045438467167346691, 2045438415971672066, '1231231', '123123', '1231231', 1, '2026-04-18 17:45:07', '2026-04-18 17:45:10', 1);
INSERT INTO `product_material` VALUES (2045438479301468164, 2045438415971672066, '1231231', '123123', '1231231', 1, '2026-04-18 17:45:10', '2026-04-18 17:55:18', 1);
INSERT INTO `product_material` VALUES (2045441029530521602, 2045438415971672066, '1231231', '123123', '1231231', 1, '2026-04-18 17:55:18', '2026-04-18 17:55:24', 1);
INSERT INTO `product_material` VALUES (2045441053144453124, 2045438415971672066, '1231231', '123123', '1231231', 1, '2026-04-18 17:55:24', '2026-04-18 18:00:48', 1);
INSERT INTO `product_material` VALUES (2045442412413579268, 2045438415971672066, '1231231', '123123', '1231231', 1, '2026-04-18 18:00:48', '2026-04-25 17:40:55', 1);
INSERT INTO `product_material` VALUES (2046044813835182083, 2046044813835182081, '123', '123', '123', 1, '2026-04-20 09:54:31', '2026-04-25 17:40:52', 1);
INSERT INTO `product_material` VALUES (2046047708353732609, 2046047708286623745, '111', '111', '111', 1, '2026-04-20 10:06:02', '2026-04-25 17:40:57', 1);
INSERT INTO `product_material` VALUES (3100000000000004201, 3100000000000004001, '高白泥', '景德镇', '85%', 1, '2026-04-04 10:10:00', '2026-04-04 10:10:00', 0);
INSERT INTO `product_material` VALUES (3100000000000004202, 3100000000000004001, '木灰釉', '景德镇', '15%', 2, '2026-04-04 10:10:00', '2026-04-04 10:10:00', 0);
INSERT INTO `product_material` VALUES (3100000000000004203, 3100000000000004002, '黑胡桃木', '北美进口', '95%', 1, '2026-04-04 11:10:00', '2026-04-04 11:10:00', 0);
INSERT INTO `product_material` VALUES (3100000000000004204, 3100000000000004002, '黄铜扣件', '国内五金', '5%', 2, '2026-04-04 11:10:00', '2026-04-04 11:10:00', 0);
INSERT INTO `product_material` VALUES (3100000000000004205, 3100000000000004003, '真丝绢面', '苏州', '60%', 1, '2026-04-04 12:10:00', '2026-04-04 12:10:00', 0);
INSERT INTO `product_material` VALUES (3100000000000004206, 3100000000000004003, '竹制扇骨', '浙江安吉', '40%', 2, '2026-04-04 12:10:00', '2026-04-04 12:10:00', 0);
INSERT INTO `product_material` VALUES (3100000000000004207, 3100000000000004004, '藤条', '安吉', '90%', 1, '2026-04-04 13:10:00', '2026-04-04 13:10:00', 0);
INSERT INTO `product_material` VALUES (3100000000000004208, 3100000000000004004, '棉绳', '绍兴', '10%', 2, '2026-04-04 13:10:00', '2026-04-04 13:10:00', 0);

-- ----------------------------
-- Table structure for product_review
-- ----------------------------
DROP TABLE IF EXISTS `product_review`;
CREATE TABLE `product_review`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '商品评价id',
  `orderId` bigint UNSIGNED NOT NULL COMMENT '订单id',
  `orderItemId` bigint UNSIGNED NOT NULL COMMENT '订单明细id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `skuId` bigint UNSIGNED NOT NULL COMMENT 'sku id',
  `score` tinyint NOT NULL DEFAULT 5 COMMENT '评分',
  `reviewContent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评价内容',
  `reviewImages` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评价图片，多个地址用逗号分隔',
  `isAnonymous` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否匿名',
  `replyContent` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商家回复',
  `replyTime` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-待审核 1-展示 2-隐藏',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_product_review_orderItemId`(`orderItemId` ASC) USING BTREE,
  INDEX `idx_product_review_userId`(`userId` ASC) USING BTREE,
  INDEX `idx_product_review_productId`(`productId` ASC) USING BTREE,
  INDEX `fk_product_review_orderId`(`orderId` ASC) USING BTREE,
  INDEX `fk_product_review_skuId`(`skuId` ASC) USING BTREE,
  CONSTRAINT `fk_product_review_orderId` FOREIGN KEY (`orderId`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_product_review_orderItemId` FOREIGN KEY (`orderItemId`) REFERENCES `order_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_product_review_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_product_review_skuId` FOREIGN KEY (`skuId`) REFERENCES `product_sku` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_product_review_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_review
-- ----------------------------
INSERT INTO `product_review` VALUES (3100000000000013001, 3100000000000008001, 3100000000000009001, 3100000000000001004, 3100000000000004001, 3100000000000005002, 5, '杯型很顺手，釉色比图片更有层次，送朋友也很体面。', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', 0, '感谢喜欢，我们后续会继续上新同系列茶器。', '2026-04-12 21:10:00', 1, '2026-04-12 20:30:00', '2026-04-15 22:07:56', 0);

-- ----------------------------
-- Table structure for product_similarity
-- ----------------------------
DROP TABLE IF EXISTS `product_similarity`;
CREATE TABLE `product_similarity`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '商品相似度id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `similarProductId` bigint UNSIGNED NOT NULL COMMENT '相似商品id',
  `similarityScore` decimal(10, 6) NOT NULL DEFAULT 0.000000 COMMENT '相似度分数',
  `coBehaviorCount` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '共同交互次数',
  `algorithmVersion` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'item_cf_v1' COMMENT '算法版本',
  `calculatedTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_product_similarity_pair`(`productId` ASC, `similarProductId` ASC, `algorithmVersion` ASC) USING BTREE,
  INDEX `idx_product_similarity_productId`(`productId` ASC) USING BTREE,
  INDEX `idx_product_similarity_similarProductId`(`similarProductId` ASC) USING BTREE,
  INDEX `idx_product_similarity_score`(`similarityScore` DESC) USING BTREE,
  CONSTRAINT `fk_product_similarity_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_product_similarity_similarProductId` FOREIGN KEY (`similarProductId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `chk_product_similarity_not_self` CHECK (`productId` <> `similarProductId`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品相似度表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_similarity
-- ----------------------------
INSERT INTO `product_similarity` VALUES (2048394170845925378, 3100000000000004003, 2047962529810669570, 0.964764, 1, 'item_cf_v1', '2026-04-26 21:30:02', '2026-04-26 21:30:02', '2026-04-26 21:30:02', 0);
INSERT INTO `product_similarity` VALUES (2048394170845925379, 3100000000000004003, 2047978956726853633, 0.964764, 1, 'item_cf_v1', '2026-04-26 21:30:02', '2026-04-26 21:30:02', '2026-04-26 21:30:02', 0);
INSERT INTO `product_similarity` VALUES (2048394170845925380, 2047962529810669570, 2047978956726853633, 1.000000, 1, 'item_cf_v1', '2026-04-26 21:30:02', '2026-04-26 21:30:02', '2026-04-26 21:30:02', 0);
INSERT INTO `product_similarity` VALUES (2048394170845925381, 2047962529810669570, 3100000000000004003, 0.964764, 1, 'item_cf_v1', '2026-04-26 21:30:02', '2026-04-26 21:30:02', '2026-04-26 21:30:02', 0);
INSERT INTO `product_similarity` VALUES (2048394170845925382, 2047978956726853633, 2047962529810669570, 1.000000, 1, 'item_cf_v1', '2026-04-26 21:30:02', '2026-04-26 21:30:02', '2026-04-26 21:30:02', 0);
INSERT INTO `product_similarity` VALUES (2048394170845925383, 2047978956726853633, 3100000000000004003, 0.964764, 1, 'item_cf_v1', '2026-04-26 21:30:02', '2026-04-26 21:30:02', '2026-04-26 21:30:02', 0);

-- ----------------------------
-- Table structure for product_sku
-- ----------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '商品sku id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `skuCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'sku编码',
  `skuName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'sku名称',
  `skuCover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'sku图片',
  `specText` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规格描述',
  `materialType` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '材质类型',
  `weight` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '重量',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '销售价',
  `originalPrice` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '原价',
  `stock` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '库存',
  `lockedStock` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '锁定库存',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-停用 1-启用',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_product_sku_skuCode`(`skuCode` ASC) USING BTREE,
  INDEX `idx_product_sku_productId`(`productId` ASC) USING BTREE,
  CONSTRAINT `fk_product_sku_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品sku表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_sku
-- ----------------------------
INSERT INTO `product_sku` VALUES (2045438416009420804, 2045438415971672066, 'sku-2045438416009420804', '123', '12312', '3', '1231231', 123.00, 123.00, 123.00, 123, 0, 1, '2026-04-18 17:44:55', '2026-04-18 17:45:07', 1);
INSERT INTO `product_sku` VALUES (2045438467167346692, 2045438415971672066, 'sku-2045438467167346692', '123', '12312', '3', '1231231', 123.00, 123.00, 123.00, 123, 0, 1, '2026-04-18 17:45:07', '2026-04-18 17:45:10', 1);
INSERT INTO `product_sku` VALUES (2045438479301468165, 2045438415971672066, 'sku-2045438479301468165', '123', '12312', '3', '1231231', 123.00, 123.00, 123.00, 123, 0, 1, '2026-04-18 17:45:10', '2026-04-18 17:55:18', 1);
INSERT INTO `product_sku` VALUES (2045441029530521603, 2045438415971672066, 'sku-2045441029530521603', '123', '12312', '3', '1231231', 123.00, 123.00, 123.00, 123, 0, 1, '2026-04-18 17:55:18', '2026-04-18 17:55:24', 1);
INSERT INTO `product_sku` VALUES (2045441053211561985, 2045438415971672066, 'sku-2045441053211561985', '123', '12312', '3', '1231231', 123.00, 123.00, 123.00, 123, 0, 1, '2026-04-18 17:55:24', '2026-04-18 18:00:48', 1);
INSERT INTO `product_sku` VALUES (2045442412413579269, 2045438415971672066, 'sku-2045442412413579269', '123', '12312', '3', '1231231', 3.00, 123.00, 123.00, 123, 0, 1, '2026-04-18 18:00:48', '2026-04-25 17:40:55', 1);
INSERT INTO `product_sku` VALUES (2046044813835182084, 2046044813835182081, 'sku-2046044813835182084', '123', '/upload/product/feac01e14c9b405db46a91114522aed9.png', '123', '123', 123.00, 123.00, 123.00, 123, 0, 1, '2026-04-20 09:54:31', '2026-04-25 17:40:52', 1);
INSERT INTO `product_sku` VALUES (2046047708353732610, 2046047708286623745, 'sku-2046047708353732610', '111', '/upload/product/b6fa18f739974ff58e32eed3ec89cf27.png', '111', '11', 111.00, 111.00, 111.00, 111, 0, 1, '2026-04-20 10:06:02', '2026-04-25 17:40:57', 1);
INSERT INTO `product_sku` VALUES (2047965506462146561, 2047962529810669570, 'sku-2047965506462146561', '默认规格', NULL, '默认规格', '123', 123.00, 123.00, 123.00, 123, 0, 1, '2026-04-25 17:06:40', '2026-04-25 17:08:26', 1);
INSERT INTO `product_sku` VALUES (2047965506499895297, 2047962529810669570, 'sku-2047965506499895297', '123', NULL, '123', '123', 123123.00, 123123.00, 123.00, 123, 0, 1, '2026-04-25 17:06:40', '2026-04-25 17:08:26', 1);
INSERT INTO `product_sku` VALUES (2047965950391476226, 2047962529810669570, 'sku-2047965950391476226', '默认规格', NULL, '默认规格', '123', 123.00, 123.00, 123.00, 123, 0, 0, '2026-04-25 17:08:26', '2026-04-25 17:08:47', 1);
INSERT INTO `product_sku` VALUES (2047965950391476227, 2047962529810669570, 'sku-2047965950391476227', '123', NULL, '123', '123', 123123.00, 123123.00, 123.00, 123, 0, 0, '2026-04-25 17:08:26', '2026-04-25 17:08:47', 1);
INSERT INTO `product_sku` VALUES (2047966038136315906, 2047962529810669570, 'sku-2047966038136315906', '默认规格', NULL, '默认规格', '123', 123.00, 123.00, 123.00, 123, 0, 1, '2026-04-25 17:08:47', '2026-04-25 17:27:54', 1);
INSERT INTO `product_sku` VALUES (2047966038203424770, 2047962529810669570, 'sku-2047966038203424770', '123', NULL, '123', '123', 123123.00, 123123.00, 123.00, 115, 0, 1, '2026-04-25 17:08:47', '2026-04-25 17:27:54', 1);
INSERT INTO `product_sku` VALUES (2047970850080944130, 2047962529810669570, 'sku-2047970850080944130', '123', '/upload/product/5ad18a5a51f545408a68ae85daf9c26e.png', '123', '123', 123123.00, 123123.00, 123.00, 115, 0, 1, '2026-04-25 17:27:54', '2026-04-25 17:30:44', 1);
INSERT INTO `product_sku` VALUES (2047970850080944131, 2047962529810669570, 'sku-2047970850080944131', 't', '/upload/product/59d729742720417fb824451067d23030.png', 't', 't', 123.00, 123.00, 1234.00, 1231, 0, 1, '2026-04-25 17:27:54', '2026-04-25 17:30:44', 1);
INSERT INTO `product_sku` VALUES (2047971560721870850, 2047962529810669570, 'sku-2047971560721870850', '123', '/upload/product/5ad18a5a51f545408a68ae85daf9c26e.png', '123', '123', 123123.00, 123123.00, 123.00, 115, 0, 1, '2026-04-25 17:30:44', '2026-04-25 17:51:41', 1);
INSERT INTO `product_sku` VALUES (2047971560721870851, 2047962529810669570, 'sku-2047971560721870851', 't', '/upload/product/1cd86901fa0749dd922d408387cf91c3.jpg', 't', 't', 123.00, 123.00, 1234.00, 1231, 0, 1, '2026-04-25 17:30:44', '2026-04-25 17:51:41', 1);
INSERT INTO `product_sku` VALUES (2047976835948343298, 2047962529810669570, 'sku-2047976835948343298', '123', '/upload/product/5ad18a5a51f545408a68ae85daf9c26e.png', '123', '123', 123123.00, 123123.00, 123.00, 108, 0, 1, '2026-04-25 17:51:41', '2026-04-25 18:12:14', 0);
INSERT INTO `product_sku` VALUES (2047976835948343299, 2047962529810669570, 'sku-2047976835948343299', 't', '/upload/product/1cd86901fa0749dd922d408387cf91c3.jpg', 't', 't', 123.00, 123.00, 1234.00, 1221, 0, 1, '2026-04-25 17:51:41', '2026-04-25 18:42:24', 0);
INSERT INTO `product_sku` VALUES (2047978071208615937, 3100000000000004003, 'sku-2047978071208615937', '月白花影款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm', '真丝', 0.16, 168.00, 188.00, 0, 0, 1, '2026-04-25 17:56:36', '2026-04-26 18:45:39', 0);
INSERT INTO `product_sku` VALUES (2047978071208615938, 3100000000000004003, 'sku-2047978071208615938', '题字定制款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm，支持题字', '真丝', 0.18, 228.00, 248.00, 2, 0, 1, '2026-04-25 17:56:36', '2026-04-25 17:56:36', 0);
INSERT INTO `product_sku` VALUES (2047978956726853634, 2047978956726853633, 'sku-2047978956726853634', '默认规格1', '/upload/product/3e38ef296a8944e78770a60da4711ced.jpg', '默认规格描述', '金', 100.00, 1000.00, 10000.00, 996, 0, 1, '2026-04-25 18:00:07', '2026-04-25 18:11:59', 0);
INSERT INTO `product_sku` VALUES (2047978956726853635, 2047978956726853633, 'sku-2047978956726853635', '默认规格2', '/upload/product/b076a69fc9184bd0a51d50b1becab3fa.jpg', '默认规格2', '塑料', 10.00, 10.00, 100.00, 1780, 0, 1, '2026-04-25 18:00:07', '2026-04-26 18:00:26', 0);
INSERT INTO `product_sku` VALUES (3100000000000005001, 3100000000000004001, 'manual-mug-001', '暮青蓝单杯装', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '330ml', '陶', 0.45, 89.00, 109.00, 20, 2, 1, '2026-04-04 10:20:00', '2026-04-15 22:08:00', 0);
INSERT INTO `product_sku` VALUES (3100000000000005002, 3100000000000004001, 'manual-mug-002', '礼盒装双杯组', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '330ml*2', '陶', 0.95, 129.00, 149.00, 26, 1, 1, '2026-04-04 10:25:00', '2026-04-22 20:48:49', 0);
INSERT INTO `product_sku` VALUES (3100000000000005003, 3100000000000004002, 'manual-box-001', '原木标准款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '18cm x 12cm', '木作', 0.82, 239.00, 269.00, 7, 1, 1, '2026-04-04 11:20:00', '2026-04-24 18:03:21', 0);
INSERT INTO `product_sku` VALUES (3100000000000005004, 3100000000000004002, 'manual-box-002', '刻字定制款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '18cm x 12cm，支持盒盖刻字', '木作', 0.85, 299.00, 329.00, 10, 1, 1, '2026-04-04 11:25:00', '2026-04-22 21:02:39', 0);
INSERT INTO `product_sku` VALUES (3100000000000005005, 3100000000000004003, 'manual-fan-001', '月白花影款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm', '真丝', 0.16, 168.00, 188.00, 5, 0, 1, '2026-04-04 12:20:00', '2026-04-25 17:56:36', 1);
INSERT INTO `product_sku` VALUES (3100000000000005006, 3100000000000004003, 'manual-fan-002', '题字定制款', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '圆扇 21cm，支持题字', '真丝', 0.18, 228.00, 248.00, 2, 0, 1, '2026-04-04 12:25:00', '2026-04-25 17:56:36', 1);
INSERT INTO `product_sku` VALUES (3100000000000005007, 3100000000000004004, 'manual-tray-001', '圆形轻食托盘', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '直径 24cm', '藤编', 0.38, 96.00, 118.00, 16, 0, 1, '2026-04-04 13:20:00', '2026-04-15 22:08:00', 0);
INSERT INTO `product_sku` VALUES (3100000000000005008, 3100000000000004004, 'manual-tray-002', '椭圆玄关托盘', 'https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80', '30cm x 18cm', '藤编', 0.46, 128.00, 148.00, 14, 0, 1, '2026-04-04 13:25:00', '2026-04-15 22:08:00', 0);

-- ----------------------------
-- Table structure for staff_profile
-- ----------------------------
DROP TABLE IF EXISTS `staff_profile`;
CREATE TABLE `staff_profile`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '店员资料id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `staffName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '店员姓名',
  `staffNo` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工编号',
  `positionName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职位名称',
  `salary` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '工资',
  `entryTime` datetime NULL DEFAULT NULL COMMENT '入职时间',
  `staffStatus` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0离职 1在职',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_staff_profile_user_id`(`userId` ASC) USING BTREE,
  UNIQUE INDEX `uk_staff_profile_staff_no`(`staffNo` ASC) USING BTREE,
  CONSTRAINT `fk_staff_profile_user_id` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '店员资料表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of staff_profile
-- ----------------------------
INSERT INTO `staff_profile` VALUES (2047946658576953346, 2047946658576953345, 'z', '2047946658576953347', '1', 5000.00, '2026-04-25 15:51:43', 1, '2026-04-25 15:51:47', '2026-04-25 16:15:22', 0);

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '地址id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `receiverName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货人',
  `receiverPhone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货手机号',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区县',
  `detailAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `postalCode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮编',
  `tagName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址标签',
  `isDefault` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_address_userId`(`userId` ASC) USING BTREE,
  INDEX `idx_user_address_isDefault`(`userId` ASC, `isDefault` ASC) USING BTREE,
  CONSTRAINT `fk_user_address_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户收货地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (2046857939480522753, 2044038386325221377, 'Luigi Bello', '12345678900', 'Glendale', 'Glendale', '纽约', '12313213', NULL, NULL, 1, '2026-04-22 15:45:36', '2026-04-22 15:45:36', 0);
INSERT INTO `user_address` VALUES (2046934986638819330, 2044038386325221377, 'Barry Allen', '11111', 'Smyrna', 'Smyrna', 'a', 'asdasdad', 'asd', 'asda', 0, '2026-04-22 20:51:45', '2026-04-22 20:51:45', 0);
INSERT INTO `user_address` VALUES (3100000000000006001, 3100000000000001004, '林知夏', '13800000004', '上海市', '上海市', '徐汇区', '漕溪北路 88 号 8 幢 1602', '200030', '家', 1, '2026-04-08 18:20:00', '2026-04-12 09:00:00', 0);
INSERT INTO `user_address` VALUES (3100000000000006002, 3100000000000001004, '林知夏', '13800000004', '上海市', '上海市', '杨浦区', '大学路 15 号 2 单元 503', '200082', '公司', 0, '2026-04-09 12:10:00', '2026-04-09 12:10:00', 0);
INSERT INTO `user_address` VALUES (3100000000000006003, 3100000000000001005, '周聿风', '13800000005', '浙江省', '杭州市', '西湖区', '文三路 188 号 9 幢 902', '310012', '家', 1, '2026-04-09 16:40:00', '2026-04-12 10:00:00', 0);

-- ----------------------------
-- Table structure for user_product_behavior
-- ----------------------------
DROP TABLE IF EXISTS `user_product_behavior`;
CREATE TABLE `user_product_behavior`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '用户商品行为id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `skuId` bigint UNSIGNED NULL DEFAULT NULL COMMENT 'sku id',
  `behaviorType` tinyint NOT NULL COMMENT '行为类型：1浏览 2收藏 3加购 4下单 5评价',
  `behaviorWeight` decimal(10, 4) NOT NULL DEFAULT 1.0000 COMMENT '行为权重',
  `behaviorTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为发生时间',
  `sourceType` tinyint NOT NULL DEFAULT 0 COMMENT '来源类型：0手动 1浏览 2收藏 3购物车 4订单 5评价',
  `sourceId` bigint UNSIGNED NULL DEFAULT NULL COMMENT '来源记录id',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product_behavior_source`(`sourceType` ASC, `sourceId` ASC, `behaviorType` ASC, `userId` ASC, `productId` ASC, `skuId` ASC) USING BTREE,
  INDEX `idx_user_product_behavior_userId`(`userId` ASC) USING BTREE,
  INDEX `idx_user_product_behavior_productId`(`productId` ASC) USING BTREE,
  INDEX `idx_user_product_behavior_skuId`(`skuId` ASC) USING BTREE,
  INDEX `idx_user_product_behavior_time`(`behaviorTime` ASC) USING BTREE,
  CONSTRAINT `fk_user_product_behavior_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_product_behavior_skuId` FOREIGN KEY (`skuId`) REFERENCES `product_sku` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_product_behavior_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户商品行为表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_product_behavior
-- ----------------------------
INSERT INTO `user_product_behavior` VALUES (2048341384387960834, 2044038386325221377, 2047978956726853633, NULL, 1, 1.0000, '2026-04-26 18:00:17', 1, NULL, '2026-04-26 18:00:17', '2026-04-26 18:00:17', 0);
INSERT INTO `user_product_behavior` VALUES (2048341424825245697, 2044038386325221377, 2047978956726853633, 2047978956726853635, 4, 5.0000, '2026-04-26 18:00:26', 4, 2048341421398499330, '2026-04-26 18:00:26', '2026-04-26 18:00:26', 0);
INSERT INTO `user_product_behavior` VALUES (2048352736913465346, 2044038386325221377, 2047978956726853633, NULL, 1, 1.0000, '2026-04-26 18:45:23', 1, NULL, '2026-04-26 18:45:23', '2026-04-26 18:45:23', 0);
INSERT INTO `user_product_behavior` VALUES (2048352762985259011, 2044038386325221377, 3100000000000004003, NULL, 1, 1.0000, '2026-04-26 18:45:29', 1, NULL, '2026-04-26 18:45:29', '2026-04-26 18:45:29', 0);
INSERT INTO `user_product_behavior` VALUES (2048352804903133186, 2044038386325221377, 3100000000000004003, 2047978071208615937, 4, 5.0000, '2026-04-26 18:45:39', 4, 2048352800243261441, '2026-04-26 18:45:39', '2026-04-26 18:45:39', 0);
INSERT INTO `user_product_behavior` VALUES (2048353819199086595, 2044038386325221377, 2047962529810669570, NULL, 1, 1.0000, '2026-04-26 18:49:41', 1, NULL, '2026-04-26 18:49:41', '2026-04-26 18:49:41', 0);
INSERT INTO `user_product_behavior` VALUES (2048356594154831875, 2044038386325221377, 3100000000000004003, NULL, 1, 1.0000, '2026-04-26 19:00:43', 1, NULL, '2026-04-26 19:00:43', '2026-04-26 19:00:43', 0);
INSERT INTO `user_product_behavior` VALUES (2048356629923856386, 2044038386325221377, 3100000000000004003, NULL, 2, 3.0000, '2026-04-26 19:00:51', 2, 2048356629923856385, '2026-04-26 19:00:51', '2026-04-26 19:00:54', 1);
INSERT INTO `user_product_behavior` VALUES (2048356646868844546, 2044038386325221377, 3100000000000004003, NULL, 2, 3.0000, '2026-04-26 19:00:55', 2, 2048356629923856385, '2026-04-26 19:00:55', '2026-04-26 19:00:55', 0);
INSERT INTO `user_product_behavior` VALUES (2048356679978680323, 2044038386325221377, 3100000000000004003, NULL, 1, 1.0000, '2026-04-26 19:01:03', 1, NULL, '2026-04-26 19:01:03', '2026-04-26 19:01:03', 0);
INSERT INTO `user_product_behavior` VALUES (2048393996216078337, 3100000000000001004, 3100000000000004003, NULL, 2, 3.0000, '2026-04-26 21:29:20', 2, 3100000000000012002, '2026-04-26 21:29:20', '2026-04-26 21:29:20', 0);

-- ----------------------------
-- Table structure for user_product_recommend
-- ----------------------------
DROP TABLE IF EXISTS `user_product_recommend`;
CREATE TABLE `user_product_recommend`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '用户商品推荐id',
  `userId` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `productId` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `recommendScore` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '推荐分数',
  `recommendReason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '推荐原因',
  `rankNo` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '推荐排序',
  `algorithmVersion` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'item_cf_v1' COMMENT '算法版本',
  `expireTime` datetime NULL DEFAULT NULL COMMENT '推荐过期时间',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product_recommend_user_product`(`userId` ASC, `productId` ASC, `algorithmVersion` ASC) USING BTREE,
  INDEX `idx_user_product_recommend_user_rank`(`userId` ASC, `rankNo` ASC) USING BTREE,
  INDEX `idx_user_product_recommend_productId`(`productId` ASC) USING BTREE,
  INDEX `idx_user_product_recommend_score`(`recommendScore` DESC) USING BTREE,
  INDEX `idx_user_product_recommend_expireTime`(`expireTime` ASC) USING BTREE,
  CONSTRAINT `fk_user_product_recommend_productId` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_product_recommend_userId` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户商品推荐结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_product_recommend
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `userAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录账号',
  `userPassword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录密码',
  `userName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatarUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像地址',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
  `userRole` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '角色：user/admin',
  `userStatus` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `lastLoginTime` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_users_userAccount`(`userAccount` ASC) USING BTREE,
  UNIQUE INDEX `uk_users_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_users_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商城用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (2044038386325221377, 'Truth', 'f230d6eabd129fd89ff1bd8c8c0319c4', '手作用户', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=240&q=80', NULL, NULL, 0, 'admin', 0, 8256642.00, '2026-04-26 21:30:02', '2026-04-14 21:01:42', '2026-04-26 18:45:39', 0);
INSERT INTO `users` VALUES (2047946658576953345, 'd0001', 'f230d6eabd129fd89ff1bd8c8c0319c4', 'zzz', NULL, '1', '1', 0, 'staff', 0, 0.00, NULL, '2026-04-25 15:51:47', '2026-04-25 16:15:22', 0);
INSERT INTO `users` VALUES (3100000000000001001, 'admin_manual', 'f230d6eabd129fd89ff1bd8c8c0319c4', '平台管理员', 'https://example.com/avatar/admin_manual.png', '13800000001', 'admin@manualmall.com', 0, 'user', 0, 0.00, '2026-04-13 08:30:00', '2026-04-13 08:00:00', '2026-04-19 19:06:02', 0);
INSERT INTO `users` VALUES (3100000000000001002, 'artisan_lu', 'f230d6eabd129fd89ff1bd8c8c0319c4', '陆青禾', 'https://example.com/avatar/artisan_lu.png', '13800000002', 'luqinghe@manualmall.com', 2, 'artisan', 0, 0.00, '2026-04-12 21:15:00', '2026-04-01 10:00:00', '2026-04-19 19:06:02', 0);
INSERT INTO `users` VALUES (3100000000000001003, 'artisan_qin', 'f230d6eabd129fd89ff1bd8c8c0319c4', '秦木生', 'https://example.com/avatar/artisan_qin.png', '13800000003', 'qinmusheng@manualmall.com', 1, 'artisan', 0, 0.00, '2026-04-12 20:50:00', '2026-04-02 09:20:00', '2026-04-19 19:06:02', 0);
INSERT INTO `users` VALUES (3100000000000001004, 'buyer_lin', 'f230d6eabd129fd89ff1bd8c8c0319c4', '林知夏', 'https://example.com/avatar/buyer_lin.png', '13800000004', 'linzhixia@manualmall.com', 2, 'artisan', 0, 10010680.50, '2026-04-20 10:32:23', '2026-04-05 11:30:00', '2026-04-19 19:06:02', 0);
INSERT INTO `users` VALUES (3100000000000001005, 'buyer_zhou', 'f230d6eabd129fd89ff1bd8c8c0319c4', '周聿风', '/upload/user/5ec59436a47b45bfb5051e9285c7518f.png', '13800000005', 'zhouyufeng@manualmall.com', 1, 'user', 0, 991.00, '2026-04-24 18:02:52', '2026-04-06 14:20:00', '2026-04-24 18:03:21', 0);
INSERT INTO `users` VALUES (3100000000000001006, 'artisan_shen', 'f230d6eabd129fd89ff1bd8c8c0319c4', '沈绣宁', 'https://example.com/avatar/artisan_shen.png', '13800000006', 'shenxiuning@manualmall.com', 2, 'artisan', 0, 0.00, '2026-04-20 09:12:47', '2026-04-03 13:15:00', '2026-04-18 16:03:21', 0);

SET FOREIGN_KEY_CHECKS = 1;
