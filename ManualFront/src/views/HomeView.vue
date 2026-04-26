<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

import { getMyRecommendations } from '@/api/recommendation'
import { resolveUploadUrl } from '@/api/upload'
import LandingNav from '@/components/layout/LandingNav.vue'
import { useHomePage } from '@/composables/useHomePage'
import { useUserStore } from '@/stores/user'
import type { RecommendationProduct } from '@/types/recommendation'

const router = useRouter()
const userStore = useUserStore()
const { errorMessage, homeData, loadHomePage, loading } = useHomePage()

const recommendationLoading = ref(false)
const recommendedProducts = ref<RecommendationProduct[]>([])

const categories = computed(() => homeData.value?.categories ?? [])
const featuredProducts = computed(() => homeData.value?.products.slice(0, 3) ?? [])
const signalProducts = computed(() => {
    const products = recommendedProducts.value.length ? recommendedProducts.value : featuredProducts.value
    return products.slice(0, 2)
})

function getPriceRange(minPrice: number, maxPrice: number) {
    return minPrice === maxPrice ? `CNY ${minPrice}` : `CNY ${minPrice} - ${maxPrice}`
}

function getSupportLabel(supportCustom: number) {
    return supportCustom ? '支持定制' : '现货零售'
}

async function loadRecommendations() {
    if (!userStore.isLoggedIn) {
        recommendedProducts.value = []
        return
    }
    recommendationLoading.value = true
    try {
        recommendedProducts.value = await getMyRecommendations(2)
    } catch {
        recommendedProducts.value = []
    } finally {
        recommendationLoading.value = false
    }
}

function goToProductDetail(productId: string) {
    void router.push({
        name: 'product-detail',
        params: {
            id: productId,
        },
    })
}

function goToCategoryProducts(categoryId: string) {
    void router.push({
        name: 'products',
        query: {
            categoryId,
        },
    })
}

onMounted(() => {
    void loadRecommendations()
})

watch(() => userStore.currentUser?.id, () => {
    void loadRecommendations()
})
</script>

<template>
    <main class="page">
        <section class="hero shell">
            <LandingNav />

            <div class="hero-grid">
                <div class="hero-copy">
                    <p class="eyebrow">手作品牌展示、精选作品、门店商品</p>
                    <h1>把手工门店做成既有品牌温度，也方便用户浏览作品的前台站点。</h1>
                    <p class="lead">
                        首页保留公开展示所需的导购信息，作品和分类都来自真实数据，用户登录后会在今日门店精选里看到为自己计算的推荐作品。
                    </p>

                    <a-space size="middle" wrap>
                        <RouterLink to="/products">
                            <a-button class="manual-ant-btn manual-ant-btn-primary" size="large">查看精选作品</a-button>
                        </RouterLink>
                    </a-space>

                    <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage">
                        <template #action>
                            <a-button type="link" @click="loadHomePage(true)">重新加载</a-button>
                        </template>
                    </a-alert>
                </div>

                <a-card class="hero-panel soft-card" :bordered="false">
                    <template #cover>
                        <a-image
                            :preview="false"
                            src="https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80"
                            alt="handmade studio"
                        />
                    </template>

                    <template #title>
                        <span class="card-title">今日门店精选</span>
                    </template>

                    <a-skeleton v-if="loading || recommendationLoading" active :paragraph="{ rows: 4 }" />

                    <template v-else>
                        <div v-if="signalProducts.length" class="signal-list">
                            <div
                                v-for="product in signalProducts"
                                :key="product.id"
                                class="signal-item signal-item-clickable"
                                @click="goToProductDetail(product.id)"
                            >
                                <a-avatar shape="square" :size="56" :src="resolveUploadUrl(product.productCover)" />
                                <div class="signal-body">
                                    <strong>{{ product.productName }}</strong>
                                    <small>{{ product.categoryName }} / {{ product.craftType }}</small>
                                </div>
                                <span>{{ getPriceRange(product.minPrice, product.maxPrice) }}</span>
                            </div>
                        </div>
                        <a-empty v-else description="暂无精选作品" />
                    </template>
                </a-card>
            </div>
        </section>

        <section class="shell section">
            <p class="eyebrow">精选分类</p>

            <a-skeleton v-if="loading" active :paragraph="{ rows: 6 }" />

            <a-row v-else-if="categories.length" :gutter="[20, 20]">
                <a-col v-for="category in categories" :key="category.id" :xs="24" :sm="12" :xl="6">
                    <a-card
                        class="soft-card image-card category-card"
                        hoverable
                        :bordered="false"
                        role="button"
                        tabindex="0"
                        @click="goToCategoryProducts(category.id)"
                        @keydown.enter="goToCategoryProducts(category.id)"
                        @keydown.space.prevent="goToCategoryProducts(category.id)"
                    >
                        <template #cover>
                            <a-image :preview="false" :src="resolveUploadUrl(category.categoryIcon)" :alt="category.categoryName" />
                        </template>
                        <a-tag color="gold">level {{ category.categoryLevel }}</a-tag>
                        <h3>{{ category.categoryName }}</h3>
                        <p>{{ category.categoryDesc }}</p>
                    </a-card>
                </a-col>
            </a-row>
            <a-empty v-else description="暂无分类数据" />
        </section>

        <section class="shell section">
            <div class="section-head">
                <div>
                    <p class="eyebrow">作品预览</p>
                </div>
                <RouterLink to="/products">
                    <a-button class="manual-ant-btn manual-ant-btn-ghost" size="large">进入作品页</a-button>
                </RouterLink>
            </div>

            <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />

            <a-row v-else-if="featuredProducts.length" :gutter="[22, 22]">
                <a-col v-for="product in featuredProducts" :key="product.id" :xs="24" :lg="8">
                    <a-card class="soft-card image-card product-card" hoverable :bordered="false" @click="goToProductDetail(product.id)">
                        <template #cover>
                            <a-image :preview="false" :src="resolveUploadUrl(product.productCover)" :alt="product.productName" />
                        </template>

                        <div class="product-top">
                            <a-tag color="orange">{{ product.categoryName }}</a-tag>
                            <a-tag :color="product.supportCustom ? 'green' : 'default'">
                                {{ getSupportLabel(product.supportCustom) }}
                            </a-tag>
                        </div>

                        <h3>{{ product.productName }}</h3>
                        <p>{{ product.productSubtitle }}</p>

                        <div class="product-foot">
                            <span class="text-link">{{ product.categoryName }}</span>
                            <strong>{{ getPriceRange(product.minPrice, product.maxPrice) }}</strong>
                        </div>
                    </a-card>
                </a-col>
            </a-row>
            <a-empty v-else description="暂无作品数据" />
        </section>
    </main>
