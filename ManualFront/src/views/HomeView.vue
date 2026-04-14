<script setup lang="ts">
import {
    ArrowRightOutlined,
    FireOutlined,
    GiftOutlined,
    ShoppingOutlined,
    ShopOutlined,
    TeamOutlined,
} from '@ant-design/icons-vue'

import LandingNav from '@/components/layout/LandingNav.vue'
import { homePageMock } from '@/mocks/home'

const homeData = homePageMock

const dashboardStats = [
    {
        label: 'Today revenue',
        value: 1224,
        prefix: 'CNY',
        icon: FireOutlined,
        note: 'Gift sets and runners continue to convert.',
    },
    {
        label: 'Live products',
        value: 62,
        icon: ShoppingOutlined,
        note: 'Small-batch drops keep the page feeling fresh.',
    },
    {
        label: 'Maker partners',
        value: 18,
        icon: TeamOutlined,
        note: 'Ceramic, wood, textile, and scent studios included.',
    },
]

const operatingHighlights = [
    'Custom gift boxes and brand bundles are already part of the story.',
    'The layout is prepared for later backend data replacement.',
    'Public storefront storytelling and operating signals share one visual system.',
]

function getPriceRange(minPrice: number, maxPrice: number) {
    return minPrice === maxPrice ? `CNY ${minPrice}` : `CNY ${minPrice} - ${maxPrice}`
}

function getSupportLabel(supportCustom: number) {
    return supportCustom ? 'Custom ready' : 'Retail stock'
}
</script>

