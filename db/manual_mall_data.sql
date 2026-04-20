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
    (3100000000000003001, null, '闄惰壓', 'https://example.com/category/pottery.png', '鎵嬩綔闄舵澂銆佽尪鍣ㄤ笌鎽嗕欢', 1, 1, 1, '2026-04-01 09:00:00', '2026-04-01 09:00:00', 0),
    (3100000000000003002, null, '鏈ㄤ綔', 'https://example.com/category/woodwork.png', '鏈ㄧ洅銆佹湪鐩樹笌瀹跺眳灏忎欢', 1, 2, 1, '2026-04-01 09:05:00', '2026-04-01 09:05:00', 0),
    (3100000000000003003, null, '鍒虹唬', 'https://example.com/category/embroidery.png', '鍥㈡墖銆佹寕楗颁笌甯冭壓鍒虹唬', 1, 3, 1, '2026-04-01 09:10:00', '2026-04-01 09:10:00', 0),
    (3100000000000003004, null, '缂栫粐', 'https://example.com/category/weaving.png', '钘ょ紪銆佽崏缂栦笌杞鍣ㄥ叿', 1, 4, 1, '2026-04-01 09:15:00', '2026-04-01 09:15:00', 0),
    (3100000000000003005, 3100000000000003001, '鑼跺櫒鏉叿', 'https://example.com/category/tea_ware.png', '閫傚悎鏃ュ父楗尪鍜屽挅鍟″満鏅?, 2, 10, 1, '2026-04-01 09:20:00', '2026-04-01 09:20:00', 0),
    (3100000000000003006, 3100000000000003002, '鏈ㄧ洅鏀剁撼', 'https://example.com/category/storage_box.png', '绀肩洅銆侀楗扮洅涓庢闈㈡敹绾?, 2, 20, 1, '2026-04-01 09:25:00', '2026-04-01 09:25:00', 0),
    (3100000000000003007, 3100000000000003003, '鍥㈡墖閰嶉グ', 'https://example.com/category/fan.png', '鍒虹唬鍥㈡墖涓庤妭浠ょぜ鐗?, 2, 30, 1, '2026-04-01 09:30:00', '2026-04-01 09:30:00', 0),
    (3100000000000003008, 3100000000000003004, '钘ょ紪鏀剁撼', 'https://example.com/category/rattan.png', '钘ょ紪鎵樼洏涓庡眳瀹舵敹绾?, 2, 40, 1, '2026-04-01 09:35:00', '2026-04-01 09:35:00', 0);

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
    (3100000000000004001, 3100000000000003005, 3100000000000002001, '鏌寸儳閲夐櫠椹厠鏉?, '鏅痉闀囨墜浣滐紝鏉部鍦嗘鼎锛岄€傚悎鏃ュ父鍜栧暋涓庣儹鑼?, 'https://example.com/product/mug_cover.png', '鏉韩淇濈暀鎵嬪伐鎷夊澂绾圭悊锛岄噳闈㈠憟鐜拌嚜鐒剁獞鍙橈紝姣忎竴鍙汗璺暐鏈夊樊寮傘€?, '鎷夊澂鏌寸儳', '楂樼櫧娉ャ€佹湪鐏伴噳', '鏅痉闀?, 5, 1, 46, 18, 1, 1, 89.00, 129.00, 1, 1, 100, '2026-04-04 10:00:00', '2026-04-12 18:00:00', 0),
    (3100000000000004002, 3100000000000003006, 3100000000000002002, '榛戣儭妗冩Λ鍗楗扮洅', '鍙姞鍒诲瓧鐨勫弻灞傛敹绾崇洅锛岄€傚悎浣滀负绀肩墿', 'https://example.com/product/box_cover.png', '閲囩敤榛戣儭妗冩湪鎵嬪伐鎵撶（涓庡ぉ鐒舵湪铚℃补灏佸眰锛岀洅鐩栦笌鐩掕韩涓烘Λ鍗嫾鎺ョ粨鏋勩€?, '姒嵂鏈ㄤ綔', '榛戣儭妗冩湪銆侀粍閾滄墸浠?, '鏉窞', 7, 1, 18, 6, 1, 0, 239.00, 299.00, 1, 1, 95, '2026-04-04 11:00:00', '2026-04-12 18:05:00', 0),
    (3100000000000004003, 3100000000000003007, 3100000000000002003, '鑻忕唬鑺卞奖鐪熶笣鍥㈡墖', '鍙岄潰鍒虹唬锛屾墖楠ㄨ交宸э紝閫傚悎澶忔棩绌挎惌涓庨€佺ぜ', 'https://example.com/product/fan_cover.png', '閫夌敤鐪熶笣缁㈤潰涓庣鍒舵墖楠紝鍙岄潰缁ｈ姳褰卞浘妗堬紝鏀寔棰樺瓧涓庡寘瑁呭畾鍒躲€?, '鍙岄潰鑻忕唬', '鐪熶笣銆佺楠?, '鑻忓窞', 10, 1, 12, 4, 1, 0, 168.00, 228.00, 1, 1, 90, '2026-04-04 12:00:00', '2026-04-12 18:10:00', 0),
    (3100000000000004004, 3100000000000003008, 3100000000000002002, '钘ょ紪灞呭鏀剁撼鎵樼洏', '椁愭涓庣巹鍏充袱鐢紝杞讳究鑰愮湅', 'https://example.com/product/tray_cover.png', '鎵嬪伐钘ょ紪缁撴瀯绱у瘑锛岄€傚悎姘存灉銆侀潰鍖呬笌鐜勫叧灏忕墿鏀剁撼銆?, '鎵嬪伐钘ょ紪', '钘ゆ潯銆佹缁?, '瀹夊悏', 4, 0, 30, 9, 0, 0, 96.00, 128.00, 1, 1, 85, '2026-04-04 13:00:00', '2026-04-12 18:15:00', 0);

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
    (3100000000000004106, 3100000000000004003, 'https://example.com/product/fan_detail_1.png', 'detail', 2, '2026-04-04 12:05:00', '2026-04-04 12:05:00', 0),
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
    (3100000000000004201, 3100000000000004001, '楂樼櫧娉?, '鏅痉闀?, '85%', 1, '2026-04-04 10:10:00', '2026-04-04 10:10:00', 0),
    (3100000000000004202, 3100000000000004001, '鏈ㄧ伆閲?, '鏅痉闀?, '15%', 2, '2026-04-04 10:10:00', '2026-04-04 10:10:00', 0),
    (3100000000000004203, 3100000000000004002, '榛戣儭妗冩湪', '鍖楃編杩涘彛', '95%', 1, '2026-04-04 11:10:00', '2026-04-04 11:10:00', 0),
    (3100000000000004204, 3100000000000004002, '榛勯摐鎵ｄ欢', '鍥藉唴浜旈噾', '5%', 2, '2026-04-04 11:10:00', '2026-04-04 11:10:00', 0),
    (3100000000000004205, 3100000000000004003, '鐪熶笣缁㈤潰', '鑻忓窞', '60%', 1, '2026-04-04 12:10:00', '2026-04-04 12:10:00', 0),
    (3100000000000004206, 3100000000000004003, '绔瑰埗鎵囬', '娴欐睙瀹夊悏', '40%', 2, '2026-04-04 12:10:00', '2026-04-04 12:10:00', 0),
    (3100000000000004207, 3100000000000004004, '钘ゆ潯', '瀹夊悏', '90%', 1, '2026-04-04 13:10:00', '2026-04-04 13:10:00', 0),
    (3100000000000004208, 3100000000000004004, '妫夌怀', '缁嶅叴', '10%', 2, '2026-04-04 13:10:00', '2026-04-04 13:10:00', 0);

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
    (3100000000000005001, 3100000000000004001, 'manual-mug-001', '鏆潚钃濆崟鏉', 'https://example.com/product/mug_sku_1.png', '330ml', '闄?, 0.45, 89.00, 109.00, 20, 2, 1, '2026-04-04 10:20:00', '2026-04-12 18:00:00', 0),
    (3100000000000005002, 3100000000000004001, 'manual-mug-002', '绀肩洅瑁呭弻鏉粍', 'https://example.com/product/mug_sku_2.png', '330ml*2', '闄?, 0.95, 129.00, 149.00, 26, 1, 1, '2026-04-04 10:25:00', '2026-04-12 18:00:00', 0),
    (3100000000000005003, 3100000000000004002, 'manual-box-001', '鍘熸湪鏍囧噯娆?, 'https://example.com/product/box_sku_1.png', '18cm x 12cm', '鏈ㄤ綔', 0.82, 239.00, 269.00, 8, 1, 1, '2026-04-04 11:20:00', '2026-04-12 18:05:00', 0),
    (3100000000000005004, 3100000000000004002, 'manual-box-002', '鍒诲瓧瀹氬埗娆?, 'https://example.com/product/box_sku_2.png', '18cm x 12cm锛屾敮鎸佺洅鐩栧埢瀛?, '鏈ㄤ綔', 0.85, 299.00, 329.00, 10, 1, 1, '2026-04-04 11:25:00', '2026-04-12 18:05:00', 0),
    (3100000000000005005, 3100000000000004003, 'manual-fan-001', '鏈堢櫧鑺卞奖娆?, 'https://example.com/product/fan_sku_1.png', '鍦嗘墖 21cm', '鐪熶笣', 0.16, 168.00, 188.00, 5, 0, 1, '2026-04-04 12:20:00', '2026-04-12 18:10:00', 0),
    (3100000000000005006, 3100000000000004003, 'manual-fan-002', '棰樺瓧瀹氬埗娆?, 'https://example.com/product/fan_sku_2.png', '鍦嗘墖 21cm锛屾敮鎸侀瀛?, '鐪熶笣', 0.18, 228.00, 248.00, 7, 1, 1, '2026-04-04 12:25:00', '2026-04-12 18:10:00', 0),
    (3100000000000005007, 3100000000000004004, 'manual-tray-001', '鍦嗗舰杞婚鎵樼洏', 'https://example.com/product/tray_sku_1.png', '鐩村緞 24cm', '钘ょ紪', 0.38, 96.00, 118.00, 16, 0, 1, '2026-04-04 13:20:00', '2026-04-12 18:15:00', 0),
    (3100000000000005008, 3100000000000004004, 'manual-tray-002', '妞渾鐜勫叧鎵樼洏', 'https://example.com/product/tray_sku_2.png', '30cm x 18cm', '钘ょ紪', 0.46, 128.00, 148.00, 14, 0, 1, '2026-04-04 13:25:00', '2026-04-12 18:15:00', 0);

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
    (3100000000000006001, 3100000000000001004, '鏋楃煡澶?, '13800000004', '涓婃捣甯?, '涓婃捣甯?, '寰愭眹鍖?, '婕曟邯鍖楄矾 88 鍙?8 骞?1602', '200030', '瀹?, 1, '2026-04-08 18:20:00', '2026-04-12 09:00:00', 0),
    (3100000000000006002, 3100000000000001004, '鏋楃煡澶?, '13800000004', '涓婃捣甯?, '涓婃捣甯?, '鏉ㄦ郸鍖?, '澶у璺?15 鍙?2 鍗曞厓 503', '200082', '鍏徃', 0, '2026-04-09 12:10:00', '2026-04-09 12:10:00', 0),
    (3100000000000006003, 3100000000000001005, '鍛ㄨ伩椋?, '13800000005', '娴欐睙鐪?, '鏉窞甯?, '瑗挎箹鍖?, '鏂囦笁璺?188 鍙?9 骞?902', '310012', '瀹?, 1, '2026-04-09 16:40:00', '2026-04-12 10:00:00', 0);

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
    (3100000000000008001, 'mo202604130001', 3100000000000001004, 3100000000000006001, 3, 1, 'wechat', 2, 129.00, 0.00, 12.00, 141.00, '鏉瓙瑕佸寘瑁呭ソ锛岄伩鍏嶇纰?, '鏋楃煡澶?, '13800000004', '涓婃捣甯?, '涓婃捣甯?, '寰愭眹鍖?, '婕曟邯鍖楄矾 88 鍙?8 骞?1602', '2026-04-10 10:06:00', '2026-04-10 17:30:00', '2026-04-12 14:20:00', '2026-04-12 20:00:00', null, '2026-04-10 10:00:00', '2026-04-12 20:00:00', 0),
    (3100000000000008002, 'mo202604130002', 3100000000000001005, 3100000000000006003, 2, 1, 'alipay', 1, 299.00, 0.00, 18.00, 317.00, '鐩掔洊鍒诲瓧锛氳禒浜堟槬鏃?, '鍛ㄨ伩椋?, '13800000005', '娴欐睙鐪?, '鏉窞甯?, '瑗挎箹鍖?, '鏂囦笁璺?188 鍙?9 骞?902', '2026-04-11 13:15:00', '2026-04-12 16:40:00', null, null, null, '2026-04-11 13:10:00', '2026-04-12 16:40:00', 0),
    (3100000000000008003, 'mo202604130003', 3100000000000001004, 3100000000000006001, 1, 1, 'wechat', 0, 228.00, 20.00, 12.00, 220.00, '棰樺瓧鍐呭锛氬鏃ユ湁椋?, '鏋楃煡澶?, '13800000004', '涓婃捣甯?, '涓婃捣甯?, '寰愭眹鍖?, '婕曟邯鍖楄矾 88 鍙?8 骞?1602', '2026-04-13 08:40:00', null, null, null, null, '2026-04-13 08:35:00', '2026-04-13 08:40:00', 0);

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
    (3100000000000009001, 3100000000000008001, 'mo202604130001', 3100000000000001004, 3100000000000004001, 3100000000000005002, 3100000000000002001, '鏌寸儳閲夐櫠椹厠鏉?, '绀肩洅瑁呭弻鏉粍', 'https://example.com/product/mug_cover.png', '330ml*2', 1, 129.00, 129.00, 0, 1, '2026-04-10 10:00:30', '2026-04-12 20:00:00', 0),
    (3100000000000009002, 3100000000000008002, 'mo202604130002', 3100000000000001005, 3100000000000004002, 3100000000000005004, 3100000000000002002, '榛戣儭妗冩Λ鍗楗扮洅', '鍒诲瓧瀹氬埗娆?, 'https://example.com/product/box_cover.png', '18cm x 12cm锛屾敮鎸佺洅鐩栧埢瀛?, 1, 299.00, 299.00, 0, 0, '2026-04-11 13:10:30', '2026-04-12 16:40:00', 0),
    (3100000000000009003, 3100000000000008003, 'mo202604130003', 3100000000000001004, 3100000000000004003, 3100000000000005006, 3100000000000002003, '鑻忕唬鑺卞奖鐪熶笣鍥㈡墖', '棰樺瓧瀹氬埗娆?, 'https://example.com/product/fan_cover.png', '鍦嗘墖 21cm锛屾敮鎸侀瀛?, 1, 228.00, 228.00, 0, 0, '2026-04-13 08:35:30', '2026-04-13 08:40:00', 0);

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
    (3100000000000010001, 3100000000000008002, 3100000000000009002, 3100000000000001005, 3100000000000004002, '棣栭グ鐩掑埢瀛楀畾鍒?, '甯屾湜鍦ㄧ洅鐩栨涓ぎ鍒讳笂鈥滆禒浜堟槬鏃モ€濓紝瀛椾綋鍋忔墜鍐欓锛屽寘瑁呭鍔犵背鐧借壊缂庡甫銆?, 'https://example.com/custom/box_reference_1.png', 1, '宸茬‘璁ゆ帓鐗堬紝鎸夐粯璁ら噾鑹叉弿杈瑰鐞嗐€?, '2026-04-11 18:30:00', '2026-04-11 14:00:00', '2026-04-11 18:30:00', 0),
    (3100000000000010002, 3100000000000008003, 3100000000000009003, 3100000000000001004, 3100000000000004003, '鍥㈡墖棰樺瓧瀹氬埗', '鎵囬潰鑳岄潰棰樺瓧鈥滃鏃ユ湁椋庘€濓紝甯屾湜棰樺瓧澧ㄨ壊鍋忕伆钃濓紝澶栧寘瑁呭姞娴佽嫃鎸傜怀銆?, 'https://example.com/custom/fan_reference_1.png', 0, '绛夊緟鍖犱汉纭棰樺瓧浣嶇疆銆?, null, '2026-04-13 08:45:00', '2026-04-13 08:45:00', 0);

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
    (3100000000000011001, 3100000000000008001, 'mo202604130001', '椤轰赴閫熻繍', 'SF202604100001', '闈掔闄朵綔', '13800000002', '鏋楃煡澶?, '13800000004', '鏄撶鍝佸凡鍔犲帤娉℃搏鍖呰', '2026-04-10 17:30:00', '2026-04-12 14:20:00', 2, '2026-04-10 17:35:00', '2026-04-12 14:20:00', 0),
    (3100000000000011002, 3100000000000008002, 'mo202604130002', '浜笢鐗╂祦', 'JD202604120001', '鏈ㄥ績閫犵墿', '13800000003', '鍛ㄨ伩椋?, '13800000005', '瀹氬埗鍒诲瓧娆撅紝鏈ㄧ洅鍥涜宸插仛闃叉挒澶勭悊', '2026-04-12 16:40:00', null, 1, '2026-04-12 16:45:00', '2026-04-12 16:45:00', 0);

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
    (3100000000000013001, 3100000000000008001, 3100000000000009001, 3100000000000001004, 3100000000000004001, 3100000000000005002, 5, '鏉瀷寰堥『鎵嬶紝閲夎壊姣斿浘鐗囨洿鏈夊眰娆★紝閫佹湅鍙嬩篃寰堜綋闈€?, 'https://example.com/review/mug_review_1.png,https://example.com/review/mug_review_2.png', 0, '鎰熻阿鍠滄锛屾垜浠悗缁細缁х画涓婃柊鍚岀郴鍒楄尪鍣ㄣ€?, '2026-04-12 21:10:00', 1, '2026-04-12 20:30:00', '2026-04-12 21:10:00', 0);

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
