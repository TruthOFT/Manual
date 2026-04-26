export type AdminCustomer = {
    userId: string
    profileId: string | null
    userAccount: string
    userName: string | null
    phone: string | null
    email: string | null
    gender: number
    userStatus: number
    customerLevel: number
    points: number
    totalAmount: number | string
    orderCount: number
    preferenceTags: string | null
    lastPurchaseTime: string | null
    createTime: string | null
    updateTime: string | null
}

export type AdminCustomerSaveRequest = {
    userAccount?: string
    userPassword?: string
    userName: string
    phone?: string
    email?: string
    gender: number
    userStatus: number
    customerLevel: number
    points: number
    totalAmount: number | string
    orderCount: number
    preferenceTags?: string
    lastPurchaseTime?: string
}
