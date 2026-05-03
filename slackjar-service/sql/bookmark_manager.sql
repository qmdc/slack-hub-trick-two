CREATE TABLE IF NOT EXISTS `bookmark` (
    `id` bigint auto_increment primary key comment '主键ID',
    `user_id` bigint not null comment '用户ID',
    `url` varchar(2048) not null comment '网页链接',
    `title` varchar(512) null comment '网站标题',
    `favicon_url` varchar(1024) null comment '网站图标URL',
    `description` varchar(2000) null comment '描述',
    `tags` varchar(1024) null comment '标签（逗号分隔）',
    `category_id` bigint null comment '分类ID',
    `create_time` bigint null comment '创建时间（毫秒时间戳）',
    `update_time` bigint null comment '更新时间（毫秒时间戳）',
    `deleted` tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    `version` bigint default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书签表';

CREATE TABLE IF NOT EXISTS `bookmark_category` (
    `id` bigint auto_increment primary key comment '主键ID',
    `user_id` bigint not null comment '用户ID',
    `name` varchar(128) not null comment '分类名称',
    `icon` varchar(128) null comment '分类图标',
    `sort_order` int default 0 comment '排序顺序',
    `create_time` bigint null comment '创建时间（毫秒时间戳）',
    `update_time` bigint null comment '更新时间（毫秒时间戳）',
    `deleted` tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    `version` bigint default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书签分类表';

CREATE TABLE IF NOT EXISTS `bookmark_tag` (
    `id` bigint auto_increment primary key comment '主键ID',
    `user_id` bigint not null comment '用户ID',
    `name` varchar(128) not null comment '标签名称',
    `color` varchar(32) default '#1890ff' comment '标签颜色',
    `create_time` bigint null comment '创建时间（毫秒时间戳）',
    `update_time` bigint null comment '更新时间（毫秒时间戳）',
    `deleted` tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    `version` bigint default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (`user_id`),
    unique key uk_user_tag (`user_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书签标签表';