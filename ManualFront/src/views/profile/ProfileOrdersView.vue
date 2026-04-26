<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

import { cancelOrder, getUserOrders, payOrder } from '@/api/order'
import { resolveUploadUrl } from '@/api/upload'
import { useUserStore } from '@/stores/user'
import type { OrderDetail } from '@/types/order'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const actionLoadingId = ref<string>()
const statusFilter = ref('all')
const orders = ref<OrderDetail[]>([])
const errorMessage = ref('')
const now = ref(Date.now())
let timer: number | undefined

const statusOptions = [
    { label: '全部', value: 'all' },
    { label: '待支付', value: '0' },
    { label: '待发货', value: '1' },
    { label: '待收货', value: '2' },
    { label: '已完成', value: '3' },
    { label: '已取消', value: '4' },
]

const pendingPaymentCount = computed(() => orders.value.filter((item) => item.orderStatus === 0).length)

function getOrderStatusText(value: number) {
    if (value === 0) return '待支付'
    if (value === 1) return '待发货'
    if (value === 2) return '待收货'
    if (value === 3) return '已完成'
    if (value === 4) return '已取消'
    return '未知'
}

function getOrderStatusColor(value: number) {
    if (value === 0) return 'orange'
    if (value === 1) return 'gold'
    if (value === 2) return 'blue'
    if (value === 3) return 'green'
    if (value === 4) return 'default'
    return 'default'
}

function formatMoney(value: number | string | null | undefined) {
    return `CNY ${Number(value || 0).toFixed(2)}`
}

function parseDateTime(value: string | null | undefined) {
    if (!value) {
        return 0
    }
    return new Date(value.replace(' ', 'T')).getTime()
}

function getRemainingMilliseconds(order: OrderDetail) {
    const expireAt = parseDateTime(order.expireTime)
    if (!expireAt) {
        return 0
    }
    return Math.max(0, expireAt - now.value)
}

function getCountdownText(order: OrderDetail) {
    const remaining = getRemainingMilliseconds(order)
    if (remaining <= 0) {
        return '支付时间已到，等待系统取消'
    }
    const totalSeconds = Math.ceil(remaining / 1000)
    const minutes = Math.floor(totalSeconds / 60)
    const seconds = totalSeconds % 60
    return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

function canPay(order: OrderDetail) {
    return order.orderStatus === 0 && getRemainingMilliseconds(order) > 0
}

async function loadOrders() {
    loading.value = true
    errorMessage.value = ''
    try {
        orders.value = await getUserOrders({
            orderStatus: statusFilter.value === 'all' ? undefined : Number(statusFilter.value),
        })
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载订单失败'
    } finally {
        loading.value = false
    }
}

async function handlePay(order: OrderDetail) {
    actionLoadingId.value = order.orderId
    try {
        const nextUser = await payOrder(order.orderId)
        userStore.setCurrentUser(nextUser)
        await loadOrders()
    } catch (error) {
        const errorMessageText = error instanceof Error ? error.message : '支付失败'
        if (errorMessageText.includes('余额') || errorMessageText.toLowerCase().includes('balance')) {
            message.error('余额不足，请先充值后再支付')
        } else {
            message.error(errorMessageText)
        }
    } finally {
        actionLoadingId.value = undefined
    }
}

async function handleCancel(order: OrderDetail) {
    actionLoadingId.value = order.orderId
    try {
        await cancelOrder(order.orderId)
        await loadOrders()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '取消订单失败')
    } finally {
        actionLoadingId.value = undefined
    }
}

onMounted(() => {
    timer = window.setInterval(() => {
        now.value = Date.now()
    }, 1000)
    void loadOrders()
})

onBeforeUnmount(() => {
    if (timer) {
        window.clearInterval(timer)
    }
})
</script>

