export type AdminOrderItem = {
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

export type AdminOrderListItem = {
    orderId: string
    orderNo: string
    buyerName: string
    userAccount: string
    productName: string
    skuName: string
    quantity: number
    payAmount: number | string
    orderStatus: number
    payStatus: number
    payType: string | null
    createTime: string | null
    payTime: string | null
}

export type AdminOrderDetail = {
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
    item: AdminOrderItem | null
}
