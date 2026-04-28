<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
    ArrowUpOutlined,
    ArrowDownOutlined,
    DollarOutlined,
    ShoppingCartOutlined,
    UserAddOutlined,
    WarningOutlined,
} from '@ant-design/icons-vue'

import { getAdminDashboardOverview } from '@/api/dashboard'
import type { AdminDashboardOverview } from '@/types/dashboard'

const pageEyebrow = '管理员概览'
const pageTitle = '平台实时运营面板'
const refreshLabel = '刷新数据'
const todoTitle = '今日待办'
const trendTitle = '经营快照'
const errorDescription = '当前展示的是最近一次成功加载的数据，请检查后台服务或管理员登录状态。'
const refreshSuccessMessage = '管理员首页数据已刷新'
const loadErrorMessage = '管理员首页数据加载失败'

const emptyOverview: AdminDashboardOverview = {
    pendingAuditProductCount: 0,
    pendingOrderCount: 0,
    pendingShipmentOrderCount: 0,
    abnormalAddressOrderCount: 0,
    activeCustomerCount: 0,
    riskAlertCount: 0,
    lowStockRiskCount: 0,
    refundAlertCount: 0,
    negativeReviewRiskCount: 0,
    recentSevenDaysGmv: 0,
    recentSevenDaysOrderCount: 0,
    recentSevenDaysNewUserCount: 0,
}

const loading = ref(false)
const errorMessage = ref('')
const overview = ref<AdminDashboardOverview>(emptyOverview)
const lastUpdateTime = ref('')

const metrics = computed(() => [
    {
        label: '待处理订单',
        value: formatCount(overview.value.pendingOrderCount),
        note: '优先关注超时未发货和地址异常订单。',
        detail: `待发货 ${formatCount(overview.value.pendingShipmentOrderCount)} 单，地址异常 ${formatCount(overview.value.abnormalAddressOrderCount)} 单。`,
        color: '#17a36b',
        icon: ShoppingCartOutlined,
    },
    {
        label: '活跃顾客',
        value: formatCount(overview.value.activeCustomerCount),
        note: '近 7 天有下单行为的顾客数。',
        detail: '按近 7 天订单创建记录实时统计。',
        color: '#f0a22e',
        icon: UserAddOutlined,
    },
    {
        label: '风险提醒',
        value: formatCount(overview.value.riskAlertCount),
        note: '包含库存波动、评价预警和售后异常。',
        detail: `低库存 ${formatCount(overview.value.lowStockRiskCount)}，退款申请 ${formatCount(overview.value.refundAlertCount)}，低分评价 ${formatCount(overview.value.negativeReviewRiskCount)}。`,
        color: '#f1707d',
        icon: WarningOutlined,
    },
])

const quickActions = [
    { label: '商品管理', path: '/products', icon: '📦', color: '#4f46e5' },
    { label: '订单管理', path: '/orders', icon: '📋', color: '#10b981' },
    { label: '顾客管理', path: '/customers', icon: '👥', color: '#f59e0b' },
    { label: '优惠券管理', path: '/coupons', icon: '🎫', color: '#ef4444' },
]

const todoItems = computed(() => [
    {
        title: '待发货订单',
        owner: `${formatCount(overview.value.pendingShipmentOrderCount)} 单待跟进`,
        tagText: '优先',
        tagColor: 'gold',
        path: '/orders',
    },
    {
        title: '地址异常订单',
        owner: `${formatCount(overview.value.abnormalAddressOrderCount)} 单需校验`,
        tagText: '核对',
        tagColor: 'volcano',
        path: '/orders',
    },
    {
        title: '待审核商品',
        owner: `${formatCount(overview.value.pendingAuditProductCount)} 件待审核`,
        tagText: '审核',
        tagColor: 'blue',
        path: '/products',
    },
])

const trendItems = computed(() => [
    {
        label: '近 7 日 GMV',
        value: formatCurrency(overview.value.recentSevenDaysGmv),
        trend: `${formatCount(overview.value.recentSevenDaysOrderCount)} 笔订单`,
        tagColor: 'green',
        icon: DollarOutlined,
    },
    {
        label: '近 7 日订单数',
        value: formatCount(overview.value.recentSevenDaysOrderCount),
        trend: '按订单创建时间统计',
        tagColor: 'blue',
        icon: ShoppingCartOutlined,
    },
    {
        label: '近 7 日新增用户',
        value: formatCount(overview.value.recentSevenDaysNewUserCount),
        trend: '不含管理员账号',
        tagColor: 'purple',
        icon: UserAddOutlined,
    },
])

async function loadOverview(showSuccess = false) {
    loading.value = true
    errorMessage.value = ''
    try {
        overview.value = await getAdminDashboardOverview()
        lastUpdateTime.value = new Date().toLocaleString('zh-CN')
        if (showSuccess) {
            message.success(refreshSuccessMessage)
        }
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : loadErrorMessage
        if (showSuccess) {
            message.error(errorMessage.value)
        }
    } finally {
        loading.value = false
    }
}

function handleRefresh() {
    void loadOverview(true)
}

function formatCount(value: number | string) {
    const count = Number(value ?? 0)
    return new Intl.NumberFormat('zh-CN').format(Number.isFinite(count) ? count : 0)
}

function formatCurrency(value: number | string) {
    const amount = Number(value ?? 0)
    return `¥ ${new Intl.NumberFormat('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    }).format(Number.isFinite(amount) ? amount : 0)}`
}

onMounted(() => {
    void loadOverview()
})
</script>

