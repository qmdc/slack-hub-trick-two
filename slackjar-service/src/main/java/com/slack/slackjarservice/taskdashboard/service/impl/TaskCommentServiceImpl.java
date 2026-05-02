package com.slack.slackjarservice.taskdashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slack.slackjarservice.common.enumtype.foundation.ResponseEnum;
import com.slack.slackjarservice.common.enumtype.taskdashboard.TaskDashboardPushEnum;
import com.slack.slackjarservice.common.util.AssertUtil;
import com.slack.slackjarservice.foundation.entity.SysUser;
import com.slack.slackjarservice.foundation.model.dto.SocketMessageDTO;
import com.slack.slackjarservice.foundation.service.SysUserService;
import com.slack.slackjarservice.foundation.socketio.BackendMessagePush;
import com.slack.slackjarservice.taskdashboard.dao.TaskCommentDao;
import com.slack.slackjarservice.taskdashboard.entity.Task;
import com.slack.slackjarservice.taskdashboard.entity.TaskComment;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskCommentDTO;
import com.slack.slackjarservice.taskdashboard.model.request.CreateTaskCommentRequest;
import com.slack.slackjarservice.taskdashboard.service.TaskCommentService;
import com.slack.slackjarservice.taskdashboard.service.TaskService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务评论表(TaskComment)表服务实现类
 *
 * @author zhn
 */
@Slf4j
@Service("taskCommentService")
public class TaskCommentServiceImpl extends ServiceImpl<TaskCommentDao, TaskComment> implements TaskCommentService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private TaskService taskService;

    @Resource
    private BackendMessagePush backendMessagePush;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TaskCommentDTO createComment(CreateTaskCommentRequest request, Long userId) {
        Task task = taskService.getById(request.getTaskId());
        AssertUtil.notNull(task, ResponseEnum.DATA_NOT_EXISTS);

        TaskComment comment = new TaskComment();
        comment.setTaskId(request.getTaskId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());

        this.save(comment);

        TaskCommentDTO commentDTO = convertToDTO(comment);

        broadcastCommentChange(TaskDashboardPushEnum.TASK_COMMENT_ADDED, commentDTO);

        return commentDTO;
    }

    @Override
    public List<TaskCommentDTO> getCommentsByTaskId(Long taskId) {
        LambdaQueryWrapper<TaskComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskComment::getTaskId, taskId);
        queryWrapper.orderByDesc(TaskComment::getCreateTime);

        List<TaskComment> comments = this.list(queryWrapper);

        if (CollectionUtils.isEmpty(comments)) {
            return new ArrayList<>();
        }

        Set<Long> userIds = comments.stream()
                .map(TaskComment::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, SysUser> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<SysUser> users = sysUserService.listByIds(userIds);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        }

        List<TaskCommentDTO> result = new ArrayList<>();
        for (TaskComment comment : comments) {
            TaskCommentDTO dto = new TaskCommentDTO();
            BeanUtils.copyProperties(comment, dto);

            if (comment.getUserId() != null && userMap.containsKey(comment.getUserId())) {
                dto.setUserName(userMap.get(comment.getUserId()).getNickname());
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        TaskComment comment = this.getById(commentId);
        AssertUtil.notNull(comment, ResponseEnum.DATA_NOT_EXISTS);

        AssertUtil.isTrue(Objects.equals(comment.getUserId(), userId), ResponseEnum.NO_PERMISSION);

        this.removeById(commentId);
    }

    @Override
    public TaskCommentDTO convertToDTO(TaskComment comment) {
        if (comment == null) {
            return null;
        }

        TaskCommentDTO dto = new TaskCommentDTO();
        BeanUtils.copyProperties(comment, dto);

        if (comment.getUserId() != null) {
            SysUser user = sysUserService.getById(comment.getUserId());
            if (user != null) {
                dto.setUserName(user.getNickname());
            }
        }

        return dto;
    }

    private void broadcastCommentChange(TaskDashboardPushEnum pushType, Object content) {
        SocketMessageDTO message = new SocketMessageDTO(content, pushType.getCode());
        backendMessagePush.broadcastMessage(message);
    }
}
