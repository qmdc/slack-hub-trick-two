package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 分享卡组到市场请求
 */
@Data
public class ShareToMarketRequest {

    @NotNull(message = "卡组ID不能为空")
    private Long deckId;

    private String name;

    private String description;

    private List<String> tags;
}
