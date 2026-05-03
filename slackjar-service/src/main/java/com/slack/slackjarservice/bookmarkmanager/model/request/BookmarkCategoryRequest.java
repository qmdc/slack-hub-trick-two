package com.slack.slackjarservice.bookmarkmanager.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookmarkCategoryRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String icon;

    private Integer sortOrder;
}