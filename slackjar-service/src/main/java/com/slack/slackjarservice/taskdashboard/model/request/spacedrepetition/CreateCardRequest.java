package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建卡片请求
 */
@Data
public class CreateCardRequest {

    @NotNull(message = "卡组ID不能为空")
    private Long deckId;

    private String frontContent;

    private List<Long> frontImageIds;

    private String backContent;

    private List<Long> backImageIds;

    private List<Long> tagIds;

    private Integer isImportant;

    private Integer sortOrder;
}
