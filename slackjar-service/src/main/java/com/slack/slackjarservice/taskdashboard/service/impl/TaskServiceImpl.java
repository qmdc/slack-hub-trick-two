package com.slack.slackjarservice.taskdashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.enumtype.taskdashboard.TaskDashboardPushEnum;
import com.slack.slackjarservice.common.enumtype.taskdashboard.TaskPriorityEnum;
import com.slack.slackjarservice.common.enumtype.taskdashboard.TaskStatusEnum;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.foundation.entity.SysUser;
import com.slack.slackjarservice.foundation.model.dto.SocketMessageDTO;
import com.slack.slackjarservice.foundation.service.SysUserService;
import com.slack.slackjarservice.foundation.socketio.BackendMessagePush;
import com.slack.slackjarservice.taskdashboard.dao.TaskDao;
import com.slack.slackjarservice.taskdashboard.entity.Task;
import com.slack.slackjarservice.taskdashboard.model.dto.BoardDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskDTO;
import com.slack.slackjarservice.taskdashboard.model.request.CreateTaskRequest;
import com.slack.slackjarservice.taskdashboard.model.request.TaskPageQuery;
import com.slack.slackjarservice.taskdashboard.model.request.UpdateTaskRequest;
import com.slack.slackjarservice.taskdashboard.model.request.UpdateTaskStatusRequest;
import com.slack.slackjarservice.taskdashboard.service.TaskCommentService;
import com.slack.slackjarservice.taskdashboard.service.TaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务表(Task)表服务实现类
 *
 * @author zhn
 */
