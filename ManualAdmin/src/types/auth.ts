export type LoginUser = {
    id: string | number
    username: string | null
    userAccount: string
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

export type AdminLoginRequest = {
    userAccount: string
    userPassword: string
    rememberMe: boolean
}
