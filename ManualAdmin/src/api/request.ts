import axios, { AxiosError, type AxiosRequestConfig } from 'axios'
import { message } from 'ant-design-vue'

type BaseResponse<T> = {
    code: number
    data: T
    message: string
}

type RequestOptions = AxiosRequestConfig & {
    showSuccessMessage?: boolean
}

export const BASE_URL = 'http://localhost:8080'
export const API_CONTEXT_PATH = '/api'

const myAxios = axios.create({
    baseURL: BASE_URL + API_CONTEXT_PATH,
    timeout: 60000,
    withCredentials: true,
})

myAxios.interceptors.response.use(
    (response) => response,
    (error: AxiosError<BaseResponse<unknown>>) => {
        const result = error.response?.data
        if (result?.message) {
            return Promise.reject(new Error(result.message))
        }
        return Promise.reject(new Error(error.message || 'Request failed'))
    },
)

export function request<T>(url: string, options: RequestOptions = {}): Promise<T> {
    return myAxios
        .request<BaseResponse<T>>({
            url,
            ...options,
        })
        .then((response) => {
            if (response.data.code !== 0) {
                let fallbackMessage = 'Request failed'
                if (response.data.code === 40100) {
                    fallbackMessage = 'Please log in as admin first'
                } else if (response.data.code === 40101) {
                    fallbackMessage = 'Current account has no admin permission'
                }
                throw new Error(response.data.message || fallbackMessage)
            }
            const method = String(options.method ?? 'get').toLowerCase()
            const shouldShowSuccessMessage = options.showSuccessMessage ?? method !== 'get'
            if (shouldShowSuccessMessage && response.data.message) {
                message.success(response.data.message)
            }
            return response.data.data
        })
}

export default myAxios
