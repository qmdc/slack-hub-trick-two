package com.slack.slackjarservice.bookmarkmanager.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookmarkCreateRequest {

    @NotBlank(message = "URL不能为空")
    private String url;

    private String title;

    private String description;

    private String tags;

    private Long categoryId;
}