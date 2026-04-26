export type UserCoupon = {
    id: string
    receiveId?: string | null
    couponName: string
    couponType: number
    thresholdAmount: number | string
    discountAmount: number | string
    discountRate?: number | string | null
    totalCount: number
    receiveCount: number
    remainingCount: number
    startTime: string
    endTime: string
    receiveTime?: string | null
    useStatus?: number | null
    useTime?: string | null
    orderId?: string | null
}
