import { request } from '@/api/request'
import type { AdminStaff, AdminStaffSaveRequest } from '@/types/staff'

type StaffListParams = {
    keyword?: string
    userStatus?: number
    staffStatus?: number
}

export function getAdminStaff(params: StaffListParams = {}) {
    return request<AdminStaff[]>('/admin/staff', { params })
}

export function getAdminStaffDetail(userId: string) {
    return request<AdminStaff>(`/admin/staff/${userId}`)
}

export function createAdminStaff(data: AdminStaffSaveRequest) {
    return request<string>('/admin/staff', {
        method: 'POST',
        data,
    })
}

export function updateAdminStaff(userId: string, data: AdminStaffSaveRequest) {
    return request<boolean>(`/admin/staff/${userId}`, {
        method: 'PUT',
        data,
    })
}

export function deleteAdminStaff(userId: string) {
    return request<boolean>(`/admin/staff/${userId}`, {
        method: 'DELETE',
    })
}
