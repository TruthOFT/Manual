export type CategoryItem = {
    id: string
    parentId: string | null
    categoryName: string
    categoryIcon: string
    categoryDesc: string
    categoryLevel: number
}

export type ProductCard = {
    id: string
    categoryId: string
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
    categoryName: string
}

export type RecentOrderItem = {
    id: string
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
    recentOrders: RecentOrderItem[]
}
