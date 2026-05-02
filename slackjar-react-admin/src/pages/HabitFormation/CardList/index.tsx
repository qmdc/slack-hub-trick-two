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
    Space,
    Select,
    Badge,
    Tabs,
    TabsProps
} from 'antd'
import {
    PlusOutlined,
    EditOutlined,
    DeleteOutlined,
    EyeOutlined,
    SearchOutlined,
    ArrowLeftOutlined,
    StarOutlined,
    StarFilled
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {
    pageQueryCards,
    createCard,
    updateCard,
    deleteCard,
    getDeckById,
    getMyTags,
    type Card as CardType,
    type Tag as TagType,
    type Deck
} from '../../../apis/modules/habitFormation'
import {useNavigate, useParams} from 'react-router'

const {TextArea} = Input
const {Option} = Select

const CardList: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const {deckId} = useParams<{ deckId?: string }>()
    const [loading, setLoading] = useState(false)
    const [cards, setCards] = useState<CardType[]>([])
    const [tags, setTags] = useState<TagType[]>([])
    const [deck, setDeck] = useState<Deck | null>(null)
    const [keyword, setKeyword] = useState('')
    const [selectedTag, setSelectedTag] = useState<number | undefined>()
    const [activeTab, setActiveTab] = useState('all')
    const [currentPage, setCurrentPage] = useState(1)
    const [total, setTotal] = useState(0)
    const [pageSize] = useState(12)

    const [modalVisible, setModalVisible] = useState(false)
    const [previewVisible, setPreviewVisible] = useState(false)
    const [editingCard, setEditingCard] = useState<CardType | null>(null)
    const [previewCard, setPreviewCard] = useState<CardType | null>(null)
    const [form] = Form.useForm()

    const tabItems: TabsProps['items'] = [
        {key: 'all', label: t('habit.allCards')},
        {key: 'due', label: <Badge count={deck?.todayReviewCount || 0} showZero>{t('habit.toReview')}</Badge>},
        {key: 'new', label: t('habit.newCards')},
        {key: 'important', label: <><StarFilled style={{color: '#faad14'}}/> {t('habit.important')}</>}
    ]

    useEffect(() => {
        loadData()
    }, [deckId])

    useEffect(() => {
        loadCards()
    }, [activeTab, currentPage, keyword, selectedTag, deckId])

    const loadData = async () => {
        setLoading(true)
        try {
            const tagsRes = await getMyTags()
            if (tagsRes.code === 200) {
                setTags(tagsRes.data || [])
            }
            if (deckId) {
                const deckRes = await getDeckById(Number(deckId))
                if (deckRes.code === 200) {
                    setDeck(deckRes.data)
                }
            }
        } catch (error) {
            console.error('Failed to load data:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const loadCards = async () => {
        setLoading(true)
        try {
            const query: any = {
                pageNo: currentPage,
                pageSize: pageSize,
                keyword: keyword || undefined,
                tagIds: selectedTag ? [selectedTag] : undefined
            }
            if (deckId) {
                query.deckId = Number(deckId)
            }
            if (activeTab === 'due') {
                query.isDueToday = true
            } else if (activeTab === 'new') {
                query.masteryLevel = 0
            } else if (activeTab === 'important') {
                query.isImportant = 1
            }

            const res = await pageQueryCards(query)
            if (res.code === 200) {
                setCards(res.data?.records || [])
                setTotal(res.data?.total || 0)
            }
        } catch (error) {
            console.error('Failed to load cards:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const handleCreate = () => {
        setEditingCard(null)
        form.resetFields()
        if (deckId) {
            form.setFieldsValue({deckId: Number(deckId)})
        }
        setModalVisible(true)
    }

    const handleEdit = (card: CardType) => {
        setEditingCard(card)
        form.setFieldsValue({
            deckId: card.deckId,
            frontContent: card.frontContent || '',
            backContent: card.backContent || '',
            tagIds: card.tags?.map(t => t.id) || [],
            isImportant: card.isImportant
        })
        setModalVisible(true)
    }

    const handlePreview = (card: CardType) => {
        setPreviewCard(card)
        setPreviewVisible(true)
    }

    const handleSubmit = async (values: any) => {
        try {
            if (editingCard) {
                const res = await updateCard(editingCard.id, values)
                if (res.code === 200) {
                    message.success(t('common.updateSuccess'))
                    setModalVisible(false)
                    loadCards()
                    loadData()
                } else {
                    message.error(res.message || t('common.updateFailed'))
                }
            } else {
                const res = await createCard(values)
                if (res.code === 200) {
                    message.success(t('common.createSuccess'))
                    setModalVisible(false)
                    loadCards()
                    loadData()
                } else {
                    message.error(res.message || t('common.createFailed'))
                }
            }
        } catch (error) {
            console.error('Failed to save card:', error)
            message.error(t('common.operationFailed'))
        }
    }

    const handleDelete = async (cardId: number) => {
        try {
            const res = await deleteCard(cardId)
            if (res.code === 200) {
                message.success(t('common.deleteSuccess'))
                loadCards()
                loadData()
            } else {
                message.error(res.message || t('common.deleteFailed'))
            }
        } catch (error) {
            console.error('Failed to delete card:', error)
            message.error(t('common.operationFailed'))
        }
    }

    const getMasteryColor = (level: number): string => {
        if (level >= 4) return '#52c41a'
        if (level >= 2) return '#1890ff'
        return '#faad14'
    }

    const getMasteryText = (level: number): string => {
        const texts = [
            t('habit.masteryNew'),
            t('habit.masteryVeryLow'),
            t('habit.masteryLow'),
            t('habit.masteryMedium'),
            t('habit.masteryHigh'),
            t('habit.masteryVeryHigh')
        ]
        return texts[level] || texts[0]
    }

    return (
        <Spin spinning={loading}>
            <div style={{padding: '24px'}}>
                <div style={{marginBottom: '16px'}}>
                    <Space>
                        <Button
                            icon={<ArrowLeftOutlined/>}
                            onClick={() => navigate('/habit-formation/deck/list')}
                        >
                            {t('common.back')}
                        </Button>
                        {deck && (
                            <span style={{fontSize: '18px', fontWeight: '500'}}>
                                {deck.name}
                                {deck.todayReviewCount > 0 && (
                                    <Badge
                                        count={deck.todayReviewCount}
                                        style={{marginLeft: '12px'}}
                                        showZero
                                    />
                                )}
                            </span>
                        )}
                    </Space>
                </div>

                <div style={{marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                    <Space>
                        <Input
                            placeholder={t('habit.searchCard')}
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
                        <Button onClick={loadCards}>{t('common.refresh')}</Button>
                    </Space>
                    <Button type="primary" icon={<PlusOutlined/>} onClick={handleCreate}>
                        {t('habit.createCard')}
                    </Button>
                </div>

                <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems}/>

                <div style={{marginTop: '16px'}}>
                    {cards.length > 0 ? (
                        <Row gutter={[16, 16]}>
                            {cards.map((card: CardType) => (
                                <Col xs={24} sm={12} lg={8} xl={6} key={card.id}>
                                    <Card
                                        hoverable
                                        onClick={() => handlePreview(card)}
                                        styles={{body: {padding: '12px'}}}
                                        actions={[
                                            <EyeOutlined
                                                key="preview"
                                                onClick={(e) => {
                                                    e.stopPropagation()
                                                    handlePreview(card)
                                                }}
                                            />,
                                            <EditOutlined
                                                key="edit"
                                                onClick={(e) => {
                                                    e.stopPropagation()
                                                    handleEdit(card)
                                                }}
                                            />,
                                            <Popconfirm
                                                key="delete"
                                                title={t('habit.confirmDeleteCard')}
                                                onConfirm={(e) => {
                                                    e?.stopPropagation()
                                                    handleDelete(card.id)
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
                                        <div style={{marginBottom: '8px'}}>
                                            <div style={{fontSize: '14px', fontWeight: '500', marginBottom: '4px'}}>
                                                {card.isImportant === 1 && <StarFilled style={{color: '#faad14', marginRight: '4px'}}/>}
                                                {card.frontContent || t('habit.noContent')}
                                            </div>
                                            {card.tags && card.tags.length > 0 && (
                                                <Space size={[4, 4]} wrap>
                                                    {card.tags.map(tag => (
                                                        <Tag key={tag.id} color={tag.color} style={{fontSize: '11px'}}>
                                                            {tag.name}
                                                        </Tag>
                                                    ))}
                                                </Space>
                                            )}
                                        </div>

                                        <Row justify="space-between" align="middle">
                                            <Col>
                                                <Tag color={getMasteryColor(card.masteryLevel)}>
                                                    {getMasteryText(card.masteryLevel)}
                                                </Tag>
                                            </Col>
                                            <Col style={{fontSize: '12px', color: '#8c8c8c'}}>
                                                {t('habit.reviewCount')}: {card.reviewCount}
                                            </Col>
                                        </Row>

                                        {card.isDueToday && (
                                            <div style={{marginTop: '8px'}}>
                                                <Tag color="red">{t('habit.dueToday')}</Tag>
                                            </div>
                                        )}
                                    </Card>
                                </Col>
                            ))}
                        </Row>
                    ) : (
                        <Empty
                            description={t('habit.noCards')}
                            style={{padding: '60px 0'}}
                        >
                            <Button type="primary" icon={<PlusOutlined/>} onClick={handleCreate}>
                                {t('habit.createFirstCard')}
                            </Button>
                        </Empty>
                    )}
                </div>
            </div>

            <Modal
                title={editingCard ? t('habit.editCard') : t('habit.createCard')}
                open={modalVisible}
                onCancel={() => setModalVisible(false)}
                onOk={() => form.submit()}
                okText={t('common.confirm')}
                cancelText={t('common.cancel')}
                width={700}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                >
                    <Form.Item
                        name="deckId"
                        label={t('habit.selectDeck')}
                        rules={[{required: true, message: t('habit.pleaseSelectDeck')}]}
                    >
                        <Select placeholder={t('habit.pleaseSelectDeck')}>
                            {deck ? (
                                <Option key={deck.id} value={deck.id}>{deck.name}</Option>
                            ) : null}
                        </Select>
                    </Form.Item>
                    <Form.Item
                        name="frontContent"
                        label={t('habit.cardFront')}
                    >
                        <TextArea
                            placeholder={t('habit.pleaseEnterFrontContent')}
                            rows={4}
                            maxLength={2000}
                            showCount
                        />
                    </Form.Item>
                    <Form.Item
                        name="backContent"
                        label={t('habit.cardBack')}
                    >
                        <TextArea
                            placeholder={t('habit.pleaseEnterBackContent')}
                            rows={4}
                            maxLength={5000}
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
                        name="isImportant"
                        label={t('habit.markAsImportant')}
                        valuePropName="checked"
                        initialValue={0}
                    >
                        <Select>
                            <Option value={0}>{t('common.no')}</Option>
                            <Option value={1}>{t('common.yes')}</Option>
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title={t('habit.cardPreview')}
                open={previewVisible}
                onCancel={() => setPreviewVisible(false)}
                footer={null}
                width={600}
            >
                {previewCard && (
                    <div>
                        <div style={{marginBottom: '20px'}}>
                            <h4 style={{marginBottom: '8px'}}>{t('habit.cardFront')}</h4>
                            <Card
                                style={{background: '#f0f5ff'}}
                                styles={{body: {minHeight: '100px'}}}
                            >
                                <div style={{whiteSpace: 'pre-wrap', wordBreak: 'break-word'}}>
                                    {previewCard.frontContent || t('habit.noContent')}
                                </div>
                                {previewCard.frontImageUrls && previewCard.frontImageUrls.length > 0 && (
                                    <div style={{marginTop: '12px'}}>
                                        {previewCard.frontImageUrls.map((url, index) => (
                                            <img
                                                key={index}
                                                src={url}
                                                alt=""
                                                style={{maxWidth: '100%', marginBottom: '8px'}}
                                            />
                                        ))}
                                    </div>
                                )}
                            </Card>
                        </div>
                        <div>
                            <h4 style={{marginBottom: '8px'}}>{t('habit.cardBack')}</h4>
                            <Card
                                style={{background: '#f6ffed'}}
                                styles={{body: {minHeight: '100px'}}}
                            >
                                <div style={{whiteSpace: 'pre-wrap', wordBreak: 'break-word'}}>
                                    {previewCard.backContent || t('habit.noContent')}
                                </div>
                                {previewCard.backImageUrls && previewCard.backImageUrls.length > 0 && (
                                    <div style={{marginTop: '12px'}}>
                                        {previewCard.backImageUrls.map((url, index) => (
                                            <img
                                                key={index}
                                                src={url}
                                                alt=""
                                                style={{maxWidth: '100%', marginBottom: '8px'}}
                                            />
                                        ))}
                                    </div>
                                )}
                            </Card>
                        </div>
                        <div style={{marginTop: '16px'}}>
                            <Row gutter={16}>
                                <Col span={8}>
                                    <Tag color={getMasteryColor(previewCard.masteryLevel)}>
                                        {getMasteryText(previewCard.masteryLevel)}
                                    </Tag>
                                </Col>
                                <Col span={8}>
                                    <span style={{color: '#8c8c8c'}}>
                                        {t('habit.reviewCount')}: {previewCard.reviewCount}
                                    </span>
                                </Col>
                                <Col span={8}>
                                    {previewCard.isImportant === 1 && (
                                        <Tag color="gold"><StarFilled/> {t('habit.important')}</Tag>
                                    )}
                                </Col>
                            </Row>
                        </div>
                    </div>
                )}
            </Modal>
        </Spin>
    )
}

export default CardList
