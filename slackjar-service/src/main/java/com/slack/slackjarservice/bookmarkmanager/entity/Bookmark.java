package com.slack.slackjarservice.bookmarkmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bookmark")
public class Bookmark extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String url;

    private String title;

    private String faviconUrl;

    private String description;

    private String tags;

    private Long categoryId;
}