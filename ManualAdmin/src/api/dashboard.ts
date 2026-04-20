import { request } from '@/api/request'
import type { AdminDashboardOverview } from '@/types/dashboard'

export function getAdminDashboardOverview() {
    return request<AdminDashboardOverview>('/admin/dashboard/overview')
}