import request from '../request'
import type {PageResult, ResponseData} from './types'

// ============================================
// 卡组管理 API
// ============================================

/**
 * 创建卡组
 */
export function createDeck(data: CreateDeckParams): Promise<ResponseData<Deck>> {
    return request.post('/spaced-repetition/deck', data)
}

/**
 * 更新卡组
 */
export function updateDeck(deckId: number, data: UpdateDeckParams): Promise<ResponseData<Deck>> {
    return request.put(`/spaced-repetition/deck/${deckId}`, data)
}

/**
 * 删除卡组
 */
export function deleteDeck(deckId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/spaced-repetition/deck/${deckId}`)
}

/**
 * 获取卡组详情
 */
export function getDeckById(deckId: number): Promise<ResponseData<Deck>> {
    return request.get(`/spaced-repetition/deck/${deckId}`)
}

/**
 * 分页查询卡组
 */
export function pageQueryDecks(data: DeckPageQuery): Promise<ResponseData<PageResult<Deck>>> {
    return request.post('/spaced-repetition/deck/page-query', data)
}

/**
 * 获取用户的所有卡组
 */
export function getMyDecks(): Promise<ResponseData<Deck[]>> {
    return request.get('/spaced-repetition/deck/my')
}

// ============================================
// 卡片管理 API
// ============================================

/**
 * 创建卡片
 */
export function createCard(data: CreateCardParams): Promise<ResponseData<Card>> {
    return request.post('/spaced-repetition/card', data)
}

/**
 * 更新卡片
 */
export function updateCard(cardId: number, data: UpdateCardParams): Promise<ResponseData<Card>> {
    return request.put(`/spaced-repetition/card/${cardId}`, data)
}

/**
 * 删除卡片
 */
export function deleteCard(cardId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/spaced-repetition/card/${cardId}`)
}

/**
 * 获取卡片详情
 */
export function getCardById(cardId: number): Promise<ResponseData<Card>> {
    return request.get(`/spaced-repetition/card/${cardId}`)
}

/**
 * 分页查询卡片
 */
export function pageQueryCards(data: CardPageQuery): Promise<ResponseData<PageResult<Card>>> {
    return request.post('/spaced-repetition/card/page-query', data)
}

/**
 * 获取今日需复习的卡片
 */
export function getTodayReviewCards(deckId?: number, limit?: number): Promise<ResponseData<Card[]>> {
    const params: Record<string, any> = {}
    if (deckId != null) params.deckId = deckId
    if (limit != null) params.limit = limit
    return request.get('/spaced-repetition/card/today-review', {params})
}

/**
 * 获取新卡片（未复习过的）
 */
export function getNewCards(deckId?: number, limit?: number): Promise<ResponseData<Card[]>> {
    const params: Record<string, any> = {}
    if (deckId != null) params.deckId = deckId
    if (limit != null) params.limit = limit
    return request.get('/spaced-repetition/card/new', {params})
}

// ============================================
// 复习功能 API
// ============================================

/**
 * 复习卡片
 */
export function reviewCard(data: ReviewCardParams): Promise<ResponseData<ReviewResult>> {
    return request.post('/spaced-repetition/review', data)
}

// ============================================
// 标签管理 API
// ============================================

/**
 * 创建标签
 */
export function createTag(data: CreateTagParams): Promise<ResponseData<Tag>> {
    return request.post('/spaced-repetition/tag', data)
}

/**
 * 更新标签
 */
export function updateTag(tagId: number, data: CreateTagParams): Promise<ResponseData<Tag>> {
    return request.put(`/spaced-repetition/tag/${tagId}`, data)
}

/**
 * 删除标签
 */
