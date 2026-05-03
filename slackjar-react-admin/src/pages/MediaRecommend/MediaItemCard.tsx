import React from 'react';
import { Rate, Tag, Popconfirm } from 'antd';
import { EditOutlined, DeleteOutlined, EyeOutlined } from '@ant-design/icons';
import type { MediaItemDTO } from '../../apis/modules/mediaRecommend';

interface MediaItemCardProps {
    item: MediaItemDTO;
    onEdit: (item: MediaItemDTO) => void;
    onDelete: (id: number) => void;
}

const MediaItemCard: React.FC<MediaItemCardProps> = ({ item, onEdit, onDelete }) => {
    const getTypeLabel = (type: number) => type === 1 ? '电影' : '书籍';
    const getStatusLabel = (status: number) => status === 0 ? '想看' : '已看';
    const getStatusColor = (status: number) => status === 0 ? '#faad14' : '#52c41a';

    return (
        <div style={{
            border: '1px solid #e8e8e8',
            borderRadius: 8,
            padding: 16,
            marginBottom: 16,
            backgroundColor: '#fff',
        }}>
            <div style={{ display: 'flex', gap: 16 }}>
                <div style={{
                    width: 120,
                    height: 160,
                    borderRadius: 8,
                    backgroundColor: '#f5f5f5',
                    overflow: 'hidden',
                    flexShrink: 0,
                }}>
                    {item.coverUrl ? (
                        <img
                            src={item.coverUrl}
                            alt={item.title}
                            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                    ) : (
                        <div style={{
                            width: '100%',
                            height: '100%',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            color: '#999',
                        }}>
                            <EyeOutlined style={{ fontSize: 48 }} />
                        </div>
                    )}
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
                        <h3 style={{ margin: 0, fontSize: 18, fontWeight: 500 }}>{item.title}</h3>
                        <span style={{
                            padding: '2px 8px',
                            borderRadius: 4,
                            backgroundColor: '#f0f5ff',
                            color: '#1890ff',
                            fontSize: 12,
                        }}>
                            {getTypeLabel(item.type)}
                        </span>
                        <span style={{
                            padding: '2px 8px',
                            borderRadius: 4,
                            backgroundColor: '#fff7e6',
                            color: getStatusColor(item.status),
                            fontSize: 12,
                        }}>
                            {getStatusLabel(item.status)}
                        </span>
                    </div>
                    {item.author && (
                        <p style={{ margin: 0, fontSize: 14, color: '#666', marginBottom: 4 }}>
                            {item.type === 1 ? '导演：' : '作者：'}{item.author}
                        </p>
                    )}
                    {item.year && (
                        <p style={{ margin: 0, fontSize: 14, color: '#666', marginBottom: 8 }}>
                            {item.type === 1 ? '上映年份：' : '出版年份：'}{item.year}
                        </p>
                    )}
                    {item.tags && item.tags.length > 0 && (
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4, marginBottom: 8 }}>
                            {item.tags.map(tag => (
                                <Tag key={tag} color="blue">{tag}</Tag>
                            ))}
                        </div>
                    )}
                    {item.rating && (
                        <div style={{ marginBottom: 8 }}>
                            <Rate disabled defaultValue={item.rating} />
                        </div>
                    )}
                    {item.review && (
                        <p style={{
                            margin: 0,
                            fontSize: 14,
                            color: '#666',
                            lineHeight: 1.5,
                            display: '-webkit-box',
                            WebkitLineClamp: 2,
                            WebkitBoxOrient: 'vertical',
                            overflow: 'hidden',
                        }}>
                            {item.review}
                        </p>
                    )}
                    <div style={{ display: 'flex', gap: 8, marginTop: 12 }}>
                        <button
                            onClick={() => onEdit(item)}
                            style={{
                                padding: '4px 12px',
                                borderRadius: 4,
                                border: '1px solid #1890ff',
                                backgroundColor: '#fff',
                                color: '#1890ff',
                                cursor: 'pointer',
                                display: 'flex',
                                alignItems: 'center',
                                gap: 4,
                            }}
                        >
                            <EditOutlined /> 编辑
                        </button>
                        <Popconfirm
                            title="确定删除这个影视书籍吗？"
                            onConfirm={() => onDelete(item.id!)}
                        >
                            <button
                                style={{
                                    padding: '4px 12px',
                                    borderRadius: 4,
                                    border: '1px solid #ff4d4f',
                                    backgroundColor: '#fff',
                                    color: '#ff4d4f',
                                    cursor: 'pointer',
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 4,
                                }}
                            >
                                <DeleteOutlined /> 删除
                            </button>
                        </Popconfirm>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MediaItemCard;