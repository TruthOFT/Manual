<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

import { createOrder, getOrderAddresses } from '@/api/order'
import { getProductDetail } from '@/api/product'
import { resolveUploadUrl } from '@/api/upload'
import LandingNav from '@/components/layout/LandingNav.vue'
import { useUserStore } from '@/stores/user'
import type { UserAddress } from '@/types/order'
import type { ProductDetail, ProductReview, ProductSku } from '@/types/product'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const errorMessage = ref('')
const productDetail = ref<ProductDetail | null>(null)
const selectedSkuId = ref<string>()
const quantity = ref(1)
const checkoutVisible = ref(false)
const addressLoading = ref(false)
const submitting = ref(false)
const addresses = ref<UserAddress[]>([])
const selectedAddressId = ref<string>()
const buyerRemark = ref('')

const detailImages = computed(() => {
    const detail = productDetail.value
    if (!detail) {
        return []
    }
    return detail.images.length ? detail.images : [{ id: detail.id, imageUrl: detail.productCover, imageType: 'cover' }]
})

const availableSkus = computed(() => productDetail.value?.skus.filter((item) => item.status === 1) ?? [])

const selectedSku = computed(() => availableSkus.value.find((item) => item.id === selectedSkuId.value) ?? availableSkus.value[0])

const availableStock = computed(() => {
    const sku = selectedSku.value
    if (!sku) {
        return 0
    }
    return Math.max(0, Number(sku.stock || 0) - Number(sku.lockedStock || 0))
})

const selectedSkuTotalStock = computed(() => Number(selectedSku.value?.stock || 0))

const selectedSkuLockedStock = computed(() => Number(selectedSku.value?.lockedStock || 0))

const totalAmount = computed(() => {
    const sku = selectedSku.value
    if (!sku) {
        return 0
    }
    return Number(sku.price || 0) * quantity.value
})

async function loadProductDetail(productId: string) {
    loading.value = true
    errorMessage.value = ''
    productDetail.value = null
    selectedSkuId.value = undefined
    quantity.value = 1
    try {
        const detail = await getProductDetail(productId)
        productDetail.value = detail
        selectedSkuId.value = detail.skus.find((item) => item.status === 1 && Number(item.stock) > Number(item.lockedStock))?.id
            ?? detail.skus[0]?.id
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载作品详情失败，请稍后重试。'
    } finally {
        loading.value = false
    }
}

function formatMoney(value: number | string | null | undefined) {
    return `CNY ${Number(value || 0).toFixed(2)}`
}

function getPriceRange(minPrice: number, maxPrice: number) {
    return minPrice === maxPrice ? formatMoney(minPrice) : `${formatMoney(minPrice)} - ${formatMoney(maxPrice)}`
}

function getReviewImages(review: ProductReview) {
    return review.reviewImages
        ? review.reviewImages
              .split(',')
              .map((item) => item.trim())
              .filter(Boolean)
        : []
}

function getSkuCover(sku: ProductSku) {
    return resolveUploadUrl(sku.skuCover || productDetail.value?.productCover || '')
}

function goToArtisanDetail(artisanId: string) {
    void router.push({
        name: 'artisan-detail',
        params: {
            id: artisanId,
        },
    })
}

async function openCheckout() {
    if (!userStore.isLoggedIn) {
        const redirect = router.currentRoute.value.fullPath
        await router.push(`/login?redirect=${encodeURIComponent(redirect)}`)
        return
    }
    if (!selectedSku.value) {
        message.warning('请选择商品规格')
        return
    }
    if (availableStock.value <= 0) {
        message.warning('当前规格库存不足')
        return
    }
    addressLoading.value = true
    checkoutVisible.value = true
    try {
        await loadAddresses()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载收货地址失败')
    } finally {
        addressLoading.value = false
    }
}

async function loadAddresses() {
    addresses.value = await getOrderAddresses()
    selectedAddressId.value = addresses.value.find((item) => item.isDefault === 1)?.id ?? addresses.value[0]?.id
    if (!addresses.value.length) {
        message.warning('请先在个人中心维护收货地址')
    }
}

async function refreshAddresses() {
    addressLoading.value = true
    try {
        await loadAddresses()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '刷新收货地址失败')
    } finally {
        addressLoading.value = false
    }
}

async function goManageAddresses() {
    checkoutVisible.value = false
    await router.push('/profile/addresses')
}

