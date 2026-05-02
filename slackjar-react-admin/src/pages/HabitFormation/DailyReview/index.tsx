import React, {useEffect, useState, useCallback} from 'react'
import {
    Card,
    Button,
    message,
    Spin,
    Tag,
    Row,
    Col,
    Modal,
    Progress,
    Result,
    Statistic,
    Empty,
    Space
} from 'antd'
import {
    CheckCircleOutlined,
    CloseCircleOutlined,
    EyeOutlined,
    ReloadOutlined,
    TrophyOutlined,
    ClockCircleOutlined,
    ArrowLeftOutlined
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {
    getTodayReviewCards,
    reviewCard,
    getMyDecks,
    type Card as CardType,
    type Deck,
    type ReviewRating
} from '../../../apis/modules/habitFormation'
import {useNavigate} from 'react-router'

const DailyReview: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [cards, setCards] = useState<CardType[]>([])
    const [decks, setDecks] = useState<Deck[]>([])
    const [currentIndex, setCurrentIndex] = useState(0)
    const [showAnswer, setShowAnswer] = useState(false)
    const [reviewStartTime, setReviewStartTime] = useState<number>(0)
    const [completedCards, setCompletedCards] = useState<CardType[]>([])
    const [correctCount, setCorrectCount] = useState(0)
    const [showResult, setShowResult] = useState(false)

    const currentCard = cards[currentIndex]
    const progress = cards.length > 0 ? ((completedCards.length) / cards.length) * 100 : 0

    useEffect(() => {
        loadData()
    }, [])

    const loadData = async () => {
        setLoading(true)
        try {
            const [cardsRes, decksRes] = await Promise.all([
                getTodayReviewCards(undefined, 50),
                getMyDecks()
            ])
            if (cardsRes.code === 200) {
                setCards(cardsRes.data || [])
            }
            if (decksRes.code === 200) {
                setDecks(decksRes.data || [])
            }
        } catch (error) {
            console.error('Failed to load review cards:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const startReview = useCallback(() => {
        setCurrentIndex(0)
        setShowAnswer(false)
        setCompletedCards([])
        setCorrectCount(0)
        setShowResult(false)
        setReviewStartTime(Date.now())
    }, [])

    const showAnswerHandler = () => {
        setShowAnswer(true)
    }

    const getDeckName = (deckId: number): string => {
        const deck = decks.find(d => d.id === deckId)
        return deck?.name || ''
    }

    const handleRating = async (rating: ReviewRating) => {
        if (!currentCard) return

        const reviewDuration = reviewStartTime > 0
            ? Math.floor((Date.now() - reviewStartTime) / 1000)
            : undefined

        try {
            const res = await reviewCard({
                cardId: currentCard.id,
                rating: rating as number,
                reviewDuration
            })

            if (res.code === 200) {
                if (res.data?.isCorrect) {
                    setCorrectCount(prev => prev + 1)
                }

                setCompletedCards(prev => [...prev, currentCard])

                if (currentIndex < cards.length - 1) {
                    setCurrentIndex(prev => prev + 1)
                    setShowAnswer(false)
                    setReviewStartTime(Date.now())
                } else {
                    setShowResult(true)
                }
            } else {
                message.error(res.message || t('common.operationFailed'))
            }
        } catch (error) {
            console.error('Failed to review card:', error)
            message.error(t('common.operationFailed'))
        }
    }

    const getRatingOptions = () => {
        return [
            {
                rating: 0 as ReviewRating,
                label: t('habit.ratingForgot'),
                color: '#ff4d4f',
                icon: <CloseCircleOutlined/>,
                desc: t('habit.ratingForgotDesc')
            },
            {
                rating: 1 as ReviewRating,
                label: t('habit.ratingVeryHard'),
                color: '#fa8c16',
                icon: <CloseCircleOutlined/>,
                desc: t('habit.ratingVeryHardDesc')
            },
            {
                rating: 2 as ReviewRating,
                label: t('habit.ratingHard'),
                color: '#faad14',
                icon: <CloseCircleOutlined/>,
                desc: t('habit.ratingHardDesc')
            },
            {
                rating: 3 as ReviewRating,
                label: t('habit.ratingMedium'),
                color: '#1890ff',
                icon: <CheckCircleOutlined/>,
                desc: t('habit.ratingMediumDesc')
            },
            {
                rating: 4 as ReviewRating,
                label: t('habit.ratingEasy'),
                color: '#52c41a',
                icon: <CheckCircleOutlined/>,
                desc: t('habit.ratingEasyDesc')
            },
            {
                rating: 5 as ReviewRating,
                label: t('habit.ratingVeryEasy'),
                color: '#722ed1',
                icon: <CheckCircleOutlined/>,
                desc: t('habit.ratingVeryEasyDesc')
            }
        ]
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

    const accuracyRate = completedCards.length > 0
        ? Math.round((correctCount / completedCards.length) * 100)
        : 0

    return (
        <Spin spinning={loading}>
            <div style={{padding: '24px', maxWidth: '900px', margin: '0 auto'}}>
                <div style={{marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                    <Button
                        icon={<ArrowLeftOutlined/>}
                        onClick={() => navigate('/habit-formation/dashboard')}
                    >
                        {t('common.back')}
                    </Button>
                    <Button
                        icon={<ReloadOutlined/>}
                        onClick={loadData}
                    >
                        {t('common.refresh')}
                    </Button>
                </div>

                {cards.length === 0 ? (
                    <Empty
                        description={t('habit.noCardsToReview')}
                        style={{padding: '80px 0'}}
                    >
                        <Button
                            type="primary"
                            onClick={() => navigate('/habit-formation/study/new')}
                        >
                            {t('habit.learnNewCards')}
                        </Button>
                    </Empty>
                ) : showResult ? (
                    <Result
                        status="success"
                        icon={<TrophyOutlined/>}
                        title={t('habit.reviewComplete')}
                        subTitle={t('habit.reviewCompleteDesc')}
                        extra={
                            <Space>
                                <Button type="primary" onClick={startReview}>
                                    {t('habit.reviewAgain')}
                                </Button>
                                <Button onClick={() => navigate('/habit-formation/dashboard')}>
                                    {t('habit.backToDashboard')}
                                </Button>
                            </Space>
                        }
                    >
                        <div style={{marginTop: '24px'}}>
                            <Row gutter={32}>
                                <Col span={8}>
                                    <Statistic
                                        title={t('habit.totalReviewed')}
                                        value={completedCards.length}
                                        suffix={t('habit.cards')}
                                    />
                                </Col>
                                <Col span={8}>
                                    <Statistic
                                        title={t('habit.correctCount')}
                                        value={correctCount}
                                        valueStyle={{color: '#52c41a'}}
                                    />
                                </Col>
                                <Col span={8}>
                                    <Statistic
                                        title={t('habit.accuracyRate')}
                                        value={accuracyRate}
                                        suffix="%"
                                        valueStyle={{color: accuracyRate >= 70 ? '#52c41a' : '#faad14'}}
                                    />
                                </Col>
                            </Row>
                        </div>
                    </Result>
                ) : completedCards.length === 0 && !showAnswer && currentIndex === 0 ? (
                    <div style={{textAlign: 'center', padding: '60px 0'}}>
                        <TrophyOutlined style={{fontSize: '64px', color: '#1890ff', marginBottom: '24px'}}/>
                        <h2 style={{marginBottom: '16px'}}>{t('habit.todayReviewTitle')}</h2>
                        <p style={{color: '#8c8c8c', marginBottom: '24px'}}>
                            {t('habit.todayReviewDesc', {count: cards.length})}
                        </p>
                        <Button type="primary" size="large" onClick={startReview}>
                            {t('habit.startReview')}
                        </Button>
                    </div>
                ) : (
                    <>
                        <div style={{marginBottom: '24px'}}>
                            <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '8px'}}>
                                <span>
                                    {t('habit.reviewProgress', {
                                        current: completedCards.length + 1,
                                        total: cards.length
                                    })}
                                </span>
                                <span style={{color: '#8c8c8c'}}>
                                    {t('habit.accuracyRate')}: {accuracyRate}%
                                </span>
                            </div>
                            <Progress percent={progress} showInfo={false}/>
                        </div>

                        {currentCard && (
                            <div>
                                <div style={{marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                                    <Space>
                                        <Tag color="blue">{getDeckName(currentCard.deckId)}</Tag>
                                        <Tag color={getMasteryColor(currentCard.masteryLevel)}>
                                            {getMasteryText(currentCard.masteryLevel)}
                                        </Tag>
                                    </Space>
                                    <span style={{color: '#8c8c8c'}}>
                                        {t('habit.reviewCount')}: {currentCard.reviewCount}
                                    </span>
                                </div>

                                <Card
                                    style={{marginBottom: '16px', minHeight: '200px'}}
                                    styles={{body: {padding: '32px', textAlign: 'center'}}}
                                >
                                    <h3 style={{marginBottom: '16px'}}>{t('habit.cardFront')}</h3>
                                    <div
                                        style={{
                                            fontSize: '18px',
                                            lineHeight: '1.6',
                                            whiteSpace: 'pre-wrap',
                                            wordBreak: 'break-word'
                                        }}
                                    >
                                        {currentCard.frontContent || t('habit.noContent')}
                                    </div>
                                    {currentCard.frontImageUrls && currentCard.frontImageUrls.length > 0 && (
                                        <div style={{marginTop: '24px'}}>
                                            {currentCard.frontImageUrls.map((url, index) => (
                                                <img
                                                    key={index}
                                                    src={url}
                                                    alt=""
                                                    style={{maxWidth: '80%', maxHeight: '200px'}}
                                                />
                                            ))}
                                        </div>
                                    )}
                                </Card>

                                {showAnswer && (
                                    <Card
                                        style={{marginBottom: '16px', background: '#f6ffed'}}
                                        styles={{body: {padding: '32px', textAlign: 'center'}}}
                                    >
                                        <h3 style={{marginBottom: '16px'}}>{t('habit.cardBack')}</h3>
                                        <div
                                            style={{
                                                fontSize: '18px',
                                                lineHeight: '1.6',
                                                whiteSpace: 'pre-wrap',
                                                wordBreak: 'break-word'
                                            }}
                                        >
                                            {currentCard.backContent || t('habit.noContent')}
                                        </div>
                                        {currentCard.backImageUrls && currentCard.backImageUrls.length > 0 && (
                                            <div style={{marginTop: '24px'}}>
                                                {currentCard.backImageUrls.map((url, index) => (
                                                    <img
                                                        key={index}
                                                        src={url}
                                                        alt=""
                                                        style={{maxWidth: '80%', maxHeight: '200px'}}
                                                    />
                                                ))}
                                            </div>
                                        )}
                                    </Card>
                                )}

                                <div style={{textAlign: 'center'}}>
                                    {!showAnswer ? (
                                        <Button
                                            type="primary"
                                            size="large"
                                            icon={<EyeOutlined/>}
                                            onClick={showAnswerHandler}
                                        >
                                            {t('habit.showAnswer')}
                                        </Button>
                                    ) : (
                                        <div>
                                            <p style={{marginBottom: '16px', color: '#8c8c8c'}}>
                                                {t('habit.ratingPrompt')}
                                            </p>
                                            <Row gutter={[8, 16]}>
                                                {getRatingOptions().map((option) => (
                                                    <Col xs={12} sm={8} key={option.rating}>
                                                        <Button
                                                            block
                                                            style={{
                                                                borderColor: option.color,
                                                                color: option.color
                                                            }}
                                                            onClick={() => handleRating(option.rating)}
                                                        >
                                                            <div>{option.icon} {option.label}</div>
                                                            <div style={{fontSize: '11px', opacity: 0.7}}>
                                                                {option.desc}
                                                            </div>
                                                        </Button>
                                                    </Col>
                                                ))}
                                            </Row>
                                        </div>
                                    )}
                                </div>
                            </div>
                        )}
                    </>
                )}
            </div>
        </Spin>
    )
}

export default DailyReview
