export type LoginUser = {
    id: number
    userAccount: string
    userName: string
    avatarUrl: string
    userRole: string
}

export type UserLoginRequest = {
    userAccount: string
    userPassword: string
}

export type UserRegisterRequest = {
    userAccount: string
    userName: string
    userPassword: string
    checkPassword: string
}