@Slf4j
@Service("taskService")
public class TaskServiceImpl extends ServiceImpl<TaskDao, Task> implements TaskService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private TaskCommentService taskCommentService;

    @Resource
    private BackendMessagePush backendMessagePush;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDTO createTask(CreateTaskRequest request, Long creatorId) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task);
        task.setCreatorId(creatorId);
        if (request.getPriority() == null) {
            task.setPriority(TaskPriorityEnum.MEDIUM.getCode());
        }
        if (request.getSortOrder() == null) {
            task.setSortOrder(getNextSortOrder(request.getStatus()));
        }

        this.save(task);

        TaskDTO taskDTO = convertToDTO(task);

        broadcastTaskChange(TaskDashboardPushEnum.TASK_CREATED, taskDTO);

        return taskDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDTO updateTask(Long taskId, UpdateTaskRequest request) {
        Task task = this.getById(taskId);
        AssertUtil.notNull(task, ResponseEnum.DATA_NOT_EXISTS);

        if (StringUtils.hasText(request.getTitle())) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getAssigneeId() != null) {
            task.setAssigneeId(request.getAssigneeId());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getSortOrder() != null) {
            task.setSortOrder(request.getSortOrder());
        }

        this.updateById(task);

        TaskDTO taskDTO = convertToDTO(this.getById(taskId));

        broadcastTaskChange(TaskDashboardPushEnum.TASK_UPDATED, taskDTO);

        return taskDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskDTO updateTaskStatus(UpdateTaskStatusRequest request) {
        Task task = this.getById(request.getTaskId());
        AssertUtil.notNull(task, ResponseEnum.DATA_NOT_EXISTS);

        TaskStatusEnum oldStatus = TaskStatusEnum.fromCode(task.getStatus());
        TaskStatusEnum newStatus = TaskStatusEnum.fromCode(request.getTargetStatus());

        if (!oldStatus.equals(newStatus)) {
            task.setStatus(request.getTargetStatus());
        }

        if (request.getTargetSortOrder() != null) {
            task.setSortOrder(request.getTargetSortOrder());
        } else {
            task.setSortOrder(getNextSortOrder(request.getTargetStatus()));
        }

        this.updateById(task);

        TaskDTO taskDTO = convertToDTO(this.getById(request.getTaskId()));

        broadcastTaskChange(TaskDashboardPushEnum.TASK_STATUS_CHANGED, taskDTO);

        return taskDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long taskId) {
        Task task = this.getById(taskId);
        AssertUtil.notNull(task, ResponseEnum.DATA_NOT_EXISTS);

        this.removeById(taskId);

        Map<String, Object> deleteInfo = new HashMap<>();
        deleteInfo.put("taskId", taskId);
        deleteInfo.put("status", task.getStatus());

        broadcastTaskChange(TaskDashboardPushEnum.TASK_DELETED, deleteInfo);
    }

    @Override
    public TaskDTO getTaskById(Long taskId) {
        Task task = this.getById(taskId);
        AssertUtil.notNull(task, ResponseEnum.DATA_NOT_EXISTS);

        TaskDTO taskDTO = convertToDTO(task);

        taskDTO.setComments(taskCommentService.getCommentsByTaskId(taskId));

        return taskDTO;
    }

    @Override
    public PageResult<TaskDTO> pageQueryTasks(TaskPageQuery query) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getKeyword())) {
            queryWrapper.and(w -> w
                    .like(Task::getTitle, query.getKeyword())
                    .or()
                    .like(Task::getDescription, query.getKeyword()));
        }

        if (query.getStatus() != null) {
            queryWrapper.eq(Task::getStatus, query.getStatus());
        }

        if (query.getPriority() != null) {
            queryWrapper.eq(Task::getPriority, query.getPriority());
        }

        if (query.getAssigneeId() != null) {
            queryWrapper.eq(Task::getAssigneeId, query.getAssigneeId());
        }

        if (query.getCreatorId() != null) {
            queryWrapper.eq(Task::getCreatorId, query.getCreatorId());
        }

        queryWrapper.orderByAsc(Task::getSortOrder).orderByDesc(Task::getCreateTime);

        Page<Task> taskPage = this.page(
                new Page<>(query.getPageNo(), query.getPageSize()),
                queryWrapper
        );

        List<TaskDTO> taskItems = convertToDTOList(taskPage.getRecords());

        return PageResult.of(taskItems, taskPage.getTotal(), query.getPageNo(), query.getPageSize());
    }

    @Override
    public BoardDTO getBoardData(String keyword, Integer priority) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(w -> w
                    .like(Task::getTitle, keyword)
                    .or()
                    .like(Task::getDescription, keyword));
        }

        if (priority != null) {
            queryWrapper.eq(Task::getPriority, priority);
        }

        queryWrapper.orderByAsc(Task::getSortOrder).orderByDesc(Task::getCreateTime);

        List<Task> allTasks = this.list(queryWrapper);
        List<TaskDTO> allTaskDTOs = convertToDTOList(allTasks);

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setTodoTasks(filterTasksByStatus(allTaskDTOs, TaskStatusEnum.TODO.getCode()));
        boardDTO.setInProgressTasks(filterTasksByStatus(allTaskDTOs, TaskStatusEnum.IN_PROGRESS.getCode()));
        boardDTO.setDoneTasks(filterTasksByStatus(allTaskDTOs, TaskStatusEnum.DONE.getCode()));

        return boardDTO;
    }

    @Override
    public List<TaskDTO> getTasksByStatus(Integer status) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getStatus, status);
        queryWrapper.orderByAsc(Task::getSortOrder).orderByDesc(Task::getCreateTime);

        List<Task> tasks = this.list(queryWrapper);
        return convertToDTOList(tasks);
    }

    @Override
    public TaskDTO convertToDTO(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO taskDTO = new TaskDTO();
        BeanUtils.copyProperties(task, taskDTO);

        TaskStatusEnum statusEnum = TaskStatusEnum.fromCode(task.getStatus());
        taskDTO.setStatusDesc(statusEnum.getDesc());

        TaskPriorityEnum priorityEnum = TaskPriorityEnum.fromCode(task.getPriority());
        taskDTO.setPriorityDesc(priorityEnum.getDesc());

        if (task.getAssigneeId() != null) {
            SysUser assignee = sysUserService.getById(task.getAssigneeId());
            if (assignee != null) {
                taskDTO.setAssigneeName(assignee.getNickname());
            }
        }

        if (task.getCreatorId() != null) {
            SysUser creator = sysUserService.getById(task.getCreatorId());
            if (creator != null) {
                taskDTO.setCreatorName(creator.getNickname());
            }
        }

        return taskDTO;
    }

    @Override
    public List<TaskDTO> convertToDTOList(List<Task> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return new ArrayList<>();
        }

        Set<Long> userIds = new HashSet<>();
        for (Task task : tasks) {
            if (task.getAssigneeId() != null) {
                userIds.add(task.getAssigneeId());
            }
            if (task.getCreatorId() != null) {
                userIds.add(task.getCreatorId());
            }
        }

        Map<Long, SysUser> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<SysUser> users = sysUserService.listByIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        }

        List<TaskDTO> result = new ArrayList<>();
        for (Task task : tasks) {
            TaskDTO taskDTO = new TaskDTO();
            BeanUtils.copyProperties(task, taskDTO);

            TaskStatusEnum statusEnum = TaskStatusEnum.fromCode(task.getStatus());
            taskDTO.setStatusDesc(statusEnum.getDesc());

            TaskPriorityEnum priorityEnum = TaskPriorityEnum.fromCode(task.getPriority());
            taskDTO.setPriorityDesc(priorityEnum.getDesc());

            if (task.getAssigneeId() != null && userMap.containsKey(task.getAssigneeId())) {
                taskDTO.setAssigneeName(userMap.get(task.getAssigneeId()).getNickname());
            }

            if (task.getCreatorId() != null && userMap.containsKey(task.getCreatorId())) {
                taskDTO.setCreatorName(userMap.get(task.getCreatorId()).getNickname());
            }

            result.add(taskDTO);
        }

        return result;
    }

    private Integer getNextSortOrder(Integer status) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Task::getStatus, status);
        queryWrapper.orderByDesc(Task::getSortOrder);
        queryWrapper.last("LIMIT 1");

        Task lastTask = this.getOne(queryWrapper);
        if (lastTask != null && lastTask.getSortOrder() != null) {
            return lastTask.getSortOrder() + 1;
        }
        return 1;
    }

    private List<TaskDTO> filterTasksByStatus(List<TaskDTO> tasks, Integer status) {
        return tasks.stream()
                .filter(t -> Objects.equals(t.getStatus(), status))
                .collect(Collectors.toList());
    }

    private void broadcastTaskChange(TaskDashboardPushEnum pushType, Object content) {
        SocketMessageDTO message = new SocketMessageDTO(content, pushType.getCode());
        backendMessagePush.broadcastMessage(message);
    }
}
