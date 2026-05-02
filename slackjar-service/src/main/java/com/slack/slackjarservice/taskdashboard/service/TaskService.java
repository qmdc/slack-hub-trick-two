package com.slack.slackjarservice.taskdashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.slack.slackjarservice.common.response.PageResult;
import com.slack.slackjarservice.taskdashboard.entity.Task;
import com.slack.slackjarservice.taskdashboard.model.dto.BoardDTO;
import com.slack.slackjarservice.taskdashboard.model.dto.TaskDTO;
import com.slack.slackjarservice.taskdashboard.model.request.CreateTaskRequest;
import com.slack.slackjarservice.taskdashboard.model.request.TaskPageQuery;
import com.slack.slackjarservice.taskdashboard.model.request.UpdateTaskRequest;
import com.slack.slackjarservice.taskdashboard.model.request.UpdateTaskStatusRequest;

import java.util.List;

/**
 * 任务表(Task)表服务接口
 *
 * @author zhn
 */
public interface TaskService extends IService<Task> {

    /**
     * 创建任务
     *
     * @param request 创建任务请求
     * @param creatorId 创建人ID
     * @return 任务详情DTO
     */
    TaskDTO createTask(CreateTaskRequest request, Long creatorId);

    /**
     * 更新任务
     *
     * @param taskId 任务ID
     * @param request 更新任务请求
     * @return 任务详情DTO
     */
    TaskDTO updateTask(Long taskId, UpdateTaskRequest request);

    /**
     * 更新任务状态（拖拽时使用）
     *
     * @param request 更新任务状态请求
     * @return 任务详情DTO
     */
    TaskDTO updateTaskStatus(UpdateTaskStatusRequest request);

    /**
     * 删除任务
     *
     * @param taskId 任务ID
     */
    void deleteTask(Long taskId);

    /**
     * 根据ID获取任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情DTO
     */
    TaskDTO getTaskById(Long taskId);

    /**
     * 分页查询任务
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<TaskDTO> pageQueryTasks(TaskPageQuery query);

    /**
     * 获取看板数据（按状态分组）
     *
     * @param keyword 搜索关键词
     * @param priority 优先级筛选
     * @return 看板数据DTO
     */
    BoardDTO getBoardData(String keyword, Integer priority);

    /**
     * 根据状态获取任务列表
     *
     * @param status 任务状态
     * @return 任务列表
     */
    List<TaskDTO> getTasksByStatus(Integer status);

    /**
     * 转换实体为DTO
     *
     * @param task 任务实体
     * @return 任务DTO
     */
    TaskDTO convertToDTO(Task task);

    /**
     * 批量转换实体为DTO
     *
     * @param tasks 任务实体列表
     * @return 任务DTO列表
     */
    List<TaskDTO> convertToDTOList(List<Task> tasks);
}
