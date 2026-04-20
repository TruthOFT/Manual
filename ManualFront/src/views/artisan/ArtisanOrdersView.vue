<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getArtisanOrders } from '@/api/artisan-center'
import type { ArtisanOrderItem } from '@/types/artisan-center'

const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const keyword = ref('')
const orderStatus = ref<number | undefined>()
const orders = ref<ArtisanOrderItem[]>([])

function getOrderStatusText(value: number) {
    if (value === 0) return '待付款'
    if (value === 1) return '待发货'
    if (value === 2) return '待收货'
    if (value === 3) return '已完成'
    if (value === 4) return '已取消'
    return '未知'
}

async function loadOrders() {
    loading.value = true
    errorMessage.value = ''
    try {
        orders.value = await getArtisanOrders({
            orderStatus: orderStatus.value,
            keyword: keyword.value || undefined,
        })
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载订单列表失败'
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    void loadOrders()
})
</script>

<template>
    <div class="artisan-view">
        <a-card class="artisan-hero-card" :bordered="false">
            <p class="eyebrow">订单履约</p>
            <h1>按匠人维度隔离订单项，只看自己要处理的履约任务。</h1>
        </a-card>

        <a-card class="artisan-panel" :bordered="false" title="筛选条件">
            <div class="filter-grid">
                <a-input v-model:value="keyword" size="large" placeholder="搜索订单号或商品名称" @press-enter="loadOrders" />
                <a-select v-model:value="orderStatus" allow-clear size="large" placeholder="订单状态">
                    <a-select-option :value="1">待发货</a-select-option>
                    <a-select-option :value="2">待收货</a-select-option>
                    <a-select-option :value="3">已完成</a-select-option>
                </a-select>
                <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" @click="loadOrders">查询</a-button>
            </div>
        </a-card>

        <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage" />
        <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />

        <div v-else class="order-grid">
            <a-empty v-if="!orders.length" description="当前没有符合条件的订单" />
            <a-card v-for="item in orders" :key="item.id" class="artisan-panel order-card" :bordered="false">
                <div class="order-head">
                    <div>
                        <strong>{{ item.productName }}</strong>
                        <p>{{ item.orderNo }} / {{ item.skuName }}</p>
                    </div>
                    <a-tag :color="item.orderStatus === 1 ? 'gold' : item.orderStatus === 2 ? 'blue' : 'green'">
                        {{ getOrderStatusText(item.orderStatus) }}
                    </a-tag>
                </div>
                <div class="meta-grid">
                    <span>买家：{{ item.buyerName }}</span>
                    <span>数量：{{ item.quantity }}</span>
                    <span>金额：¥ {{ item.totalAmount }}</span>
                    <span>下单：{{ item.createTime || '-' }}</span>
                </div>
                <a-button class="manual-ant-btn manual-ant-btn-primary" @click="router.push(`/artisan/orders/${item.id}`)">
                    查看详情
                </a-button>
            </a-card>
        </div>
    </div>
</template>

<style scoped>
.artisan-view {
    display: grid;
    gap: 24px;
}

.eyebrow {
    color: var(--coral-deep);
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

h1 {
    margin: 8px 0 0;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: clamp(2rem, 4vw, 3.1rem);
}

.filter-grid,
.order-grid,
.meta-grid {
    display: grid;
    gap: 14px;
}

.filter-grid {
    grid-template-columns: minmax(0, 2fr) minmax(0, 1fr) auto;
}

.order-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.order-card {
    display: grid;
    gap: 16px;
}

.order-head {
    display: flex;
    justify-content: space-between;
    gap: 12px;
}

.order-head strong {
    color: var(--text-strong);
}

.order-head p,
.meta-grid span {
    color: var(--text);
}

.meta-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

@media (max-width: 980px) {
    .filter-grid,
    .order-grid,
    .meta-grid {
        grid-template-columns: 1fr;
    }
}
</style>
