package com.slack.slackjarservice.taskdashboard.controller;

import com.slack.slackjarservice.common.base.BaseController;
import com.slack.slackjarservice.common.enumtype.foundation.OperationEnum;
import com.slack.slackjarservice.common.response.ApiResponse;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.taskdashboard.model.dto.BoardDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskCommentDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskReminderDTO;
import com.slack.slackjarservice.taskdashboard.model.request.*;
import com.slack.slackjarservice.taskdashboard.service.TaskCommentService;
import com.slack.slackjarservice.taskdashboard.service.TaskReminderService;
import com.slack.slackjarservice.taskdashboard.service.TaskService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务看板控制器
 *
 * @author zhn
 */
@RestController
@RequestMapping("/task-dashboard")
public class TaskDashboardController extends BaseController {

    @Resource
    private TaskService taskService;

    @Resource
    private TaskCommentService taskCommentService;

    @Resource
    private TaskReminderService taskReminderService;

    /**
     * 获取看板数据
     *
     * @param keyword 搜索关键词
     * @param priority 优先级筛选
     * @return 看板数据
     */
    @GetMapping("/board")
    public ApiResponse<BoardDTO> getBoardData(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer priority) {
        BoardDTO boardData = taskService.getBoardData(keyword, priority);
        return success(boardData);
    }

    /**
     * 创建任务
     *
     * @param request 创建任务请求
     * @return 任务详情
     */
    @PostMapping("/task")
    public ApiResponse<TaskDTO> createTask(@Validated @RequestBody CreateTaskRequest request) {
        Long creatorId = getLoginUserId();
        TaskDTO task = taskService.createTask(request, creatorId);
        recordOperateLog(OperationEnum.USER_UPSERT, "创建任务:" + task.getTitle());
        return success(task);
    }

    /**
     * 更新任务
     *
     * @param taskId 任务ID
     * @param request 更新任务请求
     * @return 任务详情
     */
    @PutMapping("/task/{taskId}")
    public ApiResponse<TaskDTO> updateTask(
            @PathVariable Long taskId,
            @Validated @RequestBody UpdateTaskRequest request) {
        TaskDTO task = taskService.updateTask(taskId, request);
        recordOperateLog(OperationEnum.USER_UPSERT, "更新任务:" + taskId);
        return success(task);
    }

    /**
     * 更新任务状态（拖拽时使用）
     *
     * @param request 更新任务状态请求
     * @return 任务详情
     */
    @PostMapping("/task/update-status")
    public ApiResponse<TaskDTO> updateTaskStatus(@Validated @RequestBody UpdateTaskStatusRequest request) {
        TaskDTO task = taskService.updateTaskStatus(request);
        recordOperateLog(OperationEnum.USER_UPSERT, "更新任务状态:" + request.getTaskId());
        return success(task);
    }

    /**
     * 删除任务
     *
     * @param taskId 任务ID
     * @return 操作结果
     */
    @DeleteMapping("/task/{taskId}")
    public ApiResponse<Boolean> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        recordOperateLog(OperationEnum.USER_UPSERT, "删除任务:" + taskId);
        return success(true);
    }

    /**
     * 获取任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情
     */
    @GetMapping("/task/{taskId}")
    public ApiResponse<TaskDTO> getTaskById(@PathVariable Long taskId) {
        TaskDTO task = taskService.getTaskById(taskId);
        recordOperateLog(OperationEnum.USER_QUERY, "查询任务详情:" + taskId);
        return success(task);
    }

    /**
     * 分页查询任务
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @PostMapping("/task/page-query")
    public ApiResponse<PageResult<TaskDTO>> pageQueryTasks(@RequestBody TaskPageQuery query) {
        PageResult<TaskDTO> result = taskService.pageQueryTasks(query);
        recordOperateLog(OperationEnum.USER_QUERY, "分页查询任务列表");
        return success(result);
    }

    /**
     * 创建任务评论
     *
     * @param request 创建评论请求
     * @return 评论详情
     */
    @PostMapping("/comment")
    public ApiResponse<TaskCommentDTO> createComment(@Validated @RequestBody CreateTaskCommentRequest request) {
        Long userId = getLoginUserId();
        TaskCommentDTO comment = taskCommentService.createComment(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "创建任务评论:" + request.getTaskId());
        return success(comment);
    }

    /**
     * 获取任务的评论列表
     *
     * @param taskId 任务ID
     * @return 评论列表
     */
    @GetMapping("/comment/task/{taskId}")
    public ApiResponse<List<TaskCommentDTO>> getCommentsByTaskId(@PathVariable Long taskId) {
        List<TaskCommentDTO> comments = taskCommentService.getCommentsByTaskId(taskId);
        return success(comments);
    }

    /**
     * 删除任务评论
     *
     * @param commentId 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/comment/{commentId}")
    public ApiResponse<Boolean> deleteComment(@PathVariable Long commentId) {
        Long userId = getLoginUserId();
        taskCommentService.deleteComment(commentId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "删除任务评论:" + commentId);
        return success(true);
    }

    /**
     * 创建任务提醒
     *
     * @param request 创建提醒请求
     * @return 提醒详情
     */
    @PostMapping("/reminder")
    public ApiResponse<TaskReminderDTO> createReminder(@Validated @RequestBody CreateTaskReminderRequest request) {
        Long userId = getLoginUserId();
        TaskReminderDTO reminder = taskReminderService.createReminder(request, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "创建任务提醒:" + request.getTaskId());
        return success(reminder);
    }

    /**
     * 获取任务的提醒列表
     *
     * @param taskId 任务ID
     * @return 提醒列表
     */
    @GetMapping("/reminder/task/{taskId}")
    public ApiResponse<List<TaskReminderDTO>> getRemindersByTaskId(@PathVariable Long taskId) {
        Long userId = getLoginUserId();
        List<TaskReminderDTO> reminders = taskReminderService.getRemindersByTaskId(taskId, userId);
        return success(reminders);
    }

    /**
     * 删除任务提醒
     *
     * @param reminderId 提醒ID
     * @return 操作结果
     */
    @DeleteMapping("/reminder/{reminderId}")
    public ApiResponse<Boolean> deleteReminder(@PathVariable Long reminderId) {
        Long userId = getLoginUserId();
        taskReminderService.deleteReminder(reminderId, userId);
        recordOperateLog(OperationEnum.USER_UPSERT, "删除任务提醒:" + reminderId);
        return success(true);
    }
}
