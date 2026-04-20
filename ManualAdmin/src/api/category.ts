import { request } from '@/api/request'
import type { AdminCategory, AdminCategorySaveRequest } from '@/types/category'

export function getAdminCategories() {
    return request<AdminCategory[]>('/admin/categories')
}

export function getAdminCategoryDetail(id: string) {
    return request<AdminCategory>(`/admin/categories/${id}`)
}

export function createAdminCategory(data: AdminCategorySaveRequest) {
    return request<string>('/admin/categories', {
        method: 'POST',
        data,
    })
}

export function updateAdminCategory(id: string, data: AdminCategorySaveRequest) {
    return request<boolean>(`/admin/categories/${id}`, {
        method: 'PUT',
        data,
    })
}

export function deleteAdminCategory(id: string) {
    return request<boolean>(`/admin/categories/${id}`, {
        method: 'DELETE',
    })
}
