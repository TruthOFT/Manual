set names utf8mb4;
set foreign_key_checks = 0;
use manual_mall;
delete from product_browse_log;
delete from product_review;
delete from product_favorite;
delete from custom_requirement;
delete from order_logistics;
delete from order_item;
delete from orders;
delete from cart_item;
delete from cart;
delete from user_address;
delete from product_sku;
delete from product_material;
delete from product_image;
delete from product;
delete from category;
delete from artisan_profile;
delete from users;

insert into users (
    id,
    userAccount,
    userPassword,
    userName,
    avatarUrl,
    phone,
    email,
    gender,
    userRole,
    userStatus,
    balance,
    lastLoginTime,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000001001, 'admin_manual', 'e10adc3949ba59abbe56e057f20f883e', 'Platform Admin', 'https://example.com/avatar/admin_manual.png', '13800000001', 'admin@manualmall.com', 0, 'admin', 0, 0.00, '2026-04-13 08:30:00', '2026-04-13 08:00:00', '2026-04-13 08:30:00', 0),
      (3100000000000001002, 'artisan_lu', 'e10adc3949ba59abbe56e057f20f883e', 'Lu Qinghe', 'https://example.com/avatar/artisan_lu.png', '13800000002', 'luqinghe@manualmall.com', 2, 'artisan', 0, 0.00, '2026-04-12 21:15:00', '2026-04-01 10:00:00', '2026-04-12 21:15:00', 0),
      (3100000000000001003, 'artisan_qin', 'e10adc3949ba59abbe56e057f20f883e', 'Qin Musheng', 'https://example.com/avatar/artisan_qin.png', '13800000003', 'qinmusheng@manualmall.com', 1, 'artisan', 0, 0.00, '2026-04-12 20:50:00', '2026-04-02 09:20:00', '2026-04-12 20:50:00', 0),
      (3100000000000001004, 'buyer_lin', 'e10adc3949ba59abbe56e057f20f883e', 'Lin Zhixia', 'https://example.com/avatar/buyer_lin.png', '13800000004', 'linzhixia@manualmall.com', 2, 'user', 0, 680.50, '2026-04-13 09:10:00', '2026-04-05 11:30:00', '2026-04-13 09:10:00', 0),
      (3100000000000001005, 'buyer_zhou', 'e10adc3949ba59abbe56e057f20f883e', 'Zhou Yufeng', 'https://example.com/avatar/buyer_zhou.png', '13800000005', 'zhouyufeng@manualmall.com', 1, 'user', 0, 1230.00, '2026-04-13 09:25:00', '2026-04-06 14:20:00', '2026-04-13 09:25:00', 0),
      (3100000000000001006, 'artisan_shen', 'e10adc3949ba59abbe56e057f20f883e', 'Shen Xiuning', 'https://example.com/avatar/artisan_shen.png', '13800000006', 'shenxiuning@manualmall.com', 2, 'artisan', 0, 0.00, '2026-04-12 18:40:00', '2026-04-03 13:15:00', '2026-04-12 18:40:00', 0);

insert into artisan_profile (
    id,
    userId,
    artisanName,
    shopName,
    artisanAvatar,
    coverUrl,
    artisanStory,
    craftCategory,
    originPlace,
    experienceYears,
    supportCustom,
    contactPhone,
    qualificationDesc,
    qualificationImages,
    depositAmount,
    auditRemark,
    auditStatus,
    shelfStatus,
    applyTime,
    auditTime,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000002001, 3100000000000001002, 'Lu Qinghe', 'Qinghe Pottery', 'https://example.com/shop/qinghe/avatar.png', 'https://example.com/shop/qinghe/cover.png', 'Focuses on daily pottery and local exhibitions.', 'pottery', 'Jingdezhen', 9, 1, '13800000002', 'Pottery training certificate and workshop proof.', 'https://example.com/application/qinghe_qualification_1.png,https://example.com/application/qinghe_qualification_2.png', 1000.00, null, 1, 1, '2026-04-01 10:05:00', '2026-04-01 12:00:00', '2026-04-01 10:10:00', '2026-04-12 20:00:00', 0),
      (3100000000000002002, 3100000000000001003, 'Qin Musheng', 'Muxin Workshop', 'https://example.com/shop/muxin/avatar.png', 'https://example.com/shop/muxin/cover.png', 'Builds mortise and tenon woodwork and custom home pieces.', 'woodwork', 'Hangzhou', 12, 1, '13800000003', 'Workshop business material and course certificate.', 'https://example.com/application/muxin_qualification_1.png', 1000.00, null, 1, 1, '2026-04-02 09:10:00', '2026-04-02 11:30:00', '2026-04-02 09:30:00', '2026-04-12 19:20:00', 0),
      (3100000000000002003, 3100000000000001006, 'Shen Xiuning', 'Xiuyun Studio', 'https://example.com/shop/xiuyunge/avatar.png', 'https://example.com/shop/xiuyunge/cover.png', 'Creates embroidery works and joins regional exhibitions.', 'embroidery', 'Suzhou', 8, 1, '13800000006', 'Training proof and exhibition records.', 'https://example.com/application/xiuyunge_qualification_1.png,https://example.com/application/xiuyunge_qualification_2.png', 1000.00, null, 1, 1, '2026-04-03 13:05:00', '2026-04-03 15:00:00', '2026-04-03 13:20:00', '2026-04-12 18:10:00', 0);