async function submitOrder() {
    const sku = selectedSku.value
    if (!sku || !selectedAddressId.value) {
        message.warning('请选择规格和收货地址')
        return
    }
    submitting.value = true
    try {
        await createOrder({
            skuId: sku.id,
            quantity: quantity.value,
            addressId: selectedAddressId.value,
            buyerRemark: buyerRemark.value || undefined,
        })
        checkoutVisible.value = false
        message.success('订单已提交，请尽快完成支付')
        await router.push('/profile/orders')
    } catch (error) {
        message.error(error instanceof Error ? error.message : '提交订单失败')
    } finally {
        submitting.value = false
    }
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
                        <a-image :preview="false" :src="resolveUploadUrl(productDetail.productCover)" :alt="productDetail.productName" />
                    </template>

                    <div v-if="detailImages.length > 1" class="thumb-grid">
                        <a-image
                            v-for="image in detailImages"
                            :key="image.id"
                            :src="resolveUploadUrl(image.imageUrl)"
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

                    <div v-if="availableSkus.length" class="buy-panel">
                        <a-radio-group v-model:value="selectedSkuId" class="sku-list">
                            <a-radio-button v-for="sku in availableSkus" :key="sku.id" :value="sku.id" class="sku-option">
                                <span>{{ sku.skuName }}</span>
                                <small>{{ sku.specText || sku.materialType || '标准规格' }}</small>
                                <strong>{{ formatMoney(sku.price) }}</strong>
                            </a-radio-button>
                        </a-radio-group>

                        <div class="quantity-row">
                            <span>
                                可购买 {{ availableStock }} 件
                                <small v-if="selectedSkuLockedStock > 0">
                                    总库存 {{ selectedSkuTotalStock }} 件，待支付锁定 {{ selectedSkuLockedStock }} 件
                                </small>
                            </span>
                            <a-input-number
                                v-model:value="quantity"
                                :min="1"
                                :max="Math.max(1, availableStock)"
                                :disabled="availableStock <= 0"
                            />
                        </div>

                        <div class="pay-row">
                            <span>合计</span>
                            <strong>{{ formatMoney(totalAmount) }}</strong>
                            <a-button
                                class="manual-ant-btn"
                                :class="availableStock <= 0 ? 'out-of-stock-btn' : 'manual-ant-btn-primary'"
                                :disabled="availableStock <= 0"
                                @click="openCheckout"
                            >
                                {{ availableStock <= 0 ? '缺货' : '立即购买' }}
                            </a-button>
                        </div>
                    </div>
                    <a-alert v-else type="warning" show-icon message="当前作品暂无可购买规格" />

                    <p class="body-copy">{{ productDetail.productDesc }}</p>

                    <a-card class="soft-card artisan-panel" :bordered="false">
                        <div class="artisan-summary">
                            <a-avatar :size="64" :src="resolveUploadUrl(productDetail.artisanAvatar)" />
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
                                                :src="resolveUploadUrl(image)"
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

        <a-modal
            v-model:open="checkoutVisible"
            title="确认订单"
            :confirm-loading="submitting"
            ok-text="提交订单"
            cancel-text="再看看"
            @ok="submitOrder"
        >
            <a-skeleton v-if="addressLoading" active :paragraph="{ rows: 4 }" />
            <div v-else class="checkout-body">
                <div class="checkout-tools">
                    <span>选择收货地址</span>
                    <a-space>
                        <a-button type="link" @click="refreshAddresses">刷新地址</a-button>
                        <a-button type="link" @click="goManageAddresses">管理地址</a-button>
                    </a-space>
                </div>
                <a-empty v-if="!addresses.length" description="暂无收货地址">
                    <a-button class="manual-ant-btn manual-ant-btn-primary" @click="goManageAddresses">
                        去维护地址
                    </a-button>
                </a-empty>
                <template v-else>
                    <a-radio-group v-model:value="selectedAddressId" class="address-list">
                        <a-radio v-for="address in addresses" :key="address.id" :value="address.id" class="address-option">
                            <strong>{{ address.receiverName }} {{ address.receiverPhone }}</strong>
                            <span>{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}</span>
                            <small>{{ address.tagName || '收货地址' }}</small>
                        </a-radio>
                    </a-radio-group>
                    <a-textarea v-model:value="buyerRemark" :rows="3" placeholder="买家备注，可不填" />
                    <div v-if="selectedSku" class="checkout-summary">
                        <img :src="getSkuCover(selectedSku)" :alt="selectedSku.skuName" />
                        <div>
                            <strong>{{ selectedSku.skuName }}</strong>
                            <span>{{ selectedSku.specText || selectedSku.materialType || '标准规格' }} x {{ quantity }}</span>
                            <b>{{ formatMoney(totalAmount) }}</b>
                        </div>
                    </div>
                </template>
            </div>
        </a-modal>
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
    border-radius: 20px;
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
    gap: 18px;
    align-content: start;
}

.eyebrow {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    color: var(--coral-deep);
    font-weight: 800;
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

.price-row,
.pay-row,
.quantity-row {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
}

.price-row strong,
.pay-row strong {
    color: var(--coral-deep);
    font-size: 1.35rem;
}

.price-row span,
.quantity-row span {
    color: var(--text-muted);
}

.quantity-row span {
    display: grid;
    gap: 4px;
}

.quantity-row small {
    color: rgba(101, 84, 69, 0.72);
    font-size: 0.82rem;
}

.out-of-stock-btn,
.out-of-stock-btn:disabled,
.out-of-stock-btn[disabled] {
    border-color: rgba(126, 117, 108, 0.32);
    background: #d8d4cf;
    color: #776f66;
    cursor: not-allowed;
}

.buy-panel {
    display: grid;
    gap: 16px;
    padding: 18px;
    border-radius: 18px;
    background: rgba(255, 247, 238, 0.86);
}

.sku-list {
    display: grid;
    gap: 10px;
}

.sku-option {
    height: auto;
    padding: 12px 14px;
    border-radius: 12px;
    white-space: normal;
}

.sku-option span,
.sku-option small,
.sku-option strong {
    display: block;
}

.sku-option small {
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

.checkout-body,
.address-list {
    display: grid;
    gap: 14px;
}

.checkout-tools {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    align-items: center;
}

.checkout-tools span {
    color: var(--text-strong);
    font-weight: 800;
}

.address-option {
    width: 100%;
    padding: 14px;
    border: 1px solid rgba(171, 118, 69, 0.18);
    border-radius: 12px;
    white-space: normal;
}

.address-option strong,
.address-option span,
.address-option small {
    display: block;
}

.checkout-summary {
    display: flex;
    gap: 14px;
    align-items: center;
    padding: 14px;
    border-radius: 12px;
    background: rgba(255, 247, 238, 0.92);
}

.checkout-summary img {
    width: 72px;
    height: 72px;
    border-radius: 10px;
    object-fit: cover;
}

.checkout-summary div {
    display: grid;
    gap: 4px;
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
    .review-head,
    .pay-row,
    .quantity-row {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
