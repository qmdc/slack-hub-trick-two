package com.slack.slackjarservice.taskdashboard.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新任务状态请求（拖拽时使用）
 *
 * @author zhn
 */
@Data
public class UpdateTaskStatusRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "目标状态不能为空")
    private Integer targetStatus;

    private Integer targetSortOrder;
}
