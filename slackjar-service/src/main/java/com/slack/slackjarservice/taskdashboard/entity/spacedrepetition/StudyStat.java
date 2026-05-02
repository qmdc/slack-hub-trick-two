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
 * 学习统计实体类
 * 记录用户的学习统计数据
 *
 * @author system
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sp_study_stat")
public class StudyStat extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 统计日期（格式：yyyy-MM-dd）
     */
    private String statDate;

    /**
     * 今日学习卡片数
     */
    private Integer learnedCards;

    /**
     * 今日复习卡片数
     */
    private Integer reviewedCards;

    /**
     * 今日新学卡片数
     */
    private Integer newCards;

    /**
     * 今日正确回答数
     */
    private Integer correctCount;

    /**
     * 今日错误回答数
     */
    private Integer incorrectCount;

    /**
     * 今日正确率
     */
    private BigDecimal accuracyRate;

    /**
     * 今日学习时长（毫秒）
     */
    private Long studyDuration;

    /**
     * 累计学习卡片数
     */
    private Integer totalLearnedCards;

    /**
     * 累计复习卡片数
     */
    private Integer totalReviewedCards;

    /**
     * 累计学习时长（毫秒）
     */
    private Long totalStudyDuration;

    /**
     * 今日需复习卡片数（冗余字段，便于查询）
     */
    private Integer todayPendingReview;
}