insert into category (
    id,
    parentId,
    categoryName,
    categoryIcon,
    categoryDesc,
    categoryLevel,
    sortOrder,
    isEnable,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000003001, null, '陶瓷', 'https://example.com/category/pottery.png', '手作陶瓷、茶器与摆件', 1, 1, 1, '2026-04-01 09:00:00', '2026-04-01 09:00:00', 0),
      (3100000000000003002, null, '木作', 'https://example.com/category/woodwork.png', '木盒、榫卯与家居小物', 1, 2, 1, '2026-04-01 09:05:00', '2026-04-01 09:05:00', 0),
      (3100000000000003003, null, '刺绣', 'https://example.com/category/embroidery.png', '团扇、挂画与布艺刺绣', 1, 3, 1, '2026-04-01 09:10:00', '2026-04-01 09:10:00', 0),
      (3100000000000003004, null, '编织', 'https://example.com/category/weaving.png', '藤编、草编与装饰器具', 1, 4, 1, '2026-04-01 09:15:00', '2026-04-01 09:15:00', 0),
      (3100000000000003005, 3100000000000003001, '茶具器具', 'https://example.com/category/tea_ware.png', '适合日常喝茶与咖啡场景', 2, 10, 1, '2026-04-01 09:20:00', '2026-04-01 09:20:00', 0),
      (3100000000000003006, 3100000000000003002, '木盒收纳', 'https://example.com/category/storage_box.png', '礼盒、首饰盒与桌面收纳', 2, 20, 1, '2026-04-01 09:25:00', '2026-04-01 09:25:00', 0),
      (300000000000003007, 3100000000000003003, '团扇配饰', 'https://example.com/category/fan.png', '刺绣团扇与节气礼品', 2, 30, 1, '2026-04-01 09:30:00', '2026-04-01 09:30:00', 0),
      (3100000000000003008, 3100000000000003004, '藤编收纳', 'https://example.com/category/rattan.png', '藤编托盘与居家收纳', 2, 40, 1, '2026-04-01 09:35:00', '2026-04-01 09:35:00', 0);

