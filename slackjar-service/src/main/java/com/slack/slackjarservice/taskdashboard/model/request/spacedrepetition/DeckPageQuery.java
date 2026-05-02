package com.slack.slackjarservice.taskdashboard.model.request.spacedrepetition;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 卡组分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeckPageQuery extends BasePagination {

    private String keyword;

    private List<Long> tagIds;

    private Integer isPublic;

    private String sortBy;

    private String sortOrder;
}
