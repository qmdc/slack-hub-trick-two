package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建标签请求
 */
@Data
public class CreateTagRequest {

    @NotBlank(message = "标签名称不能为空")
    private String name;

    private String color;

    private Integer sortOrder;
}
