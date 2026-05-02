package com.slack.slackjarservice.taskdashboard.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.Task;

/**
 * 任务表(Task)表数据库访问层
 *
 * @author zhn
 */
@Mapper
public interface TaskDao extends BaseMapper<Task> {

}
