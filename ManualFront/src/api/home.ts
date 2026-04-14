import { request } from '@/api/request'
import type { HomePageData } from '@/types/home'

export function getHomePage() {
    return request<HomePageData>('/api/home')
}
