import { request } from '@/api/request'
import type { RecommendationProduct } from '@/types/recommendation'

export type BehaviorRecordPayload = {
    productId: string
    skuId?: string
    behaviorType?: number
    sourcePage?: string
    staySeconds?: number
    deviceType?: string
}

export function getMyRecommendations(limit = 8) {
    return request<RecommendationProduct[]>('/recommendations/me', {
        params: {
            limit,
        },
    })
}

export function getSimilarProducts(productId: string, limit = 6) {
    return request<RecommendationProduct[]>(`/recommendations/similar/${productId}`, {
        params: {
            limit,
        },
    })
}

export function recordProductBehavior(data: BehaviorRecordPayload) {
    return request<boolean>('/recommendations/behaviors', {
        method: 'POST',
        data,
        showSuccessMessage: false,
    })
}
