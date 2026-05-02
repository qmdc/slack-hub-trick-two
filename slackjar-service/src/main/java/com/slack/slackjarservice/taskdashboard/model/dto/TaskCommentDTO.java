package com.slack.slackjarservice.taskdashboard.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务评论DTO
 */
@Data
public class TaskCommentDTO implements Serializable {

    private Long id;

    private Long taskId;

    private Long userId;

    private String userName;

    private String userAvatarUrl;

    private String content;

    private Long createTime;
}
