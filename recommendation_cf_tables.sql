set names utf8mb4;
set foreign_key_checks = 0;

drop table if exists user_product_recommend;
drop table if exists product_similarity;
drop table if exists user_product_behavior;

create table user_product_behavior (
    id bigint unsigned not null comment '用户商品行为id',
    userId bigint unsigned not null comment '用户id',
    productId bigint unsigned not null comment '商品id',
    skuId bigint unsigned null comment 'sku id',
    behaviorType tinyint not null comment '行为类型：1浏览 2收藏 3加购 4下单 5评价',
    behaviorWeight decimal(10, 4) not null default 1.0000 comment '行为权重',
    behaviorTime datetime not null default current_timestamp comment '行为发生时间',
    sourceType tinyint not null default 0 comment '来源类型：0手动 1浏览 2收藏 3购物车 4订单 5评价',
    sourceId bigint unsigned null comment '来源记录id',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id) using btree,
    unique index uk_user_product_behavior_source(sourceType asc, sourceId asc, behaviorType asc, userId asc, productId asc, skuId asc) using btree,
    index idx_user_product_behavior_userId(userId asc) using btree,
    index idx_user_product_behavior_productId(productId asc) using btree,
    index idx_user_product_behavior_skuId(skuId asc) using btree,
    index idx_user_product_behavior_time(behaviorTime asc) using btree,
    constraint fk_user_product_behavior_userId foreign key (userId) references users (id) on delete restrict on update restrict,
    constraint fk_user_product_behavior_productId foreign key (productId) references product (id) on delete restrict on update restrict,
    constraint fk_user_product_behavior_skuId foreign key (skuId) references product_sku (id) on delete restrict on update restrict
) engine = innodb character set = utf8mb4 collate = utf8mb4_unicode_ci comment = '用户商品行为表' row_format = dynamic;

create table product_similarity (
    id bigint unsigned not null comment '商品相似度id',
    productId bigint unsigned not null comment '商品id',
    similarProductId bigint unsigned not null comment '相似商品id',
    similarityScore decimal(10, 6) not null default 0.000000 comment '相似度分数',
    coBehaviorCount int unsigned not null default 0 comment '共同交互次数',
    algorithmVersion varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci not null default 'item_cf_v1' comment '算法版本',
    calculatedTime datetime not null default current_timestamp comment '计算时间',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id) using btree,
    unique index uk_product_similarity_pair(productId asc, similarProductId asc, algorithmVersion asc) using btree,
    index idx_product_similarity_productId(productId asc) using btree,
    index idx_product_similarity_similarProductId(similarProductId asc) using btree,
    index idx_product_similarity_score(similarityScore desc) using btree,
    constraint fk_product_similarity_productId foreign key (productId) references product (id) on delete restrict on update restrict,
    constraint fk_product_similarity_similarProductId foreign key (similarProductId) references product (id) on delete restrict on update restrict,
    constraint chk_product_similarity_not_self check (productId <> similarProductId)
) engine = innodb character set = utf8mb4 collate = utf8mb4_unicode_ci comment = '商品相似度表' row_format = dynamic;

create table user_product_recommend (
    id bigint unsigned not null comment '用户商品推荐id',
    userId bigint unsigned not null comment '用户id',
    productId bigint unsigned not null comment '商品id',
    recommendScore decimal(10, 4) not null default 0.0000 comment '推荐分数',
    recommendReason varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null default null comment '推荐原因',
    rankNo int unsigned not null default 0 comment '推荐排序',
    algorithmVersion varchar(64) character set utf8mb4 collate utf8mb4_unicode_ci not null default 'item_cf_v1' comment '算法版本',
    expireTime datetime null default null comment '推荐过期时间',
    createTime datetime null default current_timestamp,
    updateTime datetime null default current_timestamp on update current_timestamp,
    isDelete tinyint(1) null default 0,
    primary key (id) using btree,
    unique index uk_user_product_recommend_user_product(userId asc, productId asc, algorithmVersion asc) using btree,
    index idx_user_product_recommend_user_rank(userId asc, rankNo asc) using btree,
    index idx_user_product_recommend_productId(productId asc) using btree,
    index idx_user_product_recommend_score(recommendScore desc) using btree,
    index idx_user_product_recommend_expireTime(expireTime asc) using btree,
    constraint fk_user_product_recommend_userId foreign key (userId) references users (id) on delete restrict on update restrict,
    constraint fk_user_product_recommend_productId foreign key (productId) references product (id) on delete restrict on update restrict
) engine = innodb character set = utf8mb4 collate = utf8mb4_unicode_ci comment = '用户商品推荐结果表' row_format = dynamic;

set foreign_key_checks = 1;
