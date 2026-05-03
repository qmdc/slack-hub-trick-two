package com.slack.slackjarservice.mediarecommend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.slack.slackjarservice.mediarecommend.entity.ShareLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShareLinkDao extends BaseMapper<ShareLink> {

    ShareLink selectByShareCode(@Param("shareCode") String shareCode);

    List<ShareLink> selectByUserId(@Param("userId") Long userId);
}