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
 * 市场卡组收藏实体类
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_market_deck_favorite")
public class MarketDeckFavorite extends BaseModel {

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
}
