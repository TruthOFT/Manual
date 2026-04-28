import { request } from '@/api/request'
import type {
    AdminProductDetail,
    AdminProductListItem,
    AdminProductMeta,
    AdminProductSaveRequest,
} from '@/types/product'

type ProductListParams = {
    auditStatus?: number
    status?: number
    keyword?: string
    categoryId?: string
}

export function getAdminProducts(params: ProductListParams = {}) {
    return request<AdminProductListItem[]>('/admin/products', {
        params,
    })
}

export function getAdminProductDetail(productId: string) {
    return request<AdminProductDetail>(`/admin/products/${productId}`)
}

export function getAdminProductMeta() {
    return request<AdminProductMeta>('/admin/products/meta')
}

export function createAdminProduct(data: AdminProductSaveRequest) {
    return request<string>('/admin/products', {
        method: 'POST',
        data,
    })
}

export function updateAdminProduct(productId: string, data: AdminProductSaveRequest) {
    return request<boolean>(`/admin/products/${productId}`, {
        method: 'PUT',
        data,
    })
}

export function deleteAdminProduct(productId: string) {
    return request<boolean>(`/admin/products/${productId}`, {
        method: 'DELETE',
    })
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
