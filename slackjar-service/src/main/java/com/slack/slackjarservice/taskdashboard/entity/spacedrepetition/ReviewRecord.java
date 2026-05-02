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
 * 复习记录实体类
 * 记录每次复习的结果，用于间隔重复算法计算
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_review_record")
public class ReviewRecord extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 卡片ID
     */
    private Long cardId;

    /**
     * 卡组ID
     */
    private Long deckId;

    /**
     * 复习评分（0-5）
     * 0: 完全忘记，1: 非常困难，2: 困难
     * 3: 一般，4: 容易，5: 非常容易
     */
    private Integer rating;

    /**
     * 复习前的间隔天数
     */
    private BigDecimal previousInterval;

    /**
     * 复习后的间隔天数
     */
    private BigDecimal newInterval;

    /**
     * 复习前的难度系数
     */
    private BigDecimal previousDifficulty;

    /**
     * 复习后的难度系数
     */
    private BigDecimal newDifficulty;

    /**
     * 复习前的易忘度因子
     */
    private BigDecimal previousEaseFactor;

    /**
     * 复习后的易忘度因子
     */
    private BigDecimal newEaseFactor;

    /**
     * 复习耗时（毫秒）
     */
    private Long reviewDuration;

    /**
     * 复习日期（毫秒时间戳）
     */
    private Long reviewTime;

    /**
     * 此次复习是否正确
     */
    private Integer isCorrect;
}
