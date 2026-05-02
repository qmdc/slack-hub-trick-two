package com.slack.slackjarservice.emotionchat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.emotionchat.entity.ComfortMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComfortMessageDao extends BaseMapper<ComfortMessage> {

    List<ComfortMessage> selectByEmotionType(String emotionType);
}