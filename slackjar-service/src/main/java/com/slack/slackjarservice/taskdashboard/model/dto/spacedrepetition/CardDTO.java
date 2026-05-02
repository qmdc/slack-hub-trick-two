package com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 闪卡DTO
 */
@Data
public class CardDTO implements Serializable {

    private Long id;

    private Long userId;

    private Long deckId;

    private String frontContent;

    private List<Long> frontImageIds;

    private List<String> frontImageUrls;

    private String backContent;

    private List<Long> backImageIds;

    private List<String> backImageUrls;

    private Integer masteryLevel;

    private String masteryLevelDesc;

    private Integer reviewCount;

    private Integer correctCount;

    private Integer incorrectCount;

    private Integer consecutiveCorrectCount;

    private Long lastReviewTime;

    private Long nextReviewTime;

    private BigDecimal intervalDays;

    private BigDecimal difficulty;

    private BigDecimal easeFactor;

    private Integer isImportant;

    private Integer sortOrder;

    private Long createTime;

    private Long updateTime;

    private List<TagDTO> tags;

    private Boolean isDueToday;
}
