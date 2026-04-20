<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { getProductDetail } from '@/api/product'
import LandingNav from '@/components/layout/LandingNav.vue'
import type { ProductDetail, ProductReview } from '@/types/product'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const errorMessage = ref('')
const productDetail = ref<ProductDetail | null>(null)

const detailImages = computed(() => {
    const detail = productDetail.value
    if (!detail) {
        return []
    }
    return detail.images.length ? detail.images : [{ id: detail.id, imageUrl: detail.productCover, imageType: 'cover' }]
})

async function loadProductDetail(productId: string) {
    loading.value = true
    errorMessage.value = ''
    productDetail.value = null
    try {
        productDetail.value = await getProductDetail(productId)
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载作品详情失败，请稍后重试。'
    } finally {
        loading.value = false
    }
}

function getPriceRange(minPrice: number, maxPrice: number) {
    return minPrice === maxPrice ? `CNY ${minPrice}` : `CNY ${minPrice} - ${maxPrice}`
}

function getReviewImages(review: ProductReview) {
    return review.reviewImages
        ? review.reviewImages
              .split(',')
              .map((item) => item.trim())
              .filter(Boolean)
        : []
}

function goToArtisanDetail(artisanId: string) {
    void router.push({
        name: 'artisan-detail',
        params: {
            id: artisanId,
        },
    })
}

