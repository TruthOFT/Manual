<script setup lang="ts">
import { AppstoreOutlined, InboxOutlined, RiseOutlined, ShoppingOutlined } from '@ant-design/icons-vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getArtisanDashboard } from '@/api/artisan-center'
import type { ArtisanDashboardData } from '@/types/artisan-center'

const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const dashboard = ref<ArtisanDashboardData | null>(null)

async function loadDashboard() {
    loading.value = true
    errorMessage.value = ''
    try {
        dashboard.value = await getArtisanDashboard()
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载工作台数据失败'
    } finally {
        loading.value = false
    }
}

const quickActions = [
    {
        label: '新建商品',
        description: '创建草稿并完善图片、材质和 SKU。',
        action: () => router.push('/artisan/products/new'),
    },
    {
        label: '处理订单',
        description: '优先查看待发货订单并录入物流单号。',
        action: () => router.push('/artisan/orders?orderStatus=1'),
    },
    {
        label: '查看定制',
        description: '跟进待确认和处理中需求，避免积压。',
        action: () => router.push('/artisan/custom-requirements'),
    },
]

onMounted(() => {
    void loadDashboard()
})
</script>

<template>
    <div class="artisan-view">
        <a-card class="artisan-hero-card" :bordered="false">
            <p class="eyebrow">匠人工作台</p>
            <h1>把店铺状态、履约节奏和待处理事项放在一个入口里。</h1>
            <p class="lead">首期聚焦商品、订单、定制和店铺资料，保证匠人端最核心的经营动作可以直接闭环。</p>
            <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage" />
        </a-card>

        <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />

        <template v-else>
            <div class="stats-grid">
                <a-card class="artisan-panel stat-card" :bordered="false">
                    <AppstoreOutlined />
                    <span>待审核商品</span>
                    <strong>{{ dashboard?.pendingAuditCount ?? 0 }}</strong>
                    <p>还未通过审核，暂时不能上架。</p>
                </a-card>
                <a-card class="artisan-panel stat-card" :bordered="false">
                    <RiseOutlined />
                    <span>在售商品</span>
                    <strong>{{ dashboard?.onShelfCount ?? 0 }}</strong>
                    <p>已审核通过并且当前处于上架状态。</p>
                </a-card>
                <a-card class="artisan-panel stat-card" :bordered="false">
                    <ShoppingOutlined />
                    <span>待发货订单</span>
                    <strong>{{ dashboard?.pendingShipmentCount ?? 0 }}</strong>
                    <p>优先处理物流录入，避免买家等待过久。</p>
                </a-card>
                <a-card class="artisan-panel stat-card" :bordered="false">
                    <InboxOutlined />
                    <span>待处理定制</span>
                    <strong>{{ dashboard?.pendingCustomCount ?? 0 }}</strong>
                    <p>包含待确认和处理中定制需求。</p>
                </a-card>
            </div>

            <div class="content-grid">
                <a-card class="artisan-panel" :bordered="false" title="近 7 天经营概览">
                    <div class="metric-grid">
                        <div class="metric-item">
                            <span>成交金额</span>
                            <strong>¥ {{ dashboard?.recentSevenDaysAmount ?? 0 }}</strong>
                        </div>
                        <div class="metric-item">
                            <span>售出件数</span>
                            <strong>{{ dashboard?.recentSevenDaysSales ?? 0 }}</strong>
                        </div>
                    </div>
                </a-card>

                <a-card class="artisan-panel" :bordered="false" title="快捷动作">
                    <div class="action-grid">
                        <button
                            v-for="item in quickActions"
                            :key="item.label"
                            class="action-card"
                            type="button"
                            @click="item.action"
                        >
                            <strong>{{ item.label }}</strong>
                            <p>{{ item.description }}</p>
                        </button>
                    </div>
                </a-card>
            </div>
        </template>
    </div>
</template>

<style scoped>
.artisan-view {
    display: grid;
    gap: 24px;
}

.artisan-hero-card :deep(.ant-card-body) {
    display: grid;
    gap: 18px;
}

.eyebrow {
    color: var(--coral-deep);
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

h1 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: clamp(2.3rem, 4vw, 3.8rem);
    line-height: 1;
}

.lead,
.stat-card p,
.metric-item span,
.action-card p {
    color: var(--text);
}

.stats-grid,
.content-grid,
.metric-grid,
.action-grid {
    display: grid;
    gap: 18px;
}

.stats-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
}

.content-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.stat-card {
    border-radius: 22px;
    background: rgba(255, 247, 238, 0.92);
}

.stat-card :deep(.ant-card-body) {
    display: grid;
    gap: 12px;
}

.stat-card strong,
.metric-item strong,
.action-card strong {
    color: var(--text-strong);
    font-family: var(--font-display);
}

.stat-card strong {
    font-size: 2.2rem;
}

.metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.metric-item {
    padding: 16px 18px;
    border-radius: 20px;
    background: rgba(255, 247, 238, 0.92);
}

.metric-item strong {
    display: block;
    margin-top: 8px;
    font-size: 2rem;
}

.action-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
}

.action-card {
    padding: 18px;
    border-radius: 22px;
    border: 1px solid rgba(212, 149, 115, 0.18);
    background: rgba(255, 247, 238, 0.92);
    text-align: left;
    cursor: pointer;
    transition: transform 0.24s ease;
}

.action-card:hover {
    transform: translateY(-2px);
}

@media (max-width: 1100px) {
    .stats-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .content-grid,
    .action-grid {
        grid-template-columns: 1fr;
    }
}
</style>
