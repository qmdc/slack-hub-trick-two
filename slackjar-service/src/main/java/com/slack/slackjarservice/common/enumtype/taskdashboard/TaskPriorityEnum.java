package com.slack.slackjarservice.common.enumtype.taskdashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务优先级枚举
 *
 * @author zhn
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("all")
public enum TaskPriorityEnum {

    LOW(0, "低"),
    MEDIUM(1, "中"),
    HIGH(2, "高"),
    URGENT(3, "紧急");

    private final int code;
    private final String desc;

    /**
     * 根据code获取枚举值
     *
     * @param code 优先级code
     * @return 对应的枚举值
     */
    public static TaskPriorityEnum fromCode(int code) {
        for (TaskPriorityEnum priority : values()) {
            if (priority.getCode() == code) {
                return priority;
            }
        }
        return MEDIUM;
    }
}
