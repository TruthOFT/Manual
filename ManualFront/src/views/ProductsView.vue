<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { getProductList } from '@/api/product'
import { resolveUploadUrl } from '@/api/upload'
import LandingNav from '@/components/layout/LandingNav.vue'
import type { ProductListPageData } from '@/types/product'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const errorMessage = ref('')
const pageData = ref<ProductListPageData | null>(null)

const filtersVisible = ref(false)
const selectedCategoryId = ref('')
const selectedOriginPlace = ref('')
const selectedMaterialName = ref('')

const products = computed(() => pageData.value?.products ?? [])
const filterOptions = computed(() => pageData.value?.filters ?? { categories: [], originPlaces: [], materials: [] })

async function loadProducts() {
    loading.value = true
    errorMessage.value = ''
    try {
        pageData.value = await getProductList({
            categoryId: selectedCategoryId.value || undefined,
            originPlace: selectedOriginPlace.value || undefined,
            materialName: selectedMaterialName.value || undefined,
        })
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载作品数据失败，请稍后重试。'
    } finally {
        loading.value = false
    }
}

function normalizeCategoryId(categoryId: unknown) {
    if (Array.isArray(categoryId)) {
        return typeof categoryId[0] === 'string' ? categoryId[0] : ''
    }
    return typeof categoryId === 'string' ? categoryId : ''
}

async function syncCategoryQuery(categoryId: string) {
    const nextQuery = { ...route.query }
    if (categoryId) {
        nextQuery.categoryId = categoryId
    } else {
        delete nextQuery.categoryId
    }
    await router.replace({
        name: 'products',
        query: nextQuery,
    })
}

function handleCategoryChange(categoryId: string) {
    selectedCategoryId.value = categoryId
    void syncCategoryQuery(categoryId)
}

function toggleFiltersVisible() {
    filtersVisible.value = !filtersVisible.value
}

function getPriceRange(minPrice: number, maxPrice: number) {
    return minPrice === maxPrice ? `CNY ${minPrice}` : `CNY ${minPrice} - ${maxPrice}`
}

function goToProductDetail(productId: string) {
    void router.push({
        name: 'product-detail',
        params: {
            id: productId,
        },
    })
}

watch(() => route.query.categoryId, (queryCategoryId) => {
    selectedCategoryId.value = normalizeCategoryId(queryCategoryId)
    void loadProducts()
}, {
    immediate: true,
})
</script>

