export type ProfileStat = {
    label: string
    value: string
    note: string
}

export type ProfileOrder = {
    id: number
    orderNo: string
    productName: string
    productCover: string
    amount: number
    status: string
    orderTime: string
}

export type FavoriteItem = {
    id: number
    productName: string
    productCover: string
    priceText: string
    craftType: string
}

export type AddressItem = {
    id: number
    receiverName: string
    phone: string
    region: string
    detailAddress: string
    isDefault: boolean
}

export const profileStats: ProfileStat[] = [
    {
        label: '待收货订单',
        value: '3',
        note: '近期订单正在配送中，可在订单页查看物流进度。',
    },
    {
        label: '收藏作品',
        value: '12',
        note: '陶艺器皿和布艺礼品关注度最高。',
    },
    {
        label: '常用地址',
        value: '2',
        note: '支持后续接入默认地址与编辑能力。',
    },
]

export const profileOrders: ProfileOrder[] = [
    {
        id: 1,
        orderNo: 'so202604150001',
        productName: '暮色手作茶杯礼盒',
        productCover: 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?auto=format&fit=crop&w=900&q=80',
        amount: 456,
        status: '待收货',
        orderTime: '2026-04-15 09:20',
    },
    {
        id: 2,
        orderNo: 'so202604140019',
        productName: '晨雾织物桌旗',
        productCover: 'https://images.unsplash.com/photo-1511988617509-a57c8a288659?auto=format&fit=crop&w=900&q=80',
        amount: 299,
        status: '已发货',
        orderTime: '2026-04-14 11:45',
    },
    {
        id: 3,
        orderNo: 'so202604120006',
        productName: '山影胡桃木陈列座',
        productCover: 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80',
        amount: 469,
        status: '已完成',
        orderTime: '2026-04-12 14:08',
    },
]

export const favoriteItems: FavoriteItem[] = [
    {
        id: 1,
        productName: '暮色手作茶杯礼盒',
        productCover: 'https://images.unsplash.com/photo-1610701596007-11502861dcfa?auto=format&fit=crop&w=900&q=80',
        priceText: 'CNY 168 - 228',
        craftType: '手捏陶艺',
    },
    {
        id: 2,
        productName: '晨雾织物桌旗',
        productCover: 'https://images.unsplash.com/photo-1511988617509-a57c8a288659?auto=format&fit=crop&w=900&q=80',
        priceText: 'CNY 239 - 299',
        craftType: '手工织物',
    },
    {
        id: 3,
        productName: '松影香氛蜡烛',
        productCover: 'https://images.unsplash.com/photo-1602872029708-84d970d338d4?auto=format&fit=crop&w=900&q=80',
        priceText: 'CNY 128 - 168',
        craftType: '香氛手作',
    },
]

export const addressItems: AddressItem[] = [
    {
        id: 1,
        receiverName: '林安',
        phone: '138****5678',
        region: '上海市 浦东新区',
        detailAddress: '花木路 588 弄 18 号 1206 室',
        isDefault: true,
    },
    {
        id: 2,
        receiverName: '林安',
        phone: '139****2468',
        region: '杭州市 西湖区',
        detailAddress: '灵隐街道 云栖路 80 号',
        isDefault: false,
    },
]
