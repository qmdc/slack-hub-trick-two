package com.slack.slackjarservice.taskdashboard.dao.spacedrepetition;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.slack.slackjarservice.taskdashboard.entity.spacedrepetition.MarketDeckDownload;

/**
 * 市场卡组下载记录(MarketDeckDownload)表数据库访问层
 *
 * @author system
 */
@Mapper
public interface MarketDeckDownloadDao extends BaseMapper<MarketDeckDownload> {

}
