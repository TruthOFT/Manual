export type AdminCategory = {
    id: string
    parentId: string | null
    parentName: string | null
    categoryName: string
    categoryIcon: string | null
    categoryDesc: string | null
    categoryLevel: number
    sortOrder: number
    isEnable: number
    productCount: number
    createTime: string | null
    updateTime: string | null
}

export type AdminCategorySaveRequest = {
    parentId?: string
    categoryName: string
    categoryIcon?: string
    categoryDesc?: string
    categoryLevel: number
    sortOrder: number
    isEnable: number
}
