export type ArtisanApplication = {
    hasApplication: boolean
    id?: string
    userId?: string
    userRole?: string
    artisanName?: string | null
    shopName?: string | null
    artisanAvatar?: string | null
    coverUrl?: string | null
    artisanStory?: string | null
    craftCategory?: string | null
    originPlace?: string | null
    experienceYears?: number | null
    supportCustom?: number | null
    contactPhone?: string | null
    qualificationDesc?: string | null
    qualificationImages: string[]
    depositAmount?: number | string | null
    auditRemark?: string | null
    auditStatus?: number | null
    shelfStatus?: number | null
    applyTime?: string | null
    auditTime?: string | null
}

export type ArtisanApplicationSubmitRequest = {
    artisanName: string
    shopName: string
    artisanAvatar: string
    coverUrl: string
    artisanStory: string
    craftCategory: string
    originPlace: string
    experienceYears: number
    supportCustom: number
    contactPhone: string
    qualificationDesc: string
    qualificationImages: string[]
}