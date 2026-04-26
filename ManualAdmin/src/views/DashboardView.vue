<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'

import { getAdminDashboardOverview } from '@/api/dashboard'
import type { AdminDashboardOverview } from '@/types/dashboard'

const pageEyebrow = '\u7ba1\u7406\u5458\u6982\u89c8'
const pageTitle = '\u5e73\u53f0\u5b9e\u65f6\u8fd0\u8425\u9762\u677f'
const refreshLabel = '\u5237\u65b0\u6570\u636e'
const todoTitle = '\u4eca\u65e5\u5f85\u529e'
const trendTitle = '\u7ecf\u8425\u5feb\u7167'
const errorDescription = '\u5f53\u524d\u5c55\u793a\u7684\u662f\u6700\u8fd1\u4e00\u6b21\u6210\u529f\u52a0\u8f7d\u7684\u6570\u636e\uff0c\u8bf7\u68c0\u67e5\u540e\u53f0\u670d\u52a1\u6216\u7ba1\u7406\u5458\u767b\u5f55\u72b6\u6001\u3002'
const refreshSuccessMessage = '\u7ba1\u7406\u5458\u9996\u9875\u6570\u636e\u5df2\u5237\u65b0'
const loadErrorMessage = '\u7ba1\u7406\u5458\u9996\u9875\u6570\u636e\u52a0\u8f7d\u5931\u8d25'

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

const metrics = computed(() => [
    {
        label: '\u5f85\u5904\u7406\u8ba2\u5355',
        value: formatCount(overview.value.pendingOrderCount),
        note: '\u4f18\u5148\u5173\u6ce8\u8d85\u65f6\u672a\u53d1\u8d27\u548c\u5730\u5740\u5f02\u5e38\u8ba2\u5355\u3002',
        detail: `\u5f85\u53d1\u8d27 ${formatCount(overview.value.pendingShipmentOrderCount)} \u5355\uff0c\u5730\u5740\u5f02\u5e38 ${formatCount(overview.value.abnormalAddressOrderCount)} \u5355\u3002`,
        color: '#17a36b',
    },
    {
        label: '\u6d3b\u8dc3\u987e\u5ba2',
        value: formatCount(overview.value.activeCustomerCount),
        note: '\u8fd1 7 \u5929\u6709\u4e0b\u5355\u884c\u4e3a\u7684\u987e\u5ba2\u6570\u3002',
        detail: '\u6309\u8fd1 7 \u5929\u8ba2\u5355\u521b\u5efa\u8bb0\u5f55\u5b9e\u65f6\u7edf\u8ba1\u3002',
        color: '#f0a22e',
    },
    {
        label: '\u98ce\u9669\u63d0\u9192',
        value: formatCount(overview.value.riskAlertCount),
        note: '\u5305\u542b\u5e93\u5b58\u6ce2\u52a8\u3001\u8bc4\u4ef7\u9884\u8b66\u548c\u552e\u540e\u5f02\u5e38\u3002',
        detail: `\u4f4e\u5e93\u5b58 ${formatCount(overview.value.lowStockRiskCount)}\uff0c\u9000\u6b3e\u7533\u8bf7 ${formatCount(overview.value.refundAlertCount)}\uff0c\u4f4e\u5206\u8bc4\u4ef7 ${formatCount(overview.value.negativeReviewRiskCount)}\u3002`,
        color: '#f1707d',
    },
])

const todoItems = computed(() => [
    {
        title: '\u5f85\u53d1\u8d27\u8ba2\u5355',
        owner: `${formatCount(overview.value.pendingShipmentOrderCount)} \u5355\u5f85\u8ddf\u8fdb`,
        tagText: '\u4f18\u5148',
        tagColor: 'gold',
    },
    {
        title: '\u5730\u5740\u5f02\u5e38\u8ba2\u5355',
        owner: `${formatCount(overview.value.abnormalAddressOrderCount)} \u5355\u9700\u6821\u9a8c`,
        tagText: '\u6838\u5bf9',
        tagColor: 'volcano',
    },
])

const trendItems = computed(() => [
    {
        label: '\u8fd1 7 \u65e5 GMV',
        value: formatCurrency(overview.value.recentSevenDaysGmv),
        trend: `${formatCount(overview.value.recentSevenDaysOrderCount)} \u7b14\u8ba2\u5355`,
        tagColor: 'green',
    },
    {
        label: '\u8fd1 7 \u65e5\u8ba2\u5355\u6570',
        value: formatCount(overview.value.recentSevenDaysOrderCount),
        trend: '\u6309\u8ba2\u5355\u521b\u5efa\u65f6\u95f4\u7edf\u8ba1',
        tagColor: 'blue',
    },
    {
        label: '\u8fd1 7 \u65e5\u65b0\u589e\u7528\u6237',
        value: formatCount(overview.value.recentSevenDaysNewUserCount),
        trend: '\u4e0d\u542b\u7ba1\u7406\u5458\u8d26\u53f7',
        tagColor: 'purple',
    },
])

async function loadOverview(showSuccess = false) {
    loading.value = true
    errorMessage.value = ''
    try {
        overview.value = await getAdminDashboardOverview()
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
            </div>
            <a-button class="refresh-btn" type="primary" :loading="loading" @click="handleRefresh">{{ refreshLabel }}</a-button>
        </section>

        <a-alert
            v-if="errorMessage"
            class="page-alert"
            type="error"
            show-icon
            :message="errorMessage"
            :description="errorDescription"
        />

        <section class="stats-grid">
            <a-card v-for="item in metrics" :key="item.label" class="panel stat-card" :bordered="false" :loading="loading">
                <span class="stat-label">{{ item.label }}</span>
                <strong :style="{ color: item.color }">{{ item.value }}</strong>
                <p>{{ item.note }}</p>
                <small>{{ item.detail }}</small>
            </a-card>
        </section>

        <section class="content-grid">
            <a-card class="panel" :bordered="false" :title="todoTitle" :loading="loading">
                <div class="todo-list">
                    <div v-for="item in todoItems" :key="item.title" class="todo-item">
                        <div>
                            <strong>{{ item.title }}</strong>
                            <p>{{ item.owner }}</p>
                        </div>
                        <a-tag :color="item.tagColor">{{ item.tagText }}</a-tag>
                    </div>
                </div>
            </a-card>

            <a-card class="panel" :bordered="false" :title="trendTitle" :loading="loading">
                <div class="trend-list">
                    <div v-for="item in trendItems" :key="item.label" class="trend-item">
                        <span>{{ item.label }}</span>
                        <strong>{{ item.value }}</strong>
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

.refresh-btn {
    min-width: 120px;
}

.page-alert {
    border-radius: 20px;
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

.stat-label {
    color: var(--text-muted);
    font-size: 0.92rem;
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

.todo-item,
.trend-item {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
    padding: 16px 18px;
    border-radius: 20px;
    background: var(--bg-accent);
}

.todo-item strong,
.trend-item strong {
    color: var(--text-strong);
}

.trend-item {
    display: grid;
    grid-template-columns: minmax(0, 1fr) auto auto;
}

.trend-item span {
    color: var(--text-muted);
}

@media (max-width: 1100px) {
    .stats-grid,
    .content-grid {
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
        grid-template-columns: 1fr;
        justify-items: start;
    }
}
</style>