<template>
    <main class="page">
        <section class="shell top-section">
            <LandingNav />
        </section>

        <section class="shell hero-section">
            <div class="hero-copy">
                <p class="eyebrow">精选作品</p>
                <h1>按材料类型、分类和产地筛选作品，再进入详情页查看它的来历与制作信息。</h1>

                <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage">
                    <template #action>
                        <a-button type="link" @click="loadProducts">重新加载</a-button>
                    </template>
                </a-alert>
            </div>
        </section>

        <section class="shell section">
            <div class="filter-toggle-bar">
                <a-button
                    class="manual-ant-btn manual-ant-btn-soft"
                    size="large"
                    :aria-expanded="filtersVisible"
                    @click="toggleFiltersVisible"
                >
                    过滤
                </a-button>
            </div>

            <a-card v-if="filtersVisible" class="soft-card filter-panel" :bordered="false">
                <div class="filter-grid">
                    <label class="filter-field">
                        <span>分类</span>
                        <a-select v-model:value="selectedCategoryId" @change="handleCategoryChange">
                            <a-select-option value="">全部分类</a-select-option>
                            <a-select-option
                                v-for="category in filterOptions.categories"
                                :key="category.id"
                                :value="category.id"
                            >
                                {{ category.categoryName }}
                            </a-select-option>
                        </a-select>
                    </label>

                    <label class="filter-field">
                        <span>产地</span>
                        <a-select v-model:value="selectedOriginPlace" @change="loadProducts">
                            <a-select-option value="">全部产地</a-select-option>
                            <a-select-option v-for="originPlace in filterOptions.originPlaces" :key="originPlace" :value="originPlace">
                                {{ originPlace }}
                            </a-select-option>
                        </a-select>
                    </label>

                    <label class="filter-field">
                        <span>材料类型</span>
                        <a-select v-model:value="selectedMaterialName" @change="loadProducts">
                            <a-select-option value="">全部材料</a-select-option>
                            <a-select-option v-for="material in filterOptions.materials" :key="material" :value="material">
                                {{ material }}
                            </a-select-option>
                        </a-select>
                    </label>
                </div>
            </a-card>
        </section>

        <section class="shell section product-section">
            <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />

            <a-row v-else-if="products.length" :gutter="[22, 22]">
                <a-col v-for="product in products" :key="product.id" :xs="24" :lg="8">
                    <a-card class="soft-card product-card" hoverable @click="goToProductDetail(product.id)">
                        <template #cover>
                            <a-image :preview="false" :src="resolveUploadUrl(product.productCover)" :alt="product.productName" />
                        </template>

                        <div class="product-top">
                            <a-tag color="orange">{{ product.categoryName }}</a-tag>
                            <a-tag>{{ product.craftType }}</a-tag>
                        </div>

                        <h3>{{ product.productName }}</h3>
                        <p>{{ product.productSubtitle }}</p>

                        <a-space wrap>
                            <a-tag>{{ product.craftType }}</a-tag>
                            <a-tag>{{ product.originPlace }}</a-tag>
                            <a-tag>{{ product.handmadeCycleDays }} 天手作周期</a-tag>
                        </a-space>

                        <div class="product-foot">
                            <span class="text-link">{{ product.categoryName }}</span>
                            <strong>{{ getPriceRange(product.minPrice, product.maxPrice) }}</strong>
                        </div>

                        <a-button class="manual-ant-btn manual-ant-btn-ghost" @click.stop="goToProductDetail(product.id)">
                            查看详情
                        </a-button>
                    </a-card>
                </a-col>
            </a-row>
            <a-empty v-else description="当前筛选条件下暂无作品" />
        </section>
    </main>
</template>

<style scoped>
.page {
    padding: 24px 24px 72px;
}

.shell {
    width: min(1180px, 100%);
    margin: 0 auto;
}

.top-section {
    padding-top: 24px;
}

.hero-section {
    padding: 48px 0 28px;
}

.section {
    padding-bottom: 32px;
}

.product-section {
    padding-bottom: 48px;
}

.hero-copy {
    display: grid;
    gap: 18px;
    max-width: 760px;
}

.eyebrow {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    color: var(--coral-deep);
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.eyebrow::before {
    content: '';
    width: 34px;
    height: 10px;
    border-radius: 999px;
    background: linear-gradient(90deg, var(--gold), var(--coral));
}

h1,
h3 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
}

h1 {
    font-size: clamp(2.8rem, 5vw, 4.8rem);
    line-height: 0.98;
}

h3 {
    font-size: 1.4rem;
}

.lead,
.product-card p {
    color: var(--text);
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.filter-panel :deep(.ant-card-body) {
    padding: 24px;
}

.filter-toggle-bar {
    display: flex;
    justify-content: flex-start;
    margin-bottom: 18px;
}

.filter-grid {
    display: grid;
    gap: 18px;
    grid-template-columns: repeat(3, minmax(0, 1fr));
}

.filter-field {
    display: grid;
    gap: 8px;
}

.filter-field span {
    color: var(--text-muted);
    font-size: 0.92rem;
    font-weight: 700;
}

.product-card {
    cursor: pointer;
}

.product-card :deep(.ant-card-cover img) {
    height: 240px;
    object-fit: cover;
}

.product-card :deep(.ant-card-body) {
    display: grid;
    gap: 14px;
}

.product-top,
.product-foot {
    display: flex;
    justify-content: space-between;
    gap: 12px;
}

.product-foot {
    align-items: center;
    color: var(--text-muted);
}

.product-foot strong {
    color: var(--coral-deep);
}

.text-link {
    border: 0;
    padding: 0;
    background: transparent;
    color: var(--text-muted);
    cursor: pointer;
    text-align: left;
}

.text-link:hover {
    color: var(--coral-deep);
}

@media (max-width: 900px) {
    .filter-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 760px) {
    .page {
        padding: 16px 16px 56px;
    }

    .product-top,
    .product-foot {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
