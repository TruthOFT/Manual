<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { getArtisanDetail } from '@/api/artisan'
import LandingNav from '@/components/layout/LandingNav.vue'
import type { ArtisanDetail } from '@/types/artisan'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const artisanDetail = ref<ArtisanDetail | null>(null)

const products = computed(() => artisanDetail.value?.products ?? [])

async function loadArtisanDetail(artisanId: string) {
    loading.value = true
    errorMessage.value = ''
    artisanDetail.value = null
    try {
        artisanDetail.value = await getArtisanDetail(artisanId)
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载匠人详情失败，请稍后重试。'
    } finally {
        loading.value = false
    }
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

watch(
    () => route.params.id,
    (id) => {
        if (typeof id !== 'string' || !id.trim()) {
            errorMessage.value = '匠人参数无效。'
            artisanDetail.value = null
            return
        }
        void loadArtisanDetail(id)
    },
    { immediate: true },
)
</script>

<template>
    <main class="page">
        <section class="shell top-section">
            <LandingNav />
        </section>

        <section class="shell hero-section">
            <a-button class="manual-ant-btn manual-ant-btn-ghost back-btn" @click="$router.back()">返回上一页</a-button>

            <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage">
                <template #action>
                    <a-button
                        v-if="typeof route.params.id === 'string' && route.params.id"
                        type="link"
                        @click="loadArtisanDetail(route.params.id)"
                    >
                        重新加载
                    </a-button>
                </template>
            </a-alert>

            <a-skeleton v-if="loading" active :paragraph="{ rows: 10 }" />

            <div v-else-if="artisanDetail" class="hero-grid">
                <a-card class="soft-card cover-card" :bordered="false">
                    <template #cover>
                        <a-image :preview="false" :src="artisanDetail.coverUrl" :alt="artisanDetail.artisanName" />
                    </template>
                </a-card>

                <div class="hero-copy">
                    <div class="artisan-head">
                        <a-avatar :size="72" :src="artisanDetail.artisanAvatar" />
                        <div>
                            <p class="eyebrow">匠人详情</p>
                            <h1>{{ artisanDetail.artisanName }}</h1>
                            <p class="subhead">{{ artisanDetail.shopName }}</p>
                        </div>
                    </div>

                    <a-space wrap>
                        <a-tag color="geekblue">{{ artisanDetail.craftCategory }}</a-tag>
                        <a-tag>{{ artisanDetail.originPlace }}</a-tag>
                        <a-tag>{{ artisanDetail.experienceYears }} 年经验</a-tag>
                        <a-tag>{{ artisanDetail.productCount }} 件在售作品</a-tag>
                        <a-tag :color="artisanDetail.supportCustom ? 'green' : 'default'">
                            {{ artisanDetail.supportCustom ? '支持定制' : '现货零售' }}
                        </a-tag>
                    </a-space>

                    <p class="story-copy">
                        {{ artisanDetail.artisanStory || '当前匠人暂未填写更多故事内容。' }}
                    </p>
                </div>
            </div>
        </section>

        <section v-if="artisanDetail" class="shell section">
            <div class="section-head">
                <div>
                    <p class="eyebrow">匠人作品</p>
                    <h2>继续看看这位匠人当前在售的公开作品。</h2>
                </div>
            </div>

            <a-row v-if="products.length" :gutter="[22, 22]">
                <a-col v-for="product in products" :key="product.id" :xs="24" :lg="8">
                    <a-card class="soft-card product-card" hoverable @click="goToProductDetail(product.id)">
                        <template #cover>
                            <a-image :preview="false" :src="product.productCover" :alt="product.productName" />
                        </template>

                        <div class="product-top">
                            <a-tag color="orange">{{ product.categoryName }}</a-tag>
                            <a-tag :color="product.supportCustom ? 'green' : 'default'">
                                {{ product.supportCustom ? '支持定制' : '现货零售' }}
                            </a-tag>
                        </div>

                        <h3>{{ product.productName }}</h3>
                        <p>{{ product.productSubtitle }}</p>

                        <a-space wrap>
                            <a-tag>{{ product.craftType }}</a-tag>
                            <a-tag>{{ product.originPlace }}</a-tag>
                        </a-space>

                        <div class="product-foot">
                            <span>{{ product.shopName }}</span>
                            <strong>{{ getPriceRange(product.minPrice, product.maxPrice) }}</strong>
                        </div>
                    </a-card>
                </a-col>
            </a-row>
            <a-empty v-else description="该匠人当前暂无公开在售作品" />
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
    display: grid;
    gap: 20px;
    padding: 36px 0;
}

.section {
    padding-bottom: 48px;
}

.back-btn {
    width: fit-content;
}

.hero-grid {
    display: grid;
    gap: 24px;
    grid-template-columns: minmax(0, 0.92fr) minmax(0, 1.08fr);
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.cover-card :deep(.ant-card-cover img) {
    height: 360px;
    object-fit: cover;
}

.hero-copy {
    display: grid;
    gap: 18px;
    align-content: start;
}

.artisan-head {
    display: flex;
    gap: 16px;
    align-items: center;
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
    font-size: clamp(2.6rem, 5vw, 4.4rem);
    line-height: 0.98;
}

h2 {
    font-size: clamp(2rem, 4vw, 3.2rem);
    line-height: 0.98;
    margin-top: 12px;
}

.subhead,
.story-copy,
.product-card p {
    color: var(--text);
}

.section-head {
    margin-bottom: 24px;
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

@media (max-width: 900px) {
    .hero-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 760px) {
    .page {
        padding: 16px 16px 56px;
    }

    .artisan-head,
    .product-top,
    .product-foot {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
