import { request } from '@/api/request'
import type {
    AdminArtisanApplicationAuditRequest,
    AdminArtisanApplicationDetail,
    AdminArtisanApplicationItem,
} from '@/types/artisan-application'

type AdminArtisanApplicationListParams = {
    auditStatus?: number
    keyword?: string
}

export function getAdminArtisanApplications(params: AdminArtisanApplicationListParams = {}) {
    return request<AdminArtisanApplicationItem[]>('/admin/artisan-applications', {
        params,
    })
}

export function getAdminArtisanApplicationDetail(id: string) {
    return request<AdminArtisanApplicationDetail>(`/admin/artisan-applications/${id}`)
}

export function approveAdminArtisanApplication(id: string, data: AdminArtisanApplicationAuditRequest = {}) {
    return request<boolean>(`/admin/artisan-applications/${id}/approve`, {
        method: 'POST',
        data,
    })
}

export function rejectAdminArtisanApplication(id: string, data: AdminArtisanApplicationAuditRequest) {
    return request<boolean>(`/admin/artisan-applications/${id}/reject`, {
        method: 'POST',
        data,
    })
}