</template>

<style scoped>
.page {
    padding: 24px 24px 72px;
    overflow: hidden;
}

.shell {
    width: min(1180px, 100%);
    margin: 0 auto;
}

.section {
    padding: 36px 0 72px;
}

.hero {
    padding: 24px 0 88px;
}

.hero-grid {
    display: grid;
    gap: 24px;
    grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr);
    align-items: start;
    padding-top: 52px;
}

.hero-copy {
    display: grid;
    gap: 24px;
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
h2,
h3 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
}

h1 {
    max-width: 12ch;
    font-size: clamp(3.2rem, 6vw, 5.4rem);
    line-height: 0.96;
}

h3 {
    font-size: 1.35rem;
}

.lead,
.product-card p {
    color: var(--text);
}

.section-head {
    display: flex;
    align-items: end;
    justify-content: space-between;
    gap: 24px;
    margin-bottom: 24px;
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.hero-panel :deep(.ant-card-cover img),
.image-card :deep(.ant-card-cover img) {
    height: 240px;
    object-fit: cover;
}

.hero-panel :deep(.ant-card-body) {
    padding: 24px;
}

.card-title {
    color: var(--text-muted);
    letter-spacing: 0.08em;
    text-transform: uppercase;
    font-size: 0.78rem;
    font-weight: 800;
}

.signal-list {
    display: grid;
    gap: 12px;
}

.signal-item {
    display: grid;
    grid-template-columns: auto 1fr auto;
    gap: 14px;
    align-items: center;
    padding: 14px 16px;
    border-radius: 18px;
    background: rgba(255, 247, 238, 0.9);
}

.signal-item-clickable,
.category-card,
.product-card {
    cursor: pointer;
}

.category-card {
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.category-card:hover,
.category-card:focus-visible {
    transform: translateY(-4px);
    box-shadow: 0 20px 44px rgba(126, 69, 47, 0.18);
}

.signal-body {
    display: grid;
}

.signal-body strong {
    color: var(--text-strong);
}

.signal-body small {
    color: var(--text-muted);
}

.image-card :deep(.ant-card-body) {
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

@media (max-width: 1120px) {
    .hero-grid {
        grid-template-columns: 1fr;
    }

    .section-head {
        align-items: flex-start;
        flex-direction: column;
    }
}

@media (max-width: 760px) {
    .page {
        padding: 16px 16px 56px;
    }

    .hero {
        padding-top: 8px;
        padding-bottom: 64px;
    }

    .hero-grid {
        gap: 32px;
        padding-top: 34px;
    }

    h1 {
        max-width: none;
    }

    .product-top,
    .product-foot {
        flex-direction: column;
        align-items: flex-start;
    }

    .signal-item {
        grid-template-columns: 1fr;
    }

    :deep(.manual-ant-btn.ant-btn) {
        width: 100%;
    }
}
</style>
