export type LoginUser = {
    id: number | string
    username: string
    userAccount: string
    avatarUrl: string
    phone: string | null
    email: string | null
    gender: number | null
    userRole: string
    userStatus: number | null
    balance: number | string | null
    lastLoginTime: string | number | null
    createTime: string | number | null
    updateTime: string | number | null
}

export type UserLoginRequest = {
    userAccount: string
    userPassword: string
    rememberMe?: boolean
}

export type UserRegisterRequest = {
    userAccount: string
    userPassword: string
    checkPassword: string
}

export type RechargeRequest = {
    amount: number | string
}