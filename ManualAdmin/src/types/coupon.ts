export type AdminCoupon = {
    id: string
    couponName: string
    couponType: number
    thresholdAmount: number | string
    discountAmount: number | string
    discountRate: number | string | null
    totalCount: number
    receiveCount: number
    usedCount: number
    startTime: string
    endTime: string
    couponStatus: number
    createTime: string | null
    updateTime: string | null
}

export type AdminCouponReceive = {
    id: string
    couponId: string
    userId: string
    userAccount: string
    userName: string | null
    phone: string | null
    receiveTime: string | null
    useStatus: number
    useTime: string | null
    orderId: string | null
}

export type AdminCouponSaveRequest = {
    couponName: string
    couponType: number
    thresholdAmount: number | string
    discountAmount: number | string
    discountRate?: number | string | null
    totalCount: number
    startTime: string
    endTime: string
    couponStatus: number
}
