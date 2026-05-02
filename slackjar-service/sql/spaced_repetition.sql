-- 间隔重复（知识卡片记忆）模块数据库表
-- 基于艾宾浩斯遗忘曲线的智能记忆系统

-- 闪卡组表
create table slackjar_trick_two.sp_deck
(
    id                   bigint auto_increment comment '卡组ID'
        primary key,
    user_id              bigint            not null comment '所属用户ID（关联sys_user表）',
    name                 varchar(200)      not null comment '卡组名称',
    description          text              null comment '卡组描述',
    cover_image_id       bigint            null comment '封面图片ID（关联sys_file表）',
    cover_image_url      varchar(500)      null comment '封面图片URL',
    card_count           int     default 0 not null comment '卡片数量',
    today_review_count   int     default 0 not null comment '今日需复习卡片数',
    mastery_rate         decimal(5, 2)     null comment '掌握率（百分比）',
    is_public            tinyint default 0 not null comment '是否公开到市场（0-私有，1-公开）',
    sort_order           int     default 0 not null comment '排序顺序',
    create_time          bigint            null comment '创建时间（毫秒时间戳）',
    update_time          bigint            null comment '更新时间（毫秒时间戳）',
    deleted              tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version              bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (user_id),
    index idx_is_public (is_public)
)
    comment '闪卡组表';

-- 闪卡表
create table slackjar_trick_two.sp_card
(
    id                        bigint auto_increment comment '卡片ID'
        primary key,
    user_id                   bigint            not null comment '所属用户ID（关联sys_user表）',
    deck_id                   bigint            not null comment '所属卡组ID（关联sp_deck表）',
    front_content             text              null comment '卡片正面内容（问题，支持富文本）',
    front_image_ids           varchar(1000)     null comment '卡片正面图片ID列表（JSON数组）',
    front_image_urls          text              null comment '卡片正面图片URL列表（JSON数组）',
    back_content              text              null comment '卡片背面内容（答案，支持富文本）',
    back_image_ids            varchar(1000)     null comment '卡片背面图片ID列表（JSON数组）',
    back_image_urls           text              null comment '卡片背面图片URL列表（JSON数组）',
    mastery_level             tinyint default 0 not null comment '掌握程度（0-新卡片，1-非常不熟悉，2-不熟悉，3-一般，4-熟悉，5-非常熟悉）',
    review_count              int     default 0 not null comment '复习次数',
    correct_count             int     default 0 not null comment '正确回答次数',
    incorrect_count           int     default 0 not null comment '错误回答次数',
    consecutive_correct_count int     default 0 not null comment '连续正确次数',
    last_review_time          bigint            null comment '最近复习时间（毫秒时间戳）',
    next_review_time          bigint            null comment '下次复习时间（毫秒时间戳）',
    interval_days             decimal(10, 2)    null comment '间隔天数（用于间隔重复算法）',
    difficulty                decimal(5, 2)     null comment '难度系数',
    ease_factor               decimal(5, 2)     null comment '易忘度因子（SM-2算法用）',
    is_important              tinyint default 0 not null comment '是否标记为重点（0-否，1-是）',
    sort_order                int     default 0 not null comment '排序顺序',
    create_time               bigint            null comment '创建时间（毫秒时间戳）',
    update_time               bigint            null comment '更新时间（毫秒时间戳）',
    deleted                   tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version                   bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (user_id),
    index idx_deck_id (deck_id),
    index idx_next_review_time (next_review_time),
    index idx_mastery_level (mastery_level)
)
    comment '闪卡表';

-- 标签表
create table slackjar_trick_two.sp_tag
(
    id          bigint auto_increment comment '标签ID'
        primary key,
    user_id     bigint            not null comment '所属用户ID（关联sys_user表）',
    name        varchar(100)      not null comment '标签名称',
    color       varchar(20)       null comment '标签颜色（HEX格式）',
    card_count  int     default 0 not null comment '关联卡片数量',
    sort_order  int     default 0 not null comment '排序顺序',
    create_time bigint            null comment '创建时间（毫秒时间戳）',
    update_time bigint            null comment '更新时间（毫秒时间戳）',
    deleted     tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version     bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (user_id)
)
    comment '标签表';

-- 卡片-标签关联表
create table slackjar_trick_two.sp_card_tag
(
    id          bigint auto_increment comment '关联ID'
        primary key,
    card_id     bigint            not null comment '卡片ID（关联sp_card表）',
    tag_id      bigint            not null comment '标签ID（关联sp_tag表）',
    create_time bigint            null comment '创建时间（毫秒时间戳）',
    update_time bigint            null comment '更新时间（毫秒时间戳）',
    deleted     tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version     bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_card_tag (card_id, tag_id),
    index idx_card_id (card_id),
    index idx_tag_id (tag_id)
)
    comment '卡片-标签关联表';

-- 复习记录表
create table slackjar_trick_two.sp_review_record
(
    id                    bigint auto_increment comment '记录ID'
        primary key,
    user_id               bigint            not null comment '用户ID（关联sys_user表）',
    card_id               bigint            not null comment '卡片ID（关联sp_card表）',
    deck_id               bigint            not null comment '卡组ID（关联sp_deck表）',
    rating                tinyint           not null comment '复习评分（0-5）',
    previous_interval     decimal(10, 2)    null comment '复习前的间隔天数',
    new_interval          decimal(10, 2)    null comment '复习后的间隔天数',
    previous_difficulty   decimal(5, 2)     null comment '复习前的难度系数',
    new_difficulty        decimal(5, 2)     null comment '复习后的难度系数',
    previous_ease_factor  decimal(5, 2)     null comment '复习前的易忘度因子',
    new_ease_factor       decimal(5, 2)     null comment '复习后的易忘度因子',
    review_duration       bigint            null comment '复习耗时（毫秒）',
    review_time           bigint            not null comment '复习时间（毫秒时间戳）',
    is_correct            tinyint default 1 not null comment '此次复习是否正确（0-错误，1-正确）',
    create_time           bigint            null comment '创建时间（毫秒时间戳）',
    update_time           bigint            null comment '更新时间（毫秒时间戳）',
    deleted               tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version               bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (user_id),
    index idx_card_id (card_id),
    index idx_deck_id (deck_id),
    index idx_review_time (review_time)
)
    comment '复习记录表';

