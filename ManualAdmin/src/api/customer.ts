import { request } from '@/api/request'
import type { AdminCustomer, AdminCustomerSaveRequest } from '@/types/customer'

type CustomerListParams = {
    keyword?: string
    userStatus?: number
}

export function getAdminCustomers(params: CustomerListParams = {}) {
    return request<AdminCustomer[]>('/admin/customers', { params })
}

export function getAdminCustomerDetail(userId: string) {
    return request<AdminCustomer>(`/admin/customers/${userId}`)
}

export function createAdminCustomer(data: AdminCustomerSaveRequest) {
    return request<string>('/admin/customers', {
        method: 'POST',
        data,
    })
}

export function updateAdminCustomer(userId: string, data: AdminCustomerSaveRequest) {
    return request<boolean>(`/admin/customers/${userId}`, {
        method: 'PUT',
        data,
    })
}

export function deleteAdminCustomer(userId: string) {
    return request<boolean>(`/admin/customers/${userId}`, {
        method: 'DELETE',
    })
}
