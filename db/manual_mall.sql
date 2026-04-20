set names utf8mb4;
set foreign_key_checks = 0;

create database if not exists manual_mall default character set utf8mb4 collate utf8mb4_unicode_ci;
use manual_mall;

drop table if exists product_browse_log;
drop table if exists product_review;
drop table if exists product_favorite;
drop table if exists custom_requirement;
drop table if exists order_logistics;
drop table if exists order_item;
drop table if exists orders;
drop table if exists cart_item;
drop table if exists cart;
drop table if exists user_address;
drop table if exists product_sku;
drop table if exists product_material;
drop table if exists product_image;
drop table if exists product;
drop table if exists category;
drop table if exists artisan_profile;
drop table if exists users;

create table users (
    id bigint unsigned not null comment '鐢ㄦ埛id',
    userAccount varchar(64) not null comment '鐧诲綍璐﹀彿',
    userPassword varchar(255) not null comment '鐧诲綍瀵嗙爜',
    userName varchar(64) null comment '鐢ㄦ埛鏄电О',
    avatarUrl varchar(255) null comment '澶村儚鍦板潃',
    phone varchar(20) null comment '鎵嬫満鍙?,
    email varchar(128) null comment '閭',
    gender tinyint null default 0 comment '鎬у埆锛?-鏈煡 1-鐢?2-濂?,
    userRole varchar(32) not null default 'user' comment '瑙掕壊锛歶ser/admin',
    userStatus tinyint not null default 0 comment '鐘舵€侊細0-姝ｅ父 1-绂佺敤',
    balance decimal(10, 2) not null default 0.00 comment '璐︽埛浣欓',
    lastLoginTime datetime null comment '鏈€鍚庣櫥褰曟椂闂?,
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_users_userAccount (userAccount),
    unique key uk_users_phone (phone),
    unique key uk_users_email (email)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗煄鐢ㄦ埛琛?;

create table artisan_profile (
    id bigint unsigned not null comment 'artisan profile id',
    userId bigint unsigned not null comment 'user id',
    artisanName varchar(64) not null comment 'artisan name',
    shopName varchar(128) not null comment 'shop name',
    artisanAvatar varchar(255) null comment 'artisan avatar',
    coverUrl varchar(255) null comment 'shop cover',
    artisanStory varchar(1000) null comment 'artisan story',
    craftCategory varchar(64) null comment 'craft category',
    originPlace varchar(128) null comment 'origin place',
    experienceYears int unsigned not null default 0 comment 'experience years',
    supportCustom tinyint(1) not null default 0 comment 'support custom',
    contactPhone varchar(20) null comment 'contact phone',
    qualificationDesc varchar(1000) null comment 'qualification description',
    qualificationImages varchar(2000) null comment 'qualification images joined by comma',
    depositAmount decimal(10, 2) not null default 0.00 comment 'deposit amount',
    auditRemark varchar(1000) null comment 'audit remark',
    auditStatus tinyint not null default 0 comment 'audit status: 0 pending 1 approved 2 rejected',
    shelfStatus tinyint not null default 0 comment 'shelf status: 0 disabled 1 enabled',
    applyTime datetime null comment 'apply time',
    auditTime datetime null comment 'audit time',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_artisan_profile_userId (userId),
    key idx_artisan_profile_shopName (shopName),
    constraint fk_artisan_profile_userId foreign key (userId) references users (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = 'artisan profile';
create table category (
    id bigint unsigned not null comment '鍒嗙被id',
    parentId bigint unsigned null comment '鐖剁骇鍒嗙被id',
    categoryName varchar(64) not null comment '鍒嗙被鍚嶇О',
    categoryIcon varchar(255) null comment '鍒嗙被鍥炬爣',
    categoryDesc varchar(255) null comment '鍒嗙被鎻忚堪',
    categoryLevel tinyint not null default 1 comment '鍒嗙被灞傜骇',
    sortOrder int not null default 0 comment '鎺掑簭鍊?,
    isEnable tinyint(1) not null default 1 comment '鏄惁鍚敤',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    key idx_category_parentId (parentId),
    key idx_category_sortOrder (sortOrder),
    constraint fk_category_parentId foreign key (parentId) references category (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗搧鍒嗙被琛?;

create table product (
    id bigint unsigned not null comment '鍟嗗搧id',
    categoryId bigint unsigned not null comment '鍒嗙被id',
    artisanId bigint unsigned not null comment '鍖犱汉妗ｆid',
    productName varchar(128) not null comment '鍟嗗搧鍚嶇О',
    productSubtitle varchar(255) null comment '鍟嗗搧鍓爣棰?,
    productCover varchar(255) null comment '鍟嗗搧涓诲浘',
    productDesc text null comment '鍟嗗搧鎻忚堪',
    craftType varchar(64) null comment '宸ヨ壓绫诲瀷',
    materialDesc varchar(255) null comment '鏉愯川璇存槑',
    originPlace varchar(128) null comment '浜у湴',
    handmadeCycleDays int unsigned not null default 0 comment '鍒朵綔鍛ㄦ湡澶╂暟',
    supportCustom tinyint(1) not null default 0 comment '鏄惁鏀寔瀹氬埗',
    inventory int unsigned not null default 0 comment '鎬诲簱瀛?,
    soldQuantity int unsigned not null default 0 comment '閿€閲?,
    favoriteCount int unsigned not null default 0 comment '鏀惰棌鏁?,
    reviewCount int unsigned not null default 0 comment '璇勪环鏁?,
    minPrice decimal(10, 2) not null default 0.00 comment '鏈€浣庡敭浠?,
    maxPrice decimal(10, 2) not null default 0.00 comment '鏈€楂樺敭浠?,
    auditStatus tinyint not null default 0 comment '瀹℃牳鐘舵€侊細0-寰呭鏍?1-閫氳繃 2-椹冲洖',
    status tinyint not null default 0 comment '鍟嗗搧鐘舵€侊細0-鑽夌 1-涓婃灦 2-涓嬫灦',
    sortOrder int not null default 0 comment '鎺掑簭鍊?,
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    key idx_product_categoryId (categoryId),
    key idx_product_artisanId (artisanId),
    key idx_product_status (status),
    constraint fk_product_categoryId foreign key (categoryId) references category (id),
    constraint fk_product_artisanId foreign key (artisanId) references artisan_profile (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鎵嬪伐鍒跺搧鍟嗗搧琛?;

create table product_image (
    id bigint unsigned not null comment '鍟嗗搧鍥剧墖id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    imageUrl varchar(255) not null comment '鍥剧墖鍦板潃',
    imageType varchar(32) not null default 'detail' comment '鍥剧墖绫诲瀷锛歝over/detail',
    sortOrder int not null default 0 comment '鎺掑簭鍊?,
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    key idx_product_image_productId (productId),
    constraint fk_product_image_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗搧鍥剧墖琛?;

create table product_material (
    id bigint unsigned not null comment '鍟嗗搧鏉愯川鏄庣粏id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    materialName varchar(64) not null comment '鏉愯川鍚嶇О',
    materialOrigin varchar(128) null comment '鏉愯川鏉ユ簮',
    materialRatio varchar(64) null comment '鏉愯川鍗犳瘮璇存槑',
    sortOrder int not null default 0 comment '鎺掑簭鍊?,
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    key idx_product_material_productId (productId),
    constraint fk_product_material_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗搧鏉愯川鏄庣粏琛?;

create table product_sku (
    id bigint unsigned not null comment '鍟嗗搧sku id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    skuCode varchar(64) not null comment 'sku缂栫爜',
    skuName varchar(128) not null comment 'sku鍚嶇О',
    skuCover varchar(255) null comment 'sku鍥剧墖',
    specText varchar(255) null comment '瑙勬牸鎻忚堪',
    materialType varchar(64) null comment '鏉愯川绫诲瀷',
    weight decimal(10, 2) null default 0.00 comment '閲嶉噺',
    price decimal(10, 2) not null default 0.00 comment '閿€鍞环',
    originalPrice decimal(10, 2) not null default 0.00 comment '鍘熶环',
    stock int unsigned not null default 0 comment '搴撳瓨',
    lockedStock int unsigned not null default 0 comment '閿佸畾搴撳瓨',
    status tinyint not null default 1 comment '鐘舵€侊細0-鍋滅敤 1-鍚敤',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_product_sku_skuCode (skuCode),
    key idx_product_sku_productId (productId),
    constraint fk_product_sku_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗搧sku琛?;

create table user_address (
    id bigint unsigned not null comment '鍦板潃id',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    receiverName varchar(64) not null comment '鏀惰揣浜?,
    receiverPhone varchar(20) not null comment '鏀惰揣鎵嬫満鍙?,
    province varchar(64) not null comment '鐪佷唤',
    city varchar(64) not null comment '鍩庡競',
    district varchar(64) not null comment '鍖哄幙',
    detailAddress varchar(255) not null comment '璇︾粏鍦板潃',
    postalCode varchar(20) null comment '閭紪',
    tagName varchar(32) null comment '鍦板潃鏍囩',
    isDefault tinyint(1) not null default 0 comment '鏄惁榛樿鍦板潃',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    key idx_user_address_userId (userId),
    key idx_user_address_isDefault (userId, isDefault),
    constraint fk_user_address_userId foreign key (userId) references users (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鐢ㄦ埛鏀惰揣鍦板潃琛?;

create table cart (
    id bigint unsigned not null comment '璐墿杞d',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_cart_userId (userId),
    constraint fk_cart_userId foreign key (userId) references users (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '璐墿杞﹁〃';

create table cart_item (
    id bigint unsigned not null comment '璐墿杞︽槑缁唅d',
    cartId bigint unsigned not null comment '璐墿杞d',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    skuId bigint unsigned not null comment 'sku id',
    quantity int unsigned not null default 1 comment '璐拱鏁伴噺',
    price decimal(10, 2) not null default 0.00 comment '鍔犲叆璐墿杞︽椂浠锋牸',
    selected tinyint(1) not null default 1 comment '鏄惁閫変腑',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_cart_item_cartId_skuId (cartId, skuId),
    key idx_cart_item_userId (userId),
    key idx_cart_item_productId (productId),
    constraint fk_cart_item_cartId foreign key (cartId) references cart (id),
    constraint fk_cart_item_userId foreign key (userId) references users (id),
    constraint fk_cart_item_productId foreign key (productId) references product (id),
    constraint fk_cart_item_skuId foreign key (skuId) references product_sku (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '璐墿杞︽槑缁嗚〃';

create table orders (
    id bigint unsigned not null comment '璁㈠崟id',
    orderNo varchar(64) not null comment '璁㈠崟缂栧彿',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    addressId bigint unsigned null comment '鏀惰揣鍦板潃id',
    orderStatus tinyint not null default 0 comment '璁㈠崟鐘舵€侊細0-寰呬粯娆?1-寰呭彂璐?2-寰呮敹璐?3-宸插畬鎴?4-宸插彇娑?,
    payStatus tinyint not null default 0 comment '鏀粯鐘舵€侊細0-鏈敮浠?1-宸叉敮浠?2-宸查€€娆?,
    payType varchar(32) null comment '鏀粯鏂瑰紡',
    deliveryStatus tinyint not null default 0 comment '鍙戣揣鐘舵€侊細0-鏈彂璐?1-宸插彂璐?2-宸茬鏀?,
    productAmount decimal(10, 2) not null default 0.00 comment '鍟嗗搧鎬婚',
    discountAmount decimal(10, 2) not null default 0.00 comment '浼樻儬閲戦',
    freightAmount decimal(10, 2) not null default 0.00 comment '杩愯垂',
    payAmount decimal(10, 2) not null default 0.00 comment '瀹炰粯閲戦',
    buyerRemark varchar(255) null comment '涔板澶囨敞',
    receiverName varchar(64) not null comment '鏀惰揣浜?,
    receiverPhone varchar(20) not null comment '鏀惰揣鎵嬫満鍙?,
    province varchar(64) not null comment '鐪佷唤',
    city varchar(64) not null comment '鍩庡競',
    district varchar(64) not null comment '鍖哄幙',
    detailAddress varchar(255) not null comment '璇︾粏鍦板潃',
    payTime datetime null comment '鏀粯鏃堕棿',
    deliveryTime datetime null comment '鍙戣揣鏃堕棿',
    receiveTime datetime null comment '鏀惰揣鏃堕棿',
    finishTime datetime null comment '瀹屾垚鏃堕棿',
    cancelTime datetime null comment '鍙栨秷鏃堕棿',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_orders_orderNo (orderNo),
    key idx_orders_userId (userId),
    key idx_orders_orderStatus (orderStatus),
    constraint fk_orders_userId foreign key (userId) references users (id),
    constraint fk_orders_addressId foreign key (addressId) references user_address (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '璁㈠崟涓昏〃';

create table order_item (
    id bigint unsigned not null comment '璁㈠崟鏄庣粏id',
    orderId bigint unsigned not null comment '璁㈠崟id',
    orderNo varchar(64) not null comment '璁㈠崟缂栧彿',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    skuId bigint unsigned not null comment 'sku id',
    artisanId bigint unsigned not null comment '鍖犱汉妗ｆid',
    productName varchar(128) not null comment '鍟嗗搧鍚嶇О蹇収',
    skuName varchar(128) not null comment 'sku鍚嶇О蹇収',
    productCover varchar(255) null comment '鍟嗗搧涓诲浘蹇収',
    specText varchar(255) null comment '瑙勬牸鎻忚堪蹇収',
    quantity int unsigned not null default 1 comment '璐拱鏁伴噺',
    salePrice decimal(10, 2) not null default 0.00 comment '鎴愪氦鍗曚环',
    totalAmount decimal(10, 2) not null default 0.00 comment '鎴愪氦鎬婚',
    refundStatus tinyint not null default 0 comment '閫€娆剧姸鎬侊細0-鏃?1-鐢宠涓?2-宸查€€娆?,
    reviewStatus tinyint not null default 0 comment '璇勪环鐘舵€侊細0-鏈瘎 1-宸茶瘎',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    key idx_order_item_orderId (orderId),
    key idx_order_item_userId (userId),
    key idx_order_item_productId (productId),
    constraint fk_order_item_orderId foreign key (orderId) references orders (id),
    constraint fk_order_item_userId foreign key (userId) references users (id),
    constraint fk_order_item_productId foreign key (productId) references product (id),
    constraint fk_order_item_skuId foreign key (skuId) references product_sku (id),
    constraint fk_order_item_artisanId foreign key (artisanId) references artisan_profile (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '璁㈠崟鏄庣粏琛?;

create table custom_requirement (
    id bigint unsigned not null comment '瀹氬埗闇€姹俰d',
    orderId bigint unsigned not null comment '璁㈠崟id',
    orderItemId bigint unsigned not null comment '璁㈠崟鏄庣粏id',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    requirementTitle varchar(128) not null comment '闇€姹傛爣棰?,
    requirementContent varchar(1000) not null comment '闇€姹傚唴瀹?,
    referenceImages varchar(1000) null comment '鍙傝€冨浘鐗囷紝澶氫釜鍦板潃鐢ㄩ€楀彿鍒嗛殧',
    confirmStatus tinyint not null default 0 comment '纭鐘舵€侊細0-寰呯‘璁?1-宸茬‘璁?2-宸查┏鍥?,
    confirmRemark varchar(255) null comment '纭澶囨敞',
    confirmTime datetime null comment '纭鏃堕棿',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_custom_requirement_orderItemId (orderItemId),
    key idx_custom_requirement_userId (userId),
    key idx_custom_requirement_productId (productId),
    constraint fk_custom_requirement_orderId foreign key (orderId) references orders (id),
    constraint fk_custom_requirement_orderItemId foreign key (orderItemId) references order_item (id),
    constraint fk_custom_requirement_userId foreign key (userId) references users (id),
    constraint fk_custom_requirement_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗搧瀹氬埗闇€姹傝〃';

create table order_logistics (
    id bigint unsigned not null comment '鐗╂祦璁板綍id',
    orderId bigint unsigned not null comment '璁㈠崟id',
    orderNo varchar(64) not null comment '璁㈠崟缂栧彿',
    companyName varchar(64) null comment '鐗╂祦鍏徃',
    trackingNo varchar(64) null comment '鐗╂祦鍗曞彿',
    senderName varchar(64) null comment '瀵勪欢浜?,
    senderPhone varchar(20) null comment '瀵勪欢鐢佃瘽',
    receiverName varchar(64) null comment '鏀朵欢浜?,
    receiverPhone varchar(20) null comment '鏀朵欢鐢佃瘽',
    logisticsRemark varchar(255) null comment '鐗╂祦澶囨敞',
    shipTime datetime null comment '鍙戣揣鏃堕棿',
    signTime datetime null comment '绛炬敹鏃堕棿',
    status tinyint not null default 0 comment '鐘舵€侊細0-寰呭彂璐?1-杩愯緭涓?2-宸茬鏀?,
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    key idx_order_logistics_orderId (orderId),
    key idx_order_logistics_trackingNo (trackingNo),
    constraint fk_order_logistics_orderId foreign key (orderId) references orders (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '璁㈠崟鐗╂祦琛?;

create table product_favorite (
    id bigint unsigned not null comment '鏀惰棌璁板綍id',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_product_favorite_userId_productId (userId, productId),
    key idx_product_favorite_productId (productId),
    constraint fk_product_favorite_userId foreign key (userId) references users (id),
    constraint fk_product_favorite_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗搧鏀惰棌琛?;

create table product_review (
    id bigint unsigned not null comment '鍟嗗搧璇勪环id',
    orderId bigint unsigned not null comment '璁㈠崟id',
    orderItemId bigint unsigned not null comment '璁㈠崟鏄庣粏id',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    skuId bigint unsigned not null comment 'sku id',
    score tinyint not null default 5 comment '璇勫垎',
    reviewContent varchar(1000) null comment '璇勪环鍐呭',
    reviewImages varchar(1000) null comment '璇勪环鍥剧墖锛屽涓湴鍧€鐢ㄩ€楀彿鍒嗛殧',
    isAnonymous tinyint(1) not null default 0 comment '鏄惁鍖垮悕',
    replyContent varchar(1000) null comment '鍟嗗鍥炲',
    replyTime datetime null comment '鍥炲鏃堕棿',
    status tinyint not null default 0 comment '鐘舵€侊細0-寰呭鏍?1-灞曠ず 2-闅愯棌',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    unique key uk_product_review_orderItemId (orderItemId),
    key idx_product_review_userId (userId),
    key idx_product_review_productId (productId),
    constraint fk_product_review_orderId foreign key (orderId) references orders (id),
    constraint fk_product_review_orderItemId foreign key (orderItemId) references order_item (id),
    constraint fk_product_review_userId foreign key (userId) references users (id),
    constraint fk_product_review_productId foreign key (productId) references product (id),
    constraint fk_product_review_skuId foreign key (skuId) references product_sku (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗搧璇勪环琛?;

create table product_browse_log (
    id bigint unsigned not null comment '娴忚璁板綍id',
    userId bigint unsigned not null comment '鐢ㄦ埛id',
    productId bigint unsigned not null comment '鍟嗗搧id',
    sourcePage varchar(32) null comment '鏉ユ簮椤甸潰',
    staySeconds int unsigned not null default 0 comment '鍋滅暀绉掓暟',
    deviceType varchar(32) null comment '璁惧绫诲瀷',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id),
    key idx_product_browse_log_userId (userId),
    key idx_product_browse_log_productId (productId),
    constraint fk_product_browse_log_userId foreign key (userId) references users (id),
    constraint fk_product_browse_log_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '鍟嗗搧娴忚鏃ュ織琛?;

set foreign_key_checks = 1;
