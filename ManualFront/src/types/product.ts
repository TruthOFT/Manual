import type { ProductCard } from '@/types/home'

export type ProductFilterCategory = {
    id: string
    categoryName: string
}

export type ProductFilterOptions = {
    categories: ProductFilterCategory[]
    originPlaces: string[]
    materials: string[]
}

export type ProductListPageData = {
    products: ProductCard[]
    filters: ProductFilterOptions
}

export type ProductImage = {
    id: string
    imageUrl: string
    imageType: string
}

export type ProductMaterial = {
    id: string
    materialName: string
    materialOrigin: string | null
    materialRatio: string | null
}

export type ProductSku = {
    id: string
    skuCode: string
    skuName: string
    skuCover: string | null
    specText: string | null
    materialType: string | null
    weight: number | string | null
    price: number | string
    originalPrice: number | string
    stock: number
    lockedStock: number
    status: number
}

export type ProductReview = {
    id: string
    score: number
    reviewContent: string | null
    reviewImages: string | null
    isAnonymous: number
    replyContent: string | null
    replyTime: string | null
    createTime: string | null
}

export type ProductDetail = {
    id: string
    categoryId: string
    artisanId: string
    productName: string
    productSubtitle: string
    productCover: string
    productDesc: string
    craftType: string
    materialDesc: string | null
    originPlace: string
    handmadeCycleDays: number
    supportCustom: number
    soldQuantity: number
    favoriteCount: number
    reviewCount: number
    minPrice: number
    maxPrice: number
    categoryName: string
    artisanName: string
    shopName: string
    artisanAvatar: string
    images: ProductImage[]
    materials: ProductMaterial[]
    skus: ProductSku[]
    reviews: ProductReview[]
}
