export type CategoryItem = {
    id: number
    parentId: number | null
    categoryName: string
    categoryIcon: string
    categoryDesc: string
    categoryLevel: number
}

export type ProductCard = {
    id: number
    productName: string
    productSubtitle: string
    productCover: string
    craftType: string
    originPlace: string
    handmadeCycleDays: number
    supportCustom: number
    soldQuantity: number
    minPrice: number
    maxPrice: number
    artisanName: string
    shopName: string
    categoryName: string
}

export type ArtisanItem = {
    id: number
    artisanName: string
    shopName: string
    artisanAvatar: string
    coverUrl: string
    craftCategory: string
    originPlace: string
    experienceYears: number
    supportCustom: number
    productCount: number
}

export type RecentOrderItem = {
    id: number
    orderNo: string
    productName: string
    skuName: string
    productCover: string
    quantity: number
    totalAmount: number
    finishTime: string | null
}

export type HomePageData = {
    categories: CategoryItem[]
    products: ProductCard[]
    artisans: ArtisanItem[]
    recentOrders: RecentOrderItem[]
}
