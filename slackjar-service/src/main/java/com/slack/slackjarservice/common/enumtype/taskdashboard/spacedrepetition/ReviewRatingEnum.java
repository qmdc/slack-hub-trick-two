package com.slack.slackjarservice.common.enumtype.taskdashboard.spacedrepetition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 复习评分枚举
 */
@Getter
@AllArgsConstructor
public enum ReviewRatingEnum {

    FORGOT(0, "完全忘记"),
    VERY_HARD(1, "非常困难"),
    HARD(2, "困难"),
    MEDIUM(3, "一般"),
    EASY(4, "容易"),
    VERY_EASY(5, "非常容易");

    private final Integer code;
    private final String desc;

    public static ReviewRatingEnum fromCode(Integer code) {
        if (code == null) {
            return MEDIUM;
        }
        for (ReviewRatingEnum rating : values()) {
            if (rating.getCode().equals(code)) {
                return rating;
            }
        }
        return MEDIUM;
    }

    public static String getDescByCode(Integer code) {
        return fromCode(code).getDesc();
    }

    public static boolean isCorrect(Integer code) {
        return code != null && code >= 3;
    }
}