<template>
    <div class="dashboard-view">
        <section class="page-head">
            <div>
                <p class="page-eyebrow">{{ pageEyebrow }}</p>
                <h2>{{ pageTitle }}</h2>
                <p v-if="lastUpdateTime" class="update-time">最后更新：{{ lastUpdateTime }}</p>
            </div>
            <a-button class="refresh-btn" type="primary" :loading="loading" @click="handleRefresh">
                {{ refreshLabel }}
            </a-button>
        </section>

        <a-alert
            v-if="errorMessage"
            class="page-alert"
            type="error"
            show-icon
            :message="errorMessage"
            :description="errorDescription"
        />

        <section class="quick-actions">
            <RouterLink
                v-for="action in quickActions"
                :key="action.path"
                :to="action.path"
                class="quick-action-card"
            >
                <span class="action-icon">{{ action.icon }}</span>
                <span class="action-label">{{ action.label }}</span>
            </RouterLink>
        </section>

        <section class="stats-grid">
            <a-card v-for="item in metrics" :key="item.label" class="panel stat-card" :bordered="false" :loading="loading">
                <div class="stat-header">
                    <span class="stat-label">{{ item.label }}</span>
                    <component :is="item.icon" class="stat-icon" :style="{ color: item.color }" />
                </div>
                <strong :style="{ color: item.color }">{{ item.value }}</strong>
                <p>{{ item.note }}</p>
                <small>{{ item.detail }}</small>
            </a-card>
        </section>

        <section class="content-grid">
            <a-card class="panel" :bordered="false" :title="todoTitle" :loading="loading">
                <div class="todo-list">
                    <RouterLink
                        v-for="item in todoItems"
                        :key="item.title"
                        :to="item.path"
                        class="todo-item"
                    >
                        <div>
                            <strong>{{ item.title }}</strong>
                            <p>{{ item.owner }}</p>
                        </div>
                        <a-tag :color="item.tagColor">{{ item.tagText }}</a-tag>
                    </RouterLink>
                </div>
            </a-card>

            <a-card class="panel" :bordered="false" :title="trendTitle" :loading="loading">
                <div class="trend-list">
                    <div v-for="item in trendItems" :key="item.label" class="trend-item">
                        <div class="trend-left">
                            <component :is="item.icon" class="trend-icon" />
                            <div>
                                <span>{{ item.label }}</span>
                                <strong>{{ item.value }}</strong>
                            </div>
                        </div>
                        <a-tag :color="item.tagColor">{{ item.trend }}</a-tag>
                    </div>
                </div>
            </a-card>
        </section>
    </div>
</template>

<style scoped>
.dashboard-view,
.stats-grid,
.content-grid,
.todo-list,
.trend-list {
    display: grid;
    gap: 18px;
}

.dashboard-view {
    gap: 22px;
}

.page-head {
    display: flex;
    align-items: end;
    justify-content: space-between;
    gap: 16px;
}

.page-head h2 {
    margin: 6px 0 0;
    font-family: var(--font-display);
    color: var(--text-strong);
    font-size: 2rem;
}

.page-eyebrow {
    margin: 0;
    color: var(--text-muted);
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.update-time {
    margin: 8px 0 0;
    color: var(--text-muted);
    font-size: 0.88rem;
}

.refresh-btn {
    min-width: 120px;
}

.page-alert {
    border-radius: 20px;
}

.quick-actions {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
}

.quick-action-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 24px;
    border-radius: 20px;
    background: var(--surface);
    box-shadow: var(--shadow);
    text-decoration: none;
    transition: all 0.3s ease;
}

.quick-action-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.action-icon {
    font-size: 2rem;
}

.action-label {
    color: var(--text-strong);
    font-weight: 600;
    font-size: 0.95rem;
}

.stats-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
}

.content-grid {
    grid-template-columns: 1.2fr 0.8fr;
}

.panel {
    border-radius: 28px;
    background: var(--surface);
    box-shadow: var(--shadow);
}

.stat-card :deep(.ant-card-body) {
    display: grid;
    gap: 12px;
}

.stat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.stat-label {
    color: var(--text-muted);
    font-size: 0.92rem;
}

.stat-icon {
    font-size: 1.5rem;
}

.stat-card strong {
    font-family: var(--font-display);
    font-size: 2.6rem;
}

.stat-card p,
.stat-card small,
.todo-item p {
    color: var(--text);
}

.stat-card small {
    font-size: 0.88rem;
}

.todo-item {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
    padding: 16px 18px;
    border-radius: 20px;
    background: var(--bg-accent);
    text-decoration: none;
    transition: all 0.2s ease;
}

.todo-item:hover {
    background: var(--bg-accent-hover, #f0f4ff);
    transform: translateX(4px);
}

.todo-item strong {
    color: var(--text-strong);
}

.trend-item {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
    padding: 16px 18px;
    border-radius: 20px;
    background: var(--bg-accent);
}

.trend-left {
    display: flex;
    align-items: center;
    gap: 12px;
}

.trend-icon {
    font-size: 1.3rem;
    color: var(--blue);
}

.trend-item strong {
    color: var(--text-strong);
    display: block;
    margin-top: 4px;
}

.trend-item span {
    color: var(--text-muted);
    font-size: 0.88rem;
}

@media (max-width: 1100px) {
    .stats-grid,
    .content-grid,
    .quick-actions {
        grid-template-columns: 1fr;
    }

    .page-head {
        align-items: start;
        flex-direction: column;
    }
}

@media (max-width: 720px) {
    .stats-grid {
        grid-template-columns: 1fr;
    }

    .trend-item {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
