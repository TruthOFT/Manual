type BaseResponse<T> = {
    code: number
    data: T
    message: string
}

export async function request<T>(url: string, init?: RequestInit): Promise<T> {
    const response = await fetch(url, {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...(init?.headers ?? {}),
        },
        ...init,
    })

    const result = (await response.json()) as BaseResponse<T>
    if (!response.ok || result.code !== 0) {
        throw new Error(result.message || '请求失败')
    }
    return result.data
}
