package com.slack.slackjarservice.emotionchat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("comfort_message")
public class ComfortMessage extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String emotionType;

    private String messageType;

    private String content;

    private Integer priority;
}