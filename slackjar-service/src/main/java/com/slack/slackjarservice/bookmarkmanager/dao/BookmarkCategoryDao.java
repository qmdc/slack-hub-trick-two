package com.slack.slackjarservice.bookmarkmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.bookmarkmanager.entity.BookmarkCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookmarkCategoryDao extends BaseMapper<BookmarkCategory> {

    List<BookmarkCategory> selectByUserIdOrderBySort(@Param("userId") Long userId);
}