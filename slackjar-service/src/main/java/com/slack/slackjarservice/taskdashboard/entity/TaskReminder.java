package com.slack.slackjarservice.taskdashboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 任务提醒表(TaskReminder)表实体类
 *
 * @author zhn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("td_task_reminder")
public class TaskReminder extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 提醒时间（毫秒时间戳）
     */
    private Long reminderTime;

    /**
     * 是否已经提醒（0-未提醒，1-已提醒）
     */
    private Integer reminded;

    /**
     * 提醒消息
     */
    private String message;
}
