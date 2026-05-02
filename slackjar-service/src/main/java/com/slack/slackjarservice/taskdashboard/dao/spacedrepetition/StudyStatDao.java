package com.slack.slackjarservice.taskdashboard.dao.spacedrepetition;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.StudyStat;

/**
 * 学习统计(StudyStat)表数据库访问层
 *
 * @author system
 */
@Mapper
public interface StudyStatDao extends BaseMapper<StudyStat> {

}
