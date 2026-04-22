import { request } from '@/api/request'
import type { AdminLoginRequest, LoginUser } from '@/types/auth'

export function loginAdmin(data: AdminLoginRequest) {
    return request<LoginUser>('/admin/auth/login', {
        method: 'POST',
        data,
    })
}

export function getCurrentAdmin() {
    return request<LoginUser>('/admin/auth/get/login', {
        showSuccessMessage: false,
    })
}

export function logoutAdmin() {
    return request<boolean>('/admin/auth/logout', {
        method: 'POST',
    })
}
