package com.slack.slackjarservice.taskdashboard.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建任务请求
 *
 * @author zhn
 */
@Data
public class CreateTaskRequest {

    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "任务状态不能为空")
    private Integer status;

    private Integer priority;

    private Long assigneeId;

    private Long dueDate;

    private Integer sortOrder;
}
