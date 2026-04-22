<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'

import { getAdminOrderDetail, getAdminOrders } from '@/api/order'
import type { AdminOrderDetail, AdminOrderListItem } from '@/types/order'

const loading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const orders = ref<AdminOrderListItem[]>([])
const currentOrder = ref<AdminOrderDetail | null>(null)
const keyword = ref('')
const orderStatus = ref<number | undefined>()
const payStatus = ref<number | undefined>()

const columns = [
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo' },
    { title: '买家', dataIndex: 'buyerName', key: 'buyerName' },
    { title: '商品', dataIndex: 'productName', key: 'productName' },
    { title: '金额', dataIndex: 'payAmount', key: 'payAmount' },
    { title: '支付', dataIndex: 'payStatus', key: 'payStatus' },
    { title: '状态', dataIndex: 'orderStatus', key: 'orderStatus' },
    { title: '下单时间', dataIndex: 'createTime', key: 'createTime' },
    { title: '操作', key: 'action' },
]

const pendingPaymentCount = computed(() => orders.value.filter((item) => item.orderStatus === 0).length)
const pendingShipmentCount = computed(() => orders.value.filter((item) => item.orderStatus === 1).length)
const paidCount = computed(() => orders.value.filter((item) => item.payStatus === 1).length)

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

function getPayStatusText(value: number) {
    if (value === 0) return '未支付'
    if (value === 1) return '已支付'
    if (value === 2) return '已退款'
    return '未知'
}

function getPayStatusColor(value: number) {
    if (value === 1) return 'green'
    if (value === 2) return 'blue'
    return 'orange'
}

function formatMoney(value: number | string | null | undefined) {
    return `CNY ${Number(value || 0).toFixed(2)}`
}

async function loadOrders() {
    loading.value = true
    try {
        orders.value = await getAdminOrders({
            keyword: keyword.value || undefined,
            orderStatus: orderStatus.value,
            payStatus: payStatus.value,
        })
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载订单失败')
    } finally {
        loading.value = false
    }
}

async function openDetail(record: AdminOrderListItem) {
    detailVisible.value = true
    detailLoading.value = true
    currentOrder.value = null
    try {
        currentOrder.value = await getAdminOrderDetail(record.orderId)
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载订单详情失败')
    } finally {
        detailLoading.value = false
    }
}

onMounted(() => {
    void loadOrders()
})
</script>

