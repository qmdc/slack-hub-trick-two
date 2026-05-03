import React, { useState, useEffect } from 'react'
import { Tag, Rate, Spin, message } from 'antd'
import { EyeOutlined, BookOutlined } from '@ant-design/icons'
import { useParams } from 'react-router'
import { getSharedItems, type MediaItemDTO } from '../../apis/modules/mediaRecommend'

const SharePage: React.FC = () => {
    const { shareCode } = useParams<{ shareCode: string }>()
    const [items, setItems] = useState<MediaItemDTO[]>([])
    const [title, setTitle] = useState('')
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const fetchData = async () => {
            if (!shareCode) {
                setLoading(false)
                return
            }
            try {
                const response = await getSharedItems(shareCode)
                if (response.code === 200) {
                    setTitle(response.data?.title || '分享清单')
                    setItems(response.data?.items || [])
                }
            } catch (error) {
                message.error('获取分享内容失败')
            } finally {
                setLoading(false)
            }
        }
        fetchData()
    }, [shareCode])

    const getTypeLabel = (type: number) => type === 1 ? '电影' : '书籍'
    const getStatusLabel = (status: number) => status === 0 ? '想看' : '已看'
    const getStatusColor = (status: number) => status === 0 ? '#faad14' : '#52c41a'

    return (
        <div style={{ minHeight: '100vh', backgroundColor: '#f5f5f5', padding: 24 }}>
            <div style={{ maxWidth: 800, margin: '0 auto', backgroundColor: '#fff', borderRadius: 8, padding: 24 }}>
                <div style={{ marginBottom: 24, textAlign: 'center' }}>
                    <BookOutlined style={{ fontSize: 48, color: '#1890ff', marginBottom: 12 }} />
                    <h1 style={{ margin: 0, fontSize: 24 }}>{title}</h1>
                    <p style={{ margin: '8px 0 0 0', color: '#999', fontSize: 14 }}>
                        共 {items.length} 个影视书籍
                    </p>
                </div>

                <Spin spinning={loading}>
                    {items.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: 60, color: '#999' }}>
                            <p>暂无分享内容</p>
                        </div>
                    ) : (
                        items.map(item => (
                            <div
                                key={item.id}
                                style={{
                                    border: '1px solid #e8e8e8',
                                    borderRadius: 8,
                                    padding: 16,
                                    marginBottom: 16,
                                }}
                            >
                                <div style={{ display: 'flex', gap: 16 }}>
                                    <div style={{
                                        width: 100,
                                        height: 140,
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
                                                <EyeOutlined style={{ fontSize: 32 }} />
                                            </div>
                                        )}
                                    </div>
                                    <div style={{ flex: 1, minWidth: 0 }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
                                            <h3 style={{ margin: 0, fontSize: 16, fontWeight: 500 }}>{item.title}</h3>
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
                                            }}>
                                                {item.review}
                                            </p>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </Spin>
            </div>
        </div>
    )
}

export default SharePage