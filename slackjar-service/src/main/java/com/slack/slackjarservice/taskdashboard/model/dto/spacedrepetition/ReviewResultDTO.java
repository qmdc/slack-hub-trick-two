package com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 复习结果DTO
 */
@Data
public class ReviewResultDTO implements Serializable {

    private Long cardId;

    private Integer rating;

    private String ratingDesc;

    private BigDecimal previousInterval;

    private BigDecimal newInterval;

    private BigDecimal previousEaseFactor;

    private BigDecimal newEaseFactor;

    private Long nextReviewTime;

    private Integer masteryLevel;

    private String masteryLevelDesc;

    private Boolean isCorrect;

    private Long reviewDuration;
}
