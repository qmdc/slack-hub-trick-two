CREATE TABLE IF NOT EXISTS `emotion_chat_session` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `session_name` VARCHAR(100) DEFAULT NULL COMMENT '会话名称',
    `last_message` VARCHAR(500) DEFAULT NULL COMMENT '最后一条消息',
    `last_emotion` VARCHAR(50) DEFAULT NULL COMMENT '最后识别的情绪',
    `unread_count` INT DEFAULT 0 COMMENT '未读消息数',
    `create_time` BIGINT NOT NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` BIGINT NOT NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删，1-已删）',
    `version` BIGINT DEFAULT 1 COMMENT '版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='情绪对话会话表';

CREATE TABLE IF NOT EXISTS `emotion_chat_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `session_id` BIGINT NOT NULL COMMENT '会话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `message` TEXT NOT NULL COMMENT '消息内容',
    `is_user` TINYINT NOT NULL COMMENT '是否用户消息（1-用户，0-系统）',
    `emotion` VARCHAR(50) DEFAULT NULL COMMENT '识别的情绪（如：happy, sad, angry, anxious）',
    `emotion_score` DECIMAL(5,2) DEFAULT NULL COMMENT '情绪置信度',
    `response_strategy` VARCHAR(100) DEFAULT NULL COMMENT '响应策略',
    `create_time` BIGINT NOT NULL COMMENT '创建时间（毫秒时间戳）',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删，1-已删）',
    `version` BIGINT DEFAULT 1 COMMENT '版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='情绪对话记录表';

CREATE TABLE IF NOT EXISTS `comfort_message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '话术ID',
    `emotion_type` VARCHAR(50) NOT NULL COMMENT '情绪类型（如：happy, sad, angry, anxious, tired）',
    `message_type` VARCHAR(50) DEFAULT 'default' COMMENT '话术类型（default-默认，encourage-鼓励，humor-幽默，empathy-共情）',
    `content` TEXT NOT NULL COMMENT '话术内容',
    `priority` INT DEFAULT 1 COMMENT '优先级（数字越小优先级越高）',
    `create_time` BIGINT NOT NULL COMMENT '创建时间（毫秒时间戳）',
    `update_time` BIGINT NOT NULL COMMENT '更新时间（毫秒时间戳）',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0-未删，1-已删）',
    `version` BIGINT DEFAULT 1 COMMENT '版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='安慰话术表';

INSERT INTO `comfort_message` (`emotion_type`, `message_type`, `content`, `priority`, `create_time`, `update_time`) VALUES
('happy', 'default', '听到你心情好真开心！继续保持这份好心情吧～', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('happy', 'encourage', '太棒了！你的积极心态会带来更多美好的事情～', 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('sad', 'default', '我明白你的感受，难过的时候哭出来也没关系，我会一直陪着你。', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('sad', 'empathy', '抱抱你～有时候生活确实很难，但请相信一切都会好起来的。', 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('sad', 'encourage', '你已经很棒了，能够面对这些情绪本身就是一种勇气。', 3, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('angry', 'default', '我理解你现在很生气，先深呼吸冷静一下，我们一起想想办法。', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('angry', 'empathy', '愤怒也是一种正常的情绪，重要的是找到释放的方式。', 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('anxious', 'default', '焦虑的时候，试着把注意力集中在当下，一步一步来。', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('anxious', 'encourage', '你不是一个人在面对，我会陪你一起度过这段时间。', 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('tired', 'default', '累了就好好休息一下，给自己放个假，身体和心情都需要充电。', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('tired', 'encourage', '你已经辛苦了很久，休息不是偷懒，是为了更好地出发。', 2, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000),
('neutral', 'default', '有什么我可以帮你的吗？随时欢迎和我聊聊～', 1, UNIX_TIMESTAMP()*1000, UNIX_TIMESTAMP()*1000);

CREATE INDEX idx_emotion_chat_session_user_id ON `emotion_chat_session`(`user_id`);
CREATE INDEX idx_emotion_chat_record_session_id ON `emotion_chat_record`(`session_id`);
CREATE INDEX idx_emotion_chat_record_user_id ON `emotion_chat_record`(`user_id`);
CREATE INDEX idx_comfort_message_emotion_type ON `comfort_message`(`emotion_type`);