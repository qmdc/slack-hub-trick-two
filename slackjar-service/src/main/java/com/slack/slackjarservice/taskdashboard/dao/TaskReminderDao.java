package com.slack.slackjarservice.taskdashboard.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.TaskReminder;

/**
 * 任务提醒表(TaskReminder)表数据库访问层
 *
 * @author zhn
 */
@Mapper
public interface TaskReminderDao extends BaseMapper<TaskReminder> {

}
