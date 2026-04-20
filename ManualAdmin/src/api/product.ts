import { request } from '@/api/request'
import type { AdminProductDetail, AdminProductListItem } from '@/types/product'

type ProductListParams = {
    auditStatus?: number
    status?: number
    keyword?: string
}

export function getAdminProducts(params: ProductListParams = {}) {
    return request<AdminProductListItem[]>('/admin/products', {
        params,
    })
}

export function getAdminProductDetail(productId: string) {
    return request<AdminProductDetail>(`/admin/products/${productId}`)
}

export function approveAdminProduct(productId: string) {
    return request<boolean>(`/admin/products/${productId}/approve`, {
        method: 'POST',
    })
}

export function rejectAdminProduct(productId: string) {
    return request<boolean>(`/admin/products/${productId}/reject`, {
        method: 'POST',
    })
}
