package com.slack.slackjarservice.taskdashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.taskdashboard.entity.TaskComment;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskCommentDTO;
import com.slack.slackjarservice.taskdashboard.model.request.CreateTaskCommentRequest;

import java.util.List;

/**
 * 任务评论表(TaskComment)表服务接口
 *
 * @author zhn
 */
public interface TaskCommentService extends IService<TaskComment> {

    /**
     * 创建任务评论
     *
     * @param request 创建评论请求
     * @param userId 用户ID
     * @return 评论DTO
     */
    TaskCommentDTO createComment(CreateTaskCommentRequest request, Long userId);

    /**
     * 获取任务的评论列表
     *
     * @param taskId 任务ID
     * @return 评论列表
     */
    List<TaskCommentDTO> getCommentsByTaskId(Long taskId);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId 操作人ID
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 转换实体为DTO
     *
     * @param comment 评论实体
     * @return 评论DTO
     */
    TaskCommentDTO convertToDTO(TaskComment comment);
}
