import { request } from '@/api/request'
import type { AdminUser, AdminUserCreateRequest, AdminUserUpdateRequest } from '@/types/user'

type UserListParams = {
    keyword?: string
    userRole?: string
    userStatus?: number
}

export function getAdminUsers(params: UserListParams = {}) {
    return request<AdminUser[]>('/admin/users', {
        params,
    })
}

export function getAdminUserDetail(id: string) {
    return request<AdminUser>(`/admin/users/${id}`)
}

export function createAdminUser(data: AdminUserCreateRequest) {
    return request<string>('/admin/users', {
        method: 'POST',
        data,
    })
}

export function updateAdminUser(id: string, data: AdminUserUpdateRequest) {
    return request<boolean>(`/admin/users/${id}`, {
        method: 'PUT',
        data,
    })
}

export function deleteAdminUser(id: string) {
    return request<boolean>(`/admin/users/${id}`, {
        method: 'DELETE',
    })
}
