package com.slack.slackjarservice.taskdashboard.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建任务提醒请求
 *
 * @author zhn
 */
@Data
public class CreateTaskReminderRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "提醒时间不能为空")
    private Long reminderTime;

    private String message;
}
