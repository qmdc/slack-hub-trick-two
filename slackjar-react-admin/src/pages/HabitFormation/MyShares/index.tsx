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
    Statistic
} from 'antd'
import {
    LikeOutlined,
    HeartOutlined,
    DownloadOutlined,
    ShareAltOutlined,
    ArrowLeftOutlined
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {
    getMyShares,
    type MarketDeck
} from '../../../apis/modules/habitFormation'
import {useNavigate} from 'react-router'

const MyShares: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [shares, setShares] = useState<MarketDeck[]>([])

    useEffect(() => {
        loadData()
    }, [])

    const loadData = async () => {
        setLoading(true)
        try {
            const res = await getMyShares()
            if (res.code === 200) {
                setShares(res.data || [])
            }
        } catch (error) {
            console.error('Failed to load my shares:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
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
                        <ShareAltOutlined style={{marginRight: '8px'}}/>
                        {t('habit.myShares')}
                    </div>
                    <div>
                        <Button onClick={loadData}>{t('common.refresh')}</Button>
                    </div>
                </div>

                {shares.length > 0 ? (
                    <Row gutter={[16, 16]}>
                        {shares.map((deck: MarketDeck) => (
                            <Col xs={24} sm={12} lg={8} xl={6} key={deck.id}>
                                <Card
                                    styles={{body: {padding: '16px'}}}
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
                                                {new Date(deck.createTime).toLocaleDateString()}
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
                        description={t('habit.noShares')}
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
        </Spin>
    )
}

export default MyShares