insert into product (
    id,
    categoryId,
    artisanId,
    productName,
    productSubtitle,
    productCover,
    productDesc,
    craftType,
    materialDesc,
    originPlace,
    handmadeCycleDays,
    supportCustom,
    inventory,
    soldQuantity,
    favoriteCount,
    reviewCount,
    minPrice,
    maxPrice,
    auditStatus,
    status,
    sortOrder,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000004001, 3100000000000003005, 3100000000000002001, '粉青釉云纹马克杯', '景德镇手作，杯型圆润，适合日常饮水与热茶', 'https://example.com/product/mug_cover.png', '杯身纯手工拉坯成型，釉面呈现自然窑变，每一只纹理略有差异。', '拉坯粉青釉', '高白泥、矿物釉', '景德镇', 5, 1, 46, 18, 1, 1, 89.00, 129.00, 1, 1, 100, '2026-04-04 10:00:00', '2026-04-12 18:00:00', 0),
      (3100000000000004002, 3100000000000003006, 3100000000000002002, '黑胡桃榫卯首饰盒', '可刻字的双层收纳盒，适合作为礼品', 'https://example.com/product/box_cover.png', '采用黑胡桃木手工打磨与天然木蜡油涂层，盒盖与盒身为榫卯拼接结构。', '榫卯木作', '黑胡桃木、黄铜配件', '杭州', 7, 1, 18, 6, 1, 0, 239.00, 299.00, 1, 1, 95, '2026-04-04 11:00:00', '2026-04-12 18:05:00', 0),
      (3100000000000004003, 3100000000000003007, 3100000000000002003, '苏绣花影真丝团扇', '双面刺绣，扇骨轻盈，适合夏日纳凉与送礼', 'https://example.com/product/fan_cover.png', '选用真丝扇面与竹制扇骨，双面绣花图案，支持题字与包装定制。', '双面苏绣', '真丝、竹骨', '苏州', 10, 1, 12, 4, 1, 0, 168.00, 228.00, 1, 1, 90, '2026-04-04 12:00:00', '2026-04-12 18:10:00', 0),
      (3100000000000004004, 3100000000000003008, 3100000000000002002, '藤编居家收纳托盘', '餐桌与客厅两用，轻便耐用', 'https://example.com/product/tray_cover.png', '手工藤编结构紧密，适合水果、面包与茶具小物收纳。', '手工藤编', '藤条、棉绳', '安吉', 4, 0, 30, 9, 0, 0, 96.00, 128.00, 1, 1, 85, '2026-04-04 13:00:00', '2026-04-12 18:15:00', 0);

insert into product_image (
    id,
    productId,
    imageUrl,
    imageType,
    sortOrder,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000004101, 3100000000000004001, 'https://example.com/product/mug_cover.png', 'cover', 1, '2026-04-04 10:05:00', '2026-04-04 10:05:00', 0),
      (3100000000000004102, 3100000000000004001, 'https://example.com/product/mug_detail_1.png', 'detail', 2, '2026-04-04 10:05:00', '2026-04-04 10:05:00', 0),
      (3100000000000004103, 3100000000000004002, 'https://example.com/product/box_cover.png', 'cover', 1, '2026-04-04 11:05:00', '2026-04-04 11:05:00', 0),
      (3100000000000004104, 3100000000000004002, 'https://example.com/product/box_detail_1.png', 'detail', 2, '2026-04-04 11:05:00', '2026-04-04 11:05:00', 0),
      (3100000000000004105, 3100000000000004003, 'https://example.com/product/fan_cover.png', 'cover', 1, '2026-04-04 12:05:00', '2026-04-04 12:05:00', 0),
      (31000000004106, 3100000000000004003, 'https://example.com/product/fan_detail_1.png', 'detail', 2, '2026-04-04 12:05:00', '2026-04-04 12:05:00', 0),
      (3100000000000004107, 3100000000000004004, 'https://example.com/product/tray_cover.png', 'cover', 1, '2026-04-04 13:05:00', '2026-04-04 13:05:00', 0),
      (3100000000000004108, 3100000000000004004, 'https://example.com/product/tray_detail_1.png', 'detail', 2, '2026-04-04 13:05:00', '2026-04-04 13:05:00', 0);

insert into product_material (
    id,
    productId,
    materialName,
    materialOrigin,
    materialRatio,
    sortOrder,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000004201, 3100000000000004001, '高白泥', '景德镇', '85%', 1, '2026-04-04 10:10:00', '2026-04-04 10:10:00', 0),
      (3100000000000004202, 3100000000000004001, '矿物釉', '景德镇', '15%', 2, '2026-04-04 10:10:00', '2026-04-04 10:10:00', 0),
      (3100000000000004203, 3100000000000004002, '黑胡桃木', '北美进口', '95%', 1, '2026-04-04 11:10:00', '2026-04-04 11:10:00', 0),
      (3100000000000004204, 3100000000000004002, '黄铜配件', '国内五金', '5%', 2, '2026-04-04 11:10:00', '2026-04-04 11:10:00', 0),
      (3100000000000004205, 3100000000000004003, '真丝扇面', '苏州', '60%', 1, '2026-04-04 12:10:00', '2026-04-04 12:10:00', 0),
      (3100000000000004206, 3100000000000004003, '竹制扇骨', '浙江安吉', '40%', 2, '2026-04-04 12:10:00', '2026-04-04 12:10:00', 0),
      (3100000000000004207, 3100000000000004004, '藤条', '安吉', '90%', 1, '2026-04-04 13:10:00', '2026-04-04 13:10:00', 0),
      (3100000000000004208, 3100000000000004004, '棉绳', '浙江', '10%', 2, '2026-04-04 13:10:00', '2026-04-04 13:10:00', 0);

