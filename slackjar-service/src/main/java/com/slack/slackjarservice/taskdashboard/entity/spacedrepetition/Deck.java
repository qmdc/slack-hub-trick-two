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
 * 闪卡组实体类
 * 用于分组管理知识卡片
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_deck")
public class Deck extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 卡组名称
     */
    private String name;

    /**
     * 卡组描述
     */
    private String description;

    /**
     * 封面图片ID
     */
    private Long coverImageId;

    /**
     * 封面图片URL
     */
    private String coverImageUrl;

    /**
     * 卡片数量
     */
    private Integer cardCount;

    /**
     * 今日需复习卡片数
     */
    private Integer todayReviewCount;

    /**
     * 掌握率（百分比）
     */
    private BigDecimal masteryRate;

    /**
     * 是否公开到市场
     */
    private Integer isPublic;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
