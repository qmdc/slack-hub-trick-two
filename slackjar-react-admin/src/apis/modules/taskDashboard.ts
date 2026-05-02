import request from '../request'
import type {PageResult, ResponseData} from './types'

/**
 * 获取看板数据
 */
export function getBoardData(keyword?: string, priority?: number): Promise<ResponseData<BoardData>> {
    const params: Record<string, any> = {}
    if (keyword) {
        params.keyword = keyword
    }
    if (priority !== undefined) {
        params.priority = priority
    }
    return request.get('/task-dashboard/board', {params})
}

/**
 * 创建任务
 */
export function createTask(data: CreateTaskRequest): Promise<ResponseData<TaskItem>> {
    return request.post('/task-dashboard/task', data)
}

/**
 * 更新任务
 */
export function updateTask(taskId: number, data: UpdateTaskRequest): Promise<ResponseData<TaskItem>> {
    return request.put(`/task-dashboard/task/${taskId}`, data)
}

/**
 * 更新任务状态（拖拽时使用）
 */
export function updateTaskStatus(data: UpdateTaskStatusRequest): Promise<ResponseData<TaskItem>> {
    return request.post('/task-dashboard/task/update-status', data)
}

/**
 * 删除任务
 */
export function deleteTask(taskId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/task-dashboard/task/${taskId}`)
}

/**
 * 获取任务详情
 */
export function getTaskById(taskId: number): Promise<ResponseData<TaskDetail>> {
    return request.get(`/task-dashboard/task/${taskId}`)
}

/**
 * 分页查询任务
 */
export function pageQueryTasks(data: TaskPageQuery): Promise<ResponseData<PageResult<TaskItem>>> {
    return request.post('/task-dashboard/task/page-query', data)
}

/**
 * 创建任务评论
 */
export function createTaskComment(data: CreateTaskCommentRequest): Promise<ResponseData<TaskComment>> {
    return request.post('/task-dashboard/comment', data)
}

/**
 * 获取任务评论列表
 */
export function getTaskComments(taskId: number): Promise<ResponseData<TaskComment[]>> {
    return request.get(`/task-dashboard/comment/task/${taskId}`)
}

/**
 * 删除任务评论
 */
export function deleteTaskComment(commentId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/task-dashboard/comment/${commentId}`)
}

/**
 * 创建任务提醒
 */
export function createTaskReminder(data: CreateTaskReminderRequest): Promise<ResponseData<TaskReminder>> {
    return request.post('/task-dashboard/reminder', data)
}

/**
 * 获取任务提醒列表
 */
export function getTaskReminders(taskId: number): Promise<ResponseData<TaskReminder[]>> {
    return request.get(`/task-dashboard/reminder/task/${taskId}`)
}

/**
 * 删除任务提醒
 */
export function deleteTaskReminder(reminderId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/task-dashboard/reminder/${reminderId}`)
}

// ============================================
// 类型定义
// ============================================

/**
 * 任务状态枚举
 */
export enum TaskStatus {
    TODO = 0,
    IN_PROGRESS = 1,
    DONE = 2
}

/**
 * 任务优先级枚举
 */
export enum TaskPriority {
    LOW = 0,
    MEDIUM = 1,
    HIGH = 2,
    URGENT = 3
}

/**
 * 看板数据
 */
export interface BoardData {
    todoTasks: TaskItem[]
    inProgressTasks: TaskItem[]
    doneTasks: TaskItem[]
}

/**
 * 任务列表项
 */
export interface TaskItem {
    id: number
    title: string
    description?: string
    status: number
    statusDesc: string
    priority: number
    priorityDesc: string
    assigneeId?: number
    assigneeName?: string
    assigneeAvatarUrl?: string
    creatorId: number
    creatorName?: string
    dueDate?: number
    sortOrder: number
    createTime: number
    updateTime: number
}

/**
 * 任务详情
 */
export interface TaskDetail extends TaskItem {
    comments?: TaskComment[]
    reminders?: TaskReminder[]
}

/**
 * 任务评论
 */
export interface TaskComment {
    id: number
    taskId: number
    userId: number
    userName?: string
    userAvatarUrl?: string
    content: string
    createTime: number
}

/**
 * 任务提醒
 */
export interface TaskReminder {
    id: number
    taskId: number
    userId: number
    reminderTime: number
    reminded: number
    message?: string
}

/**
 * 创建任务请求
 */
export interface CreateTaskRequest {
    title: string
    description?: string
    status: number
    priority?: number
    assigneeId?: number
    dueDate?: number
    sortOrder?: number
}

/**
 * 更新任务请求
 */
export interface UpdateTaskRequest {
    title?: string
    description?: string
    status?: number
    priority?: number
    assigneeId?: number
    dueDate?: number
    sortOrder?: number
}

/**
 * 更新任务状态请求
 */
export interface UpdateTaskStatusRequest {
    taskId: number
    targetStatus: number
    targetSortOrder?: number
}

/**
 * 任务分页查询请求
 */
export interface TaskPageQuery {
    pageNo?: number
    pageSize?: number
    keyword?: string
    status?: number
    priority?: number
    assigneeId?: number
    creatorId?: number
}

/**
 * 创建任务评论请求
 */
export interface CreateTaskCommentRequest {
    taskId: number
    content: string
}

/**
 * 创建任务提醒请求
 */
export interface CreateTaskReminderRequest {
    taskId: number
    reminderTime: number
    message?: string
}

/**
 * WebSocket任务看板推送类型
 */
export enum TaskDashboardPushType {
    TASK_CREATED = 'TASK_CREATED',
    TASK_UPDATED = 'TASK_UPDATED',
    TASK_DELETED = 'TASK_DELETED',
    TASK_STATUS_CHANGED = 'TASK_STATUS_CHANGED',
    TASK_COMMENT_ADDED = 'TASK_COMMENT_ADDED',
    TASK_REMINDER_SET = 'TASK_REMINDER_SET',
    BOARD_REFRESH = 'BOARD_REFRESH'
}
