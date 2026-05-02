package com.slack.slackjarservice.taskdashboard.dao.spacedrepetition;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.Deck;

/**
 * 闪卡组(Deck)表数据库访问层
 *
 * @author system
 */
@Mapper
public interface DeckDao extends BaseMapper<Deck> {

}
