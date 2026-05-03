package com.slack.slackjarservice.bookmarkmanager.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookmarkTagRequest {

    @NotBlank(message = "标签名称不能为空")
    private String name;

    private String color;
}