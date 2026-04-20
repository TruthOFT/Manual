import { request } from '@/api/request'
import type {
    ArtisanCustomRequirementActionRequest,
    ArtisanCustomRequirementDetail,
    ArtisanCustomRequirementItem,
    ArtisanDashboardData,
    ArtisanCategoryOption,
    ArtisanOrderDetail,
    ArtisanOrderItem,
    ArtisanOrderShipRequest,
    ArtisanProductDetail,
    ArtisanProductPageData,
    ArtisanProductSaveRequest,
    ArtisanCenterProfile,
} from '@/types/artisan-center'

type ProductListParams = {
    auditStatus?: number
    status?: number
    keyword?: string
}

type OrderListParams = {
    orderStatus?: number
    keyword?: string
}

type CustomRequirementListParams = {
    confirmStatus?: number
    keyword?: string
}

export function getArtisanDashboard() {
    return request<ArtisanDashboardData>('/artisan-center/dashboard')
}

export function getArtisanCenterProfile() {
    return request<ArtisanCenterProfile>('/artisan-center/profile')
}

export function updateArtisanCenterProfile(data: Partial<ArtisanCenterProfile>) {
    return request<ArtisanCenterProfile>('/artisan-center/profile', {
        method: 'PUT',
        data,
    })
}

export function getArtisanProducts(params: ProductListParams = {}) {
    return request<ArtisanProductPageData>('/artisan-center/products', {
        params,
    })
}

export function getArtisanProductCategories() {
    return request<ArtisanCategoryOption[]>('/artisan-center/product-categories')
}

export function createArtisanProduct(data: ArtisanProductSaveRequest) {
    return request<string>('/artisan-center/products', {
        method: 'POST',
        data,
    })
}

export function getArtisanProductDetail(productId: string) {
    return request<ArtisanProductDetail>(`/artisan-center/products/${productId}`)
}

export function updateArtisanProduct(productId: string, data: ArtisanProductSaveRequest) {
    return request<boolean>(`/artisan-center/products/${productId}`, {
        method: 'PUT',
        data,
    })
}

export function submitArtisanProductAudit(productId: string) {
    return request<boolean>(`/artisan-center/products/${productId}/submit-audit`, {
        method: 'POST',
    })
}

export function onShelfArtisanProduct(productId: string) {
    return request<boolean>(`/artisan-center/products/${productId}/on-shelf`, {
        method: 'POST',
    })
}

export function offShelfArtisanProduct(productId: string) {
    return request<boolean>(`/artisan-center/products/${productId}/off-shelf`, {
        method: 'POST',
    })
}

export function getArtisanOrders(params: OrderListParams = {}) {
    return request<ArtisanOrderItem[]>('/artisan-center/orders', {
        params,
    })
}

export function getArtisanOrderDetail(orderItemId: string) {
    return request<ArtisanOrderDetail>(`/artisan-center/orders/${orderItemId}`)
}

export function shipArtisanOrder(orderItemId: string, data: ArtisanOrderShipRequest) {
    return request<boolean>(`/artisan-center/orders/${orderItemId}/ship`, {
        method: 'POST',
        data,
    })
}

export function getArtisanCustomRequirements(params: CustomRequirementListParams = {}) {
    return request<ArtisanCustomRequirementItem[]>('/artisan-center/custom-requirements', {
        params,
    })
}

export function getArtisanCustomRequirementDetail(id: string) {
    return request<ArtisanCustomRequirementDetail>(`/artisan-center/custom-requirements/${id}`)
}

export function acceptArtisanCustomRequirement(id: string, data: ArtisanCustomRequirementActionRequest = {}) {
    return request<boolean>(`/artisan-center/custom-requirements/${id}/accept`, {
        method: 'POST',
        data,
    })
}

export function rejectArtisanCustomRequirement(id: string, data: ArtisanCustomRequirementActionRequest = {}) {
    return request<boolean>(`/artisan-center/custom-requirements/${id}/reject`, {
        method: 'POST',
        data,
    })
}

export function processingArtisanCustomRequirement(id: string, data: ArtisanCustomRequirementActionRequest = {}) {
    return request<boolean>(`/artisan-center/custom-requirements/${id}/processing`, {
        method: 'POST',
        data,
    })
}

export function completeArtisanCustomRequirement(id: string, data: ArtisanCustomRequirementActionRequest = {}) {
    return request<boolean>(`/artisan-center/custom-requirements/${id}/complete`, {
        method: 'POST',
        data,
    })
}
