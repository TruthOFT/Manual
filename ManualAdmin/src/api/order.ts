import { request } from '@/api/request'
import type { AdminOrderDetail, AdminOrderListItem } from '@/types/order'

export type AdminOrderListParams = {
    keyword?: string
    orderStatus?: number
    payStatus?: number
}

export function getAdminOrders(params: AdminOrderListParams = {}) {
    return request<AdminOrderListItem[]>('/admin/orders', {
        params,
    })
}

export function getAdminOrderDetail(orderId: string) {
    return request<AdminOrderDetail>(`/admin/orders/${orderId}`)
}
