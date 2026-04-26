<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getProductFavorites } from '@/api/product'
import { resolveUploadUrl } from '@/api/upload'
import { profileOrders } from '@/mocks/profile'
import { useUserStore } from '@/stores/user'
import type { ProductFavorite } from '@/types/product'

const router = useRouter()
const userStore = useUserStore()
const favoriteLoading = ref(false)
const favorites = ref<ProductFavorite[]>([])

const profileStats = computed(() => [
    {
        label: '待收货订单',
        value: '3',
        note: '近期订单正在配送中，可在订单页查看物流进度。',
    },
    {
        label: '收藏作品',
        value: String(favorites.value.length),
        note: favorites.value.length ? '已收藏的作品会在这里保持同步。' : '遇到喜欢的作品，可以在详情页点收藏。',
    },
    {
        label: '常用地址',
        value: '2',
        note: '可在地址管理中维护收货信息。',
    },
])

function formatMoney(value: number | string | null | undefined) {
    return `CNY ${Number(value || 0).toFixed(2)}`
}

function getPriceRange(item: ProductFavorite) {
    return Number(item.minPrice) === Number(item.maxPrice)
        ? formatMoney(item.minPrice)
        : `${formatMoney(item.minPrice)} - ${formatMoney(item.maxPrice)}`
}

function goToProduct(productId: string) {
    void router.push({
        name: 'product-detail',
        params: {
            id: productId,
        },
    })
}

async function loadFavorites() {
    favoriteLoading.value = true
    try {
        favorites.value = await getProductFavorites()
    } catch {
        favorites.value = []
    } finally {
        favoriteLoading.value = false
    }
}

onMounted(() => {
    void loadFavorites()
})
</script>

<template>
    <div class="profile-view">
        <a-card class="profile-hero-card" :bordered="false">
            <p class="eyebrow">个人中心</p>
            <h1>你好，{{ userStore.currentUser?.username || userStore.currentUser?.userAccount }}</h1>
            <p class="lead">这里集中查看你的最近订单、收藏作品与账号概览。</p>

            <div class="stats-grid">
                <a-card v-for="item in profileStats" :key="item.label" class="stat-card" :bordered="false">
                    <span>{{ item.label }}</span>
                    <strong>{{ item.value }}</strong>
                    <p>{{ item.note }}</p>
                </a-card>
            </div>
        </a-card>

        <div class="content-grid">
            <a-card class="profile-panel" :bordered="false" title="最近订单">
                <a-list :data-source="profileOrders.slice(0, 2)" item-layout="horizontal">
                    <template #renderItem="{ item }">
                        <a-list-item>
                            <a-list-item-meta>
                                <template #avatar>
                                    <a-avatar shape="square" :size="56" :src="item.productCover" />
                                </template>
                                <template #title>{{ item.productName }}</template>
                                <template #description>{{ item.orderNo }} / {{ item.orderTime }}</template>
                            </a-list-item-meta>
                            <a-tag color="orange">{{ item.status }}</a-tag>
                        </a-list-item>
                    </template>
                </a-list>
            </a-card>

            <a-card class="profile-panel" :bordered="false" title="最近收藏">
                <a-skeleton v-if="favoriteLoading" active :paragraph="{ rows: 3 }" />
                <div v-else-if="favorites.length" class="favorite-grid">
                    <div
                        v-for="item in favorites.slice(0, 2)"
                        :key="item.id"
                        class="favorite-item"
                        role="button"
                        tabindex="0"
                        @click="goToProduct(item.productId)"
                        @keyup.enter="goToProduct(item.productId)"
                    >
                        <a-avatar shape="square" :size="56" :src="resolveUploadUrl(item.productCover)" />
                        <div>
                            <strong>{{ item.productName }}</strong>
                            <p>{{ item.craftType || item.categoryName || '手作作品' }} / {{ getPriceRange(item) }}</p>
                        </div>
                    </div>
                </div>
                <a-empty v-else description="暂无收藏作品" />
            </a-card>
        </div>
    </div>
</template>

<style scoped>
.profile-view {
    display: grid;
    gap: 24px;
}

.profile-hero-card :deep(.ant-card-body) {
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
    font-size: clamp(2.2rem, 4vw, 3.6rem);
}

.lead,
.stat-card p,
.favorite-item p {
    color: var(--text);
}

.stats-grid,
.content-grid {
    display: grid;
    gap: 18px;
}

.stats-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
}

.content-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.stat-card {
    border-radius: 22px;
    background: rgba(255, 247, 238, 0.92);
}

.stat-card span {
    color: var(--text-muted);
}

.stat-card strong {
    display: block;
    margin: 10px 0 8px;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: 2rem;
}

.favorite-grid {
    display: grid;
    gap: 14px;
}

.favorite-item {
    display: grid;
    grid-template-columns: auto 1fr;
    gap: 14px;
    align-items: center;
    padding: 12px 14px;
    border-radius: 18px;
    background: rgba(255, 247, 238, 0.92);
    cursor: pointer;
}

.favorite-item strong {
    color: var(--text-strong);
}

@media (max-width: 980px) {
    .stats-grid,
    .content-grid {
        grid-template-columns: 1fr;
    }
}
</style>
