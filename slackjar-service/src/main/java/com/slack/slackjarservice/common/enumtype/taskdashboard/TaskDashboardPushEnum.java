package com.slack.slackjarservice.common.enumtype.taskdashboard;

import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.exception.BusinessException;
import lombok.Getter;

/**
 * Socket消息业务类型枚举 任务看板相关
 *
 * @author zhn
 */
@Getter
public enum TaskDashboardPushEnum {

    TASK_CREATED("TASK_CREATED", "任务创建"),
    TASK_UPDATED("TASK_UPDATED", "任务更新"),
    TASK_DELETED("TASK_DELETED", "任务删除"),
    TASK_STATUS_CHANGED("TASK_STATUS_CHANGED", "任务状态变更"),
    TASK_COMMENT_ADDED("TASK_COMMENT_ADDED", "任务评论添加"),
    TASK_REMINDER_SET("TASK_REMINDER_SET", "任务提醒设置"),
    BOARD_REFRESH("BOARD_REFRESH", "看板刷新");

    private final String code;
    private final String description;

    TaskDashboardPushEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举值
     *
     * @param code 业务类型code
     * @return 对应的枚举值
     */
    public static TaskDashboardPushEnum fromCode(String code) {
        for (TaskDashboardPushEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new BusinessException(ResponseEnum.SOCKET_BIZ_NOT_FOUND);
    }
}
