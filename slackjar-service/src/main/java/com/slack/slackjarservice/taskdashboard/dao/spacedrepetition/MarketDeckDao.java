package com.slack.slackjarservice.taskdashboard.dao.spacedrepetition;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.MarketDeck;

/**
 * 市场卡组(MarketDeck)表数据库访问层
 *
 * @author system
 */
@Mapper
public interface MarketDeckDao extends BaseMapper<MarketDeck> {

}
