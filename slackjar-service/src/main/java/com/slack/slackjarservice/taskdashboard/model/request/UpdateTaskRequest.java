package com.slack.slackjarservice.taskdashboard.model.request;

import lombok.Data;

/**
 * 更新任务请求
 *
 * @author zhn
 */
@Data
public class UpdateTaskRequest {

    private String title;

    private String description;

    private Integer status;

    private Integer priority;

    private Long assigneeId;

    private Long dueDate;

    private Integer sortOrder;
}
