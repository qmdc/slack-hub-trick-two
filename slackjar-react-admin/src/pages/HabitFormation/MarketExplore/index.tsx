import React, {useEffect, useState} from 'react'
import {
    Card,
    Col,
    Row,
    Button,
    message,
    Spin,
    Tag,
    Input,
    Select,
    Empty,
    Pagination,
    Modal,
    Space,
    Avatar,
    Statistic
} from 'antd'
import {
    LikeOutlined,
    LikeFilled,
    HeartOutlined,
    HeartFilled,
    DownloadOutlined,
    SearchOutlined,
    EyeOutlined,
    ShareAltOutlined
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {
    pageQueryMarketDecks,
    getHotTags,
    toggleLike,
    toggleFavorite,
    downloadDeck,
    getMarketDeckById,
    type MarketDeck
} from '../../../apis/modules/habitFormation'
import {useNavigate} from 'react-router'

const {Search} = Input
const {Option} = Select

const MarketExplore: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [marketDecks, setMarketDecks] = useState<MarketDeck[]>([])
    const [hotTags, setHotTags] = useState<string[]>([])
    const [keyword, setKeyword] = useState('')
    const [selectedTag, setSelectedTag] = useState<string | undefined>()
    const [currentPage, setCurrentPage] = useState(1)
    const [total, setTotal] = useState(0)
    const [pageSize] = useState(12)
    const [sortBy, setSortBy] = useState('downloadCount')

    const [detailModalVisible, setDetailModalVisible] = useState(false)
    const [selectedDeck, setSelectedDeck] = useState<MarketDeck | null>(null)

    useEffect(() => {
        loadData()
    }, [])

    useEffect(() => {
        loadMarketDecks()
    }, [currentPage, keyword, selectedTag, sortBy])

    const loadData = async () => {
        try {
            const tagsRes = await getHotTags(10)
            if (tagsRes.code === 200) {
                setHotTags(tagsRes.data || [])
            }
        } catch (error) {
            console.error('Failed to load hot tags:', error)
        }
    }

    const loadMarketDecks = async () => {
        setLoading(true)
        try {
            const query: any = {
                pageNo: currentPage,
                pageSize: pageSize,
                keyword: keyword || undefined,
                tags: selectedTag ? [selectedTag] : undefined,
                sortBy: sortBy,
                sortOrder: 'desc'
            }

            const res = await pageQueryMarketDecks(query)
            if (res.code === 200) {
                setMarketDecks(res.data?.records || [])
                setTotal(res.data?.total || 0)
            }
        } catch (error) {
            console.error('Failed to load market decks:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const handleSearch = (value: string) => {
        setKeyword(value)
        setCurrentPage(1)
    }

    const handleTagChange = (value: string | undefined) => {
        setSelectedTag(value)
        setCurrentPage(1)
    }

    const handleViewDetail = async (deck: MarketDeck) => {
        try {
            const res = await getMarketDeckById(deck.id)
            if (res.code === 200) {
                setSelectedDeck(res.data)
                setDetailModalVisible(true)
            }
        } catch (error) {
            console.error('Failed to load deck detail:', error)
            setSelectedDeck(deck)
            setDetailModalVisible(true)
        }
    }

    const handleToggleLike = async (deck: MarketDeck, e: React.MouseEvent) => {
        e.stopPropagation()
        try {
            const res = await toggleLike(deck.id)
            if (res.code === 200) {
                setMarketDecks(prev => prev.map(d => {
                    if (d.id === deck.id) {
                    return {
                        ...d,
                        isLiked: !d.isLiked,
                        likeCount: d.isLiked ? d.likeCount - 1 : d.likeCount + 1
                    }
                }
                return d
            }))
            message.success(deck.isLiked ? t('habit.unlikeSuccess') : t('habit.likeSuccess'))
        }
    } catch(error)
    {
        console.error('Failed to toggle like:', error)
        message.error(t('common.operationFailed'))
    }
}

const handleToggleFavorite = async (deck: MarketDeck, e: React.MouseEvent) => {
    e.stopPropagation()
    try {
        const res = await toggleFavorite(deck.id)
        if (res.code === 200) {
            setMarketDecks(prev => prev.map(d => {
                if (d.id === deck.id) {
                    return {
                        ...d,
                        isFavorited: !d.isFavorited,
                        favoriteCount: d.isFavorited ? d.favoriteCount - 1 : d.favoriteCount + 1
                    }
                }
                return d
            }))
            message.success(deck.isFavorited ? t('habit.unfavoriteSuccess') : t('habit.favoriteSuccess'))
        }
    } catch (error) {
        console.error('Failed to toggle favorite:', error)
        message.error(t('common.operationFailed'))
    }
}

const handleDownload = async (deck: MarketDeck, e: React.MouseEvent) => {
    e.stopPropagation()
    try {
        const res = await downloadDeck(deck.id)
        if (res.code === 200) {
            setMarketDecks(prev => prev.map(d => {
                if (d.id === deck.id) {
                    return {
                        ...d,
                        isDownloaded: true,
                        downloadCount: d.downloadCount + 1
                    }
                }
                return d
            }))
            message.success(t('habit.downloadSuccess'))
        } else {
            message.error(res.message || t('habit.downloadFailed'))
        }
    } catch (error) {
        console.error('Failed to download deck:', error)
        message.error(t('common.operationFailed'))
    }
}

const formatNumber = (num: number): string => {
    if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'w'
    }
    return num.toString()
}

return (
    <Spin spinning={loading}>
        <div style={{padding: '24px'}}>
            <div style={{marginBottom: '24px'}}>
                <Row gutter={[16, 16]} align="middle">
                    <Col>
                        <Search
                            placeholder={t('habit.searchMarketDeck')}
                            style={{width: 300}}
                            onSearch={handleSearch}
                            allowClear
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                        />
                    </Col>
                    <Col>
                        <Select
                            placeholder={t('habit.filterByTag')}
                            style={{width: 180}}
                            value={selectedTag}
                            onChange={handleTagChange}
                            allowClear
                        >
                            {hotTags.map(tag => (
                                <Option key={tag} value={tag}>{tag}</Option>
                            ))}
                        </Select>
                    </Col>
                    <Col>
                        <Select
                            style={{width: 150}}
                            value={sortBy}
                            onChange={setSortBy}
                        >
                            <Option value="downloadCount">{t('habit.sortByDownload')}</Option>
                            <Option value="likeCount">{t('habit.sortByLike')}</Option>
                            <Option value="createTime">{t('habit.sortByLatest')}</Option>
                        </Select>
                    </Col>
                </Row>

                {hotTags.length > 0 && (
                    <div style={{marginTop: '16px'}}>
                        <span style={{marginRight: '12px', color: '#8c8c8c'}}>
                            {t('habit.hotTags')}:
                        </span>
                        {hotTags.map(tag => (
                            <Tag
                                key={tag}
                                color={selectedTag === tag ? 'blue' : 'default'}
                                style={{cursor: 'pointer'}}
                                onClick={() => handleTagChange(selectedTag === tag ? undefined : tag)}
                            >
                                {tag}
                            </Tag>
                        ))}
                    </div>
                )}
            </div>

            {marketDecks.length > 0 ? (
                <>
                    <Row gutter={[16, 16]}>
                        {marketDecks.map((deck: MarketDeck) => (
                            <Col xs={24} sm={12} lg={8} xl={6} key={deck.id}>
                                <Card
                                    hoverable
                                    onClick={() => handleViewDetail(deck)}
                                    styles={{body: {padding: '16px'}}}
                                    actions={[
                                        <Space>
                                            <span
                                                onClick={(e) => handleToggleLike(deck, e)}
                                                style={{cursor: 'pointer'}}
                                            >
                                                {deck.isLiked ? <LikeFilled style={{color: '#1890ff'}}/> :
                                                <LikeOutlined/>}
                                                <span style={{marginLeft: '4px'}}>{formatNumber(deck.likeCount)}</span>
                                            </span>
                                        </Space>,
                                        <Space>
                                            <span
                                                onClick={(e) => handleToggleFavorite(deck, e)}
                                                style={{cursor: 'pointer'}}
                                            >
                                                {deck.isFavorited ? <HeartFilled style={{color: '#eb2f96'}}/> :
                                                <HeartOutlined/>}
                                                <span style={{marginLeft: '4px'}}>{formatNumber(deck.favoriteCount)}</span>
                                            </span>
                                        </Space>,
                                        <Space>
                                            <span
                                                onClick={(e) => !deck.isDownloaded && handleDownload(deck, e)}
                                                style={{cursor: deck.isDownloaded ? 'not-allowed' : 'pointer', opacity: deck.isDownloaded ? 0.5 : 1}}
                                            >
                                                <DownloadOutlined/>
                                                <span style={{marginLeft: '4px'}}>{formatNumber(deck.downloadCount)}</span>
                                                {deck.isDownloaded && <span style={{fontSize: '10px', color: '#52c41a'}}>
                                                    {t('habit.alreadyDownloaded')}
                                                </span>}
                                            </span>
                                        </Space>
                                    ]}
                                >
                                    <div style={{marginBottom: '12px'}}>
                                        <div style={{display: 'flex', alignItems: 'center', marginBottom: '8px'}}>
                                            <Avatar
                                                size={32}
                                                src={deck.userAvatarUrl}
                                                style={{marginRight: '8px'}}
                                            >
                                                {deck.userNickname?.charAt(0) || 'U'}
                                            </Avatar>
                                            <span style={{fontSize: '12px', color: '#8c8c8c'}}>
                                                {deck.userNickname || t('habit.unknownUser')}
                                            </span>
                                        </div>
                                        <div style={{fontSize: '16px', fontWeight: '500', marginBottom: '8px'}}>
                                            {deck.name}
                                        </div>
                                        <div style={{
                                            fontSize: '12px',
                                            color: '#8c8c8c',
                                            marginBottom: '12px',
                                            display: '-webkit-box',
                                            WebkitLineClamp: 2,
                                            WebkitBoxOrient: 'vertical',
                                            overflow: 'hidden'
                                        }}>
                                            {deck.description || t('habit.noDescription')}
                                        </div>
                                        {deck.tags && deck.tags.length > 0 && (
                                            <Space size={[4, 4]} wrap style={{marginBottom: '8px'}}>
                                                {deck.tags.slice(0, 3).map((tag, index) => (
                                                    <Tag key={index} color="blue" style={{fontSize: '11px'}}>
                                                        {tag}
                                                    </Tag>
                                                ))}
                                                {deck.tags.length > 3 && (
                                                    <Tag style={{fontSize: '11px'}}>
                                                        +{deck.tags.length - 3}
                                                    </Tag>
                                                )}
                                            </Space>
                                        )}
                                    </div>
                                    <Row justify="space-between" align="middle">
                                        <Col>
                                            <Tag color="blue">
                                                <FileTextOutlined style={{marginRight: '4px'}}/>
                                                {deck.cardCount} {t('habit.cards')}
                                            </Tag>
                                        </Col>
                                        {deck.avgRating && (
                                            <Col style={{fontSize: '12px', color: '#faad14'}}>
                                                ★ {deck.avgRating.toFixed(1)}
                                            </Col>
                                        )}
                                    </Row>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                    <div style={{marginTop: '24px', textAlign: 'center'}}>
                        <Pagination
                            current={currentPage}
                            pageSize={pageSize}
                            total={total}
                            showSizeChanger={false}
                            showQuickJumper
                            showTotal={(total) => t('common.totalRecords', {total})}
                            onChange={(page) => setCurrentPage(page)}
                        />
                    </div>
                </>
            ) : (
                <Empty
                    description={t('habit.noMarketDecks')}
                    style={{padding: '60px 0'}}
                >
                    <Button
                        type="primary"
                        icon={<ShareAltOutlined/>}
                        onClick={() => navigate('/habit-formation/deck/list')}
                    >
                        {t('habit.shareYourDeck')}
                    </Button>
                </Empty>
            )}
        </div>

        <Modal
            title={t('habit.deckDetail')}
            open={detailModalVisible}
            onCancel={() => setDetailModalVisible(false)}
            footer={
                selectedDeck ? [
                    <Button key="download" type="primary" onClick={(e) => handleDownload(selectedDeck, e)}
                            disabled={selectedDeck.isDownloaded}
                            icon={<DownloadOutlined/>}
                    >
                        {selectedDeck.isDownloaded ? t('habit.alreadyDownloaded') : t('habit.downloadNow')}
                    </Button>
                ] : null
            }
            width={600}
        >
            {selectedDeck && (
                <div>
                    <div style={{display: 'flex', alignItems: 'center', marginBottom: '16px'}}>
                        <Avatar
                            size={48}
                            src={selectedDeck.userAvatarUrl}
                            style={{marginRight: '12px'}}
                        >
                            {selectedDeck.userNickname?.charAt(0) || 'U'}
                        </Avatar>
                        <div>
                            <div style={{fontWeight: '500'}}>{selectedDeck.userNickname}</div>
                            <div style={{fontSize: '12px', color: '#8c8c8c'}}>
                                {new Date(selectedDeck.createTime).toLocaleDateString()}
                            </div>
                        </div>
                    </div>

                    <h3 style={{marginBottom: '8px'}}>{selectedDeck.name}</h3>
                    <p style={{color: '#8c8c8c', marginBottom: '16px'}}>
                        {selectedDeck.description || t('habit.noDescription')}
                    </p>

                    {selectedDeck.tags && selectedDeck.tags.length > 0 && (
                        <div style={{marginBottom: '16px'}}>
                            {selectedDeck.tags.map((tag, index) => (
                                <Tag key={index} color="blue">{tag}</Tag>
                            ))}
                        </div>
                    )}

                    <Row gutter={16} style={{marginTop: '24px'}}>
                        <Col span={6}>
                            <Statistic
                                title={t('habit.cards')}
                                value={selectedDeck.cardCount}
                            />
                        </Col>
                        <Col span={6}>
                            <Statistic
                                title={t('habit.downloads')}
                                value={formatNumber(selectedDeck.downloadCount)}
                            />
                        </Col>
                        <Col span={6}>
                            <Statistic
                                title={t('habit.likes')}
                                value={formatNumber(selectedDeck.likeCount)}
                            />
                        </Col>
                        <Col span={6}>
                            <Statistic
                                title={t('habit.favorites')}
                                value={formatNumber(selectedDeck.favoriteCount)}
                            />
                        </Col>
                    </Row>
                </div>
            )}
        </Modal>
    </Spin>
)
}

export default MarketExplore
