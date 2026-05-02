import React, {useEffect, useState} from 'react'
import {
    Card,
    Button,
    message,
    Spin,
    Tag,
    Row,
    Col,
    Select,
    Empty,
    Result
} from 'antd'
import {
    CheckCircleOutlined,
    CloseCircleOutlined,
    EyeOutlined,
    TrophyOutlined,
    ArrowLeftOutlined,
    BookOutlined
} from '@ant-design/icons'
import {useTranslation} from 'react-i18next'
import {
    getNewCards,
    getMyDecks,
    reviewCard,
    type Card,
    type Deck,
    type ReviewRating
} from '../../../apis/modules/habitFormation'
import {useNavigate} from 'react-router'

const {Option} = Select

const NewCards: React.FC = () => {
    const {t} = useTranslation()
    const navigate = useNavigate()
    const [loading, setLoading] = useState(false)
    const [cards, setCards] = useState<Card[]>([])
    const [decks, setDecks] = useState<Deck[]>([])
    const [selectedDeckId, setSelectedDeckId] = useState<number | undefined>()
    const [currentIndex, setCurrentIndex] = useState(0)
    const [showAnswer, setShowAnswer] = useState(false)
    const [completedCards, setCompletedCards] = useState<Card[]>([])
    const [showResult, setShowResult] = useState(false)

    const currentCard = cards[currentIndex]

    useEffect(() => {
        loadData()
    }, [])

    useEffect(() => {
        loadNewCards()
    }, [selectedDeckId])

    const loadData = async () => {
        try {
            const decksRes = await getMyDecks()
            if (decksRes.code === 200) {
                setDecks(decksRes.data || [])
            }
        } catch (error) {
            console.error('Failed to load decks:', error)
        }
    }

    const loadNewCards = async () => {
        setLoading(true)
        try {
            const res = await getNewCards(selectedDeckId, 20)
            if (res.code === 200) {
                setCards(res.data || [])
                setCurrentIndex(0)
                setShowAnswer(false)
                setCompletedCards([])
                setShowResult(false)
            }
        } catch (error) {
            console.error('Failed to load new cards:', error)
            message.error(t('common.loadFailed'))
        } finally {
            setLoading(false)
        }
    }

    const showAnswerHandler = () => {
        setShowAnswer(true)
    }

    const getDeckName = (deckId: number): string => {
        const deck = decks.find(d => d.id === deckId)
        return deck?.name || ''
    }

    const handleRating = async (rating: ReviewRating) => {
        if (!currentCard) return

        try {
            const res = await reviewCard({
                cardId: currentCard.id,
                rating: rating as number
            })

            if (res.code === 200) {
                setCompletedCards(prev => [...prev, currentCard])

                if (currentIndex < cards.length - 1) {
                    setCurrentIndex(prev => prev + 1)
                    setShowAnswer(false)
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
                    <div style={{fontSize: '18px', fontWeight: '500'}}>
                        <BookOutlined style={{marginRight: '8px'}}/>
                        {t('habit.learnNewCards')}
                    </div>
                    <div>
                        <Select
                            placeholder={t('habit.selectDeck')}
                            style={{width: 200}}
                            value={selectedDeckId}
                            onChange={setSelectedDeckId}
                            allowClear
                        >
                            {decks.map(deck => (
                                <Option key={deck.id} value={deck.id}>{deck.name}</Option>
                            ))}
                        </Select>
                    </div>
                </div>

                {cards.length === 0 ? (
                    <Empty
                        description={t('habit.noNewCards')}
                        style={{padding: '80px 0'}}
                    >
                        <Button
                            type="primary"
                            onClick={() => navigate('/habit-formation/deck/list')}
                        >
                            {t('habit.goToDeckList')}
                        </Button>
                    </Empty>
                ) : showResult ? (
                    <Result
                        status="success"
                        icon={<TrophyOutlined/>}
                        title={t('habit.learningComplete')}
                        subTitle={t('habit.learningCompleteDesc', {count: completedCards.length})}
                        extra={
                            <div>
                                <Button type="primary" onClick={loadNewCards} style={{marginRight: '8px'}}>
                                    {t('habit.learnMore')}
                                </Button>
                                <Button onClick={() => navigate('/habit-formation/dashboard')}>
                                    {t('habit.backToDashboard')}
                                </Button>
                            </div>
                        }
                    />
                ) : (
                    <>
                        <div style={{marginBottom: '24px', textAlign: 'center', color: '#8c8c8c'}}>
                            {t('habit.learningProgress', {
                                current: completedCards.length + 1,
                                total: cards.length
                            })}
                        </div>

                        {currentCard && (
                            <div>
                                <div style={{marginBottom: '16px', display: 'flex', justifyContent: 'center'}}>
                                    <Tag color="blue">{getDeckName(currentCard.deckId)}</Tag>
                                    <Tag color={getMasteryColor(currentCard.masteryLevel)}>
                                        {getMasteryText(currentCard.masteryLevel)}
                                    </Tag>
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
                                            <Row gutter={[16, 16]} justify="center">
                                                {getRatingOptions().map((option) => (
                                                    <Col key={option.rating}>
                                                        <Button
                                                            size="large"
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

export default NewCards