insert into product_sku (
    id,
    productId,
    skuCode,
    skuName,
    skuCover,
    specText,
    materialType,
    weight,
    price,
    originalPrice,
    stock,
    lockedStock,
    status,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000005001, 3100000000000004001, 'manual-mug-001', '浅青蓝单杯装', 'https://example.com/product/mug_sku_1.png', '330ml', '陶瓷', 0.45, 89.00, 109.00, 20, 2, 1, '2026-04-04 10:20:00', '2026-04-12 18:00:00', 0),
      (3100000000000005002, 3100000000000004001, 'manual-mug-002', '礼盒装双杯组', 'https://example.com/product/mug_sku_2.png', '330ml*2', '陶瓷', 0.95, 129.00, 149.00, 26, 1, 1, '2026-04-04 10:25:00', '2026-04-12 18:00:00', 0),
      (3100000000000005003, 3100000000000004002, 'manual-box-001', '原木标准款', 'https://example.com/product/box_sku_1.png', '18cm x 12cm', '木作', 0.82, 239.00, 269.00, 8, 1, 1, '2026-04-04 11:20:00', '2026-04-12 18:05:00', 0),
      (3100000000000005004, 3100000000000004002, 'manual-box-002', '刻字定制款', 'https://example.com/product/box_sku_2.png', '18cm x 12cm，支持盒盖刻字', '木作', 0.85, 299.00, 329.00, 10, 1, 1, '2026-04-04 11:25:00', '2026-04-12 18:05:00', 0),
      (3100000000000005005, 3100000000000004003, 'manual-fan-001', '月白花影款', 'https://example.com/product/fan_sku_1.png', '圆扇 21cm', '真丝', 0.16, 168.00, 188.00, 5, 0, 1, '2026-04-04 12:20:00', '2026-04-12 18:10:00', 0),
      (3100000000000005006, 3100000000000004003, 'manual-fan-002', '题字定制款', 'https://example.com/product/fan_sku_2.png', '圆扇 21cm，支持题字', '真丝', 0.18, 228.00, 248.00, 7, 1, 1, '2026-04-04 12:25:00', '2026-04-12 18:10:00', 0),
      (3100000000000005007, 3100000000000004004, 'manual-tray-001', '圆形餐用托盘', 'https://example.com/product/tray_sku_1.png', '直径 24cm', '藤编', 0.38, 96.00, 118.00, 16, 0, 1, '2026-04-04 13:20:00', '2026-04-12 18:15:00', 0),
      (3100000000000005008, 3100000000000004004, 'manual-tray-002', '椭圆茶具托盘', 'https://example.com/product/tray_sku_2.png', '30cm x 18cm', '藤编', 0.46, 128.00, 148.00, 14, 0, 1, '2026-04-04 13:25:00', '2026-04-12 18:15:00', 0);

insert into user_address (
    id,
    userId,
    receiverName,
    receiverPhone,
    province,
    city,
    district,
    detailAddress,
    postalCode,
    tagName,
    isDefault,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000006001, 3100000000000001004, '林志夏', '13800000004', '上海市', '上海市', '徐汇区', '衡山路 88 号 8 号楼 1602', '200030', '家', 1, '2026-04-08 18:20:00', '2026-04-12 09:00:00', 0),
      (3100000000000006002, 3100000000000001004, '林志夏', '13800000004', '上海市', '上海市', '杨浦区', '大学路 15 号 2 单元 503', '200082', '公司', 0, '2026-04-09 12:10:00', '2026-04-09 12:10:00', 0),
      (3100000000000006003, 3100000000000001005, '周宇风', '13800000005', '浙江省', '杭州市', '西湖区', '文三路 188 号 9 号楼 902', '310012', '家', 1, '2026-04-09 16:40:00', '2026-04-12 10:00:00', 0);

insert into cart (
    id,
    userId,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000007001, 3100000000000001004, '2026-04-12 19:10:00', '2026-04-13 09:00:00', 0),
      (3100000000000007002, 3100000000000001005, '2026-04-12 20:20:00', '2026-04-13 09:05:00', 0);

