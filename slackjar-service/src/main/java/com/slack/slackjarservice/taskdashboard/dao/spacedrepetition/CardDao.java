package com.slack.slackjarservice.taskdashboard.dao.spacedrepetition;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.Card;

/**
 * 闪卡(Card)表数据库访问层
 *
 * @author system
 */
@Mapper
public interface CardDao extends BaseMapper<Card> {

}
