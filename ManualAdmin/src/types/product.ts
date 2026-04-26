export type AdminProductListItem = {
    id: string
    categoryId: string
    categoryName: string | null
    productName: string
    productSubtitle: string | null
    productCover: string | null
    inventory: number
    minPrice: number | string
    maxPrice: number | string
    auditStatus: number
    status: number
    createTime: string | null
    updateTime: string | null
}

export type AdminProductCategoryOption = {
    id: string
    parentId: string | null
    parentName: string | null
    categoryName: string
    categoryLevel: number
}

export type AdminProductMeta = {
    categories: AdminProductCategoryOption[]
}

export type AdminProductImage = {
    id: string
    imageUrl: string
    imageType: string | null
}

export type AdminProductMaterial = {
    id: string
    materialName: string
    materialOrigin: string | null
    materialRatio: string | null
}

export type AdminProductSku = {
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
    status: number
}

export type AdminProductSkuSaveRequest = {
    skuCode?: string
    skuName: string
    skuCover: string
    specText: string
    materialType: string
    weight: number | string
    price: number | string
    originalPrice: number | string
    stock: number
    status: number
}

export type AdminProductDetail = {
    id: string
    categoryId: string
    categoryName: string | null
    productName: string
    productSubtitle: string | null
    productCover: string | null
    productDesc: string | null
    craftType: string | null
    materialDesc: string | null
    originPlace: string | null
    handmadeCycleDays: number
    supportCustom: number
    inventory: number
    soldQuantity: number
    favoriteCount: number
    reviewCount: number
    auditStatus: number
    status: number
    sortOrder: number
    minPrice: number | string
    maxPrice: number | string
    images: AdminProductImage[]
    materials: AdminProductMaterial[]
    skus: AdminProductSku[]
}

export type AdminProductSaveRequest = {
    categoryId: string
    productName: string
    productSubtitle: string
    productCover: string
    productDesc: string
    craftType: string
    materialDesc: string
    originPlace: string
    handmadeCycleDays: number
    supportCustom: number
    inventory: number
    minPrice: number | string
    maxPrice: number | string
    auditStatus: number
    status: number
    sortOrder: number
    skus: AdminProductSkuSaveRequest[]
}