<template>
    <a-card class="profile-panel orders-panel" :bordered="false" title="我的订单">
        <template #extra>
            <a-space>
                <a-tag color="orange">待支付 {{ pendingPaymentCount }}</a-tag>
                <a-button class="manual-ant-btn manual-ant-btn-soft" @click="router.push('/profile/settings')">去充值</a-button>
            </a-space>
        </template>

        <div class="filter-row">
            <a-segmented v-model:value="statusFilter" :options="statusOptions" @change="loadOrders" />
            <a-button class="manual-ant-btn manual-ant-btn-soft" :loading="loading" @click="loadOrders">刷新</a-button>
        </div>

        <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage" />
        <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />

        <div v-else class="order-list">
            <a-empty v-if="!orders.length" description="暂无订单" />
            <a-card v-for="order in orders" :key="order.orderId" class="order-card" :bordered="false">
                <div class="order-head">
                    <div>
                        <strong>{{ order.orderNo }}</strong>
                        <span>{{ order.createTime || '-' }}</span>
                    </div>
                    <a-tag :color="getOrderStatusColor(order.orderStatus)">
                        {{ getOrderStatusText(order.orderStatus) }}
                    </a-tag>
                </div>

                <div v-if="order.item" class="item-row">
                    <img :src="resolveUploadUrl(order.item.skuCover || order.item.productCover)" :alt="order.item.productName" />
                    <div>
                        <strong>{{ order.item.productName }}</strong>
                        <span>{{ order.item.skuName }} / {{ order.item.specText || '标准规格' }}</span>
                        <span>x {{ order.item.quantity }}</span>
                    </div>
                    <b>{{ formatMoney(order.payAmount) }}</b>
                </div>

                <div class="address-line">
                    {{ order.receiverName }} {{ order.receiverPhone }} / {{ order.province }}{{ order.city }}{{ order.district }}{{ order.detailAddress }}
                </div>

                <div class="order-actions">
                    <span v-if="order.orderStatus === 0" class="countdown">
                        剩余支付时间：{{ getCountdownText(order) }}
                    </span>
                    <span v-else>支付方式：{{ order.payType === 'balance' ? '账户余额' : '未支付' }}</span>
                    <a-space v-if="order.orderStatus === 0">
                        <a-button
                            class="manual-ant-btn manual-ant-btn-primary"
                            :disabled="!canPay(order)"
                            :loading="actionLoadingId === order.orderId"
                            @click="handlePay(order)"
                        >
                            立即支付
                        </a-button>
                        <a-button
                            class="manual-ant-btn manual-ant-btn-ghost"
                            :loading="actionLoadingId === order.orderId"
                            @click="handleCancel(order)"
                        >
                            取消订单
                        </a-button>
                    </a-space>
                </div>
            </a-card>
        </div>
    </a-card>
</template>

<style scoped>
.orders-panel :deep(.ant-card-body),
.order-list {
    display: grid;
    gap: 16px;
}

.filter-row {
    display: flex;
    justify-content: space-between;
    gap: 14px;
    align-items: center;
}

.order-card {
    border-radius: 16px;
    background: rgba(255, 247, 238, 0.86);
}

.order-card :deep(.ant-card-body) {
    display: grid;
    gap: 14px;
}

.order-head,
.item-row,
.order-actions {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
}

.order-head div,
.item-row div {
    display: grid;
    gap: 4px;
}

.order-head strong,
.item-row strong,
.item-row b {
    color: var(--text-strong);
}

.order-head span,
.item-row span,
.order-actions span,
.address-line {
    color: var(--text-muted);
}

.countdown {
    color: var(--coral-deep);
    font-weight: 800;
}

.item-row img {
    width: 76px;
    height: 76px;
    border-radius: 12px;
    object-fit: cover;
}

.item-row div {
    flex: 1;
}

@media (max-width: 760px) {
    .filter-row,
    .order-head,
    .item-row,
    .order-actions {
        align-items: flex-start;
        flex-direction: column;
    }
}
</style>
