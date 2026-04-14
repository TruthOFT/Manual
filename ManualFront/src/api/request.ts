import axios, { AxiosError, type AxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'

import { pinia } from '@/stores'

type BaseResponse<T> = {
    code: number
    data: T
    message: string
}

export const BASE_URL = 'http://localhost:8080'
const CONTEXT_PATH = '/api'

const myAxios = axios.create({
    baseURL: BASE_URL + CONTEXT_PATH,
    timeout: 60000,
    withCredentials: true,
})

myAxios.interceptors.response.use(
    async (response) => {
        const result = response.data as BaseResponse<unknown>
        if (result.code === 40100) {
            const { useUserStore } = await import('@/stores/user')
            const userStore = useUserStore(pinia)
            userStore.clearCurrentUser()
            const requestUrl = String(response.config.url ?? '')
            if (!requestUrl.includes('/user/get/login') && window.location.pathname !== '/login') {
                await message.warning('Please sign in first.')
                const redirect = `${window.location.pathname}${window.location.search}${window.location.hash}`
                window.location.href = `/login?redirect=${encodeURIComponent(redirect)}`
            }
            return Promise.reject(new Error(result.message || 'Please sign in first.'))
        }
        return response
    },
    (error: AxiosError<BaseResponse<unknown>>) => {
        const result = error.response?.data
        if (result?.message) {
            return Promise.reject(new Error(result.message))
        }
        return Promise.reject(new Error(error.message || 'Request failed'))
    },
)

export function request<T>(url: string, options: AxiosRequestConfig = {}): Promise<T> {
    return myAxios.request<BaseResponse<T>>({
        url,
        ...options,
    }).then((response) => {
        if (response.data.code !== 0) {
            throw new Error(response.data.message || 'Request failed')
        }
        return response.data.data
    })
}

export default myAxios
