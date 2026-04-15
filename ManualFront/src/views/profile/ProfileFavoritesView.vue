<script setup lang="ts">
import { favoriteItems } from '@/mocks/profile'
</script>

<template>
    <a-card class="profile-panel" :bordered="false" title="我的收藏">
        <a-row :gutter="[18, 18]">
            <a-col v-for="item in favoriteItems" :key="item.id" :xs="24" :md="12" :xl="8">
                <a-card class="favorite-card" :bordered="false" hoverable>
                    <template #cover>
                        <a-image :preview="false" :src="item.productCover" :alt="item.productName" />
                    </template>
                    <h3>{{ item.productName }}</h3>
                    <p>{{ item.shopName }} / {{ item.craftType }}</p>
                    <strong>{{ item.priceText }}</strong>
                </a-card>
            </a-col>
        </a-row>
    </a-card>
</template>

<style scoped>
.favorite-card {
    border-radius: 22px;
    background: rgba(255, 247, 238, 0.92);
}

.favorite-card :deep(.ant-card-cover img) {
    height: 180px;
    object-fit: cover;
}

h3 {
    margin: 0 0 8px;
    color: var(--text-strong);
    font-family: var(--font-display);
}

p {
    margin: 0 0 10px;
    color: var(--text);
}

strong {
    color: var(--coral-deep);
}
</style>
