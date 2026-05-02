package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 复习卡片请求
 */
@Data
public class ReviewCardRequest {

    @NotNull(message = "卡片ID不能为空")
    private Long cardId;

    @NotNull(message = "复习评分不能为空")
    @Min(value = 0, message = "评分范围0-5")
    @Max(value = 5, message = "评分范围0-5")
    private Integer rating;

    private Long reviewDuration;
}
