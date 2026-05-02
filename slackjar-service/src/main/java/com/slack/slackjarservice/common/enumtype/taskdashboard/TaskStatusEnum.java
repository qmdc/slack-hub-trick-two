package com.slack.slackjarservice.common.enumtype.taskdashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态枚举
 *
 * @author zhn
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("all")
public enum TaskStatusEnum {

    TODO(0, "待办"),
    IN_PROGRESS(1, "进行中"),
    DONE(2, "已完成");

    private final int code;
    private final String desc;

    /**
     * 根据code获取枚举值
     *
     * @param code 状态code
     * @return 对应的枚举值
     */
    public static TaskStatusEnum fromCode(int code) {
        for (TaskStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return TODO;
    }
}
