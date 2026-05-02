package com.slack.slackjarservice.taskdashboard.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 任务详情DTO
 */
@Data
public class TaskDTO implements Serializable {

    private Long id;

    private String title;

    private String description;

    private Integer status;

    private String statusDesc;

    private Integer priority;

    private String priorityDesc;

    private Long assigneeId;

    private String assigneeName;

    private String assigneeAvatarUrl;

    private Long creatorId;

    private String creatorName;

    private Long dueDate;

    private Integer sortOrder;

    private Long createTime;

    private Long updateTime;

    private List<TaskCommentDTO> comments;

    private List<TaskReminderDTO> reminders;
}
