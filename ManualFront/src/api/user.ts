import { request } from '@/api/request'
import type { LoginUser, UserLoginRequest, UserRegisterRequest } from '@/types/user'

export function loginUser(data: UserLoginRequest) {
    return request<LoginUser>('/user/login', {
        method: 'POST',
        data,
    })
}

export function registerUser(data: UserRegisterRequest) {
    return request<number>('/user/register', {
        method: 'POST',
        data,
    })
}

export function getCurrentUser() {
    return request<LoginUser | null>('/user/get/login')
}

export function logoutUser() {
    return request<boolean>('/user/logout', {
        method: 'POST',
    })
}
