import React from 'react';
import { Form, Input, Select, Rate, Modal } from 'antd';
import { PlusOutlined, TagOutlined } from '@ant-design/icons';
import type { MediaItemDTO, MediaItemRequest } from '../../apis/modules/mediaRecommend';

interface MediaItemFormProps {
    visible: boolean;
    onCancel: () => void;
    onSubmit: (values: MediaItemRequest) => void;
    editItem?: MediaItemDTO;
}

const MediaItemForm: React.FC<MediaItemFormProps> = ({ visible, onCancel, onSubmit, editItem }) => {
    const [form] = Form.useForm();
    const [customTag, setCustomTag] = React.useState('');
    const [selectedTags, setSelectedTags] = React.useState<string[]>([]);

    const defaultTags = ['科幻', '悬疑', '文学', '动作', '喜剧', '爱情', '恐怖', '纪录片', '动画', '传记'];

    React.useEffect(() => {
        if (editItem) {
            form.setFieldsValue({
                ...editItem,
            });
            setSelectedTags(editItem.tags || []);
        } else {
            form.resetFields();
            setSelectedTags([]);
        }
    }, [visible, editItem, form]);

    const handleTagClick = (tag: string) => {
        if (selectedTags.includes(tag)) {
            setSelectedTags(selectedTags.filter(t => t !== tag));
        } else {
            setSelectedTags([...selectedTags, tag]);
        }
    };

    const handleAddCustomTag = () => {
        if (customTag.trim() && !selectedTags.includes(customTag.trim())) {
            setSelectedTags([...selectedTags, customTag.trim()]);
            setCustomTag('');
        }
    };

    const handleRemoveTag = (tag: string) => {
        setSelectedTags(selectedTags.filter(t => t !== tag));
    };

    const onFinish = (values: Record<string, unknown>) => {
        const request: MediaItemRequest = {
            ...values,
            tags: selectedTags,
        } as MediaItemRequest;
        if (editItem?.id) {
            request.id = editItem.id;
        }
        onSubmit(request);
    };

    return (
        <Modal
            title={editItem ? '编辑影视书籍' : '添加影视书籍'}
            open={visible}
            onCancel={onCancel}
            footer={null}
            width={500}
        >
            <Form
                form={form}
                layout="vertical"
                onFinish={onFinish}
            >
                <Form.Item
                    name="title"
                    label="标题"
                    rules={[{ required: true, message: '请输入标题' }]}
                >
                    <Input placeholder="请输入影视书籍名称" />
                </Form.Item>

                <Form.Item
                    name="type"
                    label="类型"
                    rules={[{ required: true, message: '请选择类型' }]}
                >
                    <Select placeholder="请选择类型">
                        <Select.Option value={1}>电影</Select.Option>
                        <Select.Option value={2}>书籍</Select.Option>
                    </Select>
                </Form.Item>

                <Form.Item
                    name="status"
                    label="状态"
                    rules={[{ required: true, message: '请选择状态' }]}
                >
                    <Select placeholder="请选择状态">
                        <Select.Option value={0}>想看</Select.Option>
                        <Select.Option value={1}>已看</Select.Option>
                    </Select>
                </Form.Item>

                <Form.Item
                    name="rating"
                    label="评分"
                >
                    <Rate allowHalf defaultValue={0} />
                </Form.Item>

                <Form.Item
                    name="author"
                    label="作者/导演"
                >
                    <Input placeholder="请输入作者或导演" />
                </Form.Item>

                <Form.Item
                    name="year"
                    label="年份"
                >
                    <Input placeholder="请输入出版或上映年份" />
                </Form.Item>

                <Form.Item
                    label="标签"
                >
                    <div style={{ marginBottom: 8 }}>
                        {defaultTags.map(tag => (
                            <button
                                key={tag}
                                type="button"
                                onClick={() => handleTagClick(tag)}
                                style={{
                                    marginRight: 8,
                                    marginBottom: 8,
                                    padding: '4px 12px',
                                    borderRadius: 20,
                                    border: '1px solid #d9d9d9',
                                    backgroundColor: selectedTags.includes(tag) ? '#1890ff' : '#fff',
                                    color: selectedTags.includes(tag) ? '#fff' : '#666',
                                    cursor: 'pointer',
                                }}
                            >
                                {tag}
                            </button>
                        ))}
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                        <Input
                            value={customTag}
                            onChange={(e) => setCustomTag(e.target.value)}
                            placeholder="输入自定义标签"
                            style={{ width: '200px', marginRight: 8 }}
                            onPressEnter={handleAddCustomTag}
                        />
                        <button
                            type="button"
                            onClick={handleAddCustomTag}
                            style={{
                                padding: '4px 12px',
                                borderRadius: 4,
                                border: '1px solid #1890ff',
                                backgroundColor: '#fff',
                                color: '#1890ff',
                                cursor: 'pointer',
                            }}
                        >
                            <PlusOutlined /> 添加
                        </button>
                    </div>
                    {selectedTags.length > 0 && (
                        <div style={{ marginTop: 8, display: 'flex', flexWrap: 'wrap' }}>
                            {selectedTags.map(tag => (
                                <span
                                    key={tag}
                                    style={{
                                        display: 'inline-flex',
                                        alignItems: 'center',
                                        marginRight: 8,
                                        marginBottom: 8,
                                        padding: '4px 8px',
                                        borderRadius: 4,
                                        backgroundColor: '#f0f5ff',
                                        color: '#1890ff',
                                    }}
                                >
                                    <TagOutlined style={{ marginRight: 4 }} />
                                    {tag}
                                    <button
                                        type="button"
                                        onClick={() => handleRemoveTag(tag)}
                                        style={{
                                            marginLeft: 4,
                                            background: 'none',
                                            border: 'none',
                                            color: '#1890ff',
                                            cursor: 'pointer',
                                        }}
                                    >
                                        ×
                                    </button>
                                </span>
                            ))}
                        </div>
                    )}
                </Form.Item>

                <Form.Item
                    name="review"
                    label="评论"
                >
                    <Input.TextArea placeholder="写下你的个人评论..." rows={4} />
                </Form.Item>

                <Form.Item
                    name="coverUrl"
                    label="封面URL"
                >
                    <Input placeholder="请输入封面图片链接" />
                </Form.Item>

                <Form.Item>
                    <button
                        type="submit"
                        style={{
                            width: '100%',
                            padding: '10px',
                            borderRadius: 4,
                            border: 'none',
                            backgroundColor: '#1890ff',
                            color: '#fff',
                            cursor: 'pointer',
                        }}
                    >
                        {editItem ? '保存修改' : '添加'}
                    </button>
                </Form.Item>
            </Form>
        </Modal>
    );
};

export default MediaItemForm;