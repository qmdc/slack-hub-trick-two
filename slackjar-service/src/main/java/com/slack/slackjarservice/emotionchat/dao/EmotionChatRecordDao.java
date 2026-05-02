package com.slack.slackjarservice.emotionchat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.emotionchat.entity.EmotionChatRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmotionChatRecordDao extends BaseMapper<EmotionChatRecord> {

    List<EmotionChatRecord> selectBySessionId(Long sessionId);

    List<EmotionChatRecord> selectRecentByUserId(Long userId, Integer limit);
}