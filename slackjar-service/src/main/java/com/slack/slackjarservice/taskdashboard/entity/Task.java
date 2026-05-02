package com.slack.slackjarservice.taskdashboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.slack.slackjarservice.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务表(Task)表实体类
 *
 * @author zhn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("td_task")
public class Task extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务状态（0-待办，1-进行中，2-已完成）
     */
    private Integer status;

    /**
     * 任务优先级（0-低，1-中，2-高，3-紧急）
     */
    private Integer priority;

    /**
     * 负责人ID
     */
    private Long assigneeId;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 截止日期（毫秒时间戳）
     */
    private Long dueDate;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
