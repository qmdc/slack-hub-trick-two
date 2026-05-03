import React, { useState, useEffect } from 'react'
import { Button, Select, Tag, message, Tabs, Spin } from 'antd'
import { PlusOutlined, ShareAltOutlined, StarOutlined } from '@ant-design/icons'
import MediaItemForm from './MediaItemForm'
import MediaItemCard from './MediaItemCard'
import ShareModal from './ShareModal'
import {
    addItem,
    updateItem,
    deleteItem,
    listItems,
    getRecommendations,
    getAllTags,
    createShareLink,
    type MediaItemDTO,
    type MediaItemRequest,
} from '../../apis/modules/mediaRecommend'

const { TabPane } = Tabs

const MediaRecommend: React.FC = () => {
    const [items, setItems] = useState<MediaItemDTO[]>([])
    const [recommendations, setRecommendations] = useState<MediaItemDTO[]>([])
    const [allTags, setAllTags] = useState<string[]>([])
    const [loading, setLoading] = useState(false)
    const [formVisible, setFormVisible] = useState(false)
    const [shareModalVisible, setShareModalVisible] = useState(false)
    const [currentShareUrl, setCurrentShareUrl] = useState('')
    const [editingItem, setEditingItem] = useState<MediaItemDTO | undefined>()
    const [selectedType, setSelectedType] = useState<number | null>(null)
    const [selectedStatus, setSelectedStatus] = useState<number | null>(null)
    const [selectedTag, setSelectedTag] = useState<string>('')

    const fetchItems = async () => {
        setLoading(true)
        try {
            const response = await listItems(selectedType ?? undefined, selectedStatus ?? undefined)
            if (response.code === 200) {
                let filteredItems = response.data || []
                if (selectedTag) {
                    filteredItems = filteredItems.filter((item: MediaItemDTO) =>
                        item.tags && item.tags.includes(selectedTag)
                    )
                }
                setItems(filteredItems)
            }
        } catch (error) {
            message.error('获取列表失败')
        } finally {
            setLoading(false)
        }
    }

    const fetchRecommendations = async () => {
        try {
            const response = await getRecommendations(10)
            if (response.code === 200) {
                setRecommendations(response.data || [])
            }
        } catch (error) {
            console.error('获取推荐失败', error)
        }
    }

    const fetchTags = async () => {
        try {
            const response = await getAllTags()
            if (response.code === 200) {
                setAllTags(response.data || [])
            }
        } catch (error) {
            console.error('获取标签失败', error)
        }
    }

    useEffect(() => {
        fetchItems()
        fetchRecommendations()
        fetchTags()
    }, [selectedType, selectedStatus, selectedTag])

    const handleAdd = () => {
        setEditingItem(undefined)
        setFormVisible(true)
    }

    const handleEdit = (item: MediaItemDTO) => {
        setEditingItem(item)
        setFormVisible(true)
    }

    const handleDelete = async (id: number) => {
        try {
            const response = await deleteItem(id)
            if (response.code === 200) {
                message.success('删除成功')
                fetchItems()
            }
        } catch (error) {
            message.error('删除失败')
        }
    }

    const handleSubmit = async (request: MediaItemRequest) => {
        try {
            let response
            if (request.id) {
                response = await updateItem(request)
            } else {
                response = await addItem(request)
            }
            if (response.code === 200) {
                message.success(request.id ? '更新成功' : '添加成功')
                setFormVisible(false)
                fetchItems()
                fetchTags()
            }
        } catch (error) {
            message.error(request.id ? '更新失败' : '添加失败')
        }
    }

    const handleCreateShareLink = async () => {
        try {
            const response = await createShareLink({ title: '我的书单影单' })
            if (response.code === 200) {
                setCurrentShareUrl(response.data?.shareUrl || '')
                setShareModalVisible(true)
            }
        } catch (error) {
            message.error('生成分享链接失败')
        }
    }

    const getTypeLabel = (type: number) => type === 1 ? '电影' : '书籍'

    return (
        <div style={{ padding: 24 }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 24 }}>
                <h2 style={{ margin: 0 }}>影视书籍推荐清单</h2>
                <div style={{ display: 'flex', gap: 12 }}>
                    <Button
                        type="primary"
                        icon={<ShareAltOutlined />}
                        onClick={handleCreateShareLink}
                    >
                        生成分享链接
                    </Button>
                    <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={handleAdd}
                    >
                        添加影视书籍
                    </Button>
                </div>
            </div>

            <Tabs defaultActiveKey="1">
                <TabPane tab="我的清单" key="1">
                    <div style={{ display: 'flex', gap: 16, marginBottom: 16, flexWrap: 'wrap' }}>
                        <Select
                            placeholder="选择类型"
                            style={{ width: 120 }}
                            allowClear
                            value={selectedType ?? undefined}
                            onChange={(value) => setSelectedType(value)}
                        >
                            <Select.Option value={1}>电影</Select.Option>
                            <Select.Option value={2}>书籍</Select.Option>
                        </Select>
                        <Select
                            placeholder="选择状态"
                            style={{ width: 120 }}
                            allowClear
                            value={selectedStatus ?? undefined}
                            onChange={(value) => setSelectedStatus(value)}
                        >
                            <Select.Option value={0}>想看</Select.Option>
                            <Select.Option value={1}>已看</Select.Option>
                        </Select>
                        <Select
                            placeholder="选择标签"
                            style={{ width: 150 }}
                            allowClear
                            value={selectedTag}
                            onChange={(value) => setSelectedTag(value)}
                        >
                            {allTags.map(tag => (
                                <Select.Option key={tag} value={tag}>
                                    {tag}
                                </Select.Option>
                            ))}
                        </Select>
                    </div>

                    <Spin spinning={loading}>
                        {items.length === 0 ? (
                            <div style={{
                                textAlign: 'center',
                                padding: 60,
                                color: '#999',
                            }}>
                                <p>暂无影视书籍</p>
                                <p style={{ fontSize: 14, marginTop: 8 }}>点击右上角按钮添加</p>
                            </div>
                        ) : (
                            items.map(item => (
                                <MediaItemCard
                                    key={item.id}
                                    item={item}
                                    onEdit={handleEdit}
                                    onDelete={handleDelete}
                                />
                            ))
                        )}
                    </Spin>
                </TabPane>

                <TabPane tab={<span><StarOutlined /> 为你推荐</span>} key="2">
                    <div style={{ marginBottom: 16 }}>
                        <p style={{ margin: 0, color: '#666', fontSize: 14 }}>
                            根据你的收藏标签为你推荐相关影视书籍
                        </p>
                    </div>
                    {recommendations.length === 0 ? (
                        <div style={{
                            textAlign: 'center',
                            padding: 60,
                            color: '#999',
                        }}>
                            <p>暂无推荐内容</p>
                            <p style={{ fontSize: 14, marginTop: 8 }}>添加更多影视书籍以获取推荐</p>
                        </div>
                    ) : (
                        recommendations.map(item => (
                            <MediaItemCard
                                key={item.id}
                                item={item}
                                onEdit={handleEdit}
                                onDelete={handleDelete}
                            />
                        ))
                    )}
                </TabPane>
            </Tabs>

            <MediaItemForm
                visible={formVisible}
                onCancel={() => setFormVisible(false)}
                onSubmit={handleSubmit}
                editItem={editingItem}
            />

            <ShareModal
                visible={shareModalVisible}
                onCancel={() => setShareModalVisible(false)}
                shareUrl={currentShareUrl}
            />
        </div>
    )
}

export default MediaRecommend