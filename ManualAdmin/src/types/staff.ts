export type AdminStaff = {
    userId: string
    profileId: string | null
    userAccount: string
    userName: string | null
    phone: string | null
    email: string | null
    gender: number
    userStatus: number
    staffName: string
    staffNo: string
    positionName: string | null
    salary: number | null
    entryTime: string | null
    staffStatus: number
    createTime: string | null
    updateTime: string | null
}

export type AdminStaffSaveRequest = {
    userAccount?: string
    userPassword?: string
    userName: string
    phone?: string
    email?: string
    gender: number
    userStatus: number
    staffName: string
    staffNo?: string
    positionName?: string
    salary?: number | null
    entryTime?: string
    staffStatus: number
}
