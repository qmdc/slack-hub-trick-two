package com.slack.slackjarservice.common.enumtype.taskdashboard.spacedrepetition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 掌握程度枚举
 */
@Getter
@AllArgsConstructor
public enum MasteryLevelEnum {

    NEW(0, "新卡片"),
    VERY_LOW(1, "非常不熟悉"),
    LOW(2, "不熟悉"),
    MEDIUM(3, "一般"),
    HIGH(4, "熟悉"),
    VERY_HIGH(5, "非常熟悉");

    private final Integer code;
    private final String desc;

    public static MasteryLevelEnum fromCode(Integer code) {
        if (code == null) {
            return NEW;
        }
        for (MasteryLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level;
            }
        }
        return NEW;
    }

    public static String getDescByCode(Integer code) {
        return fromCode(code).getDesc();
    }
}
