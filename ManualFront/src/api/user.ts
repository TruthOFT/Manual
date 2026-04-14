import { request } from '@/api/request'
import type { LoginUser, UserLoginRequest, UserRegisterRequest } from '@/types/user'

export function loginUser(data: UserLoginRequest) {
    return request<LoginUser>('/api/user/login', {
        method: 'POST',
        body: JSON.stringify(data),
    })
}

export function registerUser(data: UserRegisterRequest) {
    return request<number>('/api/user/register', {
        method: 'POST',
        body: JSON.stringify(data),
    })
}

export function getCurrentUser() {
    return request<LoginUser | null>('/api/user/get/login')
}

export function logoutUser() {
    return request<boolean>('/api/user/logout', {
        method: 'POST',
    })
}
