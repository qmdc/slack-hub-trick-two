package com.slack.slackjarservice.taskdashboard.dao.spacedrepetition;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.CardTag;

/**
 * 卡片-标签关联(CardTag)表数据库访问层
 *
 * @author system
 */
@Mapper
public interface CardTagDao extends BaseMapper<CardTag> {

}
