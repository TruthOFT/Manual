import { request } from '@/api/request'
import type { AdminCoupon, AdminCouponReceive, AdminCouponSaveRequest } from '@/types/coupon'

type CouponListParams = {
    keyword?: string
    couponStatus?: number
}

export function getAdminCoupons(params: CouponListParams = {}) {
    return request<AdminCoupon[]>('/admin/coupons', { params })
}

export function getAdminCouponDetail(couponId: string) {
    return request<AdminCoupon>(`/admin/coupons/${couponId}`)
}

export function createAdminCoupon(data: AdminCouponSaveRequest) {
    return request<string>('/admin/coupons', {
        method: 'POST',
        data,
    })
}

export function updateAdminCoupon(couponId: string, data: AdminCouponSaveRequest) {
    return request<boolean>(`/admin/coupons/${couponId}`, {
        method: 'PUT',
        data,
    })
}

export function deleteAdminCoupon(couponId: string) {
    return request<boolean>(`/admin/coupons/${couponId}`, {
        method: 'DELETE',
    })
}

export function updateAdminCouponStatus(couponId: string, couponStatus: number) {
    return request<boolean>(`/admin/coupons/${couponId}/status`, {
        method: 'POST',
        params: { couponStatus },
    })
}

export function receiveAdminCoupon(couponId: string, userId: string) {
    return request<boolean>(`/admin/coupons/${couponId}/receive`, {
        method: 'POST',
        data: { userId },
    })
}

export function getAdminCouponReceives(couponId: string) {
    return request<AdminCouponReceive[]>(`/admin/coupons/${couponId}/receives`)
}
