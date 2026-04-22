import { API_CONTEXT_PATH, BASE_URL, request } from '@/api/request'

const MAX_UPLOAD_SIZE = 100 * 1024 * 1024

export function uploadFile(biz: 'product' | 'user', file: File) {
    if (file.size > MAX_UPLOAD_SIZE) {
        return Promise.reject(new Error('上传文件不能超过100M'))
    }
    const formData = new FormData()
    formData.append('file', file)
    return request<string>(`/upload/${biz}`, {
        method: 'POST',
        data: formData,
        showSuccessMessage: false,
    })
}

export function resolveUploadUrl(url?: string | null) {
    if (!url) {
        return ''
    }
    let value = String(url).trim()
    if ((value.startsWith('"') && value.endsWith('"')) || (value.startsWith("'") && value.endsWith("'"))) {
        value = value.slice(1, -1).trim()
    }
    if (value.startsWith('[')) {
        try {
            const parsed = JSON.parse(value)
            if (Array.isArray(parsed) && typeof parsed[0] === 'string') {
                value = parsed[0].trim()
            }
        } catch {
            // ignore malformed JSON-like legacy data
        }
    }
    value = value.replaceAll('\\', '/')
    if (!value) {
        return ''
    }
    if (value.startsWith('blob:') || value.startsWith('data:')) {
        return value
    }
    if (value.startsWith('http://') || value.startsWith('https://')) {
        try {
            const parsedUrl = new URL(value)
            if ((parsedUrl.hostname === 'localhost' || parsedUrl.hostname === '127.0.0.1')
                && parsedUrl.pathname.startsWith('/upload/')) {
                return `${BASE_URL}${API_CONTEXT_PATH}${parsedUrl.pathname}${parsedUrl.search}${parsedUrl.hash}`
            }
        } catch {
            // keep original value
        }
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
