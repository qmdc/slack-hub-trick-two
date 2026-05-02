import React, {useCallback, useEffect, useState} from 'react';
import {
    Modal,
    Avatar,
    Tag,
    Select,
    DatePicker,
    Input,
    Button,
    List,
    message,
    Descriptions,
    Divider
} from 'antd';
import {
    ClockCircleOutlined,
    UserOutlined,
    DeleteOutlined,
    PlusOutlined
} from '@ant-design/icons';
import dayjs from 'dayjs';
import type {Dayjs} from 'dayjs';
import type {
    TaskDetail,
    TaskComment,
    TaskReminder,
    CreateTaskCommentRequest,
    CreateTaskReminderRequest,
    UpdateTaskRequest
} from '../../apis/modules/taskDashboard';
import {
    TaskPriority,
    TaskStatus,
    getTaskById,
    updateTask,
    createTaskComment,
    getTaskComments,
    deleteTask,
    createTaskReminder,
    getTaskReminders,
    deleteTaskReminder,
    getTaskComments as fetchTaskComments
} from '../../apis/modules/taskDashboard';
import styles from './index.module.scss';

const {TextArea} = Input;
const {RangePicker} = DatePicker;

/**
 * 任务详情模态框组件
 * @param open - 是否打开
 * @param taskId - 任务ID
 * @param onCancel - 取消事件
 * @param onSuccess - 成功事件（更新、删除后）
 */
interface TaskDetailModalProps {
    open: boolean;
    taskId: number | null;
    onCancel: () => void;
    onSuccess: () => void;
}

