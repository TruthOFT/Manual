export type LoginUser = {
    id: number
    username: string
    userAccount: string
    avatarUrl: string
    userRole: string
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
