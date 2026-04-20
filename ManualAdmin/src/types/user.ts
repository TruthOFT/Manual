export type AdminUser = {
    id: string
    userAccount: string
    userName: string | null
    avatarUrl: string | null
    phone: string | null
    email: string | null
    gender: number | null
    userRole: string
    userStatus: number
    balance: number | string | null
    lastLoginTime: string | number | null
    createTime: string | number | null
    updateTime: string | number | null
}

export type AdminUserCreateRequest = {
    userAccount: string
    userPassword: string
    userName: string
    avatarUrl?: string
    phone?: string
    email?: string
    gender?: number
    userRole: string
    userStatus: number
    balance?: number
}

export type AdminUserUpdateRequest = {
    userPassword?: string
    userName: string
    avatarUrl?: string
    phone?: string
    email?: string
    gender?: number
    userRole: string
    userStatus: number
    balance?: number
}
