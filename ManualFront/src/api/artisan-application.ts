import { request } from '@/api/request'
import type { ArtisanApplication, ArtisanApplicationSubmitRequest } from '@/types/artisan-application'

export function getArtisanApplication() {
    return request<ArtisanApplication>('/artisan-application')
}

export function submitArtisanApplication(data: ArtisanApplicationSubmitRequest) {
    return request<ArtisanApplication>('/artisan-application', {
        method: 'POST',
        data,
    })
}