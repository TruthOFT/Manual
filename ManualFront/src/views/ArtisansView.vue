<script setup lang="ts">
import { computed } from 'vue'

import LandingNav from '@/components/layout/LandingNav.vue'
import { useHomePage } from '@/composables/useHomePage'

const { errorMessage, homeData, loadHomePage, loading } = useHomePage()

const artisans = computed(() => homeData.value?.artisans ?? [])
</script>

<template>
    <main class="page">
        <section class="shell top-section">
            <LandingNav />
        </section>

        <section class="shell hero-section">
            <div class="hero-copy">
                <p class="eyebrow">匠人故事</p>
                <h1>让每位手作人的工坊、作品和经验，成为门店品牌的一部分。</h1>
                <p class="lead">
                    匠人页现在直接读取真实匠人档案和在售作品数量，方便用户从商品之外继续理解品牌背后的创作来源。
                </p>

                <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage">
                    <template #action>
                        <a-button type="link" @click="loadHomePage(true)">重新加载</a-button>
                    </template>
                </a-alert>
            </div>
        </section>

        <section class="shell section">
            <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />

            <a-row v-else-if="artisans.length" :gutter="[22, 22]">
                <a-col v-for="artisan in artisans" :key="artisan.id" :xs="24" :lg="8">
                    <a-card class="soft-card artisan-card" hoverable :bordered="false">
                        <template #cover>
                            <a-image :preview="false" :src="artisan.coverUrl" :alt="artisan.artisanName" />
                        </template>

                        <div class="artisan-head">
                            <a-avatar :size="58" :src="artisan.artisanAvatar" />
                            <div>
                                <h3>{{ artisan.artisanName }}</h3>
                                <p>{{ artisan.shopName }}</p>
                            </div>
                        </div>

                        <a-space wrap>
                            <a-tag color="geekblue">{{ artisan.craftCategory }}</a-tag>
                            <a-tag>{{ artisan.originPlace }}</a-tag>
                            <a-tag>{{ artisan.experienceYears }} 年经验</a-tag>
                            <a-tag>{{ artisan.productCount }} 件作品</a-tag>
                        </a-space>

                        <p class="card-copy">
                            {{
                                artisan.supportCustom
                                    ? '支持礼盒、企业赠礼和个性化文字定制，适合做品牌礼赠和节庆专题。'
                                    : '以现货零售和日常陈列作品为主，强调稳定出品和长期店铺风格。'
                            }}
                        </p>
                    </a-card>
                </a-col>
            </a-row>
            <a-empty v-else description="暂无匠人数据" />
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
    font-size: 1.45rem;
}

.lead,
.card-copy,
.artisan-head p {
    color: var(--text);
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.artisan-card :deep(.ant-card-cover img) {
    height: 260px;
    object-fit: cover;
}

.artisan-card :deep(.ant-card-body) {
    display: grid;
    gap: 16px;
}

.artisan-head {
    display: flex;
    align-items: center;
    gap: 14px;
}

@media (max-width: 760px) {
    .page {
        padding: 16px 16px 56px;
    }
}
</style>
