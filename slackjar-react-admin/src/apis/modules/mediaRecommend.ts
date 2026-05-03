import request from '../request'
import type {ResponseData} from './types'

export interface MediaItemDTO {
    id?: number
    title: string
    type: number
    status: number
    rating?: number
    review?: string
    tags: string[]
    coverUrl?: string
    author?: string
    year?: string
    createTime?: number
    updateTime?: number
}

export interface MediaItemRequest {
    id?: number
    title: string
    type: number
    status: number
    rating?: number
    review?: string
    tags?: string[]
    coverUrl?: string
    author?: string
    year?: string
}

export interface ShareRequest {
    title?: string
    expireTime?: number
}

export interface ShareLinkResponse {
    shareCode: string
    shareUrl: string
    title?: string
    expireTime?: number
}

export function addItem(data: MediaItemRequest): Promise<ResponseData<MediaItemDTO>> {
    return request.post('/media/item', data)
}

export function updateItem(data: MediaItemRequest): Promise<ResponseData<MediaItemDTO>> {
    return request.put('/media/item', data)
}

export function deleteItem(id: number): Promise<ResponseData<void>> {
    return request.delete(`/media/item/${id}`)
}

export function getItemById(id: number): Promise<ResponseData<MediaItemDTO>> {
    return request.get(`/media/item/${id}`)
}

export function listItems(type?: number, status?: number): Promise<ResponseData<MediaItemDTO[]>> {
    const params: Record<string, number> = {}
    if (type !== undefined) params.type = type
    if (status !== undefined) params.status = status
    return request.get('/media/items', { params })
}

export function getRecommendations(limit?: number): Promise<ResponseData<MediaItemDTO[]>> {
    const params: Record<string, number> = {}
    if (limit !== undefined) params.limit = limit
    return request.get('/media/recommendations', { params })
}

export function getAllTags(): Promise<ResponseData<string[]>> {
    return request.get('/media/tags')
}

export function createShareLink(data: ShareRequest): Promise<ResponseData<ShareLinkResponse>> {
    return request.post('/media/share', data)
}

export function getSharedItems(shareCode: string): Promise<ResponseData<{ title?: string; items: MediaItemDTO[] }>> {
    return request.get(`/media/share/${shareCode}`)
}

export function deleteShareLink(shareCode: string): Promise<ResponseData<void>> {
    return request.delete(`/media/share/${shareCode}`)
}

export function listShareLinks(): Promise<ResponseData<ShareLinkResponse[]>> {
    return request.get('/media/shares')
}