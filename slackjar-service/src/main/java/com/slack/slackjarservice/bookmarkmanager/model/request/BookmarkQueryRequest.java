package com.slack.slackjarservice.bookmarkmanager.model.request;

import com.slack.slackjarservice.common.base.BasePagination;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookmarkQueryRequest extends BasePagination {

    private String keyword;

    private Long categoryId;

    private String tagName;
}