export function deleteTag(tagId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/spaced-repetition/tag/${tagId}`)
}

/**
 * 获取用户的所有标签
 */
export function getMyTags(): Promise<ResponseData<Tag[]>> {
    return request.get('/spaced-repetition/tag/my')
}

// ============================================
// 学习统计 API
// ============================================

/**
 * 获取学习仪表盘数据
 */
export function getStudyDashboard(): Promise<ResponseData<StudyDashboard>> {
    return request.get('/spaced-repetition/dashboard')
}

/**
 * 获取今日学习统计
 */
export function getTodayStat(): Promise<ResponseData<StudyStat>> {
    return request.get('/spaced-repetition/stat/today')
}

/**
 * 获取近N天的学习统计
 */
export function getRecentStats(days?: number): Promise<ResponseData<StudyStat[]>> {
    const params: Record<string, any> = {}
    if (days != null) params.days = days
    return request.get('/spaced-repetition/stat/recent', {params})
}

// ============================================
// 卡片市场 API
// ============================================

/**
 * 分享卡组到市场
 */
export function shareToMarket(data: ShareToMarketParams): Promise<ResponseData<MarketDeck>> {
    return request.post('/spaced-repetition/market/share', data)
}

/**
 * 从市场下载卡组
 */
export function downloadDeck(marketDeckId: number): Promise<ResponseData<number>> {
    return request.post(`/spaced-repetition/market/download/${marketDeckId}`)
}

/**
 * 点赞/取消点赞
 */
export function toggleLike(marketDeckId: number): Promise<ResponseData<boolean>> {
    return request.post(`/spaced-repetition/market/like/${marketDeckId}`)
}

/**
 * 收藏/取消收藏
 */
export function toggleFavorite(marketDeckId: number): Promise<ResponseData<boolean>> {
    return request.post(`/spaced-repetition/market/favorite/${marketDeckId}`)
}

/**
 * 获取市场卡组详情
 */
export function getMarketDeckById(marketDeckId: number): Promise<ResponseData<MarketDeck>> {
    return request.get(`/spaced-repetition/market/${marketDeckId}`)
}

/**
 * 分页查询市场卡组
 */
export function pageQueryMarketDecks(data: MarketDeckPageQuery): Promise<ResponseData<PageResult<MarketDeck>>> {
    return request.post('/spaced-repetition/market/page-query', data)
}

/**
 * 获取我的收藏
 */
export function getMyFavorites(): Promise<ResponseData<MarketDeck[]>> {
    return request.get('/spaced-repetition/market/my-favorites')
}

/**
 * 获取我的分享
 */
export function getMyShares(): Promise<ResponseData<MarketDeck[]>> {
    return request.get('/spaced-repetition/market/my-shares')
}

/**
 * 获取热门标签
 */
export function getHotTags(limit?: number): Promise<ResponseData<string[]>> {
    const params: Record<string, any> = {}
    if (limit != null) params.limit = limit
    return request.get('/spaced-repetition/market/hot-tags', {params})
}

// ============================================
// 类型定义
// ============================================

/**
 * 卡组信息
 */
export interface Deck {
    id: number
    userId: number
    name: string
    description?: string
    coverImageId?: number
    coverImageUrl?: string
    cardCount: number
    todayReviewCount: number
    masteryRate: number
    isPublic: number
    sortOrder: number
    createTime: number
    updateTime: number
    tags?: Tag[]
}

/**
 * 创建卡组请求参数
 */
export interface CreateDeckParams {
    name: string
    description?: string
    coverImageId?: number
    coverImageUrl?: string
    tagIds?: number[]
    sortOrder?: number
}

/**
 * 更新卡组请求参数
 */
export interface UpdateDeckParams {
    name?: string
    description?: string
    coverImageId?: number
    coverImageUrl?: string
    tagIds?: number[]
    isPublic?: number
    sortOrder?: number
}

/**
 * 卡组分页查询参数
 */
export interface DeckPageQuery {
    pageNo?: number
    pageSize?: number
    keyword?: string
    tagIds?: number[]
    isPublic?: number
    sortBy?: string
    sortOrder?: string
}

/**
 * 卡片信息
 */
export interface Card {
    id: number
    userId: number
    deckId: number
    frontContent?: string
    frontImageIds?: number[]
    frontImageUrls?: string[]
    backContent?: string
    backImageIds?: number[]
    backImageUrls?: string[]
    masteryLevel: number
    masteryLevelDesc?: string
    reviewCount: number
    correctCount: number
    incorrectCount: number
    consecutiveCorrectCount: number
    lastReviewTime?: number
    nextReviewTime?: number
    intervalDays?: number
    difficulty?: number
    easeFactor?: number
    isImportant: number
    sortOrder: number
    createTime: number
    updateTime: number
    tags?: Tag[]
    isDueToday?: boolean
}

/**
 * 创建卡片请求参数
 */
export interface CreateCardParams {
    deckId: number
    frontContent?: string
    frontImageIds?: number[]
    backContent?: string
    backImageIds?: number[]
    tagIds?: number[]
    isImportant?: number
    sortOrder?: number
}

/**
 * 更新卡片请求参数
 */
export interface UpdateCardParams {
    deckId?: number
    frontContent?: string
    frontImageIds?: number[]
    backContent?: string
    backImageIds?: number[]
    tagIds?: number[]
    isImportant?: number
    sortOrder?: number
}

/**
 * 卡片分页查询参数
 */
export interface CardPageQuery {
    pageNo?: number
    pageSize?: number
    deckId?: number
    keyword?: string
    tagIds?: number[]
    masteryLevel?: number
    isImportant?: number
    isDueToday?: boolean
    sortBy?: string
    sortOrder?: string
}

/**
 * 复习卡片请求参数
 */
export interface ReviewCardParams {
    cardId: number
    rating: number
    reviewDuration?: number
}

/**
 * 复习结果
 */
export interface ReviewResult {
    cardId: number
    rating: number
    ratingDesc?: string
    previousInterval?: number
    newInterval?: number
    previousEaseFactor?: number
    newEaseFactor?: number
    nextReviewTime?: number
    masteryLevel: number
    masteryLevelDesc?: string
    isCorrect: boolean
    reviewDuration?: number
}

/**
 * 标签信息
 */
export interface Tag {
    id: number
    userId: number
    name: string
    color?: string
    cardCount: number
    sortOrder: number
    createTime: number
    updateTime: number
}

/**
 * 创建标签请求参数
 */
export interface CreateTagParams {
    name: string
    color?: string
    sortOrder?: number
}

/**
 * 学习仪表盘数据
 */
export interface StudyDashboard {
    todayPendingReview: number
    todayReviewed: number
    todayNewCards: number
    todayCorrectCount: number
    todayIncorrectCount: number
    todayAccuracyRate: number
    todayStudyDuration: number
    todayStudyDurationFormatted?: string
    totalDecks: number
    totalCards: number
    totalReviewedCards: number
    totalMasteredCards: number
    overallMasteryRate: number
    recentDecks?: Deck[]
    todayReviewCards?: Card[]
    hotTags?: Tag[]
}

/**
 * 学习统计
 */
export interface StudyStat {
    id: number
    userId: number
    statDate: string
    learnedCards: number
    reviewedCards: number
    newCards: number
    correctCount: number
    incorrectCount: number
    accuracyRate: number
    studyDuration: number
    studyDurationFormatted?: string
    totalLearnedCards: number
    totalReviewedCards: number
    totalStudyDuration: number
    totalStudyDurationFormatted?: string
    todayPendingReview: number
    createTime: number
    updateTime: number
}

/**
 * 市场卡组信息
 */
export interface MarketDeck {
    id: number
    originalDeckId: number
    userId: number
    userNickname?: string
    userAvatarUrl?: string
    name: string
    description?: string
    coverImageUrl?: string
    cardCount: number
    tags?: string[]
    likeCount: number
    favoriteCount: number
    downloadCount: number
    avgRating?: number
    ratingCount: number
    status: number
    statusDesc?: string
    sortOrder: number
    createTime: number
    updateTime: number
    isLiked?: boolean
    isFavorited?: boolean
    isDownloaded?: boolean
}

/**
 * 分享卡组到市场请求参数
 */
export interface ShareToMarketParams {
    deckId: number
    name?: string
    description?: string
    tags?: string[]
}

/**
 * 市场卡组分页查询参数
 */
export interface MarketDeckPageQuery {
    pageNo?: number
    pageSize?: number
    keyword?: string
    tags?: string[]
    userId?: number
    sortBy?: string
    sortOrder?: string
}

/**
 * 掌握程度枚举
 */
export enum MasteryLevel {
    NEW = 0,
    VERY_LOW = 1,
    LOW = 2,
    MEDIUM = 3,
    HIGH = 4,
    VERY_HIGH = 5
}

/**
 * 复习评分枚举
 */
export enum ReviewRating {
    FORGOT = 0,
    VERY_HARD = 1,
    HARD = 2,
    MEDIUM = 3,
    EASY = 4,
    VERY_EASY = 5
}

/**
 * 市场卡组状态枚举
 */
export enum MarketDeckStatus {
    PENDING = 0,
    PUBLISHED = 1,
    OFF_SHELF = 2
}
