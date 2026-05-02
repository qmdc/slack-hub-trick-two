-- 任务看板模块数据库表

-- 任务表
create table slackjar_trick_two.td_task
(
    id           bigint auto_increment comment '任务ID'
        primary key,
    title        varchar(200)      not null comment '任务标题',
    description  text              null comment '任务描述',
    status       tinyint default 0 not null comment '任务状态（0-待办，1-进行中，2-已完成）',
    priority     tinyint default 1 not null comment '任务优先级（0-低，1-中，2-高，3-紧急）',
    assignee_id  bigint            null comment '负责人ID（关联sys_user表）',
    creator_id   bigint            not null comment '创建人ID（关联sys_user表）',
    due_date     bigint            null comment '截止日期（毫秒时间戳）',
    sort_order   int     default 0 not null comment '排序顺序',
    create_time  bigint            null comment '创建时间（毫秒时间戳）',
    update_time  bigint            null comment '更新时间（毫秒时间戳）',
    deleted      tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version      bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_status (status),
    index idx_priority (priority),
    index idx_assignee (assignee_id),
    index idx_creator (creator_id)
)
    comment '任务表';

-- 任务评论表
create table slackjar_trick_two.td_task_comment
(
    id          bigint auto_increment comment '评论ID'
        primary key,
    task_id     bigint            not null comment '任务ID（关联td_task表）',
    user_id     bigint            not null comment '评论人ID（关联sys_user表）',
    content     text              not null comment '评论内容',
    create_time bigint            null comment '创建时间（毫秒时间戳）',
    update_time bigint            null comment '更新时间（毫秒时间戳）',
    deleted     tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version     bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_task_id (task_id),
    index idx_user_id (user_id)
)
    comment '任务评论表';

-- 任务提醒表
create table slackjar_trick_two.td_task_reminder
(
    id            bigint auto_increment comment '提醒ID'
        primary key,
    task_id       bigint            not null comment '任务ID（关联td_task表）',
    user_id       bigint            not null comment '用户ID（关联sys_user表）',
    reminder_time bigint            not null comment '提醒时间（毫秒时间戳）',
    reminded      tinyint default 0 not null comment '是否已经提醒（0-未提醒，1-已提醒）',
    message       varchar(500)      null comment '提醒消息',
    create_time   bigint            null comment '创建时间（毫秒时间戳）',
    update_time   bigint            null comment '更新时间（毫秒时间戳）',
    deleted       tinyint default 0 not null comment '逻辑删除（0-未删，1-已删）',
    version       bigint  default 1 not null comment '版本号（用于乐观锁）',
    index idx_task_id (task_id),
    index idx_user_id (user_id),
    index idx_reminder_time (reminder_time)
)
    comment '任务提醒表';
