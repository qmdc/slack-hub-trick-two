package com.slack.slackjarservice.taskdashboard.model.request;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务分页查询请求
 *
 * @author zhn
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskPageQuery extends BasePagination {

    private String keyword;

    private Integer status;

    private Integer priority;

    private Long assigneeId;

    private Long creatorId;
}
