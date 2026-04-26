import { request } from '@/api/request'
import type { UserCoupon } from '@/types/coupon'

type MyCouponParams = {
    useStatus?: number
}

export function getAvailableCoupons() {
    return request<UserCoupon[]>('/coupons/available')
}

export function receiveCoupon(couponId: string) {
    return request<boolean>(`/coupons/${couponId}/receive`, {
        method: 'POST',
    })
}

export function getMyCoupons(params: MyCouponParams = {}) {
    return request<UserCoupon[]>('/coupons/my', { params })
}