insert into cart_item (
    id,
    cartId,
    userId,
    productId,
    skuId,
    quantity,
    price,
    selected,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000007101, 3100000000000007001, 3100000000000001004, 3100000000000004003, 3100000000000005005, 1, 168.00, 1, '2026-04-12 19:20:00', '2026-04-13 09:00:00', 0),
      (3100000000000007102, 3100000000000007001, 3100000000000001004, 3100000000000004004, 3100000000000005008, 2, 128.00, 1, '2026-04-12 19:25:00', '2026-04-13 09:00:00', 0),
      (3100000000000007103, 3100000000000007002, 3100000000000001005, 3100000000000004001, 3100000000000005002, 1, 129.00, 1, '2026-04-12 20:30:00', '2026-04-13 09:05:00', 0);

insert into orders (
    id,
    orderNo,
    userId,
    addressId,
    orderStatus,
    payStatus,
    payType,
    deliveryStatus,
    productAmount,
    discountAmount,
    freightAmount,
    payAmount,
    buyerRemark,
    receiverName,
    receiverPhone,
    province,
    city,
    district,
    detailAddress,
    payTime,
    deliveryTime,
    receiveTime,
    finishTime,
    cancelTime,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000008001, 'mo202604130001', 3100000000000001004, 3100000000000006001, 3, 1, 'wechat', 2, 129.00, 0.00, 12.00, 141.00, '杯子包装好，避免磕碰', '林志夏', '13800000004', '上海市', '上海市', '徐汇区', '衡山路 88 号 8 号楼 1602', '2026-04-10 10:06:00', '2026-04-10 17:30:00', '2026-04-12 14:20:00', '2026-04-12 20:00:00', null, '2026-04-10 10:00:00', '2026-04-12 20:00:00', 0),
      (3100000000000008002, 'mo202604130002', 3100000000000001005, 3100000000000006003, 2, 1, 'alipay', 1, 299.00, 0.00, 18.00, 317.00, '盒面刻字：赠予春日', '周宇风', '13800000005', '浙江省', '杭州市', '西湖区', '文三路 188 号 9 号楼 902', '2026-04-11 13:15:00', '2026-04-12 16:40:00', null, null, null, '2026-04-11 13:10:00', '2026-04-12 16:40:00', 0),
      (3100000000000008003, 'mo202604130003', 3100000000000001004, 3100000000000006001, 1, 1, 'wechat', 0, 228.00, 20.00, 12.00, 220.00, '题字内容：夏日有风', '林志夏', '13800000004', '上海市', '上海市', '徐汇区', '衡山路 88 号 8 号楼 1602', '2026-04-13 08:40:00', null, null, null, null, '2026-04-13 08:35:00', '2026-04-13 08:40:00', 0);

insert into order_item (
    id,
    orderId,
    orderNo,
    userId,
    productId,
    skuId,
    artisanId,
    productName,
    skuName,
    productCover,
    specText,
    quantity,
    salePrice,
    totalAmount,
    refundStatus,
    reviewStatus,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000009001, 3100000000000008001, 'mo202604130001', 3100000000000001004, 3100000000000004001, 3100000000000005002, 3100000000000002001, '粉青釉云纹马克杯', '礼盒装双杯组', 'https://example.com/product/mug_cover.png', '330ml*2', 1, 129.00, 129.00, 0, 1, '2026-04-10 10:00:30', '2026-04-12 20:00:00', 0),
      (3100000000000009002, 3100000000000008002, 'mo202604130002', 3100000000000001005, 3100000000000004002, 3100000000000005004, 3100000000000002002, '黑胡桃榫卯首饰盒', '刻字定制款', 'https://example.com/product/box_cover.png', '18cm x 12cm，支持盒盖刻字', 1, 299.00, 299.00, 0, 0, '2026-04-11 13:10:30', '2026-04-12 16:40:00', 0),
      (3100000000000009003, 3100000000000008003, 'mo202604130003', 3100000000000001004, 3100000000000004003, 3100000000000005006, 3100000000000002003, '苏绣花影真丝团扇', '题字定制款', 'https://example.com/product/fan_cover.png', '圆扇 21cm，支持题字', 1, 228.00, 228.00, 0, 0, '2026-04-13 08:35:30', '2026-04-13 08:40:00', 0);

