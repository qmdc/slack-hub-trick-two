import request from '../request'
import type {PageResult, ResponseData} from './types'

export function pageQueryBookmarks(data: BookmarkQueryRequest): Promise<ResponseData<PageResult<BookmarkDTO>>> {
    return request.post('/bookmark/pageQuery', data)
}

export function getBookmarkById(id: number): Promise<ResponseData<BookmarkDTO>> {
    return request.get(`/bookmark/${id}`)
}

export function createBookmark(data: BookmarkCreateRequest): Promise<ResponseData<BookmarkDTO>> {
    return request.post('/bookmark', data)
}

export function updateBookmark(id: number, data: BookmarkUpdateRequest): Promise<ResponseData<BookmarkDTO>> {
    return request.put(`/bookmark/${id}`, data)
}

export function deleteBookmark(id: number): Promise<ResponseData<boolean>> {
    return request.delete(`/bookmark/${id}`)
}

export function importBookmarks(data: BookmarkImportRequest): Promise<ResponseData<void>> {
    return request.post('/bookmark/import', data)
}

export function exportBookmarks(): Promise<ResponseData<BookmarkExportItem[]>> {
    return request.get('/bookmark/export')
}

export function listCategories(): Promise<ResponseData<BookmarkCategoryDTO[]>> {
    return request.get('/bookmark/categories')
}

export function createCategory(data: BookmarkCategoryRequest): Promise<ResponseData<BookmarkCategoryDTO>> {
    return request.post('/bookmark/categories', data)
}

export function updateCategory(id: number, data: BookmarkCategoryRequest): Promise<ResponseData<BookmarkCategoryDTO>> {
    return request.put(`/bookmark/categories/${id}`, data)
}

export function deleteCategory(id: number): Promise<ResponseData<boolean>> {
    return request.delete(`/bookmark/categories/${id}`)
}

export function listTags(): Promise<ResponseData<BookmarkTagDTO[]>> {
    return request.get('/bookmark/tags')
}

export function createTag(data: BookmarkTagRequest): Promise<ResponseData<BookmarkTagDTO>> {
    return request.post('/bookmark/tags', data)
}

export function updateTag(id: number, data: BookmarkTagRequest): Promise<ResponseData<BookmarkTagDTO>> {
    return request.put(`/bookmark/tags/${id}`, data)
}

export function deleteTag(id: number): Promise<ResponseData<boolean>> {
    return request.delete(`/bookmark/tags/${id}`)
}

export interface BookmarkQueryRequest {
    pageNo?: number
    pageSize?: number
    keyword?: string
    categoryId?: number
    tagName?: string
}

export interface BookmarkDTO {
    id: number
    url: string
    title: string
    faviconUrl: string
    description: string
    tags: string
    categoryId: number
    categoryName: string
    createTime: number
    updateTime: number
}

export interface BookmarkCreateRequest {
    url: string
    title?: string
    description?: string
    tags?: string
    categoryId?: number
}

export interface BookmarkUpdateRequest {
    title?: string
    description?: string
    tags?: string
    categoryId?: number
}

export interface BookmarkImportRequest {
    bookmarks: BookmarkImportItem[]
}

export interface BookmarkImportItem {
    url: string
    title?: string
    description?: string
    tags?: string
    category?: string
}

export interface BookmarkExportItem {
    url: string
    title: string
    description: string
    tags: string
    category: string
    createTime: number
}

export interface BookmarkCategoryDTO {
    id: number
    name: string
    icon: string
    sortOrder: number
    createTime: number
}

export interface BookmarkCategoryRequest {
    name: string
    icon?: string
    sortOrder?: number
}

export interface BookmarkTagDTO {
    id: number
    name: string
    color: string
    createTime: number
}

export interface BookmarkTagRequest {
    name: string
    color?: string
}