import { request } from '@/api/request'
import type { ProductDetail, ProductFavorite, ProductListPageData } from '@/types/product'

type ProductListParams = {
    categoryId?: string
    originPlace?: string
    priceRange?: string
}

export function getProductList(params: ProductListParams = {}) {
    return request<ProductListPageData>('/products', {
        params,
    })
}

export function getProductDetail(productId: string) {
    return request<ProductDetail>(`/products/${productId}`)
}

export function getProductFavorites() {
    return request<ProductFavorite[]>('/products/favorites')
}

export function favoriteProduct(productId: string) {
    return request<boolean>(`/products/${productId}/favorite`, {
        method: 'POST',
    })
}

export function unfavoriteProduct(productId: string) {
    return request<boolean>(`/products/${productId}/favorite`, {
        method: 'DELETE',
    })
}
