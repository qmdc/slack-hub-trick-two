package com.slack.slackjarservice.taskdashboard.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建任务评论请求
 *
 * @author zhn
 */
@Data
public class CreateTaskCommentRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
