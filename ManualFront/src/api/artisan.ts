import { request } from '@/api/request'
import type { ArtisanDetail } from '@/types/artisan'
import type { ArtisanItem } from '@/types/home'

export function getArtisanList() {
    return request<ArtisanItem[]>('/artisans')
}

export function getArtisanDetail(artisanId: string) {
    return request<ArtisanDetail>(`/artisans/${artisanId}`)
}
