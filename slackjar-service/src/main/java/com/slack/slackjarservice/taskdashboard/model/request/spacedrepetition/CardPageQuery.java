package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 卡片分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CardPageQuery extends BasePagination {

    private Long deckId;

    private String keyword;

    private List<Long> tagIds;

    private Integer masteryLevel;

    private Integer isImportant;

    private Boolean isDueToday;

    private String sortBy;

    private String sortOrder;
}
