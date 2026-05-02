package com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 闪卡组DTO
 */
@Data
public class DeckDTO implements Serializable {

    private Long id;

    private Long userId;

    private String name;

    private String description;

    private Long coverImageId;

    private String coverImageUrl;

    private Integer cardCount;

    private Integer todayReviewCount;

    private BigDecimal masteryRate;

    private Integer isPublic;

    private Integer sortOrder;

    private Long createTime;

    private Long updateTime;

    private List<TagDTO> tags;
}
