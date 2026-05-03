import request from '../request';

export interface MediaItemDTO {
    id?: number;
    title: string;
    type: number;
    status: number;
    rating?: number;
    review?: string;
    tags: string[];
    coverUrl?: string;
    author?: string;
    year?: string;
    createTime?: number;
    updateTime?: number;
}

export interface MediaItemRequest {
    id?: number;
    title: string;
    type: number;
    status: number;
    rating?: number;
    review?: string;
    tags?: string[];
    coverUrl?: string;
    author?: string;
    year?: string;
}

export interface ShareRequest {
    title?: string;
    expireTime?: number;
}

export interface ShareLinkResponse {
    shareCode: string;
    shareUrl: string;
    title?: string;
    expireTime?: number;
}

export interface ApiResponse<T> {
    code: number;
    message: string;
    data: T;
}

export const addItem = (data: MediaItemRequest) => {
    return request.post<ApiResponse<MediaItemDTO>>('/media/item', data);
};

export const updateItem = (data: MediaItemRequest) => {
    return request.put<ApiResponse<MediaItemDTO>>('/media/item', data);
};

export const deleteItem = (id: number) => {
    return request.delete<ApiResponse<void>>(`/media/item/${id}`);
};

export const getItemById = (id: number) => {
    return request.get<ApiResponse<MediaItemDTO>>(`/media/item/${id}`);
};

export const listItems = (type?: number, status?: number) => {
    const params: Record<string, number> = {};
    if (type !== undefined) params.type = type;
    if (status !== undefined) params.status = status;
    return request.get<ApiResponse<MediaItemDTO[]>>('/media/items', { params });
};

export const getRecommendations = (limit?: number) => {
    const params: Record<string, number> = {};
    if (limit !== undefined) params.limit = limit;
    return request.get<ApiResponse<MediaItemDTO[]>>('/media/recommendations', { params });
};

export const getAllTags = () => {
    return request.get<ApiResponse<string[]>>('/media/tags');
};

export const createShareLink = (data: ShareRequest) => {
    return request.post<ApiResponse<ShareLinkResponse>>('/media/share', data);
};

export const getSharedItems = (shareCode: string) => {
    return request.get<ApiResponse<{ title?: string; items: MediaItemDTO[] }>>(`/media/share/${shareCode}`);
};

export const deleteShareLink = (shareCode: string) => {
    return request.delete<ApiResponse<void>>(`/media/share/${shareCode}`);
};

export const listShareLinks = () => {
    return request.get<ApiResponse<ShareLinkResponse[]>>('/media/shares');
};