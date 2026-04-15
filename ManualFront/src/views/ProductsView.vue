<script setup lang="ts">
import { computed } from 'vue'

import LandingNav from '@/components/layout/LandingNav.vue'
import { useHomePage } from '@/composables/useHomePage'

const { errorMessage, homeData, loadHomePage, loading } = useHomePage()

const products = computed(() => homeData.value?.products ?? [])
const categories = computed(() => homeData.value?.categories ?? [])

function getPriceRange(minPrice: number, maxPrice: number) {
    return minPrice === maxPrice ? `CNY ${minPrice}` : `CNY ${minPrice} - ${maxPrice}`
}
</script>

<template>
    <main class="page">
        <section class="shell top-section">
            <LandingNav />
        </section>

        <section class="shell hero-section">
            <div class="hero-copy">
                <p class="eyebrow">精选作品</p>
                <h1>把最能代表门店气质的手工作品，以清晰分类和质感卡片完整呈现。</h1>
                <p class="lead">
                    当前作品页已经改成真实接口数据，直接读取数据库里的在售作品、分类和匠人信息，不再依赖前端 mock。
                </p>

                <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage">
                    <template #action>
                        <a-button type="link" @click="loadHomePage(true)">重新加载</a-button>
                    </template>
                </a-alert>
            </div>
        </section>

        <section class="shell section">
            <a-skeleton v-if="loading" active :paragraph="{ rows: 6 }" />

            <template v-else>
                <div v-if="categories.length" class="category-row">
                    <a-tag v-for="category in categories" :key="category.id" class="filter-tag">
                        {{ category.categoryName }}
                    </a-tag>
                </div>

                <a-row v-if="products.length" :gutter="[22, 22]">
                    <a-col v-for="product in products" :key="product.id" :xs="24" :lg="8">
                        <a-card class="soft-card product-card" hoverable>
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
                                <a-tag>{{ product.handmadeCycleDays }} 天手作周期</a-tag>
                            </a-space>

                            <div class="product-foot">
                                <span>{{ product.shopName }} / {{ product.artisanName }}</span>
                                <strong>{{ getPriceRange(product.minPrice, product.maxPrice) }}</strong>
                            </div>
                        </a-card>
                    </a-col>
                </a-row>
                <a-empty v-else description="暂无作品数据" />
            </template>
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

.category-row {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 28px;
}

.filter-tag {
    padding: 10px 14px;
    border-radius: 999px;
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
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