<template>
    <main class="page">
        <section class="hero shell" id="top">
            <LandingNav />

            <div class="hero-grid">
                <div class="hero-copy">
                    <p class="eyebrow">creative retail, artisan stories, warm operations</p>
                    <h1>Shape the handmade creative shop into a storefront that feels premium and operational at first glance.</h1>
                    <p class="lead">
                        This first home page uses mock data to carry the brand story, featured work, artisan partners, and order movement. Later backend work can swap the data source without changing the structure.
                    </p>

                    <a-space size="middle" wrap>
                        <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" href="#products">View featured work</a-button>
                        <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" href="#cta">Apply to join</a-button>
                    </a-space>

                    <div class="stats-grid">
                        <a-card v-for="item in dashboardStats" :key="item.label" class="soft-card stat-card" :bordered="false">
                            <div class="stat-head">
                                <component :is="item.icon" />
                                <span>{{ item.label }}</span>
                            </div>
                            <a-statistic :value="item.value" :prefix="item.prefix" />
                            <p>{{ item.note }}</p>
                        </a-card>
                    </div>
                </div>

                <a-card class="hero-panel" :bordered="false">
                    <template #cover>
                        <a-image
                            :preview="false"
                            src="https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80"
                            alt="handmade studio"
                        />
                    </template>

                    <template #title>
                        <span class="card-title">today's store pulse</span>
                    </template>

                    <p class="hero-panel-copy">One page now carries both brand atmosphere and store signals.</p>

                    <div class="signal-list">
                        <div v-for="product in homeData.featuredProducts.slice(0, 2)" :key="product.id" class="signal-item">
                            <a-avatar shape="square" :size="56" :src="product.productCover" />
                            <div class="signal-body">
                                <strong>{{ product.productName }}</strong>
                                <small>{{ product.shopName }} / {{ product.craftType }}</small>
                            </div>
                            <span>{{ getPriceRange(product.minPrice, product.maxPrice) }}</span>
                        </div>
                    </div>

                    <a-divider />

                    <a-space wrap>
                        <a-tag v-for="item in operatingHighlights" :key="item" class="info-tag">{{ item }}</a-tag>
                    </a-space>
                </a-card>
            </div>
        </section>

        <section class="shell section" id="categories">
            <p class="eyebrow">craft categories</p>
            <h2>Start with clear categories so the store mood, product direction, and browsing flow feel intentional.</h2>
            <p class="intro">Each category card maps to the existing data shape, while the visual system stays aligned with the premium warm look you already prefer.</p>

            <a-row :gutter="[20, 20]">
                <a-col v-for="category in homeData.categories" :key="category.id" :xs="24" :sm="12" :xl="6">
                    <a-card class="soft-card image-card" hoverable :bordered="false">
                        <template #cover>
                            <a-image :preview="false" :src="category.categoryIcon" :alt="category.categoryName" />
                        </template>
                        <a-tag color="gold">level {{ category.categoryLevel }}</a-tag>
                        <h3>{{ category.categoryName }}</h3>
                        <p>{{ category.categoryDesc }}</p>
                    </a-card>
                </a-col>
            </a-row>
        </section>

        <section class="shell section" id="products">
            <p class="eyebrow">featured handmade products</p>
            <h2>Let the featured cards explain the store taste now, then receive real inventory and pricing later.</h2>
            <p class="intro">The product cards already consume the current `featuredProducts` structure, so the later backend hookup can stay focused on data replacement only.</p>

            <a-row :gutter="[22, 22]">
                <a-col v-for="product in homeData.featuredProducts" :key="product.id" :xs="24" :lg="8">
                    <a-card class="soft-card image-card product-card" hoverable :bordered="false">
                        <template #cover>
                            <a-image :preview="false" :src="product.productCover" :alt="product.productName" />
                        </template>

                        <div class="product-top">
                            <a-tag color="orange">{{ product.categoryName }}</a-tag>
                            <a-tag color="green">{{ getSupportLabel(product.supportCustom) }}</a-tag>
                        </div>

                        <h3>{{ product.productName }}</h3>
                        <p>{{ product.productSubtitle }}</p>

                        <a-space wrap>
                            <a-tag>{{ product.craftType }}</a-tag>
                            <a-tag>{{ product.originPlace }}</a-tag>
                            <a-tag>{{ product.handmadeCycleDays }} day cycle</a-tag>
                        </a-space>

                        <div class="product-foot">
                            <span>{{ product.artisanName }} / sold {{ product.soldQuantity }}</span>
                            <strong>{{ getPriceRange(product.minPrice, product.maxPrice) }}</strong>
                        </div>
                    </a-card>
                </a-col>
            </a-row>
        </section>

        <section class="shell section" id="artisans">
            <p class="eyebrow">artisan partners</p>
            <h2>Turn maker stories and studio relationships into part of the storefront value.</h2>
            <p class="intro">This section is mock-backed now, but the visual grouping is already suitable for real artisan data once the backend is ready.</p>

            <a-row :gutter="[22, 22]">
                <a-col v-for="artisan in homeData.artisans" :key="artisan.id" :xs="24" :lg="8">
                    <a-card class="soft-card image-card artisan-card" hoverable :bordered="false">
                        <template #cover>
                            <a-image :preview="false" :src="artisan.coverUrl" :alt="artisan.artisanName" />
                        </template>

                        <div class="artisan-head">
                            <a-avatar :size="58" :src="artisan.artisanAvatar" />
                            <div>
                                <h3>{{ artisan.artisanName }}</h3>
                                <p>{{ artisan.shopName }} / {{ artisan.originPlace }}</p>
                            </div>
                        </div>

                        <a-space wrap>
                            <a-tag color="geekblue">{{ artisan.craftCategory }}</a-tag>
                            <a-tag>{{ artisan.experienceYears }} years</a-tag>
                            <a-tag>{{ artisan.productCount }} products</a-tag>
                            <a-tag :color="artisan.supportCustom ? 'green' : 'default'">{{ getSupportLabel(artisan.supportCustom) }}</a-tag>
                        </a-space>
                    </a-card>
                </a-col>
            </a-row>
        </section>

        <section class="shell section" id="orders">
            <p class="eyebrow">recent orders</p>
            <h2>Order activity should feel like part of the story, not just raw numbers.</h2>
            <p class="intro">This operating block is driven by mock order data for now and is ready to be replaced with real order flow later.</p>

            <div class="two-col">
                <a-card class="soft-card" :bordered="false">
                    <template #title>
                        <span class="card-title">recent order rhythm</span>
                    </template>
                    <template #extra>
                        <a-tag color="orange">mock data</a-tag>
                    </template>

                    <a-list :data-source="homeData.recentOrders" item-layout="horizontal">
                        <template #renderItem="{ item }">
                            <a-list-item>
                                <a-list-item-meta>
                                    <template #avatar>
                                        <a-avatar shape="square" :size="60" :src="item.productCover" />
                                    </template>
                                    <template #title>
                                        <span>{{ item.productName }}</span>
                                    </template>
                                    <template #description>
                                        <span>{{ item.skuName }} / x{{ item.quantity }} / {{ item.orderNo }} / {{ item.finishTime }}</span>
                                    </template>
                                </a-list-item-meta>
                                <strong>CNY {{ item.totalAmount }}</strong>
                            </a-list-item>
                        </template>
                    </a-list>
                </a-card>

                <a-card class="soft-card notes-card" :bordered="false">
                    <template #title>
                        <span class="card-title">operating notes</span>
                    </template>

                    <p class="results-copy">
                        Gift sets, runners, and display blocks are the strongest early performers. The pattern suggests gifting and display-led merchandising should remain a core direction.
                    </p>

                    <div class="note-block">
                        <GiftOutlined />
                        <div>
                            <span>High-potential lane</span>
                            <strong>Ceramic gifts / scent bundles</strong>
                        </div>
                    </div>
                    <div class="note-block">
                        <ShopOutlined />
                        <div>
                            <span>Customer preference</span>
                            <strong>Small batch, customizable, display friendly</strong>
                        </div>
                    </div>
                    <div class="note-block">
                        <ArrowRightOutlined />
                        <div>
                            <span>Next backend step</span>
                            <strong>Swap real orders, products, and artisan data module by module</strong>
                        </div>
                    </div>
                </a-card>
            </div>
        </section>

        <section class="shell section" id="cta">
            <a-card class="cta-card" :bordered="false">
                <div class="cta-grid">
                    <div>
                        <p class="eyebrow">join the creative network</p>
                        <h2>Build the store image and operating frame first, then connect real backend capabilities.</h2>
                        <p class="intro cta-copy">
                            The next round can wire user, home, product, and order endpoints into this same visual shell without a structural redesign.
                        </p>
                    </div>

                    <a-space size="middle" wrap>
                        <RouterLink to="/register">
                            <a-button class="manual-ant-btn manual-ant-btn-cream" size="large">Register store</a-button>
                        </RouterLink>
                        <RouterLink to="/login">
                            <a-button class="manual-ant-btn manual-ant-btn-light" size="large">Login to system</a-button>
                        </RouterLink>
                    </a-space>
                </div>
            </a-card>
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

