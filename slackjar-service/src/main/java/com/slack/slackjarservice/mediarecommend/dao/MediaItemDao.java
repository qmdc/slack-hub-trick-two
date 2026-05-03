package com.slack.slackjarservice.mediarecommend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.mediarecommend.entity.MediaItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MediaItemDao extends BaseMapper<MediaItem> {

    List<MediaItem> selectByUserId(@Param("userId") Long userId);

    List<MediaItem> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") Integer type);

    List<MediaItem> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    List<MediaItem> selectByUserIdTypeAndStatus(@Param("userId") Long userId, @Param("type") Integer type, @Param("status") Integer status);

    List<MediaItem> selectRecommendations(@Param("userId") Long userId, @Param("tagList") List<String> tagList, @Param("excludeIds") List<Long> excludeIds, @Param("limit") Integer limit);

    List<String> selectAllTagsByUserId(@Param("userId") Long userId);
}