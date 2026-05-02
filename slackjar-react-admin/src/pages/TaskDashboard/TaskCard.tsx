import React from 'react';
import {Avatar, Tag, Tooltip} from 'antd';
import {ClockCircleOutlined} from '@ant-design/icons';
import dayjs from 'dayjs';
import type {TaskItem} from '../../apis/modules/taskDashboard';
import {TaskPriority} from '../../apis/modules/taskDashboard';
import styles from './index.module.scss';

/**
 * 任务卡片组件
 * @param task - 任务数据
 * @param onDragStart - 拖拽开始事件
 * @param onDragEnd - 拖拽结束事件
 * @param onClick - 点击事件
 */
interface TaskCardProps {
    task: TaskItem;
    onDragStart?: (e: React.DragEvent, task: TaskItem) => void;
    onDragEnd?: (e: React.DragEvent, task: TaskItem) => void;
    onClick?: (task: TaskItem) => void;
}

const TaskCard: React.FC<TaskCardProps> = ({task, onDragStart, onDragEnd, onClick}) => {

    const getPriorityClass = (priority: number): string => {
        switch (priority) {
            case TaskPriority.LOW:
                return styles.priorityLow;
            case TaskPriority.MEDIUM:
                return styles.priorityMedium;
            case TaskPriority.HIGH:
                return styles.priorityHigh;
            case TaskPriority.URGENT:
                return styles.priorityUrgent;
            default:
                return styles.priorityMedium;
        }
    };

    const getPriorityTagClass = (priority: number): string => {
        switch (priority) {
            case TaskPriority.LOW:
                return styles.priorityTagLow;
            case TaskPriority.MEDIUM:
                return styles.priorityTagMedium;
            case TaskPriority.HIGH:
                return styles.priorityTagHigh;
            case TaskPriority.URGENT:
                return styles.priorityTagUrgent;
            default:
                return styles.priorityTagMedium;
        }
    };

    const getDueDateClass = (): string => {
        if (!task.dueDate) {
            return '';
        }
        const now = dayjs();
        const due = dayjs(task.dueDate);
        const diffDays = due.diff(now, 'day');

        if (diffDays < 0) {
            return styles.overdue;
        } else if (diffDays <= 2) {
            return styles.dueSoon;
        }
        return '';
    };

    const handleDragStart = (e: React.DragEvent) => {
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('taskId', String(task.id));
        e.dataTransfer.setData('taskStatus', String(task.status));
        if (onDragStart) {
            onDragStart(e, task);
        }
    };

    const handleDragEnd = (e: React.DragEvent) => {
        if (onDragEnd) {
            onDragEnd(e, task);
        }
    };

    const handleClick = () => {
        if (onClick) {
            onClick(task);
        }
    };

    return (
        <div
            className={`${styles.taskCard} ${getPriorityClass(task.priority)}`}
            draggable
            onDragStart={handleDragStart}
            onDragEnd={handleDragEnd}
            onClick={handleClick}
        >
            <div className={styles.taskContent}>
                <div className={styles.taskTitle}>
                    {task.title}
                </div>
                {task.description && (
                    <div className={styles.taskDescription}>
                        {task.description}
                    </div>
                )}
                <div className={styles.taskMeta}>
                    <div className={styles.taskAssignee}>
                        {task.assigneeName ? (
                            <Tooltip title={`负责人: ${task.assigneeName}`} autoAdjustOverflow placement="topLeft">
                                <Avatar size={24} style={{backgroundColor: '#1890ff'}}>
                                    {task.assigneeName.charAt(0)}
                                </Avatar>
                            </Tooltip>
                        ) : null}
                        <Tag className={`${styles.priorityTag} ${getPriorityTagClass(task.priority)}`}>
                            {task.priorityDesc}
                        </Tag>
                    </div>
                    {task.dueDate && (
                        <div className={`${styles.taskDueDate} ${getDueDateClass()}`}>
                            <ClockCircleOutlined/>
                            <span>{dayjs(task.dueDate).format('MM-DD')}</span>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TaskCard;