.hero-grid,
.two-col,
.cta-grid {
    display: grid;
    gap: 24px;
}

.hero-grid {
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
    font-size: clamp(3.3rem, 6vw, 5.8rem);
    line-height: 0.96;
}

h2 {
    font-size: clamp(2.2rem, 4vw, 4rem);
    line-height: 0.98;
    margin: 14px 0 12px;
}

h3 {
    font-size: 1.35rem;
}

.lead,
.intro,
.results-copy {
    margin: 0;
    color: var(--text);
    font-size: 1.05rem;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 16px;
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.stat-card :deep(.ant-card-body) {
    display: grid;
    gap: 12px;
}

.stat-head {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    color: var(--text-muted);
    font-weight: 700;
}

.stat-card p {
    margin: 0;
    color: var(--text);
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

.hero-panel-copy {
    margin: 0 0 18px;
    color: var(--text);
    font-size: 1rem;
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

.signal-body {
    display: grid;
}

.signal-body strong {
    color: var(--text-strong);
}

.signal-body small {
    color: var(--text-muted);
}

.info-tag {
    padding: 8px 12px;
    border-radius: 999px;
}

.image-card :deep(.ant-card-body) {
    display: grid;
    gap: 14px;
}

.product-top,
.artisan-head,
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

.artisan-head {
    align-items: center;
}

.artisan-head p {
    margin: 4px 0 0;
    color: var(--text-muted);
}

.two-col {
    grid-template-columns: minmax(0, 1.08fr) minmax(300px, 0.92fr);
    align-items: start;
}

.notes-card :deep(.ant-card-body) {
    display: grid;
    gap: 16px;
}

.note-block {
    display: grid;
    grid-template-columns: auto 1fr;
    gap: 14px;
    align-items: start;
    padding: 16px 18px;
    border-radius: 20px;
    background: rgba(255, 247, 238, 0.92);
    color: var(--text-strong);
}

.note-block span {
    display: block;
    margin-bottom: 6px;
    color: var(--text-muted);
    font-size: 0.92rem;
}

.cta-card {
    border-radius: 34px;
    background: linear-gradient(135deg, #d16f51, #ed9766);
    box-shadow: 0 30px 70px rgba(194, 104, 72, 0.24);
}

.cta-card :deep(.ant-card-body) {
    padding: 36px;
}

.cta-grid {
    grid-template-columns: minmax(0, 1fr) auto;
    align-items: center;
}

.cta-card .eyebrow,
.cta-card h2,
.cta-copy {
    color: #fffaf6;
}

.cta-card .eyebrow::before {
    background: linear-gradient(90deg, rgba(255, 242, 225, 0.95), rgba(255, 208, 160, 0.88));
}

@media (max-width: 1120px) {
    .hero-grid,
    .two-col,
    .cta-grid,
    .stats-grid {
        grid-template-columns: 1fr;
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
    .artisan-head,
    .product-foot {
        flex-direction: column;
        align-items: flex-start;
    }

    .signal-item {
        grid-template-columns: 1fr;
    }

    .cta-card :deep(.ant-card-body) {
        padding: 28px 22px;
    }

    :deep(.manual-ant-btn.ant-btn) {
        width: 100%;
    }
}
</style>
