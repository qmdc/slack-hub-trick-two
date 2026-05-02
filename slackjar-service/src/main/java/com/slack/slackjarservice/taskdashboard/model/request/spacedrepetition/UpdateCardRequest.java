package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import lombok.Data;

import java.util.List;

/**
 * 更新卡片请求
 */
@Data
public class UpdateCardRequest {

    private Long deckId;

    private String frontContent;

    private List<Long> frontImageIds;

    private String backContent;

    private List<Long> backImageIds;

    private List<Long> tagIds;

    private Integer isImportant;

    private Integer sortOrder;
}