-- 学习统计表
create table slackjar_trick_two.sp_study_stat
(
    id                     bigint auto_increment comment '统计ID'
        primary key,
    user_id                bigint            not null comment '用户ID（关联sys_user表）',
    stat_date              varchar(20)       not null comment '统计日期（格式：yyyy-MM-dd）',
    learned_cards          int     default 0 not null comment '今日学习卡片数',
    reviewed_cards         int     default 0 not null comment '今日复习卡片数',
    new_cards              int     default 0 not null comment '今日新学卡片数',
    correct_count          int     default 0 not null comment '今日正确回答数',
    incorrect_count        int     default 0 not null comment '今日错误回答数',
    accuracy_rate          decimal(5, 2)     null comment '今日正确率',
    study_duration         bigint  default 0 not null comment '今日学习时长（毫秒）',
    total_learned_cards    int     default 0 not null comment '累计学习卡片数',
    total_reviewed_cards   int     default 0 not null comment '累计复习卡片数',
    total_study_duration   bigint  default 0 not null comment '累计学习时长（毫秒）',
    today_pending_review   int     default 0 not null comment '今日需复习卡片数',
    create_time            bigint            null comment '创建时间（毫秒时间戳）',
    update_time            bigint            null comment '更新时间（毫秒时间戳）',
    deleted                tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version                bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_user_date (user_id, stat_date),
    index idx_user_id (user_id),
    index idx_stat_date (stat_date)
)
    comment '学习统计表';

-- 市场卡组表
create table slackjar_trick_two.sp_market_deck
(
    id              bigint auto_increment comment '市场卡组ID'
        primary key,
    original_deck_id bigint           not null comment '原卡组ID',
    user_id         bigint            not null comment '分享用户ID（关联sys_user表）',
    user_nickname   varchar(100)      null comment '分享用户昵称',
    name            varchar(200)      not null comment '卡组名称',
    description     text              null comment '卡组描述',
    cover_image_url varchar(500)      null comment '封面图片URL',
    card_count      int     default 0 not null comment '卡片数量',
    tags            varchar(1000)     null comment '标签（JSON数组）',
    like_count      int     default 0 not null comment '点赞数',
    favorite_count  int     default 0 not null comment '收藏数',
    download_count  int     default 0 not null comment '下载数',
    avg_rating      decimal(3, 2)     null comment '平均评分',
    rating_count    int     default 0 not null comment '评分人数',
    status          tinyint default 1 not null comment '状态（0-待审核，1-已上架，2-已下架）',
    sort_order      int     default 0 not null comment '排序顺序',
    create_time     bigint            null comment '创建时间（毫秒时间戳）',
    update_time     bigint            null comment '更新时间（毫秒时间戳）',
    deleted         tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version         bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_user_id (user_id),
    index idx_status (status),
    index idx_like_count (like_count),
    index idx_download_count (download_count)
)
    comment '市场卡组表';

-- 市场卡组点赞表
create table slackjar_trick_two.sp_market_deck_like
(
    id            bigint auto_increment comment '点赞ID'
        primary key,
    market_deck_id bigint           not null comment '市场卡组ID（关联sp_market_deck表）',
    user_id       bigint            not null comment '用户ID（关联sys_user表）',
    create_time   bigint            null comment '创建时间（毫秒时间戳）',
    update_time   bigint            null comment '更新时间（毫秒时间戳）',
    deleted       tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version       bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_deck_user (market_deck_id, user_id),
    index idx_market_deck_id (market_deck_id),
    index idx_user_id (user_id)
)
    comment '市场卡组点赞表';

-- 市场卡组收藏表
create table slackjar_trick_two.sp_market_deck_favorite
(
    id            bigint auto_increment comment '收藏ID'
        primary key,
    market_deck_id bigint           not null comment '市场卡组ID（关联sp_market_deck表）',
    user_id       bigint            not null comment '用户ID（关联sys_user表）',
    create_time   bigint            null comment '创建时间（毫秒时间戳）',
    update_time   bigint            null comment '更新时间（毫秒时间戳）',
    deleted       tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version       bigint  default 1 not null comment '版本号（用于乐观锁）',
    unique key uk_deck_user (market_deck_id, user_id),
    index idx_market_deck_id (market_deck_id),
    index idx_user_id (user_id)
)
    comment '市场卡组收藏表';

-- 市场卡组下载记录表
create table slackjar_trick_two.sp_market_deck_download
(
    id            bigint auto_increment comment '下载记录ID'
        primary key,
    market_deck_id bigint           not null comment '市场卡组ID（关联sp_market_deck表）',
    user_id       bigint            not null comment '用户ID（关联sys_user表）',
    download_time bigint            not null comment '下载时间（毫秒时间戳）',
    create_time   bigint            null comment '创建时间（毫秒时间戳）',
    update_time   bigint            null comment '更新时间（毫秒时间戳）',
    deleted       tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version       bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_market_deck_id (market_deck_id),
    index idx_user_id (user_id)
)
    comment '市场卡组下载记录表';
