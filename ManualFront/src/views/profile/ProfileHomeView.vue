<script setup lang="ts">
import { favoriteItems, profileOrders, profileStats } from '@/mocks/profile'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<template>
    <div class="profile-view">
        <a-card class="profile-hero-card" :bordered="false">
            <p class="eyebrow">个人中心</p>
            <h1>你好，{{ userStore.currentUser?.username || userStore.currentUser?.userAccount }}。</h1>
            <p class="lead">这里集中查看你的最近订单、收藏作品与账号概览，后续直接接用户侧真实数据即可。</p>

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
                <div class="favorite-grid">
                    <div v-for="item in favoriteItems.slice(0, 2)" :key="item.id" class="favorite-item">
                        <a-avatar shape="square" :size="56" :src="item.productCover" />
                        <div>
                            <strong>{{ item.productName }}</strong>
                            <p>{{ item.shopName }} / {{ item.priceText }}</p>
                        </div>
                    </div>
                </div>
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
