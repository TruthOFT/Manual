import { API_CONTEXT_PATH, BASE_URL, request } from '@/api/request'

export function uploadFile(biz: string, file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request<string>(`/upload/${biz}`, {
        method: 'POST',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
        showSuccessMessage: false,
    })
}

export function resolveUploadUrl(url?: string | null) {
    if (!url) {
        return ''
    }
    const value = String(url).trim().replaceAll('\\', '/')
    if (!value) {
        return ''
    }
    if (/^(https?:|data:|blob:)/i.test(value)) {
        return value
    }
    const normalizedPath = value.startsWith('/') ? value : `/${value}`
    if (normalizedPath.startsWith(`${API_CONTEXT_PATH}/`)) {
        return `${BASE_URL}${normalizedPath}`
    }
    if (normalizedPath.startsWith('/upload/')) {
        return `${BASE_URL}${API_CONTEXT_PATH}${normalizedPath}`
    }
    return `${BASE_URL}${normalizedPath}`
}
