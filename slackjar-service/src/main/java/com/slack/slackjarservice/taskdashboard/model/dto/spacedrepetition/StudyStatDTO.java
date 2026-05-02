package com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 学习统计DTO
 */
@Data
public class StudyStatDTO implements Serializable {

    private Long id;

    private Long userId;

    private String statDate;

    private Integer learnedCards;

    private Integer reviewedCards;

    private Integer newCards;

    private Integer correctCount;

    private Integer incorrectCount;

    private BigDecimal accuracyRate;

    private Long studyDuration;

    private String studyDurationFormatted;

    private Integer totalLearnedCards;

    private Integer totalReviewedCards;

    private Long totalStudyDuration;

    private String totalStudyDurationFormatted;

    private Integer todayPendingReview;

    private Long createTime;

    private Long updateTime;
}
