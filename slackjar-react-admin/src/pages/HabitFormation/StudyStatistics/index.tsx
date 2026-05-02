import React, {useEffect, useState} from 'react'
import {
    Card,
    Col,
    Row,
    Statistic,
    message,
    Spin,
    DatePicker,
    Tabs,
    TabsProps,
    Table,
    TableColumnsType,
    Button
} from 'antd'
import {
    TrophyOutlined,
    BookOutlined,
    CheckCircleOutlined,
    ClockCircleOutlined,
    RiseOutlined,
    ArrowLeftOutlined,
    FileTextOutlined
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {
    getStudyDashboard,
    getRecentStats,
    getTodayStat,
    type StudyDashboard,
    type StudyStat
} from '../../../apis/modules/habitFormation'
import {useNavigate} from 'react-router'
import dayjs, {Dayjs} from 'dayjs'

const {RangePicker} = DatePicker

const StudyStatistics: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [dashboard, setDashboard] = useState<StudyDashboard | null>(null)
    const [recentStats, setRecentStats] = useState<StudyStat[]>([])
    const [todayStat, setTodayStat] = useState<StudyStat | null>(null)
    const [activeTab, setActiveTab] = useState('overview')
    const [dateRange, setDateRange] = useState<[Dayjs, Dayjs] | null>(null)

    const tabItems: TabsProps['items'] = [
        {key: 'overview', label: t('habit.statOverview')},
        {key: 'daily', label: t('habit.statDaily')},
        {key: 'trend', label: t('habit.statTrend')}
    ]

    useEffect(() => {
        loadData()
    }, [])

    const loadData = async () => {
        setLoading(true)
        try {
            const [dashboardRes, recentRes, todayRes] = await Promise.all([
                getStudyDashboard(),
                getRecentStats(30),
                getTodayStat()
            ])
            if (dashboardRes.code === 200) {
                setDashboard(dashboardRes.data)
            }
            if (recentRes.code === 200) {
                setRecentStats(recentRes.data || [])
            }
            if (todayRes.code === 200) {
                setTodayStat(todayRes.data)
            }
        } catch (error) {
            console.error('Failed to load statistics:', error)
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

    const columns: TableColumnsType<StudyStat> = [
        {
            title: t('habit.statDate'),
            dataIndex: 'statDate',
            key: 'statDate',
            width: 120,
            render: (text: string) => text
        },
        {
            title: t('habit.newCardsLearned'),
            dataIndex: 'newCards',
            key: 'newCards',
            width: 100
        },
        {
            title: t('habit.reviewedCards'),
            dataIndex: 'reviewedCards',
            key: 'reviewedCards',
            width: 100
        },
        {
            title: t('habit.learnedTotal'),
            dataIndex: 'learnedCards',
            key: 'learnedCards',
            width: 100
        },
        {
            title: t('habit.correctCount'),
            dataIndex: 'correctCount',
            key: 'correctCount',
            width: 80
        },
        {
            title: t('habit.incorrectCount'),
            dataIndex: 'incorrectCount',
            key: 'incorrectCount',
            width: 80
        },
        {
            title: t('habit.accuracyRate'),
            dataIndex: 'accuracyRate',
            key: 'accuracyRate',
            width: 100,
            render: (value: number) => `${Math.round(value || 0)}%`
        },
        {
            title: t('habit.studyDuration'),
            dataIndex: 'studyDuration',
            key: 'studyDuration',
            width: 120,
            render: (value: number) => formatDuration(value || 0)
        }
    ]

    return (
        <Spin spinning={loading}>
            <div style={{padding: '24px'}}>
                <div style={{marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                    <Button
                        icon={<ArrowLeftOutlined/>}
                        onClick={() => navigate('/habit-formation/dashboard')}
                    >
                        {t('common.back')}
                    </Button>
                    <div style={{fontSize: '20px', fontWeight: '500'}}>
                        {t('habit.studyStatistics')}
                    </div>
                    <div>
                        <RangePicker
                            value={dateRange}
                            onChange={(dates) => setDateRange(dates as [Dayjs, Dayjs] | null)}
                            style={{width: 280}}
                        />
                    </div>
                </div>

                <Tabs activeKey={activeTab} onChange={setActiveTab} items={tabItems}/>

                {activeTab === 'overview' && (
                    <div>
                        <Row gutter={[16, 16]} style={{marginBottom: '16px'}}>
                            <Col xs={24} sm={12} lg={6}>
                                <Card>
                                    <Statistic
                                        title={t('habit.totalDecks')}
                                        value={dashboard?.totalDecks || 0}
                                        prefix={<BookOutlined/>}
                                        valueStyle={{color: '#1890ff'}}
                                    />
                                </Card>
                            </Col>
                            <Col xs={24} sm={12} lg={6}>
                                <Card>
                                    <Statistic
                                        title={t('habit.totalCards')}
                                        value={dashboard?.totalCards || 0}
                                        prefix={<FileTextOutlined/>}
                                        valueStyle={{color: '#52c41a'}}
                                    />
                                </Card>
                            </Col>
                            <Col xs={24} sm={12} lg={6}>
                                <Card>
                                    <Statistic
                                        title={t('habit.totalMastered')}
                                        value={dashboard?.totalMasteredCards || 0}
                                        prefix={<TrophyOutlined/>}
                                        valueStyle={{color: '#722ed1'}}
                                    />
                                </Card>
                            </Col>
                            <Col xs={24} sm={12} lg={6}>
                                <Card>
                                    <Statistic
                                        title={t('habit.overallMasteryRate')}
                                        value={Math.round(dashboard?.overallMasteryRate || 0)}
                                        prefix={<RiseOutlined/>}
                                        suffix="%"
                                        valueStyle={{
                                            color: (dashboard?.overallMasteryRate || 0) >= 70 ? '#52c41a' : '#faad14'
                                        }}
                                    />
                                </Card>
                            </Col>
                        </Row>

                        <Row gutter={[16, 16]}>
                            <Col xs={24} sm={12} lg={8}>
                                <Card title={t('habit.todaySummary')}>
                                    <Row gutter={[16, 16]}>
                                        <Col span={12}>
                                            <Statistic
                                                title={t('habit.todayReviewed')}
                                                value={todayStat?.reviewedCards || dashboard?.todayReviewed || 0}
                                                valueStyle={{fontSize: '20px'}}
                                            />
                                        </Col>
                                        <Col span={12}>
                                            <Statistic
                                                title={t('habit.todayNewCards')}
                                                value={todayStat?.newCards || dashboard?.todayNewCards || 0}
                                                valueStyle={{fontSize: '20px'}}
                                            />
                                        </Col>
                                        <Col span={12}>
                                            <Statistic
                                                title={t('habit.todayCorrect')}
                                                value={todayStat?.correctCount || dashboard?.todayCorrectCount || 0}
                                                valueStyle={{fontSize: '20px', color: '#52c41a'}}
                                            />
                                        </Col>
                                        <Col span={12}>
                                            <Statistic
                                                title={t('habit.todayAccuracy')}
                                                value={Math.round(todayStat?.accuracyRate || dashboard?.todayAccuracyRate || 0)}
                                                suffix="%"
                                                valueStyle={{fontSize: '20px'}}
                                            />
                                        </Col>
                                        <Col span={24}>
                                            <Statistic
                                                title={t('habit.todayStudyTime')}
                                                value={formatDuration(todayStat?.studyDuration || dashboard?.todayStudyDuration || 0)}
                                                prefix={<ClockCircleOutlined/>}
                                            />
                                        </Col>
                                    </Row>
                                </Card>
                            </Col>
                            <Col xs={24} sm={12} lg={8}>
                                <Card title={t('habit.totalAccumulated')}>
                                    <Row gutter={[16, 16]}>
                                        <Col span={12}>
                                            <Statistic
                                                title={t('habit.totalReviewed')}
                                                value={todayStat?.totalReviewedCards || dashboard?.totalReviewedCards || 0}
                                                valueStyle={{fontSize: '20px', color: '#1890ff'}}
                                            />
                                        </Col>
                                        <Col span={12}>
                                            <Statistic
                                                title={t('habit.totalLearned')}
                                                value={todayStat?.totalLearnedCards || 0}
                                                valueStyle={{fontSize: '20px'}}
                                            />
                                        </Col>
                                        <Col span={24}>
                                            <Statistic
                                                title={t('habit.totalStudyTime')}
                                                value={formatDuration(todayStat?.totalStudyDuration || 0)}
                                                prefix={<ClockCircleOutlined/>}
                                            />
                                        </Col>
                                    </Row>
                                </Card>
                            </Col>
                            <Col xs={24} sm={12} lg={8}>
                                <Card title={t('habit.todayPending')}>
                                    <Statistic
                                        title={t('habit.todayPendingReview')}
                                        value={todayStat?.todayPendingReview || dashboard?.todayPendingReview || 0}
                                        valueStyle={{
                                            fontSize: '32px',
                                            color: (todayStat?.todayPendingReview || dashboard?.todayPendingReview || 0) > 0 ? '#ff4d4f' : '#52c41a'
                                        }}
                                        suffix={t('habit.cards')}
                                    />
                                    <div style={{marginTop: '16px', color: '#8c8c8c'}}>
                                        {t('habit.streakDays', {days: 1})}
                                    </div>
                                </Card>
                            </Col>
                        </Row>
                    </div>
                )}

                {activeTab === 'daily' && (
                    <div>
                        <Table
                            columns={columns}
                            dataSource={recentStats}
                            rowKey="id"
                            pagination={{
                                pageSize: 10,
                                showSizeChanger: true,
                                showQuickJumper: true,
                                showTotal: (total) => t('common.totalRecords', {total})
                            }}
                        />
                    </div>
                )}

                {activeTab === 'trend' && (
                    <div style={{padding: '40px', textAlign: 'center', color: '#8c8c8c'}}>
                        <RiseOutlined style={{fontSize: '48px', marginBottom: '16px'}}/>
                        <div>{t('habit.trendChartComing')}</div>
                        <div style={{marginTop: '16px', fontSize: '12px'}}>
                            {t('habit.trendChartDesc')}
                        </div>
                    </div>
                )}
            </div>
        </Spin>
    )
}

export default StudyStatistics
