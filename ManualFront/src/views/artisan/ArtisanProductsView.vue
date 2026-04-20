<script setup lang="ts">
import { message } from 'ant-design-vue'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import {
    getArtisanProducts,
    offShelfArtisanProduct,
    onShelfArtisanProduct,
    submitArtisanProductAudit,
} from '@/api/artisan-center'
import type { ArtisanProductPageData } from '@/types/artisan-center'

const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const keyword = ref('')
const auditStatus = ref<number | undefined>()
const status = ref<number | undefined>()
const pageData = ref<ArtisanProductPageData | null>(null)

const products = computed(() => pageData.value?.products ?? [])
const categories = computed(() => pageData.value?.categories ?? [])

function getAuditText(value: number) {
    if (value === -1) return '草稿'
    if (value === 0) return '待审核'
    if (value === 1) return '已通过'
    if (value === 2) return '已驳回'
    return '未知'
}

function getStatusText(value: number) {
    if (value === 0) return '草稿'
    if (value === 1) return '上架中'
    if (value === 2) return '已下架'
    return '未知'
}

async function loadProducts() {
    loading.value = true
    errorMessage.value = ''
    try {
        pageData.value = await getArtisanProducts({
            auditStatus: auditStatus.value,
            status: status.value,
            keyword: keyword.value || undefined,
        })
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载商品列表失败'
    } finally {
        loading.value = false
    }
}

async function runAction(action: () => Promise<boolean>, successText: string) {
    try {
        await action()
        message.success(successText)
        await loadProducts()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '操作失败')
    }
}

onMounted(() => {
    void loadProducts()
})
</script>

<template>
    <div class="artisan-view">
        <a-card class="artisan-hero-card" :bordered="false">
            <div class="hero-head">
                <div>
                    <p class="eyebrow">商品管理</p>
                    <h1>统一查看草稿、待审核、上架与下架商品。</h1>
                </div>
                <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" @click="router.push('/artisan/products/new')">
                    新建商品
                </a-button>
            </div>
            <p class="lead">商品列表同时返回分类选项，便于首期编辑页直接复用，不再单独拉一份分类接口。</p>
        </a-card>

        <a-card class="artisan-panel" :bordered="false" title="筛选条件">
            <div class="filter-grid">
                <a-input v-model:value="keyword" size="large" placeholder="搜索商品名称或副标题" @press-enter="loadProducts" />
                <a-select v-model:value="auditStatus" allow-clear size="large" placeholder="审核状态">
                    <a-select-option :value="-1">草稿</a-select-option>
                    <a-select-option :value="0">待审核</a-select-option>
                    <a-select-option :value="1">已通过</a-select-option>
                    <a-select-option :value="2">已驳回</a-select-option>
                </a-select>
                <a-select v-model:value="status" allow-clear size="large" placeholder="上架状态">
                    <a-select-option :value="0">草稿</a-select-option>
                    <a-select-option :value="1">上架中</a-select-option>
                    <a-select-option :value="2">已下架</a-select-option>
                </a-select>
                <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" @click="loadProducts">查询</a-button>
            </div>
            <p class="category-note">当前可选分类：{{ categories.map((item) => item.categoryName).join(' / ') || '暂无' }}</p>
        </a-card>

        <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage" />
        <a-skeleton v-if="loading" active :paragraph="{ rows: 10 }" />

        <div v-else class="card-grid">
            <a-empty v-if="!products.length" description="当前没有符合条件的商品" />
            <a-card v-for="item in products" :key="item.id" class="artisan-panel product-card" :bordered="false">
                <div class="product-card-head">
                    <a-avatar shape="square" :size="68" :src="item.productCover" />
                    <div class="product-copy">
                        <strong>{{ item.productName }}</strong>
                        <p>{{ item.productSubtitle || '暂无副标题' }}</p>
                        <a-space wrap>
                            <a-tag>{{ item.categoryName || '未分类' }}</a-tag>
                            <a-tag :color="item.auditStatus === 1 ? 'green' : item.auditStatus === 2 ? 'red' : 'gold'">
                                {{ getAuditText(item.auditStatus) }}
                            </a-tag>
                            <a-tag :color="item.status === 1 ? 'blue' : 'default'">{{ getStatusText(item.status) }}</a-tag>
                        </a-space>
                    </div>
                </div>

                <div class="meta-grid">
                    <span>库存：{{ item.inventory }}</span>
                    <span>价格：¥ {{ item.minPrice }} - ¥ {{ item.maxPrice }}</span>
                    <span>评价：{{ item.reviewCount }}</span>
                    <span>更新：{{ item.updateTime || '-' }}</span>
                </div>

                <div class="action-row">
                    <a-button class="manual-ant-btn manual-ant-btn-ghost" @click="router.push(`/artisan/products/${item.id}`)">
                        查看详情
                    </a-button>
                    <a-button class="manual-ant-btn manual-ant-btn-soft" @click="router.push(`/artisan/products/${item.id}/edit`)">
                        编辑
                    </a-button>
                    <a-button
                        v-if="item.auditStatus !== 0"
                        class="manual-ant-btn manual-ant-btn-cream"
                        @click="runAction(() => submitArtisanProductAudit(item.id), '已提交审核')"
                    >
                        提交审核
                    </a-button>
                    <a-button
                        v-if="item.auditStatus === 1 && item.status !== 1"
                        class="manual-ant-btn manual-ant-btn-primary"
                        @click="runAction(() => onShelfArtisanProduct(item.id), '商品已上架')"
                    >
                        上架
                    </a-button>
                    <a-button
                        v-if="item.status === 1"
                        class="manual-ant-btn manual-ant-btn-soft"
                        @click="runAction(() => offShelfArtisanProduct(item.id), '商品已下架')"
                    >
                        下架
                    </a-button>
                </div>
            </a-card>
        </div>
    </div>
</template>

<style scoped>
.artisan-view {
    display: grid;
    gap: 24px;
}

.hero-head {
    display: flex;
    justify-content: space-between;
    gap: 18px;
    align-items: flex-start;
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
    font-size: clamp(2.1rem, 4vw, 3.4rem);
}

.lead,
.category-note,
.product-copy p,
.meta-grid span {
    color: var(--text);
}

.filter-grid,
.card-grid,
.meta-grid,
.action-row {
    display: grid;
    gap: 14px;
}

.filter-grid {
    grid-template-columns: minmax(0, 2fr) repeat(2, minmax(0, 1fr)) auto;
}

.category-note {
    margin-top: 14px;
}

.card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.product-card {
    display: grid;
    gap: 18px;
}

.product-card-head {
    display: grid;
    grid-template-columns: auto 1fr;
    gap: 14px;
}

.product-copy strong {
    display: block;
    color: var(--text-strong);
    font-size: 1.1rem;
}

.meta-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.action-row {
    grid-template-columns: repeat(3, minmax(0, max-content));
    align-items: center;
}

@media (max-width: 1120px) {
    .filter-grid,
    .card-grid,
    .meta-grid,
    .action-row {
        grid-template-columns: 1fr;
    }

    .hero-head {
        flex-direction: column;
    }
}
</style>
