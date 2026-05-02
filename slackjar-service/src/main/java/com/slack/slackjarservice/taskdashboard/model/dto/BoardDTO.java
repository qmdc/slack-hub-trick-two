package com.slack.slackjarservice.taskdashboard.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 看板数据DTO
 */
@Data
public class BoardDTO implements Serializable {

    private List<TaskDTO> todoTasks;

    private List<TaskDTO> inProgressTasks;

    private List<TaskDTO> doneTasks;
}
