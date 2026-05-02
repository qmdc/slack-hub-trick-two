import React, {useCallback, useEffect, useState, useRef} from 'react';
import {
    Input,
    Select,
    Button,
    message,
    Spin,
    Badge
} from 'antd';
import {
    SearchOutlined,
    PlusOutlined,
    ReloadOutlined
} from '@ant-design/icons';
import TaskCard from './TaskCard';
import TaskDetailModal from './TaskDetailModal';
import CreateTaskModal from './CreateTaskModal';
import type {
    BoardData,
    TaskItem,
    TaskStatus
} from '../../../apis/modules/taskDashboard';
import {
    TaskPriority,
    TaskDashboardPushType,
    getBoardData,
    updateTaskStatus
} from '../../../apis/modules/taskDashboard';
import {
    socketManager,
    type SocketMessageDTO
} from '../../../socketio';
import styles from './index.module.scss';
import globalStyles from '../../global.module.scss';

const {Search} = Input;

/**
 * 任务看板页面组件
 */
const TaskDashboard: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [boardData, setBoardData] = useState<BoardData>({
        todoTasks: [],
        inProgressTasks: [],
        doneTasks: []
    });
    const [keyword, setKeyword] = useState('');
    const [priorityFilter, setPriorityFilter] = useState<number | undefined>(undefined);
    const [detailModalOpen, setDetailModalOpen] = useState(false);
    const [selectedTaskId, setSelectedTaskId] = useState<number | null>(null);
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [createDefaultStatus, setCreateDefaultStatus] = useState<number>(0);
    const [dragOverColumn, setDragOverColumn] = useState<string | null>(null);
    const [draggingTask, setDraggingTask] = useState<TaskItem | null>(null);

    const boardDataRef = useRef<BoardData>(boardData);

    useEffect(() => {
        boardDataRef.current = boardData;
    }, [boardData]);

    const fetchBoardData = useCallback(() => {
        setLoading(true);
        getBoardData(keyword || undefined, priorityFilter)
            .then((res) => {
                if (res.code === 200 && res.data) {
                    setBoardData(res.data);
                }
            })
            .finally(() => setLoading(false));
    }, [keyword, priorityFilter]);

    useEffect(() => {
        fetchBoardData();
    }, [fetchBoardData]);

    const handleWebSocketMessage = useCallback((message: SocketMessageDTO) => {
        const {bizType, content} = message;

        switch (bizType) {
            case TaskDashboardPushType.TASK_CREATED:
                const newTask = content as TaskItem;
                setBoardData((prev) => {
                    const newData = {...prev};
                    switch (newTask.status) {
                        case 0:
                            newData.todoTasks = [...prev.todoTasks, newTask];
                            break;
                        case 1:
                            newData.inProgressTasks = [...prev.inProgressTasks, newTask];
                            break;
                        case 2:
                            newData.doneTasks = [...prev.doneTasks, newTask];
                            break;
                    }
                    return newData;
                });
                message.info('有新任务被创建');
                break;

            case TaskDashboardPushType.TASK_UPDATED:
            case TaskDashboardPushType.TASK_STATUS_CHANGED:
                const updatedTask = content as TaskItem;
                setBoardData((prev) => {
                    const removeTaskFromColumn = (tasks: TaskItem[]) =>
                        tasks.filter((t) => t.id !== updatedTask.id);

                    const newData = {
                        todoTasks: removeTaskFromColumn(prev.todoTasks),
                        inProgressTasks: removeTaskFromColumn(prev.inProgressTasks),
                        doneTasks: removeTaskFromColumn(prev.doneTasks)
                    };

                    switch (updatedTask.status) {
                        case 0:
                            newData.todoTasks = [...newData.todoTasks, updatedTask];
                            break;
                        case 1:
                            newData.inProgressTasks = [...newData.inProgressTasks, updatedTask];
                            break;
                        case 2:
                            newData.doneTasks = [...newData.doneTasks, updatedTask];
                            break;
                    }

                    return newData;
                });
                break;

            case TaskDashboardPushType.TASK_DELETED:
                const deleteInfo = content as { taskId: number; status: number };
                setBoardData((prev) => {
                    const newData = {...prev};
                    const filterFn = (t: TaskItem) => t.id !== deleteInfo.taskId;
                    switch (deleteInfo.status) {
                        case 0:
                            newData.todoTasks = prev.todoTasks.filter(filterFn);
                            break;
                        case 1:
                            newData.inProgressTasks = prev.inProgressTasks.filter(filterFn);
                            break;
                        case 2:
                            newData.doneTasks = prev.doneTasks.filter(filterFn);
                            break;
                    }
                    return newData;
                });
                break;

            case TaskDashboardPushType.BOARD_REFRESH:
                fetchBoardData();
                break;
        }
    }, [fetchBoardData]);

    useEffect(() => {
        const pushTypes = Object.values(TaskDashboardPushType);
        pushTypes.forEach((type) => {
            socketManager.registerHandler(type, handleWebSocketMessage);
        });

        return () => {
            pushTypes.forEach((type) => {
                socketManager.registerHandler(type, handleWebSocketMessage);
            });
        };
    }, [handleWebSocketMessage]);

    const handleSearch = (value: string) => {
        setKeyword(value);
    };

    const handlePriorityChange = (value: number | undefined) => {
        setPriorityFilter(value);
    };

    const handleRefresh = () => {
        fetchBoardData();
    };

    const handleCreateTask = (status: number) => {
        setCreateDefaultStatus(status);
        setCreateModalOpen(true);
    };

    const handleTaskClick = (task: TaskItem) => {
        setSelectedTaskId(task.id);
        setDetailModalOpen(true);
    };

    const handleCreateSuccess = (task: TaskItem) => {
        setCreateModalOpen(false);
        fetchBoardData();
    };

    const handleDetailSuccess = () => {
        fetchBoardData();
    };

    const handleDragStart = (e: React.DragEvent, task: TaskItem) => {
        setDraggingTask(task);
        e.dataTransfer.effectAllowed = 'move';
    };

    const handleDragEnd = () => {
        setDraggingTask(null);
        setDragOverColumn(null);
    };

    const handleDragOver = (e: React.DragEvent, column: string) => {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'move';
        setDragOverColumn(column);
    };

    const handleDragLeave = () => {
        setDragOverColumn(null);
    };

    const handleDrop = async (e: React.DragEvent, targetStatus: number) => {
        e.preventDefault();

        if (!draggingTask || draggingTask.status === targetStatus) {
            setDragOverColumn(null);
            return;
        }

        try {
            const res = await updateTaskStatus({
                taskId: draggingTask.id,
                targetStatus
            });

            if (res.code === 200) {
                message.success('任务状态已更新');
            }
        } catch (error) {
            message.error('更新任务状态失败');
        } finally {
            setDragOverColumn(null);
            setDraggingTask(null);
        }
    };

    const renderColumn = (
        title: string,
        status: number,
        tasks: TaskItem[],
        icon: React.ReactNode
    ) => {
        const columnKey = String(status);
        const isDragOver = dragOverColumn === columnKey;

        return (
            <div
                className={`${styles.column} ${isDragOver ? styles.dragOver : ''}`}
                onDragOver={(e) => handleDragOver(e, columnKey)}
                onDragLeave={handleDragLeave}
                onDrop={(e) => handleDrop(e, status)}
            >
                <div className={styles.columnHeader}>
                    <div className={styles.columnTitle}>
                        {icon}
                        <span>{title}</span>
                        <Badge count={tasks.length} showZero/>
                    </div>
                    <Button
                        type="text"
                        icon={<PlusOutlined/>}
                        onClick={() => handleCreateTask(status)}
                    />
                </div>
                <div className={`${styles.columnTasks} ${globalStyles.scrollbar}`}>
                    {tasks.map((task) => (
                        <TaskCard
                            key={task.id}
                            task={task}
                            onDragStart={handleDragStart}
                            onDragEnd={handleDragEnd}
                            onClick={handleTaskClick}
                        />
                    ))}
                </div>
            </div>
        );
    };

    return (
        <div className={`${globalStyles.pageContainer} ${styles.kanbanContainer}`}>
            <div className={styles.toolbar}>
                <div className={styles.searchArea}>
                    <Search
                        className={styles.searchInput}
                        placeholder="搜索任务..."
                        allowClear
                        onSearch={handleSearch}
                        onChange={(e) => setKeyword(e.target.value)}
                        value={keyword}
                    />
                    <Select
                        className={styles.priorityFilter}
                        placeholder="优先级"
                        allowClear
                        value={priorityFilter}
                        onChange={handlePriorityChange}
                        options={[
                            {value: TaskPriority.LOW, label: '低'},
                            {value: TaskPriority.MEDIUM, label: '中'},
                            {value: TaskPriority.HIGH, label: '高'},
                            {value: TaskPriority.URGENT, label: '紧急'}
                        ]}
                    />
                </div>
                <div className={styles.actionArea}>
                    <Button
                        icon={<ReloadOutlined/>}
                        onClick={handleRefresh}
                    >
                        刷新
                    </Button>
                    <Button
                        type="primary"
                        icon={<PlusOutlined/>}
                        onClick={() => handleCreateTask(0)}
                    >
                        新建任务
                    </Button>
                </div>
            </div>

            <Spin spinning={loading}>
                <div className={styles.kanbanBoard}>
                    {renderColumn(
                        '待办',
                        0,
                        boardData.todoTasks,
                        <span style={{
                            width: 12,
                            height: 12,
                            borderRadius: '50%',
                            backgroundColor: '#bfbfbf'
                        }}/>
                    )}
                    {renderColumn(
                        '进行中',
                        1,
                        boardData.inProgressTasks,
                        <span style={{
                            width: 12,
                            height: 12,
                            borderRadius: '50%',
                            backgroundColor: '#1890ff'
                        }}/>
                    )}
                    {renderColumn(
                        '已完成',
                        2,
                        boardData.doneTasks,
                        <span style={{
                            width: 12,
                            height: 12,
                            borderRadius: '50%',
                            backgroundColor: '#52c41a'
                        }}/>
                    )}
                </div>
            </Spin>

            <TaskDetailModal
                open={detailModalOpen}
                taskId={selectedTaskId}
                onCancel={() => setDetailModalOpen(false)}
                onSuccess={handleDetailSuccess}
            />

            <CreateTaskModal
                open={createModalOpen}
                defaultStatus={createDefaultStatus}
                onCancel={() => setCreateModalOpen(false)}
                onSuccess={handleCreateSuccess}
            />
        </div>
    );
};

export default TaskDashboard;
