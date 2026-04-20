import type { ProductImage, ProductMaterial, ProductReview } from '@/types/product'

export type ArtisanDashboardData = {
    pendingAuditCount: number
    onShelfCount: number
    pendingShipmentCount: number
    pendingCustomCount: number
    recentSevenDaysAmount: number | string
    recentSevenDaysSales: number
}

export type ArtisanCenterProfile = {
    id: string
    userId: string
    artisanName: string
    shopName: string
    artisanAvatar: string | null
    coverUrl: string | null
    artisanStory: string | null
    craftCategory: string | null
    originPlace: string | null
    experienceYears: number
    supportCustom: number
    contactPhone: string | null
    auditStatus: number
    shelfStatus: number
}

export type ArtisanCategoryOption = {
    id: string
    parentId: string | null
    parentName: string | null
    categoryName: string
    categoryLevel: number
}

export type ArtisanProductListItem = {
    id: string
    categoryId: string
    categoryName: string | null
    productName: string
    productSubtitle: string | null
    productCover: string
    inventory: number
    minPrice: number | string
    maxPrice: number | string
    auditStatus: number
    status: number
    reviewCount: number
    createTime: string | null
    updateTime: string | null
}

export type ArtisanProductSku = {
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

export type ArtisanProductDetail = {
    id: string
    categoryId: string
    artisanId: string
    categoryName: string | null
    artisanName: string
    shopName: string
    productName: string
    productSubtitle: string | null
    productCover: string
    productDesc: string
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
    images: ProductImage[]
    materials: ProductMaterial[]
    skus: ArtisanProductSku[]
    reviews: ProductReview[]
}

export type ArtisanProductPageData = {
    products: ArtisanProductListItem[]
    categories: ArtisanCategoryOption[]
}

export type ArtisanProductImageInput = {
    imageUrl: string
    imageType: string
    sortOrder: number
}

export type ArtisanProductMaterialInput = {
    materialName: string
    materialOrigin: string
    materialRatio: string
    sortOrder: number
}

export type ArtisanProductSkuInput = {
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

export type ArtisanProductSaveRequest = {
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
    sortOrder: number
    images: ArtisanProductImageInput[]
    materials: ArtisanProductMaterialInput[]
    skus: ArtisanProductSkuInput[]
}

export type ArtisanOrderItem = {
    id: string
    orderId: string
    productId: string
    skuId: string
    orderNo: string
    productName: string
    skuName: string
    productCover: string | null
    quantity: number
    totalAmount: number | string
    orderStatus: number
    deliveryStatus: number
    buyerName: string
    createTime: string | null
}

export type ArtisanOrderLogistics = {
    id: string
    companyName: string | null
    trackingNo: string | null
    senderName: string | null
    senderPhone: string | null
    receiverName: string | null
    receiverPhone: string | null
    logisticsRemark: string | null
    shipTime: string | null
    signTime: string | null
    status: number
}

export type ArtisanOrderDetail = {
    id: string
    orderId: string
    productId: string
    skuId: string
    userId: string
    orderNo: string
    productName: string
    skuName: string
    productCover: string | null
    specText: string | null
    quantity: number
    salePrice: number | string
    totalAmount: number | string
    payAmount: number | string
    orderStatus: number
    payStatus: number
    deliveryStatus: number
    buyerName: string
    buyerRemark: string | null
    receiverName: string
    receiverPhone: string
    province: string
    city: string
    district: string
    detailAddress: string
    createTime: string | null
    payTime: string | null
    deliveryTime: string | null
    logistics: ArtisanOrderLogistics | null
}

export type ArtisanOrderShipRequest = {
    companyName: string
    trackingNo: string
    senderName: string
    senderPhone: string
    logisticsRemark: string
}

export type ArtisanCustomRequirementItem = {
    id: string
    orderId: string
    orderItemId: string
    productId: string
    userId: string
    orderNo: string
    productName: string
    buyerName: string
    requirementTitle: string
    requirementContent: string
    confirmStatus: number
    confirmRemark: string | null
    confirmTime: string | null
    createTime: string | null
}

export type ArtisanCustomRequirementDetail = ArtisanCustomRequirementItem & {
    referenceImages: string | null
}

export type ArtisanCustomRequirementActionRequest = {
    confirmRemark?: string
}
