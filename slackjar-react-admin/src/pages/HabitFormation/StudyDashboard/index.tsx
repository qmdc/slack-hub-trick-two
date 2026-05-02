import React, {useEffect, useState} from 'react'
import {
    Card,
    Col,
    Row,
    Statistic,
    Button,
    Progress,
    Empty,
    Spin,
    Tag,
    Avatar,
    message,
    Space
} from 'antd'
import {
    BookOutlined,
    ClockCircleOutlined,
    CheckCircleOutlined,
    FileTextOutlined,
    TrophyOutlined,
    ThunderboltOutlined,
    RiseOutlined,
    HistoryOutlined,
    ShopOutlined
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {getStudyDashboard, type StudyDashboard as StudyDashboardType, type Deck, type Card as CardType} from '../../../apis/modules/habitFormation'
import {useNavigate} from 'react-router'

const StudyDashboard: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [dashboard, setDashboard] = useState<StudyDashboardType | null>(null)

    useEffect(() => {
        loadDashboard()
    }, [])

    const loadDashboard = async () => {
        setLoading(true)
        try {
            const res = await getStudyDashboard()
            if (res.code === 200) {
                setDashboard(res.data)
            } else {
                message.error(res.message || t('common.loadFailed'))
            }
        } catch (error) {
            console.error('Failed to load dashboard:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const formatDuration = (seconds: number): string => {
        if (!seconds) return '0分钟'
        const hours = Math.floor(seconds / 3600)
        const minutes = Math.floor((seconds % 3600) / 60)
        if (hours > 0) {
            return `${hours}小时${minutes}分钟`
        }
        return `${minutes}分钟`
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
                <div style={{marginBottom: '24px'}}>
                    <Row gutter={[16, 16]}>
                        <Col xs={24} sm={12} lg={6}>
                            <Card>
                                <Statistic
                                    title={t('habit.todayPendingReview')}
                                    value={dashboard?.todayPendingReview || 0}
                                    prefix={<BookOutlined/>}
                                    valueStyle={{color: '#1890ff'}}
                                    suffix={<span style={{fontSize: '14px'}}>{t('habit.cards')}</span>}
                                />
                                {dashboard?.todayPendingReview ? (
                                    <Button
                                        type="primary"
                                        style={{marginTop: '12px', width: '100%'}}
                                        onClick={() => navigate('/habit-formation/study/review')}
                                    >
                                        {t('habit.startReview')}
                                    </Button>
                                ) : (
                                    <Button
                                        style={{marginTop: '12px', width: '100%'}}
                                        onClick={() => navigate('/habit-formation/study/new')}
                                    >
                                        {t('habit.startNewCards')}
                                    </Button>
                                )}
                            </Card>
                        </Col>
                        <Col xs={24} sm={12} lg={6}>
                            <Card>
                                <Statistic
                                    title={t('habit.todayReviewed')}
                                    value={dashboard?.todayReviewed || 0}
                                    prefix={<CheckCircleOutlined/>}
                                    valueStyle={{color: '#52c41a'}}
                                    suffix={<span style={{fontSize: '14px'}}>{t('habit.cards')}</span>}
                                />
                                {dashboard && dashboard.todayReviewed > 0 && (
                                    <div style={{marginTop: '12px'}}>
                                        <Progress
                                            percent={Math.round(dashboard.todayAccuracyRate)}
                                            size="small"
                                            format={(percent) => `${t('habit.accuracyRate')}: ${percent}%`}
                                        />
                                    </div>
                                )}
                            </Card>
                        </Col>
                        <Col xs={24} sm={12} lg={6}>
                            <Card>
                                <Statistic
                                    title={t('habit.todayStudyTime')}
                                    value={formatDuration(dashboard?.todayStudyDuration || 0)}
                                    prefix={<ClockCircleOutlined/>}
                                    valueStyle={{color: '#722ed1'}}
                                />
                                <div style={{marginTop: '12px', color: '#8c8c8c', fontSize: '12px'}}>
                                    {t('habit.newCardsToday')}: {dashboard?.todayNewCards || 0} {t('habit.cards')}
                                </div>
                            </Card>
                        </Col>
                        <Col xs={24} sm={12} lg={6}>
                            <Card>
                                <Statistic
                                    title={t('habit.overallMasteryRate')}
                                    value={Math.round(dashboard?.overallMasteryRate || 0)}
                                    prefix={<TrophyOutlined/>}
                                    valueStyle={{color: getMasteryColor(dashboard?.overallMasteryRate || 0)}}
                                    suffix="%"
                                />
                                <div style={{marginTop: '12px'}}>
                                    <Progress
                                        percent={Math.round(dashboard?.overallMasteryRate || 0)}
                                        size="small"
                                        strokeColor={getMasteryColor(dashboard?.overallMasteryRate || 0)}
                                    />
                                </div>
                            </Card>
                        </Col>
                    </Row>
                </div>

                <Row gutter={[16, 16]}>
                    <Col xs={24} lg={16}>
                        <Card
                            title={
                                <Space>
                                    <ThunderboltOutlined/>
                                    <span>{t('habit.recentDecks')}</span>
                                </Space>
                            }
                            extra={
                                <Button
                                    type="link"
                                    onClick={() => navigate('/habit-formation/deck/list')}
                                >
                                    {t('habit.viewAll')}
                                </Button>
                            }
                        >
                            {dashboard?.recentDecks && dashboard.recentDecks.length > 0 ? (
                                <Row gutter={[16, 16]}>
                                    {dashboard.recentDecks.map((deck: Deck) => (
                                        <Col xs={24} sm={12} md={8} key={deck.id}>
                                            <Card
                                                size="small"
                                                hoverable
                                                onClick={() => navigate(`/habit-formation/deck/card/${deck.id}`)}
                                                styles={{body: {padding: '12px'}}}
                                            >
                                                <div style={{marginBottom: '8px', fontWeight: '500'}}>
                                                    {deck.name}
                                                </div>
                                                <div style={{fontSize: '12px', color: '#8c8c8c', marginBottom: '8px'}}>
                                                    {deck.description || t('habit.noDescription')}
                                                </div>
                                                <Row justify="space-between" align="middle">
                                                    <Col>
                                                        <Tag color="blue">{deck.cardCount} {t('habit.cards')}</Tag>
                                                    </Col>
                                                    <Col>
                                                        {deck.todayReviewCount > 0 && (
                                                            <Tag color="red">
                                                                {deck.todayReviewCount} {t('habit.toReview')}
                                                            </Tag>
                                                        )}
                                                    </Col>
                                                </Row>
                                                <div style={{marginTop: '8px'}}>
                                                    <Progress
                                                        percent={Math.round(deck.masteryRate)}
                                                        size="small"
                                                        showInfo={false}
                                                    />
                                                    <div style={{fontSize: '11px', color: '#8c8c8c', textAlign: 'right'}}>
                                                        {t('habit.masteryRate')}: {Math.round(deck.masteryRate)}%
                                                    </div>
                                                </div>
                                            </Card>
                                        </Col>
                                    ))}
                                </Row>
                            ) : (
                                <Empty
                                    description={t('habit.noDecks')}
                                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                                >
                                    <Button
                                        type="primary"
                                        onClick={() => navigate('/habit-formation/deck/list')}
                                    >
                                        {t('habit.createFirstDeck')}
                                    </Button>
                                </Empty>
                            )}
                        </Card>
                    </Col>

                    <Col xs={24} lg={8}>
                        <Card
                            title={
                                <Space>
                                    <RiseOutlined/>
                                    <span>{t('habit.quickStart')}</span>
                                </Space>
                            }
                        >
                            <div style={{display: 'flex', flexDirection: 'column', gap: '12px'}}>
                                <Button
                                    size="large"
                                    type="primary"
                                    icon={<BookOutlined/>}
                                    onClick={() => navigate('/habit-formation/study/review')}
                                    disabled={!dashboard || dashboard.todayPendingReview === 0}
                                >
                                    {t('habit.reviewNow')}
                                    {dashboard?.todayPendingReview && dashboard.todayPendingReview > 0 && (
                                        <Tag color="red" style={{marginLeft: '8px'}}>
                                            {dashboard.todayPendingReview}
                                        </Tag>
                                    )}
                                </Button>
                                <Button
                                    size="large"
                                    icon={<FileTextOutlined/>}
                                    onClick={() => navigate('/habit-formation/study/new')}
                                >
                                    {t('habit.learnNewCards')}
                                </Button>
                                <Button
                                    size="large"
                                    icon={<HistoryOutlined/>}
                                    onClick={() => navigate('/habit-formation/statistics')}
                                >
                                    {t('habit.viewStatistics')}
                                </Button>
                                <Button
                                    size="large"
                                    icon={<ShopOutlined/>}
                                    onClick={() => navigate('/habit-formation/market/explore')}
                                >
                                    {t('habit.browseMarket')}
                                </Button>
                            </div>
                        </Card>

                        <Card
                            title={
                                <Space>
                                    <TrophyOutlined/>
                                    <span>{t('habit.learningSummary')}</span>
                                </Space>
                            }
                            style={{marginTop: '16px'}}
                        >
                            <Row gutter={[16, 16]}>
                                <Col span={12}>
                                    <Statistic
                                        title={t('habit.totalDecks')}
                                        value={dashboard?.totalDecks || 0}
                                        size="small"
                                    />
                                </Col>
                                <Col span={12}>
                                    <Statistic
                                        title={t('habit.totalCards')}
                                        value={dashboard?.totalCards || 0}
                                        size="small"
                                    />
                                </Col>
                                <Col span={12}>
                                    <Statistic
                                        title={t('habit.totalMastered')}
                                        value={dashboard?.totalMasteredCards || 0}
                                        size="small"
                                        valueStyle={{color: '#52c41a'}}
                                    />
                                </Col>
                                <Col span={12}>
                                    <Statistic
                                        title={t('habit.totalReviewed')}
                                        value={dashboard?.totalReviewedCards || 0}
                                        size="small"
                                        valueStyle={{color: '#1890ff'}}
                                    />
                                </Col>
                            </Row>
                        </Card>
                    </Col>
                </Row>

                {dashboard?.todayReviewCards && dashboard.todayReviewCards.length > 0 && (
                    <Card
                        title={
                            <Space>
                                <ClockCircleOutlined/>
                                <span>{t('habit.todayReviewPreview')}</span>
                            </Space>
                        }
                        style={{marginTop: '16px'}}
                    >
                        <Row gutter={[16, 16]}>
                            {dashboard.todayReviewCards.slice(0, 4).map((card: CardType) => (
                                <Col xs={24} sm={12} lg={6} key={card.id}>
                                    <Card
                                        size="small"
                                        hoverable
                                        styles={{body: {height: '120px', overflow: 'hidden'}}}
                                    >
                                        <div
                                            style={{
                                                fontSize: '13px',
                                                fontWeight: '500',
                                                marginBottom: '8px',
                                                display: '-webkit-box',
                                                WebkitLineClamp: 2,
                                                WebkitBoxOrient: 'vertical',
                                                overflow: 'hidden'
                                            }}
                                        >
                                            {card.frontContent || t('habit.noContent')}
                                        </div>
                                        <Tag color={card.masteryLevel >= 4 ? 'green' : card.masteryLevel >= 2 ? 'orange' : 'red'}>
                                            {card.masteryLevelDesc || `Level ${card.masteryLevel}`}
                                        </Tag>
                                        {card.isImportant === 1 && (
                                            <Tag color="red">{t('habit.important')}</Tag>
                                        )}
                                    </Card>
                                </Col>
                            ))}
                        </Row>
                    </Card>
                )}
            </div>
        </Spin>
    )
}

export default StudyDashboard
