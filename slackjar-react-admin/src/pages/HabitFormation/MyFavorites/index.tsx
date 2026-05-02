import React, {useEffect, useState} from 'react'
import {
    Card,
    Col,
    Row,
    Button,
    message,
    Spin,
    Tag,
    Empty,
    Avatar,
    Statistic,
    Popconfirm
} from 'antd'
import {
    LikeOutlined,
    HeartFilled,
    DownloadOutlined,
    DeleteOutlined,
    ArrowLeftOutlined
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {
    getMyFavorites,
    toggleFavorite,
    downloadDeck,
    type MarketDeck
} from '../../../apis/modules/habitFormation'
import {useNavigate} from 'react-router'

const MyFavorites: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [favorites, setFavorites] = useState<MarketDeck[]>([])

    useEffect(() => {
        loadData()
    }, [])

    const loadData = async () => {
        setLoading(true)
        try {
            const res = await getMyFavorites()
            if (res.code === 200) {
                setFavorites(res.data || [])
            }
        } catch (error) {
            console.error('Failed to load my favorites:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const handleRemoveFavorite = async (deck: MarketDeck, e: React.MouseEvent) => {
        e.stopPropagation()
        try {
            const res = await toggleFavorite(deck.id)
            if (res.code === 200) {
                setFavorites(prev => prev.filter(d => d.id !== deck.id))
                message.success(t('habit.unfavoriteSuccess'))
            }
        } catch (error) {
            console.error('Failed to remove favorite:', error)
            message.error(t('common.operationFailed'))
        }
    }

    const handleDownload = async (deck: MarketDeck, e: React.MouseEvent) => {
        e.stopPropagation()
        if (deck.isDownloaded) {
            message.info(t('habit.alreadyDownloaded'))
            return
        }
        try {
            const res = await downloadDeck(deck.id)
            if (res.code === 200) {
                setFavorites(prev => prev.map(d => {
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
                <div style={{marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                    <Button
                        icon={<ArrowLeftOutlined/>}
                        onClick={() => navigate('/habit-formation/market/explore')}
                    >
                        {t('common.back')}
                    </Button>
                    <div style={{fontSize: '18px', fontWeight: '500'}}>
                        <HeartFilled style={{marginRight: '8px', color: '#eb2f96'}}/>
                        {t('habit.myFavorites')}
                    </div>
                    <div>
                        <Button onClick={loadData}>{t('common.refresh')}</Button>
                    </div>
                </div>

                {favorites.length > 0 ? (
                    <Row gutter={[16, 16]}>
                        {favorites.map((deck: MarketDeck) => (
                            <Col xs={24} sm={12} lg={8} xl={6} key={deck.id}>
                                <Card
                                    styles={{body: {padding: '16px'}}}
                                    actions={[
                                        <Button
                                            type="link"
                                            onClick={(e) => handleDownload(deck, e)}
                                            disabled={deck.isDownloaded}
                                            icon={<DownloadOutlined/>}
                                        >
                                            {deck.isDownloaded ? t('habit.alreadyDownloaded') : t('habit.download')}
                                        </Button>,
                                        <Popconfirm
                                            title={t('habit.confirmRemoveFavorite')}
                                            onConfirm={(e) => e && handleRemoveFavorite(deck, e as unknown as React.MouseEvent)}
                                            okText={t('common.confirm')}
                                            cancelText={t('common.cancel')}
                                        >
                                            <Button
                                                type="link"
                                                danger
                                                onClick={(e) => e.stopPropagation()}
                                                icon={<DeleteOutlined/>}
                                            >
                                                {t('habit.remove')}
                                            </Button>
                                        </Popconfirm>
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
                                            <div style={{marginBottom: '8px'}}>
                                                {deck.tags.slice(0, 3).map((tag, index) => (
                                                    <Tag key={index} color="blue" style={{fontSize: '11px'}}>
                                                        {tag}
                                                    </Tag>
                                                ))}
                                            </div>
                                        )}
                                    </div>
                                    <Row gutter={16}>
                                        <Col span={8}>
                                            <Statistic
                                                title={t('habit.cards')}
                                                value={deck.cardCount}
                                                valueStyle={{fontSize: '16px'}}
                                            />
                                        </Col>
                                        <Col span={8}>
                                            <Statistic
                                                title={t('habit.likes')}
                                                value={formatNumber(deck.likeCount)}
                                                prefix={<LikeOutlined/>}
                                                valueStyle={{fontSize: '14px'}}
                                            />
                                        </Col>
                                        <Col span={8}>
                                            <Statistic
                                                title={t('habit.downloads')}
                                                value={formatNumber(deck.downloadCount)}
                                                prefix={<DownloadOutlined/>}
                                                valueStyle={{fontSize: '14px'}}
                                            />
                                        </Col>
                                    </Row>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                ) : (
                    <Empty
                        description={t('habit.noFavorites')}
                        style={{padding: '60px 0'}}
                    >
                        <Button
                            type="primary"
                            onClick={() => navigate('/habit-formation/market/explore')}
                        >
                            {t('habit.browseMarket')}
                        </Button>
                    </Empty>
                )}
            </div>
        </Spin>
    )
}

export default MyFavorites
