package com.slack.slackjarservice.mediarecommend.model.request;

import lombok.Data;

@Data
public class ShareRequest {

    private String title;

    private Long expireTime;
}