import React, {useState} from 'react';
import {
    Modal,
    Form,
    Input,
    Select,
    DatePicker,
    message
} from 'antd';
import type {Dayjs} from 'dayjs';
import type {
    CreateTaskRequest,
    TaskItem
} from '../../apis/modules/taskDashboard';
import {
    TaskPriority,
    TaskStatus,
    createTask
} from '../../apis/modules/taskDashboard';
import styles from './index.module.scss';

const {TextArea} = Input;

/**
 * 新建任务模态框组件
 * @param open - 是否打开
 * @param defaultStatus - 默认状态
 * @param onCancel - 取消事件
 * @param onSuccess - 成功事件
 */
interface CreateTaskModalProps {
    open: boolean;
    defaultStatus?: number;
    onCancel: () => void;
    onSuccess: (task: TaskItem) => void;
}

const CreateTaskModal: React.FC<CreateTaskModalProps> = ({open, defaultStatus, onCancel, onSuccess}) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();
            setLoading(true);

            const request: CreateTaskRequest = {
                title: values.title,
                description: values.description,
                status: values.status ?? defaultStatus ?? TaskStatus.TODO,
                priority: values.priority,
                assigneeId: values.assigneeId,
                dueDate: values.dueDate ? values.dueDate.valueOf() : undefined
            };

            const res = await createTask(request);
            if (res.code === 200 && res.data) {
                message.success('任务创建成功');
                form.resetFields();
                onSuccess(res.data);
            }
        } catch (error) {
            console.error('创建任务失败:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = () => {
        form.resetFields();
        onCancel();
    };

    return (
        <Modal
            className={styles.createModal}
            open={open}
            title="新建任务"
            width={520}
            onCancel={handleCancel}
            onOk={handleSubmit}
            confirmLoading={loading}
            okText="创建"
            cancelText="取消"
        >
            <Form
                form={form}
                layout="vertical"
                initialValues={{
                    status: defaultStatus ?? TaskStatus.TODO,
                    priority: TaskPriority.MEDIUM
                }}
            >
                <Form.Item
                    name="title"
                    label="任务标题"
                    rules={[{required: true, message: '请输入任务标题'}]}
                >
                    <Input placeholder="请输入任务标题"/>
                </Form.Item>

                <Form.Item
                    name="description"
                    label="任务描述"
                >
                    <TextArea
                        placeholder="请输入任务描述"
                        rows={3}
                        showCount
                        maxLength={500}
                    />
                </Form.Item>

                <Form.Item
                    name="status"
                    label="任务状态"
                    rules={[{required: true, message: '请选择任务状态'}]}
                >
                    <Select
                        placeholder="请选择任务状态"
                        options={[
                            {value: TaskStatus.TODO, label: '待办'},
                            {value: TaskStatus.IN_PROGRESS, label: '进行中'},
                            {value: TaskStatus.DONE, label: '已完成'}
                        ]}
                    />
                </Form.Item>

                <Form.Item
                    name="priority"
                    label="优先级"
                >
                    <Select
                        placeholder="请选择优先级"
                        options={[
                            {value: TaskPriority.LOW, label: '低'},
                            {value: TaskPriority.MEDIUM, label: '中'},
                            {value: TaskPriority.HIGH, label: '高'},
                            {value: TaskPriority.URGENT, label: '紧急'}
                        ]}
                    />
                </Form.Item>

                <Form.Item
                    name="dueDate"
                    label="截止日期"
                >
                    <DatePicker
                        showTime
                        placeholder="请选择截止日期"
                        style={{width: '100%'}}
                    />
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default CreateTaskModal;
