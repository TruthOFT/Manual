import { request } from '@/api/request'

export function uploadFile(biz: 'product' | 'user', file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request<string>(`/upload/${biz}`, {
        method: 'POST',
        data: formData,
    })
}