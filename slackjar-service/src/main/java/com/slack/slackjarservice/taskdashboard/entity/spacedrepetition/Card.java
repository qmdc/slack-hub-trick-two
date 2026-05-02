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
 * 闪卡实体类
 * 知识卡片，正面问题/背面答案，支持富文本和图片
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_card")
public class Card extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 所属卡组ID
     */
    private Long deckId;

    /**
     * 卡片正面内容（问题，支持富文本）
     */
    private String frontContent;

    /**
     * 卡片正面图片ID列表（JSON数组）
     */
    private String frontImageIds;

    /**
     * 卡片正面图片URL列表（JSON数组）
     */
    private String frontImageUrls;

    /**
     * 卡片背面内容（答案，支持富文本）
     */
    private String backContent;

    /**
     * 卡片背面图片ID列表（JSON数组）
     */
    private String backImageIds;

    /**
     * 卡片背面图片URL列表（JSON数组）
     */
    private String backImageUrls;

    /**
     * 掌握程度（0-5级）
     * 0: 新卡片，1: 非常不熟悉，2: 不熟悉
     * 3: 一般，4: 熟悉，5: 非常熟悉
     */
    private Integer masteryLevel;

    /**
     * 复习次数
     */
    private Integer reviewCount;

    /**
     * 正确回答次数
     */
    private Integer correctCount;

    /**
     * 错误回答次数
     */
    private Integer incorrectCount;

    /**
     * 连续正确次数
     */
    private Integer consecutiveCorrectCount;

    /**
     * 最近复习时间（毫秒时间戳）
     */
    private Long lastReviewTime;

    /**
     * 下次复习时间（毫秒时间戳）
     */
    private Long nextReviewTime;

    /**
     * 间隔天数（用于间隔重复算法）
     */
    private BigDecimal intervalDays;

    /**
     * 难度系数（用于间隔重复算法）
     */
    private BigDecimal difficulty;

    /**
     * 易忘度因子
     */
    private BigDecimal easeFactor;

    /**
     * 是否标记为重点
     */
    private Integer isImportant;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
