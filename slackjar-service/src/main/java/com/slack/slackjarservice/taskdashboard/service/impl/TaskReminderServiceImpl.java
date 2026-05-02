package com.slack.slackjarservice.taskdashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.enumtype.taskdashboard.TaskDashboardPushEnum;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.foundation.model.dto.SocketMessageDTO;
import com.slack.slackjarservice.foundation.socketio.BackendMessagePush;
import com.slack.slackjarservice.taskdashboard.dao.TaskReminderDao;
import com.slack.slackjarservice.taskdashboard.entity.Task;
import com.slack.slackjarservice.taskdashboard.entity.TaskReminder;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskReminderDTO;
import com.slack.slackjarservice.taskdashboard.model.request.CreateTaskReminderRequest;
import com.slack.slackjarservice.taskdashboard.service.TaskReminderService;
import com.slack.slackjarservice.taskdashboard.service.TaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 任务提醒表(TaskReminder)表服务实现类
 *
 * @author zhn
 */
@Slf4j
@Service("taskReminderService")
public class TaskReminderServiceImpl extends ServiceImpl<TaskReminderDao, TaskReminder> implements TaskReminderService {

    @Resource
    private TaskService taskService;

    @Resource
    private BackendMessagePush backendMessagePush;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskReminderDTO createReminder(CreateTaskReminderRequest request, Long userId) {
        Task task = taskService.getById(request.getTaskId());
        AssertUtil.notNull(task, ResponseEnum.DATA_NOT_EXISTS);

        TaskReminder reminder = new TaskReminder();
        reminder.setTaskId(request.getTaskId());
        reminder.setUserId(userId);
        reminder.setReminderTime(request.getReminderTime());
        reminder.setReminded(0);
        reminder.setMessage(request.getMessage());

        this.save(reminder);

        TaskReminderDTO reminderDTO = convertToDTO(reminder);

        broadcastReminderChange(TaskDashboardPushEnum.TASK_REMINDER_SET, reminderDTO);

        return reminderDTO;
    }

    @Override
    public List<TaskReminderDTO> getRemindersByTaskId(Long taskId, Long userId) {
        LambdaQueryWrapper<TaskReminder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskReminder::getTaskId, taskId);
        queryWrapper.eq(TaskReminder::getUserId, userId);
        queryWrapper.orderByAsc(TaskReminder::getReminderTime);

        List<TaskReminder> reminders = this.list(queryWrapper);

        if (CollectionUtils.isEmpty(reminders)) {
            return new ArrayList<>();
        }

        List<TaskReminderDTO> result = new ArrayList<>();
        for (TaskReminder reminder : reminders) {
            TaskReminderDTO dto = new TaskReminderDTO();
            BeanUtils.copyProperties(reminder, dto);
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReminder(Long reminderId, Long userId) {
        TaskReminder reminder = this.getById(reminderId);
        AssertUtil.notNull(reminder, ResponseEnum.DATA_NOT_EXISTS);

        AssertUtil.isTrue(Objects.equals(reminder.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        this.removeById(reminderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsReminded(Long reminderId) {
        TaskReminder reminder = this.getById(reminderId);
        if (reminder != null) {
            reminder.setReminded(1);
            this.updateById(reminder);
        }
    }

    @Override
    public TaskReminderDTO convertToDTO(TaskReminder reminder) {
        if (reminder == null) {
            return null;
        }

        TaskReminderDTO dto = new TaskReminderDTO();
        BeanUtils.copyProperties(reminder, dto);
        return dto;
    }

    private void broadcastReminderChange(TaskDashboardPushEnum pushType, Object content) {
        SocketMessageDTO message = new SocketMessageDTO(content, pushType.getCode());
        backendMessagePush.broadcastMessage(message);
    }
}
