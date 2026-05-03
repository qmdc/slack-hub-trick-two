CREATE TABLE IF NOT EXISTS `media_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(255) NOT NULL COMMENT '标题（电影/书籍名称）',
    `type` TINYINT NOT NULL COMMENT '类型（1-电影，2-书籍）',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-想看，1-已看）',
    `rating` TINYINT NULL COMMENT '评分（1-5星）',
    `review` TEXT NULL COMMENT '个人评论',
    `tags` VARCHAR(500) NULL COMMENT '标签，逗号分隔',
    `cover_url` VARCHAR(500) NULL COMMENT '封面图片URL',
    `author` VARCHAR(100) NULL COMMENT '作者/导演',
    `year` VARCHAR(20) NULL COMMENT '出版/上映年份',
    `create_time` BIGINT NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` BIGINT NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` TINYINT DEFAULT 0 NOT NULL COMMENT '逻辑删除（0-未删，1-已删）',
    `version` BIGINT DEFAULT 1 NOT NULL COMMENT '版本号（用于乐观锁）',
    INDEX idx_user_id (`user_id`),
    INDEX idx_type (`type`),
    INDEX idx_status (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='影视书籍推荐清单';

CREATE TABLE IF NOT EXISTS `share_link` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `share_code` VARCHAR(32) UNIQUE NOT NULL COMMENT '分享码',
    `title` VARCHAR(255) NULL COMMENT '分享标题',
    `expire_time` BIGINT NULL COMMENT '过期时间（毫秒时间戳，NULL表示永久有效）',
    `create_time` BIGINT NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` BIGINT NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` TINYINT DEFAULT 0 NOT NULL COMMENT '逻辑删除（0-未删，1-已删）',
    `version` BIGINT DEFAULT 1 NOT NULL COMMENT '版本号（用于乐观锁）',
    INDEX idx_share_code (`share_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分享链接表';

-- 插入示例数据（假设用户ID为1）
INSERT INTO `media_item` (`user_id`, `title`, `type`, `status`, `rating`, `review`, `tags`, `cover_url`, `author`, `year`, `create_time`, `update_time`) VALUES
(1, '肖申克的救赎', 1, 1, 5, '经典中的经典，希望与自由的完美诠释', '剧情,犯罪,经典', 'https://example.com/shawshank.jpg', '弗兰克·德拉邦特', '1994', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '盗梦空间', 1, 1, 5, '诺兰的烧脑神作，梦境与现实交织', '科幻,悬疑,动作', 'https://example.com/inception.jpg', '克里斯托弗·诺兰', '2010', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '星际穿越', 1, 0, NULL, '一直想看但还没看的科幻巨作', '科幻,冒险,太空', 'https://example.com/interstellar.jpg', '克里斯托弗·诺兰', '2014', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '三体', 2, 1, 5, '中国科幻的巅峰之作，想象力惊人', '科幻,小说,刘慈欣', 'https://example.com/threebody.jpg', '刘慈欣', '2008', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '活着', 2, 1, 4, '余华的代表作，让人思考生命的意义', '文学,小说,人生', 'https://example.com/toLive.jpg', '余华', '1993', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '百年孤独', 2, 0, NULL, '马尔克斯的魔幻现实主义巨著，待阅读', '文学,魔幻现实主义,经典', 'https://example.com/hundredYears.jpg', '加西亚·马尔克斯', '1967', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '流浪地球', 1, 1, 4, '中国科幻电影的里程碑', '科幻,灾难,国产', 'https://example.com/wandering.jpg', '郭帆', '2019', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '人类简史', 2, 1, 5, '从全新视角审视人类历史，强烈推荐', '历史,科普,社会学', 'https://example.com/sapiens.jpg', '尤瓦尔·赫拉利', '2011', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '阿甘正传', 1, 1, 5, '生活就像一盒巧克力', '剧情,爱情,励志', 'https://example.com/forrest.jpg', '罗伯特·泽米吉斯', '1994', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
(1, '解忧杂货店', 2, 0, NULL, '东野圭吾的温暖治愈系作品', '小说,治愈,日本', 'https://example.com/namiya.jpg', '东野圭吾', '2012', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);