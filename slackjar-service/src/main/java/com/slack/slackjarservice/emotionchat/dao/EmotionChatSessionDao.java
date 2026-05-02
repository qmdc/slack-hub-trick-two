package com.slack.slackjarservice.emotionchat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.emotionchat.entity.EmotionChatSession;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmotionChatSessionDao extends BaseMapper<EmotionChatSession> {

    List<EmotionChatSession> selectByUserId(Long userId);
}