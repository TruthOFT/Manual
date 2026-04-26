<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

import { getProductFavorites, unfavoriteProduct } from '@/api/product'
import { resolveUploadUrl } from '@/api/upload'
import type { ProductFavorite } from '@/types/product'

const router = useRouter()
const loading = ref(false)
const favorites = ref<ProductFavorite[]>([])
const deletingId = ref<string>()

async function loadFavorites() {
    loading.value = true
    try {
        favorites.value = await getProductFavorites()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载收藏失败')
    } finally {
        loading.value = false
    }
}

async function removeFavorite(productId: string) {
    deletingId.value = productId
    try {
        await unfavoriteProduct(productId)
        favorites.value = favorites.value.filter((item) => item.productId !== productId)
    } catch (error) {
        message.error(error instanceof Error ? error.message : '取消收藏失败')
    } finally {
        deletingId.value = undefined
    }
}

function getPriceRange(minPrice: number | string, maxPrice: number | string) {
    const min = Number(minPrice || 0)
    const max = Number(maxPrice || 0)
    return min === max ? `CNY ${min.toFixed(2)}` : `CNY ${min.toFixed(2)} - ${max.toFixed(2)}`
}

function goToProduct(productId: string) {
    void router.push({
        name: 'product-detail',
        params: {
            id: productId,
        },
    })
}

onMounted(() => {
    void loadFavorites()
})
</script>

<template>
    <a-card class="profile-panel" :bordered="false" title="我的收藏">
        <a-skeleton v-if="loading" active :paragraph="{ rows: 6 }" />

        <a-row v-else-if="favorites.length" :gutter="[18, 18]">
            <a-col v-for="item in favorites" :key="item.id" :xs="24" :md="12" :xl="8">
                <a-card class="favorite-card" :bordered="false" hoverable @click="goToProduct(item.productId)">
                    <template #cover>
                        <a-image :preview="false" :src="resolveUploadUrl(item.productCover)" :alt="item.productName" />
                    </template>
                    <a-tag color="orange">{{ item.categoryName }}</a-tag>
                    <h3>{{ item.productName }}</h3>
                    <p>{{ item.productSubtitle || item.craftType }}</p>
                    <div class="favorite-foot">
                        <strong>{{ getPriceRange(item.minPrice, item.maxPrice) }}</strong>
                        <a-button
                            danger
                            type="link"
                            :loading="deletingId === item.productId"
                            @click.stop="removeFavorite(item.productId)"
                        >
                            取消收藏
                        </a-button>
                    </div>
                </a-card>
            </a-col>
        </a-row>

        <a-empty v-else description="暂无收藏作品">
            <RouterLink to="/products">
                <a-button class="manual-ant-btn manual-ant-btn-primary">去逛作品</a-button>
            </RouterLink>
        </a-empty>
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

.favorite-card :deep(.ant-card-body) {
    display: grid;
    gap: 10px;
}

h3 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
}

p {
    margin: 0;
    color: var(--text);
}

strong {
    color: var(--coral-deep);
}

.favorite-foot {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
}
</style>
