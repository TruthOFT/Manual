import { request } from '@/api/request'
import type { OrderCreateRequest, OrderDetail, PayOrderResponse, UserAddress, UserAddressSaveRequest } from '@/types/order'

export function getOrderAddresses() {
    return request<UserAddress[]>('/orders/addresses')
}

export function createOrderAddress(data: UserAddressSaveRequest) {
    return request<UserAddress>('/orders/addresses', {
        method: 'POST',
        data,
    })
}

export function updateOrderAddress(addressId: string, data: UserAddressSaveRequest) {
    return request<UserAddress>(`/orders/addresses/${addressId}`, {
        method: 'PUT',
        data,
    })
}

export function deleteOrderAddress(addressId: string) {
    return request<boolean>(`/orders/addresses/${addressId}`, {
        method: 'DELETE',
    })
}

export function getUserOrders(params: { orderStatus?: number } = {}) {
    return request<OrderDetail[]>('/orders', {
        params,
    })
}

export function getUserOrderDetail(orderId: string) {
    return request<OrderDetail>(`/orders/${orderId}`)
}

export function createOrder(data: OrderCreateRequest) {
    return request<OrderDetail>('/orders', {
        method: 'POST',
        data,
        showSuccessMessage: false,
    })
}

export function payOrder(orderId: string) {
    return request<PayOrderResponse>(`/orders/${orderId}/pay`, {
        method: 'POST',
    })
}

export function cancelOrder(orderId: string) {
    return request<boolean>(`/orders/${orderId}/cancel`, {
        method: 'POST',
    })
}
