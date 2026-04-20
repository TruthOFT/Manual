import { request } from '@/api/request'
import type { ProductDetail, ProductListPageData } from '@/types/product'

type ProductListParams = {
    categoryId?: string
    originPlace?: string
    materialName?: string
}

export function getProductList(params: ProductListParams = {}) {
    return request<ProductListPageData>('/products', {
        params,
    })
}

export function getProductDetail(productId: string) {
    return request<ProductDetail>(`/products/${productId}`)
}
