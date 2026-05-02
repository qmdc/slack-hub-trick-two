package com.slack.slackjarservice.taskdashboard.entity.spacedrepetition;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 市场卡组下载记录实体类
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_market_deck_download")
public class MarketDeckDownload extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 市场卡组ID
     */
    private Long marketDeckId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 下载时间（毫秒时间戳）
     */
    private Long downloadTime;
}
