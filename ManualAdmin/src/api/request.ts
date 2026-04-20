import axios, { AxiosError, type AxiosRequestConfig } from 'axios'

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
    (response) => response,
    (error: AxiosError<BaseResponse<unknown>>) => {
        const result = error.response?.data
        if (result?.message) {
            return Promise.reject(new Error(result.message))
        }
        return Promise.reject(new Error(error.message || '请求失败'))
    },
)

export function request<T>(url: string, options: AxiosRequestConfig = {}): Promise<T> {
    return myAxios
        .request<BaseResponse<T>>({
            url,
            ...options,
        })
        .then((response) => {
            if (response.data.code !== 0) {
                let fallbackMessage = '请求失败'
                if (response.data.code === 40100) {
                    fallbackMessage = '请先登录管理员账号'
                } else if (response.data.code === 40101) {
                    fallbackMessage = '当前账号没有管理员权限'
                }
                throw new Error(response.data.message || fallbackMessage)
            }
            return response.data.data
        })
}

export default myAxios