<template>
    <div class="orders-page">
        <a-card class="panel summary-panel" :bordered="false">
            <a-row :gutter="[16, 16]">
                <a-col :xs="24" :md="8">
                    <div class="summary-item">
                        <span>待支付</span>
                        <strong>{{ pendingPaymentCount }}</strong>
                    </div>
                </a-col>
                <a-col :xs="24" :md="8">
                    <div class="summary-item">
                        <span>待发货</span>
                        <strong>{{ pendingShipmentCount }}</strong>
                    </div>
                </a-col>
                <a-col :xs="24" :md="8">
                    <div class="summary-item">
                        <span>已支付</span>
                        <strong>{{ paidCount }}</strong>
                    </div>
                </a-col>
            </a-row>
        </a-card>

        <a-card class="panel" :bordered="false" title="订单管理">
            <div class="filter-grid">
                <a-input v-model:value="keyword" allow-clear size="large" placeholder="搜索订单号、买家、商品" @press-enter="loadOrders" />
                <a-select v-model:value="orderStatus" allow-clear size="large" placeholder="订单状态">
                    <a-select-option :value="0">待支付</a-select-option>
                    <a-select-option :value="1">待发货</a-select-option>
                    <a-select-option :value="2">待收货</a-select-option>
                    <a-select-option :value="3">已完成</a-select-option>
                    <a-select-option :value="4">已取消</a-select-option>
                </a-select>
                <a-select v-model:value="payStatus" allow-clear size="large" placeholder="支付状态">
                    <a-select-option :value="0">未支付</a-select-option>
                    <a-select-option :value="1">已支付</a-select-option>
                    <a-select-option :value="2">已退款</a-select-option>
                </a-select>
                <a-button type="primary" size="large" :loading="loading" @click="loadOrders">查询</a-button>
            </div>

            <a-table
                row-key="orderId"
                :columns="columns"
                :data-source="orders"
                :loading="loading"
                :pagination="{ pageSize: 8 }"
            >
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'productName'">
                        <div class="product-cell">
                            <strong>{{ record.productName }}</strong>
                            <span>{{ record.skuName }} x {{ record.quantity }}</span>
                        </div>
                    </template>
                    <template v-else-if="column.key === 'payAmount'">
                        {{ formatMoney(record.payAmount) }}
                    </template>
                    <template v-else-if="column.key === 'payStatus'">
                        <a-tag :color="getPayStatusColor(record.payStatus)">
                            {{ getPayStatusText(record.payStatus) }}
                        </a-tag>
                    </template>
                    <template v-else-if="column.key === 'orderStatus'">
                        <a-tag :color="getOrderStatusColor(record.orderStatus)">
                            {{ getOrderStatusText(record.orderStatus) }}
                        </a-tag>
                    </template>
                    <template v-else-if="column.key === 'action'">
                        <a-button type="link" @click="openDetail(record)">详情</a-button>
                    </template>
                </template>
            </a-table>
        </a-card>

        <a-modal v-model:open="detailVisible" title="订单详情" :footer="null" width="760px">
            <a-skeleton v-if="detailLoading" active :paragraph="{ rows: 8 }" />
            <div v-else-if="currentOrder" class="detail-body">
                <a-descriptions bordered :column="1" size="middle">
                    <a-descriptions-item label="订单号">{{ currentOrder.orderNo }}</a-descriptions-item>
                    <a-descriptions-item label="买家">{{ currentOrder.buyerName }}</a-descriptions-item>
                    <a-descriptions-item label="订单状态">
                        <a-tag :color="getOrderStatusColor(currentOrder.orderStatus)">
                            {{ getOrderStatusText(currentOrder.orderStatus) }}
                        </a-tag>
                    </a-descriptions-item>
                    <a-descriptions-item label="支付状态">
                        <a-tag :color="getPayStatusColor(currentOrder.payStatus)">
                            {{ getPayStatusText(currentOrder.payStatus) }}
                        </a-tag>
                    </a-descriptions-item>
                    <a-descriptions-item label="支付方式">
                        {{ currentOrder.payType === 'balance' ? '账户余额' : '未支付' }}
                    </a-descriptions-item>
                    <a-descriptions-item label="金额">
                        商品 {{ formatMoney(currentOrder.productAmount) }} / 实付 {{ formatMoney(currentOrder.payAmount) }}
                    </a-descriptions-item>
                    <a-descriptions-item label="收货人">
                        {{ currentOrder.receiverName }} {{ currentOrder.receiverPhone }}
                    </a-descriptions-item>
                    <a-descriptions-item label="收货地址">
                        {{ currentOrder.province }}{{ currentOrder.city }}{{ currentOrder.district }}{{ currentOrder.detailAddress }}
                    </a-descriptions-item>
                    <a-descriptions-item label="买家备注">
                        {{ currentOrder.buyerRemark || '无' }}
                    </a-descriptions-item>
                    <a-descriptions-item label="时间">
                        下单 {{ currentOrder.createTime || '-' }} / 支付 {{ currentOrder.payTime || '-' }}
                    </a-descriptions-item>
                </a-descriptions>

                <a-card v-if="currentOrder.item" class="item-card" :bordered="false">
                    <strong>{{ currentOrder.item.productName }}</strong>
                    <span>{{ currentOrder.item.skuName }} / {{ currentOrder.item.specText || '标准规格' }}</span>
                    <b>{{ formatMoney(currentOrder.item.salePrice) }} x {{ currentOrder.item.quantity }}</b>
                </a-card>
            </div>
        </a-modal>
    </div>
</template>

<style scoped>
.orders-page {
    display: grid;
    gap: 24px;
}

.panel {
    border-radius: 20px;
    background: var(--surface);
    box-shadow: var(--shadow);
}

.filter-grid {
    display: grid;
    gap: 14px;
    grid-template-columns: minmax(0, 1.5fr) minmax(150px, 0.8fr) minmax(150px, 0.8fr) auto;
    margin-bottom: 18px;
}

.summary-item {
    display: grid;
    gap: 8px;
    padding: 18px;
    border-radius: 16px;
    background: rgba(255, 247, 238, 0.8);
}

.summary-item span,
.product-cell span {
    color: var(--text-muted);
}

.summary-item strong {
    color: var(--text-strong);
    font-size: 1.8rem;
}

.product-cell,
.detail-body,
.item-card :deep(.ant-card-body) {
    display: grid;
    gap: 8px;
}

.item-card {
    margin-top: 16px;
    border-radius: 16px;
    background: rgba(255, 247, 238, 0.86);
}

@media (max-width: 980px) {
    .filter-grid {
        grid-template-columns: 1fr;
    }
}
</style>
