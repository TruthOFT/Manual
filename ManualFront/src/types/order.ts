import type { LoginUser } from '@/types/user'

export type UserAddress = {
    id: string
    receiverName: string
    receiverPhone: string
    province: string
    city: string
    district: string
    detailAddress: string
    postalCode: string | null
    tagName: string | null
    isDefault: number
}

export type OrderItem = {
    id: string
    productId: string
    skuId: string
    productName: string
    skuName: string
    productCover: string | null
    specText: string | null
    quantity: number
    salePrice: number | string
    totalAmount: number | string
}

export type OrderDetail = {
    orderId: string
    orderNo: string
    userId: string
    buyerName: string
    addressId: string
    orderStatus: number
    payStatus: number
    payType: string | null
    deliveryStatus: number
    productAmount: number | string
    discountAmount: number | string
    freightAmount: number | string
    payAmount: number | string
    buyerRemark: string | null
    receiverName: string
    receiverPhone: string
    province: string
    city: string
    district: string
    detailAddress: string
    payTime: string | null
    deliveryTime: string | null
    receiveTime: string | null
    finishTime: string | null
    cancelTime: string | null
    createTime: string | null
    expireTime: string | null
    item: OrderItem | null
}

export type OrderCreateRequest = {
    skuId: string
    quantity: number
    addressId: string
    buyerRemark?: string
}

export type UserAddressSaveRequest = {
    receiverName: string
    receiverPhone: string
    province: string
    city: string
    district: string
    detailAddress: string
    postalCode?: string
    tagName?: string
    isDefault?: number
}

export type PayOrderResponse = LoginUser
