package com.slack.slackjarservice.common.enumtype.taskdashboard.spacedrepetition;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 市场卡组状态枚举
 */
@Getter
@AllArgsConstructor
public enum MarketDeckStatusEnum {

    PENDING(0, "待审核"),
    PUBLISHED(1, "已上架"),
    OFF_SHELF(2, "已下架");

    private final Integer code;
    private final String desc;

    public static MarketDeckStatusEnum fromCode(Integer code) {
        if (code == null) {
            return PENDING;
        }
        for (MarketDeckStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return PENDING;
    }

    public static String getDescByCode(Integer code) {
        return fromCode(code).getDesc();
    }
}
