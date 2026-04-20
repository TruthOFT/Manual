<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getArtisanCustomRequirements } from '@/api/artisan-center'
import type { ArtisanCustomRequirementItem } from '@/types/artisan-center'

const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const keyword = ref('')
const confirmStatus = ref<number | undefined>()
const requirements = ref<ArtisanCustomRequirementItem[]>([])

function getStatusText(value: number) {
    if (value === 0) return '待确认'
    if (value === 1) return '已接单'
    if (value === 2) return '已拒绝'
    if (value === 3) return '处理中'
    if (value === 4) return '已完成'
    return '未知'
}

async function loadRequirements() {
    loading.value = true
    errorMessage.value = ''
    try {
        requirements.value = await getArtisanCustomRequirements({
            confirmStatus: confirmStatus.value,
            keyword: keyword.value || undefined,
        })
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载定制需求失败'
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    void loadRequirements()
})
</script>

<template>
    <div class="artisan-view">
        <a-card class="artisan-hero-card" :bordered="false">
            <p class="eyebrow">定制需求</p>
            <h1>跟进待确认、处理中与已完成的定制任务。</h1>
        </a-card>

        <a-card class="artisan-panel" :bordered="false" title="筛选条件">
            <div class="filter-grid">
                <a-input v-model:value="keyword" size="large" placeholder="搜索需求标题、订单号或商品名" @press-enter="loadRequirements" />
                <a-select v-model:value="confirmStatus" allow-clear size="large" placeholder="需求状态">
                    <a-select-option :value="0">待确认</a-select-option>
                    <a-select-option :value="1">已接单</a-select-option>
                    <a-select-option :value="2">已拒绝</a-select-option>
                    <a-select-option :value="3">处理中</a-select-option>
                    <a-select-option :value="4">已完成</a-select-option>
                </a-select>
                <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" @click="loadRequirements">查询</a-button>
            </div>
        </a-card>

        <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage" />
        <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />

        <div v-else class="requirement-grid">
            <a-empty v-if="!requirements.length" description="当前没有符合条件的定制需求" />
            <a-card v-for="item in requirements" :key="item.id" class="artisan-panel requirement-card" :bordered="false">
                <div class="requirement-head">
                    <div>
                        <strong>{{ item.requirementTitle }}</strong>
                        <p>{{ item.productName }} / {{ item.orderNo }}</p>
                    </div>
                    <a-tag :color="item.confirmStatus === 2 ? 'red' : item.confirmStatus === 4 ? 'green' : 'gold'">
                        {{ getStatusText(item.confirmStatus) }}
                    </a-tag>
                </div>
                <p class="requirement-copy">{{ item.requirementContent }}</p>
                <div class="meta-row">
                    <span>买家：{{ item.buyerName }}</span>
                    <span>创建：{{ item.createTime || '-' }}</span>
                </div>
                <a-button class="manual-ant-btn manual-ant-btn-primary" @click="router.push(`/artisan/custom-requirements/${item.id}`)">
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
.requirement-grid,
.meta-row {
    display: grid;
    gap: 14px;
}

.filter-grid {
    grid-template-columns: minmax(0, 2fr) minmax(0, 1fr) auto;
}

.requirement-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.requirement-card {
    display: grid;
    gap: 16px;
}

.requirement-head {
    display: flex;
    justify-content: space-between;
    gap: 12px;
}

.requirement-head strong {
    color: var(--text-strong);
}

.requirement-head p,
.requirement-copy,
.meta-row span {
    color: var(--text);
}

.meta-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

@media (max-width: 980px) {
    .filter-grid,
    .requirement-grid,
    .meta-row {
        grid-template-columns: 1fr;
    }
}
</style>
