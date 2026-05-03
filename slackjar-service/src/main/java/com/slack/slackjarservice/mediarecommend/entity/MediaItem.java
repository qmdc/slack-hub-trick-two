package com.slack.slackjarservice.mediarecommend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("media_item")
public class MediaItem extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private Integer type;

    private Integer status;

    private Integer rating;

    private String review;

    private String tags;

    private String coverUrl;

    private String author;

    private String year;
}