insert into custom_requirement (
    id,
    orderId,
    orderItemId,
    userId,
    productId,
    requirementTitle,
    requirementContent,
    referenceImages,
    confirmStatus,
    confirmRemark,
    confirmTime,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000010001, 3100000000000008002, 3100000000000009002, 3100000000000001005, 3100000000000004002, '首饰盒刻字定制', '希望在盒盖正中间刻上“赠予春日”，字体偏手写风，包装增加棉麻丝带。', 'https://example.com/custom/box_reference_1.png', 1, '已确认排版，按常规金色烫边处理。', '2026-04-11 18:30:00', '2026-04-11 14:00:00', '2026-04-11 18:30:00', 0),
      (3100000000000010002, 3100000000000008003, 3100000000000009003, 3100000000000001004, 3100000000000004003, '团扇题字定制', '扇面背面题字“夏日有风”，希望题字颜色偏浅蓝，外包装加流苏挂绳。', 'https://example.com/custom/fan_reference_1.png', 0, '等待匠人确认题字位置。', null, '2026-04-13 08:45:00', '2026-04-13 08:45:00', 0);

insert into order_logistics (
    id,
    orderId,
    orderNo,
    companyName,
    trackingNo,
    senderName,
    senderPhone,
    receiverName,
    receiverPhone,
    logisticsRemark,
    shipTime,
    signTime,
    status,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000011001, 3100000000000008001, 'mo202604130001', '顺丰速运', 'SF202604100001', '清河陶艺', '13800000002', '林志夏', '13800000004', '易碎品已加厚泡沫包装', '2026-04-10 17:30:00', '2026-04-12 14:20:00', 2, '2026-04-10 17:35:00', '2026-04-12 14:20:00', 0),
      (3100000000000011002, 3100000000000008002, 'mo202604130002', '京东物流', 'JD202604120001', '木心造物', '13800000003', '周宇风', '13800000005', '定制刻字款，木盒四角已做防撞处理', '2026-04-12 16:40:00', null, 1, '2026-04-12 16:45:00', '2026-04-12 16:45:00', 0);

insert into product_favorite (
    id,
    userId,
    productId,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000012001, 3100000000000001004, 3100000000000004001, '2026-04-09 21:00:00', '2026-04-09 21:00:00', 0),
      (3100000000000012002, 3100000000000001004, 3100000000000004003, '2026-04-12 20:10:00', '2026-04-12 20:10:00', 0),
      (3100000000000012003, 3100000000000001005, 3100000000000004002, '2026-04-10 22:15:00', '2026-04-10 22:15:00', 0);

insert into product_review (
    id,
    orderId,
    orderItemId,
    userId,
    productId,
    skuId,
    score,
    reviewContent,
    reviewImages,
    isAnonymous,
    replyContent,
    replyTime,
    status,
    createTime,
    updateTime,
    isDelete
) values
    (3100000000000013001, 3100000000000008001, 3100000000000009001, 3100000000000001004, 3100000000000004001, 3100000000000005002, 5, '杯型很顺手，釉色比图片更有层次，送朋友也很体面。', 'https://example.com/review/mug_review_1.png,https://example.com/review/mug_review_2.png', 0, '感谢喜欢，我们后续会继续更新同系列茶器。', '2026-04-12 21:10:00', 1, '2026-04-12 20:30:00', '2026-04-12 21:10:00', 0);

insert into product_browse_log (
    id,
    userId,
    productId,
    sourcePage,
    staySeconds,
    deviceType,
    createTime,
    updateTime,
    isDelete
) values
      (3100000000000014001, 3100000000000001004, 3100000000000004001, 'home', 36, 'h5', '2026-04-09 20:30:00', '2026-04-09 20:30:00', 0),
      (3100000000000014002, 3100000000000001004, 3100000000000004003, 'detail', 92, 'h5', '2026-04-12 20:05:00', '2026-04-12 20:05:00', 0),
      (3100000000000014003, 3100000000000001004, 3100000000000004004, 'search', 28, 'pc', '2026-04-12 20:12:00', '2026-04-12 20:12:00', 0),
      (3100000000000014004, 3100000000000001005, 3100000000000004002, 'detail', 145, 'pc', '2026-04-10 21:50:00', '2026-04-10 21:50:00', 0),
      (3100000000000014005, 3100000000000001005, 3100000000000004001, 'home', 22, 'pc', '2026-04-12 19:40:00', '2026-04-12 19:40:00', 0),
      (3100000000000014006, 3100000000000001005, 3100000000000004003, 'recommend', 51, 'pc', '2026-04-12 19:48:00', '2026-04-12 19:48:00', 0);

set foreign_key_checks = 1;