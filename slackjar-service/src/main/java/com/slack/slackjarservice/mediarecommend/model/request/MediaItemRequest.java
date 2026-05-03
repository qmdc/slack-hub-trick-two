package com.slack.slackjarservice.mediarecommend.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

@Data
public class MediaItemRequest {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotNull(message = "类型不能为空")
    @Min(value = 1, message = "类型值无效")
    @Max(value = 2, message = "类型值无效")
    private Integer type;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;

    @Min(value = 1, message = "评分值无效")
    @Max(value = 5, message = "评分值无效")
    private Integer rating;

    private String review;

    private List<String> tags;

    private String coverUrl;

    private String author;

    private String year;
}