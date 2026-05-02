package com.slack.slackjarservice.taskdashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.taskdashboard.entity.TaskReminder;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskReminderDTO;
import com.slack.slackjarservice.taskdashboard.model.request.CreateTaskReminderRequest;

import java.util.List;

/**
 * 任务提醒表(TaskReminder)表服务接口
 *
 * @author zhn
 */
public interface TaskReminderService extends IService<TaskReminder> {

    /**
     * 创建任务提醒
     *
     * @param request 创建提醒请求
     * @param userId 用户ID
     * @return 提醒DTO
     */
    TaskReminderDTO createReminder(CreateTaskReminderRequest request, Long userId);

    /**
     * 获取任务的提醒列表
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 提醒列表
     */
    List<TaskReminderDTO> getRemindersByTaskId(Long taskId, Long userId);

    /**
     * 删除提醒
     *
     * @param reminderId 提醒ID
     * @param userId 操作人ID
     */
    void deleteReminder(Long reminderId, Long userId);

    /**
     * 标记提醒已提醒
     *
     * @param reminderId 提醒ID
     */
    void markAsReminded(Long reminderId);

    /**
     * 转换实体为DTO
     *
     * @param reminder 提醒实体
     * @return 提醒DTO
     */
    TaskReminderDTO convertToDTO(TaskReminder reminder);
}