watch(
    () => route.params.id,
    (id) => {
        if (typeof id !== 'string' || !id.trim()) {
            errorMessage.value = '作品参数无效。'
            productDetail.value = null
            return
        }
        void loadProductDetail(id)
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
                        @click="loadProductDetail(route.params.id)"
                    >
                        重新加载
                    </a-button>
                </template>
            </a-alert>

            <a-skeleton v-if="loading" active :paragraph="{ rows: 10 }" />

            <div v-else-if="productDetail" class="detail-grid">
                <a-card class="soft-card gallery-card" :bordered="false">
                    <template #cover>
                        <a-image :preview="false" :src="productDetail.productCover" :alt="productDetail.productName" />
                    </template>

                    <div v-if="detailImages.length > 1" class="thumb-grid">
                        <a-image
                            v-for="image in detailImages"
                            :key="image.id"
                            :src="image.imageUrl"
                            :preview="true"
                            class="thumb-image"
                        />
                    </div>
                </a-card>

                <div class="detail-copy">
                    <p class="eyebrow">作品详情</p>
                    <h1>{{ productDetail.productName }}</h1>
                    <p class="lead">{{ productDetail.productSubtitle }}</p>

                    <a-space wrap>
                        <a-tag color="orange">{{ productDetail.categoryName }}</a-tag>
                        <a-tag color="green" v-if="productDetail.supportCustom">支持定制</a-tag>
                        <a-tag>{{ productDetail.craftType }}</a-tag>
                        <a-tag>{{ productDetail.originPlace }}</a-tag>
                        <a-tag>{{ productDetail.handmadeCycleDays }} 天手作周期</a-tag>
                    </a-space>

                    <div class="price-row">
                        <strong>{{ getPriceRange(productDetail.minPrice, productDetail.maxPrice) }}</strong>
                        <span>已售 {{ productDetail.soldQuantity }} 件</span>
                    </div>

                    <p class="body-copy">{{ productDetail.productDesc }}</p>

                    <a-card class="soft-card artisan-panel" :bordered="false">
                        <div class="artisan-summary">
                            <a-avatar :size="64" :src="productDetail.artisanAvatar" />
                            <div>
                                <span>对应匠人</span>
                                <strong>{{ productDetail.artisanName }}</strong>
                                <small>{{ productDetail.shopName }}</small>
                            </div>
                        </div>
                        <a-button class="manual-ant-btn manual-ant-btn-primary" @click="goToArtisanDetail(productDetail.artisanId)">
                            查看匠人详情
                        </a-button>
                    </a-card>
                </div>
            </div>
        </section>

        <section v-if="productDetail" class="shell section">
            <a-row :gutter="[24, 24]">
                <a-col :xs="24" :lg="12">
                    <a-card class="soft-card" :bordered="false" title="材料信息">
                        <p v-if="productDetail.materialDesc" class="summary-copy">{{ productDetail.materialDesc }}</p>
                        <a-list v-if="productDetail.materials.length" :data-source="productDetail.materials">
                            <template #renderItem="{ item }">
                                <a-list-item>
                                    <a-list-item-meta :title="item.materialName">
                                        <template #description>
                                            <span>{{ item.materialOrigin || '来源未填写' }} / {{ item.materialRatio || '比例未填写' }}</span>
                                        </template>
                                    </a-list-item-meta>
                                </a-list-item>
                            </template>
                        </a-list>
                        <a-empty v-else description="暂无材料明细" />
                    </a-card>
                </a-col>

                <a-col :xs="24" :lg="12">
                    <a-card class="soft-card" :bordered="false" title="评价与反馈">
                        <a-list v-if="productDetail.reviews.length" :data-source="productDetail.reviews">
                            <template #renderItem="{ item }">
                                <a-list-item>
                                    <div class="review-block">
                                        <div class="review-head">
                                            <strong>{{ item.isAnonymous ? '匿名用户' : '买家评价' }}</strong>
                                            <a-rate :value="item.score" disabled allow-half />
                                        </div>
                                        <p>{{ item.reviewContent || '该用户没有填写评价内容。' }}</p>
                                        <div v-if="getReviewImages(item).length" class="review-images">
                                            <a-image
                                                v-for="image in getReviewImages(item)"
                                                :key="image"
                                                :src="image"
                                                :preview="true"
                                                class="review-image"
                                            />
                                        </div>
                                        <small>{{ item.createTime }}</small>
                                        <a-alert
                                            v-if="item.replyContent"
                                            type="info"
                                            show-icon
                                            :message="item.replyContent"
                                            :description="item.replyTime ? `商家回复时间：${item.replyTime}` : undefined"
                                        />
                                    </div>
                                </a-list-item>
                            </template>
                        </a-list>
                        <a-empty v-else description="暂无公开评价" />
                    </a-card>
                </a-col>
            </a-row>
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

.detail-grid {
    display: grid;
    gap: 24px;
    grid-template-columns: minmax(0, 0.95fr) minmax(0, 1.05fr);
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.gallery-card :deep(.ant-card-cover img) {
    height: 360px;
    object-fit: cover;
}

.gallery-card :deep(.ant-card-body) {
    display: grid;
    gap: 16px;
}

.thumb-grid {
    display: grid;
    gap: 12px;
    grid-template-columns: repeat(3, minmax(0, 1fr));
}

.thumb-image :deep(img) {
    height: 110px;
    object-fit: cover;
}

.detail-copy {
    display: grid;
    gap: 20px;
    align-content: start;
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

h1 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: clamp(2.6rem, 5vw, 4.4rem);
    line-height: 0.98;
}

.lead,
.body-copy,
.summary-copy {
    color: var(--text);
}

.price-row {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
}

.price-row strong {
    color: var(--coral-deep);
    font-size: 1.6rem;
}

.price-row span {
    color: var(--text-muted);
}

.artisan-panel :deep(.ant-card-body) {
    display: grid;
    gap: 16px;
}

.artisan-summary {
    display: flex;
    align-items: center;
    gap: 16px;
}

.artisan-summary span,
.artisan-summary small {
    display: block;
    color: var(--text-muted);
}

.artisan-summary strong {
    color: var(--text-strong);
    font-size: 1.15rem;
}

.review-block {
    display: grid;
    gap: 12px;
    width: 100%;
}

.review-head {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
}

.review-images {
    display: grid;
    gap: 10px;
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.review-image :deep(img) {
    height: 120px;
    object-fit: cover;
}

@media (max-width: 900px) {
    .detail-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 760px) {
    .page {
        padding: 16px 16px 56px;
    }

    .thumb-grid,
    .review-images {
        grid-template-columns: 1fr;
    }

    .price-row,
    .review-head {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
