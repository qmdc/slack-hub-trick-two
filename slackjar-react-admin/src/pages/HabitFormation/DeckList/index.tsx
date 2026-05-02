import React, {useEffect, useState} from 'react'
import {
    Card,
    Col,
    Row,
    Button,
    Modal,
    Form,
    Input,
    message,
    Popconfirm,
    Empty,
    Spin,
    Tag,
    Progress,
    Space,
    Select,
    InputNumber
} from 'antd'
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    FileTextOutlined,
    ShareAltOutlined,
    SearchOutlined,
    FilterOutlined
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {
    getMyDecks,
    createDeck,
    updateDeck,
    deleteDeck,
    shareToMarket,
    getMyTags,
    type Deck,
    type Tag as TagType
} from '../../../apis/modules/habitFormation'
import {useNavigate} from 'react-router'

const {TextArea} = Input
const {Option} = Select

const DeckList: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [decks, setDecks] = useState<Deck[]>([])
    const [tags, setTags] = useState<TagType[]>([])
    const [keyword, setKeyword] = useState('')
    const [selectedTag, setSelectedTag] = useState<number | undefined>()

    const [modalVisible, setModalVisible] = useState(false)
    const [shareModalVisible, setShareModalVisible] = useState(false)
    const [editingDeck, setEditingDeck] = useState<Deck | null>(null)
    const [selectedDeckForShare, setSelectedDeckForShare] = useState<Deck | null>(null)
    const [form] = Form.useForm()
    const [shareForm] = Form.useForm()

    useEffect(() => {
        loadData()
    }, [])

    const loadData = async () => {
        setLoading(true)
        try {
            const [decksRes, tagsRes] = await Promise.all([
                getMyDecks(),
                getMyTags()
            ])
            if (decksRes.code === 200) {
                setDecks(decksRes.data || [])
            }
            if (tagsRes.code === 200) {
                setTags(tagsRes.data || [])
            }
        } catch (error) {
            console.error('Failed to load decks:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const filteredDecks = decks.filter(deck => {
        const matchKeyword = !keyword || deck.name.toLowerCase().includes(keyword.toLowerCase()) ||
            (deck.description && deck.description.toLowerCase().includes(keyword.toLowerCase()))
        const matchTag = !selectedTag || (deck.tags && deck.tags.some(t => t.id === selectedTag))
        return matchKeyword && matchTag
    })

    const handleCreate = () => {
        setEditingDeck(null)
        form.resetFields()
        setModalVisible(true)
    }

    const handleEdit = (deck: Deck) => {
        setEditingDeck(deck)
        form.setFieldsValue({
            name: deck.name,
            description: deck.description || '',
            tagIds: deck.tags?.map(t => t.id) || [],
            sortOrder: deck.sortOrder
        })
        setModalVisible(true)
    }

    const handleSubmit = async (values: any) => {
        try {
            if (editingDeck) {
                const res = await updateDeck(editingDeck.id, values)
                if (res.code === 200) {
                    message.success(t('common.updateSuccess'))
                    setModalVisible(false)
                    loadData()
                } else {
                    message.error(res.message || t('common.updateFailed'))
                }
            } else {
                const res = await createDeck(values)
                if (res.code === 200) {
                    message.success(t('common.createSuccess'))
                    setModalVisible(false)
                    loadData()
                } else {
                    message.error(res.message || t('common.createFailed'))
                }
            }
        } catch (error) {
            console.error('Failed to save deck:', error)
            message.error(t('common.operationFailed'))
        }
    }

    const handleDelete = async (deckId: number) => {
        try {
            const res = await deleteDeck(deckId)
            if (res.code === 200) {
                message.success(t('common.deleteSuccess'))
                loadData()
            } else {
                message.error(res.message || t('common.deleteFailed'))
            }
        } catch (error) {
            console.error('Failed to delete deck:', error)
            message.error(t('common.operationFailed'))
        }
    }

    const handleShare = (deck: Deck) => {
        setSelectedDeckForShare(deck)
        shareForm.resetFields()
        shareForm.setFieldsValue({
            name: deck.name,
            description: deck.description || '',
            tags: deck.tags?.map(t => t.name) || []
        })
        setShareModalVisible(true)
    }

    const handleShareSubmit = async (values: any) => {
        if (!selectedDeckForShare) return
        try {
            const res = await shareToMarket({
                deckId: selectedDeckForShare.id,
                ...values
            })
            if (res.code === 200) {
                message.success(t('habit.shareSuccess'))
                setShareModalVisible(false)
            } else {
                message.error(res.message || t('habit.shareFailed'))
            }
        } catch (error) {
            console.error('Failed to share deck:', error)
            message.error(t('common.operationFailed'))
        }
    }

    const getMasteryColor = (rate: number): string => {
        if (rate >= 80) return '#52c41a'
        if (rate >= 60) return '#1890ff'
        if (rate >= 40) return '#faad14'
        return '#ff4d4f'
    }

    return (
        <Spin spinning={loading}>
            <div style={{padding: '24px'}}>
                <div style={{marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                    <Space>
                        <Input
                            placeholder={t('habit.searchDeck')}
                            prefix={<SearchOutlined/>}
                            style={{width: 250}}
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                            allowClear
                        />
                        <Select
                            placeholder={t('habit.filterByTag')}
                            style={{width: 180}}
                            value={selectedTag}
                            onChange={(value) => setSelectedTag(value)}
                            allowClear
                        >
                            {tags.map(tag => (
                                <Option key={tag.id} value={tag.id}>
                                    <Tag color={tag.color}>{tag.name}</Tag>
                                </Option>
                            ))}
                        </Select>
                        <Button icon={<FilterOutlined/>} onClick={loadData}>
                            {t('common.refresh')}
                        </Button>
                    </Space>
                    <Button type="primary" icon={<PlusOutlined/>} onClick={handleCreate}>
                        {t('habit.createDeck')}
                    </Button>
                </div>

                {filteredDecks.length > 0 ? (
                    <Row gutter={[16, 16]}>
                        {filteredDecks.map((deck: Deck) => (
                            <Col xs={24} sm={12} lg={8} xl={6} key={deck.id}>
                                <Card
                                    hoverable
                                    onClick={() => navigate(`/habit-formation/deck/card/${deck.id}`)}
                                    styles={{body: {padding: '16px'}}}
                                    actions={[
                                        <EditOutlined
                                            key="edit"
                                            onClick={(e) => {
                                                e.stopPropagation()
                                                handleEdit(deck)
                                            }}
                                        />,
                                        <ShareAltOutlined
                                            key="share"
                                            onClick={(e) => {
                                                e.stopPropagation()
                                                handleShare(deck)
                                            }}
                                        />,
                                        <Popconfirm
                                            key="delete"
                                            title={t('habit.confirmDeleteDeck')}
                                            onConfirm={(e) => {
                                                e?.stopPropagation()
                                                handleDelete(deck.id)
                                            }}
                                            okText={t('common.confirm')}
                                            cancelText={t('common.cancel')}
                                        >
                                            <DeleteOutlined
                                                onClick={(e) => e.stopPropagation()}
                                                style={{color: '#ff4d4f'}}
                                            />
                                        </Popconfirm>
                                    ]}
                                >
                                    <div style={{marginBottom: '12px'}}>
                                        <div style={{fontSize: '16px', fontWeight: '500', marginBottom: '8px'}}>
                                            {deck.name}
                                        </div>
                                        <div style={{fontSize: '12px', color: '#8c8c8c', marginBottom: '8px'}}>
                                            {deck.description || t('habit.noDescription')}
                                        </div>
                                        {deck.tags && deck.tags.length > 0 && (
                                            <Space size={[4, 4]} wrap>
                                                {deck.tags.map(tag => (
                                                    <Tag key={tag.id} color={tag.color}>{tag.name}</Tag>
                                                ))}
                                            </Space>
                                        )}
                                    </div>

                                    <Row justify="space-between" style={{marginBottom: '12px'}}>
                                        <Col>
                                            <Tag color="blue">
                                                <FileTextOutlined/> {deck.cardCount} {t('habit.cards')}
                                            </Tag>
                                        </Col>
                                        <Col>
                                            {deck.todayReviewCount > 0 && (
                                                <Tag color="red">
                                                    {deck.todayReviewCount} {t('habit.toReview')}
                                                </Tag>
                                            )}
                                        </Col>
                                    </Row>

                                    <div>
                                        <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '4px'}}>
                                            <span style={{fontSize: '12px', color: '#8c8c8c'}}>
                                                {t('habit.masteryRate')}
                                            </span>
                                            <span style={{
                                                fontSize: '12px',
                                                fontWeight: '500',
                                                color: getMasteryColor(deck.masteryRate)
                                            }}>
                                                {Math.round(deck.masteryRate)}%
                                            </span>
                                        </div>
                                        <Progress
                                            percent={Math.round(deck.masteryRate)}
                                            size="small"
                                            strokeColor={getMasteryColor(deck.masteryRate)}
                                            showInfo={false}
                                        />
                                    </div>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                ) : (
                    <Empty
                        description={t('habit.noDecks')}
                        style={{padding: '60px 0'}}
                    >
                        <Button type="primary" icon={<PlusOutlined/>} onClick={handleCreate}>
                            {t('habit.createFirstDeck')}
                        </Button>
                    </Empty>
                )}
            </div>

            <Modal
                title={editingDeck ? t('habit.editDeck') : t('habit.createDeck')}
                open={modalVisible}
                onCancel={() => setModalVisible(false)}
                onOk={() => form.submit()}
                okText={t('common.confirm')}
                cancelText={t('common.cancel')}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                >
                    <Form.Item
                        name="name"
                        label={t('habit.deckName')}
                        rules={[{required: true, message: t('habit.pleaseEnterDeckName')}]}
                    >
                        <Input placeholder={t('habit.pleaseEnterDeckName')}/>
                    </Form.Item>
                    <Form.Item
                        name="description"
                        label={t('habit.description')}
                    >
                        <TextArea
                            placeholder={t('habit.pleaseEnterDescription')}
                            rows={3}
                            maxLength={500}
                            showCount
                        />
                    </Form.Item>
                    <Form.Item
                        name="tagIds"
                        label={t('habit.tags')}
                    >
                        <Select
                            mode="multiple"
                            placeholder={t('habit.selectTags')}
                        >
                            {tags.map(tag => (
                                <Option key={tag.id} value={tag.id}>
                                    <Tag color={tag.color}>{tag.name}</Tag>
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item
                        name="sortOrder"
                        label={t('habit.sortOrder')}
                        initialValue={0}
                    >
                        <InputNumber
                            style={{width: '100%'}}
                            placeholder={t('habit.pleaseEnterSortOrder')}
                        />
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title={t('habit.shareToMarket')}
                open={shareModalVisible}
                onCancel={() => setShareModalVisible(false)}
                onOk={() => shareForm.submit()}
                okText={t('common.confirm')}
                cancelText={t('common.cancel')}
            >
                <Form
                    form={shareForm}
                    layout="vertical"
                    onFinish={handleShareSubmit}
                >
                    <Form.Item
                        name="name"
                        label={t('habit.shareName')}
                        rules={[{required: true, message: t('habit.pleaseEnterShareName')}]}
                    >
                        <Input placeholder={t('habit.pleaseEnterShareName')}/>
                    </Form.Item>
                    <Form.Item
                        name="description"
                        label={t('habit.shareDescription')}
                    >
                        <TextArea
                            placeholder={t('habit.pleaseEnterShareDescription')}
                            rows={3}
                            maxLength={500}
                            showCount
                        />
                    </Form.Item>
                    <Form.Item
                        name="tags"
                        label={t('habit.shareTags')}
                    >
                        <Select
                            mode="tags"
                            placeholder={t('habit.enterShareTags')}
                            tokenSeparators={[',', '，']}
                        />
                    </Form.Item>
                </Form>
            </Modal>
        </Spin>
    )
}

export default DeckList