const TaskDetailModal: React.FC<TaskDetailModalProps> = ({open, taskId, onCancel, onSuccess}) => {
    const [loading, setLoading] = useState(false);
    const [task, setTask] = useState<TaskDetail | null>(null);
    const [comments, setComments] = useState<TaskComment[]>([]);
    const [reminders, setReminders] = useState<TaskReminder[]>([]);
    const [newComment, setNewComment] = useState('');
    const [reminderDate, setReminderDate] = useState<Dayjs | null>(null);
    const [reminderMessage, setReminderMessage] = useState('');
    const [editMode, setEditMode] = useState(false);
    const [editForm, setEditForm] = useState<UpdateTaskRequest>({});

    const fetchTaskDetail = useCallback(() => {
        if (!taskId) {
            return;
        }
        setLoading(true);
        getTaskById(taskId)
            .then((res) => {
                if (res.code === 200 && res.data) {
                    setTask(res.data);
                    setEditForm({
                        title: res.data.title,
                        description: res.data.description,
                        status: res.data.status,
                        priority: res.data.priority,
                        assigneeId: res.data.assigneeId,
                        dueDate: res.data.dueDate
                    });
                }
            })
            .finally(() => setLoading(false));
    }, [taskId]);

    const fetchComments = useCallback(() => {
        if (!taskId) {
            return;
        }
        fetchTaskComments(taskId)
            .then((res) => {
                if (res.code === 200 && res.data) {
                    setComments(res.data);
                }
            });
    }, [taskId]);

    const fetchReminders = useCallback(() => {
        if (!taskId) {
            return;
        }
        getTaskReminders(taskId)
            .then((res) => {
                if (res.code === 200 && res.data) {
                    setReminders(res.data);
                }
            });
    }, [taskId]);

    useEffect(() => {
        if (open && taskId) {
            fetchTaskDetail();
            fetchComments();
            fetchReminders();
            setEditMode(false);
            setNewComment('');
            setReminderDate(null);
            setReminderMessage('');
        }
    }, [open, taskId, fetchTaskDetail, fetchComments, fetchReminders]);

    const handleSaveEdit = () => {
        if (!taskId || !task) {
            return;
        }
        setLoading(true);
        updateTask(taskId, editForm)
            .then((res) => {
                if (res.code === 200) {
                    message.success('更新成功');
                    setEditMode(false);
                    fetchTaskDetail();
                    onSuccess();
                }
            })
            .finally(() => setLoading(false));
    };

    const handleDeleteTask = () => {
        if (!taskId) {
            return;
        }
        Modal.confirm({
            title: '确认删除',
            content: '确定要删除这个任务吗？删除后无法恢复。',
            okText: '确定',
            cancelText: '取消',
            okType: 'danger',
            onOk: () => {
                return deleteTask(taskId)
                    .then((res) => {
                        if (res.code === 200) {
                            message.success('删除成功');
                            onCancel();
                            onSuccess();
                        }
                    });
            }
        });
    };

    const handleAddComment = () => {
        if (!taskId || !newComment.trim()) {
            return;
        }
        const request: CreateTaskCommentRequest = {
            taskId,
            content: newComment.trim()
        };
        createTaskComment(request)
            .then((res) => {
                if (res.code === 200) {
                    message.success('评论已添加');
                    setNewComment('');
                    fetchComments();
                    onSuccess();
                }
            });
    };

    const handleAddReminder = () => {
        if (!taskId || !reminderDate) {
            return;
        }
        const request: CreateTaskReminderRequest = {
            taskId,
            reminderTime: reminderDate.valueOf(),
            message: reminderMessage || undefined
        };
        createTaskReminder(request)
            .then((res) => {
                if (res.code === 200) {
                    message.success('提醒已设置');
                    setReminderDate(null);
                    setReminderMessage('');
                    fetchReminders();
                    onSuccess();
                }
            });
    };

    const handleDeleteReminder = (reminderId: number) => {
        deleteTaskReminder(reminderId)
            .then((res) => {
                if (res.code === 200) {
                    message.success('提醒已删除');
                    fetchReminders();
                }
            });
    };

    const getPriorityColor = (priority: number): string => {
        switch (priority) {
            case TaskPriority.LOW:
                return 'success';
            case TaskPriority.MEDIUM:
                return 'processing';
            case TaskPriority.HIGH:
                return 'warning';
            case TaskPriority.URGENT:
                return 'error';
            default:
                return 'default';
        }
    };

    const getStatusColor = (status: number): string => {
        switch (status) {
            case TaskStatus.TODO:
                return 'default';
            case TaskStatus.IN_PROGRESS:
                return 'processing';
            case TaskStatus.DONE:
                return 'success';
            default:
                return 'default';
        }
    };

    return (
        <Modal
            className={styles.detailModal}
            open={open}
            title={task?.title || '任务详情'}
            width={680}
            onCancel={onCancel}
            loading={loading}
            footer={[
                <Button key="cancel" onClick={onCancel}>
                    关闭
                </Button>,
                <Button
                    key="delete"
                    danger
                    icon={<DeleteOutlined/>}
                    onClick={handleDeleteTask}
                >
                    删除任务
                </Button>,
                editMode ? (
                    <Button key="save" type="primary" onClick={handleSaveEdit} loading={loading}>
                        保存修改
                    </Button>
                ) : (
                    <Button key="edit" type="primary" onClick={() => setEditMode(true)}>
                        编辑
                    </Button>
                )
            ]}
        >
            {task && (
                <div>
                    {editMode ? (
                        <div style={{marginBottom: 20}}>
                            <div style={{marginBottom: 12}}>
                                <Input
                                    value={editForm.title}
                                    onChange={(e) => setEditForm({...editForm, title: e.target.value})}
                                    placeholder="任务标题"
                                    size="large"
                                />
                            </div>
                            <div style={{marginBottom: 12}}>
                                <TextArea
                                    value={editForm.description}
                                    onChange={(e) => setEditForm({...editForm, description: e.target.value})}
                                    placeholder="任务描述"
                                    rows={3}
                                />
                            </div>
                            <div style={{display: 'flex', gap: 12, flexWrap: 'wrap'}}>
                                <Select
                                    value={editForm.status}
                                    onChange={(v) => setEditForm({...editForm, status: v})}
                                    style={{width: 120}}
                                    options={[
                                        {value: TaskStatus.TODO, label: '待办'},
                                        {value: TaskStatus.IN_PROGRESS, label: '进行中'},
                                        {value: TaskStatus.DONE, label: '已完成'}
                                    ]}
                                />
                                <Select
                                    value={editForm.priority}
                                    onChange={(v) => setEditForm({...editForm, priority: v})}
                                    style={{width: 120}}
                                    options={[
                                        {value: TaskPriority.LOW, label: '低'},
                                        {value: TaskPriority.MEDIUM, label: '中'},
                                        {value: TaskPriority.HIGH, label: '高'},
                                        {value: TaskPriority.URGENT, label: '紧急'}
                                    ]}
                                />
                                <DatePicker
                                    value={editForm.dueDate ? dayjs(editForm.dueDate) : null}
                                    onChange={(date) => setEditForm({
                                        ...editForm,
                                        dueDate: date ? date.valueOf() : undefined
                                    })}
                                    showTime
                                    placeholder="截止日期"
                                />
                            </div>
                        </div>
                    ) : (
                        <div style={{marginBottom: 20}}>
                            <div style={{display: 'flex', alignItems: 'center', gap: 12, marginBottom: 12}}>
                                <Tag color={getStatusColor(task.status)}>
                                    {task.statusDesc}
                                </Tag>
                                <Tag color={getPriorityColor(task.priority)}>
                                    优先级: {task.priorityDesc}
                                </Tag>
                            </div>
                            {task.description && (
                                <div style={{marginBottom: 12, color: '#595959'}}>
                                    {task.description}
                                </div>
                            )}
                            <Descriptions column={2} size="small" bordered>
                                <Descriptions.Item label="负责人">
                                    {task.assigneeName || '未分配'}
                                </Descriptions.Item>
                                <Descriptions.Item label="创建人">
                                    {task.creatorName || '-'}
                                </Descriptions.Item>
                                <Descriptions.Item label="截止日期">
                                    {task.dueDate ? dayjs(task.dueDate).format('YYYY-MM-DD HH:mm') : '未设置'}
                                </Descriptions.Item>
                                <Descriptions.Item label="创建时间">
                                    {dayjs(task.createTime).format('YYYY-MM-DD HH:mm')}
                                </Descriptions.Item>
                            </Descriptions>
                        </div>
                    )}

                    <Divider/>

                    <div className={styles.detailSection}>
                        <div className={styles.detailSectionTitle}>评论 ({comments.length})</div>
                        <div className={styles.commentsList}>
                            {comments.length === 0 ? (
                                <div style={{textAlign: 'center', color: '#bfbfbf', padding: 20}}>
                                    暂无评论
                                </div>
                            ) : (
                                comments.map((comment) => (
                                    <div key={comment.id} className={styles.commentItem}>
                                        <Avatar
                                            size={32}
                                            icon={<UserOutlined/>}
                                            style={{backgroundColor: '#1890ff'}}
                                        >
                                            {comment.userName?.charAt(0)}
                                        </Avatar>
                                        <div className={styles.commentContent}>
                                            <div className={styles.commentHeader}>
                                                <span className={styles.commentAuthor}>{comment.userName}</span>
                                                <span className={styles.commentTime}>
                                                    {dayjs(comment.createTime).format('YYYY-MM-DD HH:mm')}
                                                </span>
                                            </div>
                                            <div className={styles.commentText}>{comment.content}</div>
                                        </div>
                                    </div>
                                ))
                            )}
                        </div>
                        <div className={styles.commentInputArea}>
                            <TextArea
                                value={newComment}
                                onChange={(e) => setNewComment(e.target.value)}
                                placeholder="添加评论..."
                                rows={2}
                                style={{flex: 1}}
                            />
                            <Button type="primary" onClick={handleAddComment}>
                                发送
                            </Button>
                        </div>
                    </div>

                    <Divider/>

                    <div className={styles.detailSection}>
                        <div className={styles.detailSectionTitle}>提醒</div>
                        {reminders.length > 0 && (
                            <List
                                size="small"
                                dataSource={reminders}
                                renderItem={(item) => (
                                    <List.Item
                                        actions={[
                                            <Button
                                                key="delete"
                                                type="text"
                                                danger
                                                size="small"
                                                icon={<DeleteOutlined/>}
                                                onClick={() => handleDeleteReminder(item.id)}
                                            />
                                        ]}
                                    >
                                        <List.Item.Meta
                                            avatar={<ClockCircleOutlined style={{color: '#1890ff'}}/>}
                                            title={item.message || '任务提醒'}
                                            description={dayjs(item.reminderTime).format('YYYY-MM-DD HH:mm')}
                                        />
                                    </List.Item>
                                )}
                            />
                        )}
                        <div style={{display: 'flex', gap: 8, marginTop: 12}}>
                            <DatePicker
                                value={reminderDate}
                                onChange={setReminderDate}
                                showTime
                                placeholder="选择提醒时间"
                                style={{flex: 1}}
                            />
                            <Input
                                value={reminderMessage}
                                onChange={(e) => setReminderMessage(e.target.value)}
                                placeholder="提醒消息（可选）"
                                style={{width: 180}}
                            />
                            <Button
                                type="primary"
                                icon={<PlusOutlined/>}
                                onClick={handleAddReminder}
                                disabled={!reminderDate}
                            >
                                添加提醒
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </Modal>
    );
};

export default TaskDetailModal;
