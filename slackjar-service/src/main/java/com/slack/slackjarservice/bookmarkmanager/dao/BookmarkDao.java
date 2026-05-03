package com.slack.slackjarservice.bookmarkmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slack.slackjarservice.bookmarkmanager.entity.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookmarkDao extends BaseMapper<Bookmark> {

    IPage<Bookmark> pageQuery(Page<Bookmark> page, @Param("userId") Long userId, 
                              @Param("keyword") String keyword, @Param("categoryId") Long categoryId,
                              @Param("tagName") String tagName);

    List<Bookmark> selectByUserId(@Param("userId") Long userId);

    int countByCategoryId(@Param("categoryId") Long categoryId);
}