package com.slack.slackjarservice.bookmarkmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.bookmarkmanager.entity.BookmarkTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookmarkTagDao extends BaseMapper<BookmarkTag> {

    List<BookmarkTag> selectByUserId(@Param("userId") Long userId);

    BookmarkTag selectByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);
}