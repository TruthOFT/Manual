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
                       id bigint unsigned not null comment '用户id',
                       userAccount varchar(64) not null comment '登录账号',
                       userPassword varchar(255) not null comment '登录密码',
                       userName varchar(64) null comment '用户昵称',
                       avatarUrl varchar(255) null comment '头像地址',
                       phone varchar(20) null comment '手机号',
                       email varchar(128) null comment '邮箱',
                       gender tinyint null default 0 comment '性别：0-未知 1-男 2-女',
                       userRole varchar(32) not null default 'user' comment '角色：user/admin',
                       userStatus tinyint not null default 0 comment '状态：0-正常 1-禁用',
                       balance decimal(10, 2) not null default 0.00 comment '账户余额',
                       lastLoginTime datetime null comment '最后登录时间',
                       createTime datetime null default current_timestamp,
                       updateTime datetime null default current_timestamp on update current_timestamp,
                       isDelete tinyint(1) null default 0,
                       primary key (id),
                       unique key uk_users_userAccount (userAccount),
                       unique key uk_users_phone (phone),
                       unique key uk_users_email (email)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商城用户表';

create table artisan_profile (
                                 id bigint unsigned not null comment '匠人信息id',
                                 userId bigint unsigned not null comment '用户id',
                                 artisanName varchar(64) not null comment '匠人名称',
                                 shopName varchar(128) not null comment '店铺名称',
                                 artisanAvatar varchar(255) null comment '匠人头像',
                                 coverUrl varchar(255) null comment '店铺封面',
                                 artisanStory varchar(1000) null comment '匠人故事',
                                 craftCategory varchar(64) null comment '工艺分类',
                                 originPlace varchar(128) null comment '产地',
                                 experienceYears int unsigned not null default 0 comment '从业年限',
                                 supportCustom tinyint(1) not null default 0 comment '是否支持定制',
                                 contactPhone varchar(20) null comment '联系电话',
                                 qualificationDesc varchar(1000) null comment '资质说明',
                                 qualificationImages varchar(2000) null comment '资质图片，多个用逗号分隔',
                                 depositAmount decimal(10, 2) not null default 0.00 comment '保证金金额',
                                 auditRemark varchar(1000) null comment '审核备注',
                                 auditStatus tinyint not null default 0 comment '审核状态：0-待审核 1-审核通过 2-审核拒绝',
                                 shelfStatus tinyint not null default 0 comment '上架状态：0-禁用 1-启用',
                                 applyTime datetime null comment '申请时间',
                                 auditTime datetime null comment '审核时间',
                                 createTime datetime null default current_timestamp,
                                 updateTime datetime null default current_timestamp on update current_timestamp,
                                 isDelete tinyint(1) null default 0,
                                 primary key (id),
                                 unique key uk_artisan_profile_userId (userId),
                                 key idx_artisan_profile_shopName (shopName),
                                 constraint fk_artisan_profile_userId foreign key (userId) references users (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '匠人信息表';

create table category (
                          id bigint unsigned not null comment '分类id',
                          parentId bigint unsigned null comment '父分类id',
                          categoryName varchar(64) not null comment '分类名称',
                          categoryIcon varchar(255) null comment '分类图标',
                          categoryDesc varchar(255) null comment '分类描述',
                          categoryLevel tinyint not null default 1 comment '分类层级',
                          sortOrder int not null default 0 comment '排序值',
                          isEnable tinyint(1) not null default 1 comment '是否启用',
                          createTime datetime null default current_timestamp,
                          updateTime datetime null default current_timestamp on update current_timestamp,
                          isDelete tinyint(1) null default 0,
                          primary key (id),
                          key idx_category_parentId (parentId),
                          key idx_category_sortOrder (sortOrder),
                          constraint fk_category_parentId foreign key (parentId) references category (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品分类表';

create table product (
                         id bigint unsigned not null comment '商品id',
                         categoryId bigint unsigned not null comment '分类id',
                         artisanId bigint unsigned not null comment '匠人信息id',
                         productName varchar(128) not null comment '商品名称',
                         productSubtitle varchar(255) null comment '商品副标题',
                         productCover varchar(255) null comment '商品主图',
                         productDesc text null comment '商品描述',
                         craftType varchar(64) null comment '工艺类型',
                         materialDesc varchar(255) null comment '材质说明',
                         originPlace varchar(128) null comment '产地',
                         handmadeCycleDays int unsigned not null default 0 comment '制作周期天数',
                         supportCustom tinyint(1) not null default 0 comment '是否支持定制',
                         inventory int unsigned not null default 0 comment '总库存',
                         soldQuantity int unsigned not null default 0 comment '销量',
                         favoriteCount int unsigned not null default 0 comment '收藏数',
                         reviewCount int unsigned not null default 0 comment '评价数',
                         minPrice decimal(10, 2) not null default 0.00 comment '最低售价',
                         maxPrice decimal(10, 2) not null default 0.00 comment '最高售价',
                         auditStatus tinyint not null default 0 comment '审核状态：0-待审核 1-审核通过 2-审核拒绝',
                         status tinyint not null default 0 comment '商品状态：0-草稿 1-上架 2-下架',
                         sortOrder int not null default 0 comment '排序值',
                         createTime datetime null default current_timestamp,
                         updateTime datetime null default current_timestamp on update current_timestamp,
                         isDelete tinyint(1) null default 0,
                         primary key (id),
                         key idx_product_categoryId (categoryId),
                         key idx_product_artisanId (artisanId),
                         key idx_product_status (status),
                         constraint fk_product_categoryId foreign key (categoryId) references category (id),
                         constraint fk_product_artisanId foreign key (artisanId) references artisan_profile (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '手工制品商品表';

create table product_image (
                               id bigint unsigned not null comment '商品图片id',
                               productId bigint unsigned not null comment '商品id',
                               imageUrl varchar(255) not null comment '图片地址',
                               imageType varchar(32) not null default 'detail' comment '图片类型：cover-封面 detail-详情',
                               sortOrder int not null default 0 comment '排序值',
                               createTime datetime null default current_timestamp,
                               updateTime datetime null default current_timestamp on update current_timestamp,
                               isDelete tinyint(1) null default 0,
                               primary key (id),
                               key idx_product_image_productId (productId),
                               constraint fk_product_image_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品图片表';

create table product_material (
                                  id bigint unsigned not null comment '商品材质明细id',
                                  productId bigint unsigned not null comment '商品id',
                                  materialName varchar(64) not null comment '材质名称',
                                  materialOrigin varchar(128) null comment '材质来源',
                                  materialRatio varchar(64) null comment '材质占比说明',
                                  sortOrder int not null default 0 comment '排序值',
                                  createTime datetime null default current_timestamp,
                                  updateTime datetime null default current_timestamp on update current_timestamp,
                                  isDelete tinyint(1) null default 0,
                                  primary key (id),
                                  key idx_product_material_productId (productId),
                                  constraint fk_product_material_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品材质明细表';

create table product_sku (
                             id bigint unsigned not null comment '商品sku id',
                             productId bigint unsigned not null comment '商品id',
                             skuCode varchar(64) not null comment 'sku编码',
                             skuName varchar(128) not null comment 'sku名称',
                             skuCover varchar(255) null comment 'sku图片',
                             specText varchar(255) null comment '规格描述',
                             materialType varchar(64) null comment '材质类型',
                             weight decimal(10, 2) null default 0.00 comment '重量',
                             price decimal(10, 2) not null default 0.00 comment '销售价格',
                             originalPrice decimal(10, 2) not null default 0.00 comment '原价',
                             stock int unsigned not null default 0 comment '库存',
                             lockedStock int unsigned not null default 0 comment '锁定库存',
                             status tinyint not null default 1 comment '状态：0-停用 1-启用',
                             createTime datetime null default current_timestamp,
                             updateTime datetime null default current_timestamp on update current_timestamp,
                             isDelete tinyint(1) null default 0,
                             primary key (id),
                             unique key uk_product_sku_skuCode (skuCode),
                             key idx_product_sku_productId (productId),
                             constraint fk_product_sku_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品sku表';

create table user_address (
                              id bigint unsigned not null comment '地址id',
                              userId bigint unsigned not null comment '用户id',
                              receiverName varchar(64) not null comment '收件人',
                              receiverPhone varchar(20) not null comment '收件人手机号',
                              province varchar(64) not null comment '省份',
                              city varchar(64) not null comment '城市',
                              district varchar(64) not null comment '区县',
                              detailAddress varchar(255) not null comment '详细地址',
                              postalCode varchar(20) null comment '邮编',
                              tagName varchar(32) null comment '地址标签',
                              isDefault tinyint(1) not null default 0 comment '是否默认地址',
                              createTime datetime null default current_timestamp,
                              updateTime datetime null default current_timestamp on update current_timestamp,
                              isDelete tinyint(1) null default 0,
                              primary key (id),
                              key idx_user_address_userId (userId),
                              key idx_user_address_isDefault (userId, isDefault),
                              constraint fk_user_address_userId foreign key (userId) references users (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '用户收货地址表';

create table cart (
                      id bigint unsigned not null comment '购物车id',
                      userId bigint unsigned not null comment '用户id',
                      createTime datetime null default current_timestamp,
                      updateTime datetime null default current_timestamp on update current_timestamp,
                      isDelete tinyint(1) null default 0,
                      primary key (id),
                      unique key uk_cart_userId (userId),
                      constraint fk_cart_userId foreign key (userId) references users (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '购物车表';

create table cart_item (
                           id bigint unsigned not null comment '购物车明细id',
                           cartId bigint unsigned not null comment '购物车id',
                           userId bigint unsigned not null comment '用户id',
                           productId bigint unsigned not null comment '商品id',
                           skuId bigint unsigned not null comment 'sku id',
                           quantity int unsigned not null default 1 comment '购买数量',
                           price decimal(10, 2) not null default 0.00 comment '加入购物车时价格',
                           selected tinyint(1) not null default 1 comment '是否选中',
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
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '购物车明细表';

create table orders (
                        id bigint unsigned not null comment '订单id',
                        orderNo varchar(64) not null comment '订单编号',
                        userId bigint unsigned not null comment '用户id',
                        addressId bigint unsigned null comment '收货地址id',
                        orderStatus tinyint not null default 0 comment '订单状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消',
                        payStatus tinyint not null default 0 comment '支付状态：0-未支付 1-已支付 2-已退款',
                        payType varchar(32) null comment '支付方式',
                        deliveryStatus tinyint not null default 0 comment '发货状态：0-未发货 1-已发货 2-已签收',
                        productAmount decimal(10, 2) not null default 0.00 comment '商品总价',
                        discountAmount decimal(10, 2) not null default 0.00 comment '优惠金额',
                        freightAmount decimal(10, 2) not null default 0.00 comment '运费',
                        payAmount decimal(10, 2) not null default 0.00 comment '实付金额',
                        buyerRemark varchar(255) null comment '买家备注',
                        receiverName varchar(64) not null comment '收件人',
                        receiverPhone varchar(20) not null comment '收件人手机号',
                        province varchar(64) not null comment '省份',
                        city varchar(64) not null comment '城市',
                        district varchar(64) not null comment '区县',
                        detailAddress varchar(255) not null comment '详细地址',
                        payTime datetime null comment '支付时间',
                        deliveryTime datetime null comment '发货时间',
                        receiveTime datetime null comment '收货时间',
                        finishTime datetime null comment '完成时间',
                        cancelTime datetime null comment '取消时间',
                        createTime datetime null default current_timestamp,
                        updateTime datetime null default current_timestamp on update current_timestamp,
                        isDelete tinyint(1) null default 0,
                        primary key (id),
                        unique key uk_orders_orderNo (orderNo),
                        key idx_orders_userId (userId),
                        key idx_orders_orderStatus (orderStatus),
                        constraint fk_orders_userId foreign key (userId) references users (id),
                        constraint fk_orders_addressId foreign key (addressId) references user_address (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '订单主表';

create table order_item (
                            id bigint unsigned not null comment '订单明细id',
                            orderId bigint unsigned not null comment '订单id',
                            orderNo varchar(64) not null comment '订单编号',
                            userId bigint unsigned not null comment '用户id',
                            productId bigint unsigned not null comment '商品id',
                            skuId bigint unsigned not null comment 'sku id',
                            artisanId bigint unsigned not null comment '匠人信息id',
                            productName varchar(128) not null comment '商品名称快照',
                            skuName varchar(128) not null comment 'sku名称快照',
                            productCover varchar(255) null comment '商品主图快照',
                            specText varchar(255) null comment '规格描述快照',
                            quantity int unsigned not null default 1 comment '购买数量',
                            salePrice decimal(10, 2) not null default 0.00 comment '成交单价',
                            totalAmount decimal(10, 2) not null default 0.00 comment '成交总价',
                            refundStatus tinyint not null default 0 comment '退款状态：0-无 1-申请中 2-已退款',
                            reviewStatus tinyint not null default 0 comment '评价状态：0-未评 1-已评',
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
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '订单明细表';

create table custom_requirement (
                                    id bigint unsigned not null comment '定制需求id',
                                    orderId bigint unsigned not null comment '订单id',
                                    orderItemId bigint unsigned not null comment '订单明细id',
                                    userId bigint unsigned not null comment '用户id',
                                    productId bigint unsigned not null comment '商品id',
                                    requirementTitle varchar(128) not null comment '需求标题',
                                    requirementContent varchar(1000) not null comment '需求内容',
                                    referenceImages varchar(1000) null comment '参考图片，多个地址用逗号分隔',
                                    confirmStatus tinyint not null default 0 comment '确认状态：0-待确认 1-已确认 2-已驳回',
                                    confirmRemark varchar(255) null comment '确认备注',
                                    confirmTime datetime null comment '确认时间',
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
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品定制需求表';

create table order_logistics (
                                 id bigint unsigned not null comment '物流记录id',
                                 orderId bigint unsigned not null comment '订单id',
                                 orderNo varchar(64) not null comment '订单编号',
                                 companyName varchar(64) null comment '物流公司',
                                 trackingNo varchar(64) null comment '物流单号',
                                 senderName varchar(64) null comment '发货人',
                                 senderPhone varchar(20) null comment '发货人电话',
                                 receiverName varchar(64) null comment '收货人',
                                 receiverPhone varchar(20) null comment '收货人电话',
                                 logisticsRemark varchar(255) null comment '物流备注',
                                 shipTime datetime null comment '发货时间',
                                 signTime datetime null comment '签收时间',
                                 status tinyint not null default 0 comment '状态：0-待发货 1-运输中 2-已签收',
                                 createTime datetime null default current_timestamp,
                                 updateTime datetime null default current_timestamp on update current_timestamp,
                                 isDelete tinyint(1) null default 0,
                                 primary key (id),
                                 key idx_order_logistics_orderId (orderId),
                                 key idx_order_logistics_trackingNo (trackingNo),
                                 constraint fk_order_logistics_orderId foreign key (orderId) references orders (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '订单物流表';

create table product_favorite (
                                  id bigint unsigned not null comment '收藏记录id',
                                  userId bigint unsigned not null comment '用户id',
                                  productId bigint unsigned not null comment '商品id',
                                  createTime datetime null default current_timestamp,
                                  updateTime datetime null default current_timestamp on update current_timestamp,
                                  isDelete tinyint(1) null default 0,
                                  primary key (id),
                                  unique key uk_product_favorite_userId_productId (userId, productId),
                                  key idx_product_favorite_productId (productId),
                                  constraint fk_product_favorite_userId foreign key (userId) references users (id),
                                  constraint fk_product_favorite_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品收藏表';

create table product_review (
                                id bigint unsigned not null comment '商品评价id',
                                orderId bigint unsigned not null comment '订单id',
                                orderItemId bigint unsigned not null comment '订单明细id',
                                userId bigint unsigned not null comment '用户id',
                                productId bigint unsigned not null comment '商品id',
                                skuId bigint unsigned not null comment 'sku id',
                                score tinyint not null default 5 comment '评分',
                                reviewContent varchar(1000) null comment '评价内容',
                                reviewImages varchar(1000) null comment '评价图片，多个地址用逗号分隔',
                                isAnonymous tinyint(1) not null default 0 comment '是否匿名',
                                replyContent varchar(1000) null comment '商家回复',
                                replyTime datetime null comment '回复时间',
                                status tinyint not null default 0 comment '状态：0-待审核 1-显示 2-隐藏',
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
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品评价表';

create table product_browse_log (
                                    id bigint unsigned not null comment '浏览记录id',
                                    userId bigint unsigned not null comment '用户id',
                                    productId bigint unsigned not null comment '商品id',
                                    sourcePage varchar(32) null comment '来源页面',
                                    staySeconds int unsigned not null default 0 comment '停留秒数',
                                    deviceType varchar(32) null comment '设备类型',
                                    createTime datetime null default current_timestamp,
                                    updateTime datetime null default current_timestamp on update current_timestamp,
                                    isDelete tinyint(1) null default 0,
                                    primary key (id),
                                    key idx_product_browse_log_userId (userId),
                                    key idx_product_browse_log_productId (productId),
                                    constraint fk_product_browse_log_userId foreign key (userId) references users (id),
                                    constraint fk_product_browse_log_productId foreign key (productId) references product (id)
) engine = innodb default charset = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品浏览日志表';

set foreign_key_checks = 1;