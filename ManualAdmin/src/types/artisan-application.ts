export type AdminArtisanApplicationItem = {
    id: string
    userId: string
    userAccount: string
    userName: string | null
    artisanName: string
    shopName: string
    contactPhone: string | null
    depositAmount: number | string | null
    auditStatus: number
    applyTime: string | null
    auditTime: string | null
}

export type AdminArtisanApplicationDetail = {
    id: string
    userId: string
    userAccount: string
    userName: string | null
    userAvatarUrl: string | null
    phone: string | null
    email: string | null
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
    qualificationDesc: string | null
    qualificationImages: string[]
    depositAmount: number | string | null
    auditRemark: string | null
    auditStatus: number
    shelfStatus: number
    applyTime: string | null
    auditTime: string | null
    createTime: string | null
    updateTime: string | null
}

export type AdminArtisanApplicationAuditRequest = {
    auditRemark?: string
}