package com.slack.slackjarservice.taskdashboard.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.TaskComment;

/**
 * 任务评论表(TaskComment)表数据库访问层
 *
 * @author zhn
 */
@Mapper
public interface TaskCommentDao extends BaseMapper<TaskComment> {

}
