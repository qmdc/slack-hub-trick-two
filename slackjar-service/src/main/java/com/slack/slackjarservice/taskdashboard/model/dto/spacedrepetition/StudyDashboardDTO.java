package com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 学习仪表盘DTO
 */
@Data
public class StudyDashboardDTO implements Serializable {

    private Integer todayPendingReview;

    private Integer todayReviewed;

    private Integer todayNewCards;

    private Integer todayCorrectCount;

    private Integer todayIncorrectCount;

    private BigDecimal todayAccuracyRate;

    private Long todayStudyDuration;

    private String todayStudyDurationFormatted;

    private Integer totalDecks;

    private Integer totalCards;

    private Integer totalReviewedCards;

    private Integer totalMasteredCards;

    private BigDecimal overallMasteryRate;

    private List<DeckDTO> recentDecks;

    private List<CardDTO> todayReviewCards;

    private List<TagDTO> hotTags;
}
