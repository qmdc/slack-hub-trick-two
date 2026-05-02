package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 创建卡组请求
 */
@Data
public class CreateDeckRequest {

    @NotBlank(message = "卡组名称不能为空")
    private String name;

    private String description;

    private Long coverImageId;

    private String coverImageUrl;

    private List<Long> tagIds;

    private Integer sortOrder;
}
