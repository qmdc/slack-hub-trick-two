package com.slack.slackjarservice.taskdashboard.entity.spacedrepetition;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 市场卡组实体类
 * 用户分享到市场的卡组
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_market_deck")
public class MarketDeck extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 原卡组ID
     */
    private Long originalDeckId;

    /**
     * 分享用户ID
     */
    private Long userId;

    /**
     * 分享用户昵称
     */
    private String userNickname;

    /**
     * 卡组名称
     */
    private String name;

    /**
     * 卡组描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverImageUrl;

    /**
     * 卡片数量
     */
    private Integer cardCount;

    /**
     * 标签（JSON数组）
     */
    private String tags;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

    /**
     * 下载数
     */
    private Integer downloadCount;

    /**
     * 平均评分
     */
    private BigDecimal avgRating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 状态（0-待审核，1-已上架，2-已下架）
     */
    private Integer status;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
