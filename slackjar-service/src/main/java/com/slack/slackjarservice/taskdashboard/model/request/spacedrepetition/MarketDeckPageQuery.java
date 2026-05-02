package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 市场卡组分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MarketDeckPageQuery extends BasePagination {

    private String keyword;

    private List<String> tags;

    private Long userId;

    private String sortBy;

    private String sortOrder;
}
