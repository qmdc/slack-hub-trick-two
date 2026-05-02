package com.slack.slackjarservice.taskdashboard.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务提醒DTO
 */
@Data
public class TaskReminderDTO implements Serializable {

    private Long id;

    private Long taskId;

    private Long userId;

    private Long reminderTime;

    private Integer reminded;

    private String message;
}
