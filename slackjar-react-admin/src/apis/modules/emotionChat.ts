import request from '../request'
import type {ResponseData} from './types'

export function getSessionList(): Promise<ResponseData<SessionListResponse[]>> {
    return request.get('/emotion-chat/sessions')
}

export function createSession(data?: SessionCreateRequest): Promise<ResponseData<number>> {
    return request.post('/emotion-chat/sessions', data || {})
}

export function deleteSession(sessionId: number): Promise<ResponseData<boolean>> {
    return request.delete(`/emotion-chat/sessions/${sessionId}`)
}

export function getChatRecords(sessionId: number): Promise<ResponseData<ChatRecordResponse[]>> {
    return request.get(`/emotion-chat/sessions/${sessionId}/records`)
}

export function sendChatMessage(data: EmotionChatRequest): Promise<ResponseData<EmotionChatResponse>> {
    return request.post('/emotion-chat/chat', data)
}

export interface SessionListResponse {
    id: number
    sessionName: string
    lastMessage: string
    lastEmotion: string
    unreadCount: number
    updateTime: number
}

export interface SessionCreateRequest {
    sessionName?: string
}

export interface ChatRecordResponse {
    id: number
    message: string
    isUser: number
    emotion: string
    createTime: number
}

export interface EmotionChatRequest {
    sessionId?: number
    message: string
}

export interface EmotionChatResponse {
    sessionId: number
    response: string
    emotion: string
    emotionScore: number
    strategy: string
}