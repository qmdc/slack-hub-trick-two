package com.slack.slackjarservice.taskdashboard.model.dto.spacedrepetition;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 市场卡组DTO
 */
@Data
public class MarketDeckDTO implements Serializable {

    private Long id;

    private Long originalDeckId;

    private Long userId;

    private String userNickname;

    private String userAvatarUrl;

    private String name;

    private String description;

    private String coverImageUrl;

    private Integer cardCount;

    private List<String> tags;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer downloadCount;

    private BigDecimal avgRating;

    private Integer ratingCount;

    private Integer status;

    private String statusDesc;

    private Integer sortOrder;

    private Long createTime;

    private Long updateTime;

    private Boolean isLiked;

    private Boolean isFavorited;

    private Boolean isDownloaded;
}
