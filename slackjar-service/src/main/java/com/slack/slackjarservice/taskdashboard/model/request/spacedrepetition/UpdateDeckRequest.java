package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import lombok.Data;

import java.util.List;

/**
 * 更新卡组请求
 */
@Data
public class UpdateDeckRequest {

    private String name;

    private String description;

    private Long coverImageId;

    private String coverImageUrl;

    private List<Long> tagIds;

    private Integer isPublic;

    private Integer sortOrder;
}
