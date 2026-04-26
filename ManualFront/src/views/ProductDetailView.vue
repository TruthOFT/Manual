<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

import { getMyCoupons } from '@/api/coupon'
import { createOrder, getOrderAddresses } from '@/api/order'
import { favoriteProduct, getProductDetail, unfavoriteProduct } from '@/api/product'
import { getSimilarProducts, recordProductBehavior } from '@/api/recommendation'
import { resolveUploadUrl } from '@/api/upload'
import LandingNav from '@/components/layout/LandingNav.vue'
import { useUserStore } from '@/stores/user'
import type { UserCoupon } from '@/types/coupon'
import type { UserAddress } from '@/types/order'
import type { ProductDetail, ProductSku } from '@/types/product'
import type { RecommendationProduct } from '@/types/recommendation'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const errorMessage = ref('')
const productDetail = ref<ProductDetail | null>(null)
const similarProducts = ref<RecommendationProduct[]>([])
const selectedSkuId = ref<string>()
const quantity = ref(1)
const checkoutVisible = ref(false)
const addressLoading = ref(false)
const couponLoading = ref(false)
const submitting = ref(false)
const favoriteSubmitting = ref(false)
const addresses = ref<UserAddress[]>([])
const orderCoupons = ref<UserCoupon[]>([])
const selectedAddressId = ref<string>()
const selectedCouponReceiveId = ref<string>()
const buyerRemark = ref('')

const availableSkus = computed(() => productDetail.value?.skus.filter((item) => item.status === 1) ?? [])

const selectedSku = computed(() => availableSkus.value.find((item) => item.id === selectedSkuId.value) ?? availableSkus.value[0])

const selectedGalleryImage = computed(() => {
    const detail = productDetail.value
    if (!detail) {
        return ''
    }
    return selectedSku.value?.skuCover?.trim() || detail.productCover || detail.images[0]?.imageUrl || ''
})

const detailImages = computed(() => {
    const detail = productDetail.value
    if (!detail) {
        return []
    }
    const images = [...(detail.images ?? [])]
    const skuCover = selectedSku.value?.skuCover?.trim()
    if (skuCover && !images.some((image) => image.imageUrl === skuCover)) {
        images.unshift({
            id: `sku-${selectedSku.value?.id}`,
            imageUrl: skuCover,
            imageType: 'sku',
        })
    }
    return images.length ? images : [{ id: detail.id, imageUrl: detail.productCover, imageType: 'cover' }]
})

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

const availableOrderCoupons = computed(() => orderCoupons.value.filter((coupon) => canUseCoupon(coupon)))

const selectedCoupon = computed(() => availableOrderCoupons.value.find((coupon) => coupon.receiveId === selectedCouponReceiveId.value))

const orderDiscountAmount = computed(() => calculateCouponDiscount(selectedCoupon.value))

const orderPayAmount = computed(() => Math.max(0, totalAmount.value - orderDiscountAmount.value))

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
        await loadSimilarProducts(productId)
        recordBrowseBehavior(productId)
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载作品详情失败，请稍后重试。'
    } finally {
        loading.value = false
    }
}

async function loadSimilarProducts(productId: string) {
    try {
        similarProducts.value = await getSimilarProducts(productId, 6)
    } catch {
        similarProducts.value = []
    }
}

function recordBrowseBehavior(productId: string) {
    if (!userStore.isLoggedIn) {
        return
    }
    void recordProductBehavior({
        productId,
        behaviorType: 1,
        sourcePage: 'product_detail',
        deviceType: window.innerWidth <= 760 ? 'mobile' : 'desktop',
    }).catch(() => undefined)
}

function formatMoney(value: number | string | null | undefined) {
    return `CNY ${Number(value || 0).toFixed(2)}`
}

function getPriceRange(minPrice: number, maxPrice: number) {
    return minPrice === maxPrice ? formatMoney(minPrice) : `${formatMoney(minPrice)} - ${formatMoney(maxPrice)}`
}

function goToProductDetail(productId: string) {
    void router.push({
        name: 'product-detail',
        params: {
            id: productId,
        },
    })
}

function getSkuCover(sku: ProductSku) {
    return resolveUploadUrl(sku.skuCover || productDetail.value?.productCover || '')
}

function formatCouponDiscount(coupon: UserCoupon) {
    if (coupon.couponType === 2) {
        return `${coupon.discountRate ?? '-'} 折`
    }
    return `减 ${formatMoney(coupon.discountAmount)}`
}

function formatCouponThreshold(coupon: UserCoupon) {
    const threshold = Number(coupon.thresholdAmount || 0)
    return threshold > 0 ? `满 ${formatMoney(threshold)} 可用` : '无门槛'
}

function canUseCoupon(coupon: UserCoupon) {
    if (!coupon.receiveId || coupon.useStatus !== 0) {
        return false
    }
    const threshold = Number(coupon.thresholdAmount || 0)
    if (totalAmount.value < threshold) {
        return false
    }
    const now = Date.now()
    const startTime = Date.parse(coupon.startTime.replace(/-/g, '/'))
    const endTime = Date.parse(coupon.endTime.replace(/-/g, '/'))
    if (!Number.isNaN(startTime) && now < startTime) {
        return false
    }
    return Number.isNaN(endTime) || now <= endTime
}

function calculateCouponDiscount(coupon: UserCoupon | undefined) {
    if (!coupon) {
        return 0
    }
    if (coupon.couponType === 2) {
        const rate = Number(coupon.discountRate || 0)
        if (rate <= 0 || rate >= 10) {
            return 0
        }
        return Math.min(totalAmount.value, totalAmount.value - (totalAmount.value * rate) / 10)
    }
    return Math.min(totalAmount.value, Number(coupon.discountAmount || 0))
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
    couponLoading.value = true
    checkoutVisible.value = true
    try {
        await Promise.all([loadAddresses(), loadOrderCoupons()])
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载结算信息失败')
    } finally {
        addressLoading.value = false
        couponLoading.value = false
    }
}

async function loadAddresses() {
    addresses.value = await getOrderAddresses()
    selectedAddressId.value = addresses.value.find((item) => item.isDefault === 1)?.id ?? addresses.value[0]?.id
    if (!addresses.value.length) {
        message.warning('请先在个人中心维护收货地址')
    }
}

async function loadOrderCoupons() {
    orderCoupons.value = await getMyCoupons({ useStatus: 0 })
    if (selectedCouponReceiveId.value && !availableOrderCoupons.value.some((coupon) => coupon.receiveId === selectedCouponReceiveId.value)) {
        selectedCouponReceiveId.value = undefined
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

async function refreshCoupons() {
    couponLoading.value = true
    try {
        await loadOrderCoupons()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '刷新优惠券失败')
    } finally {
        couponLoading.value = false
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
            couponReceiveId: selectedCouponReceiveId.value,
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

async function toggleFavorite() {
    const detail = productDetail.value
    if (!detail) {
        return
    }
    if (!userStore.isLoggedIn) {
        const redirect = router.currentRoute.value.fullPath
        await router.push(`/login?redirect=${encodeURIComponent(redirect)}`)
        return
    }
    favoriteSubmitting.value = true
    try {
        if (detail.favorited) {
            await unfavoriteProduct(detail.id)
            detail.favorited = false
            detail.favoriteCount = Math.max(0, Number(detail.favoriteCount || 0) - 1)
        } else {
            await favoriteProduct(detail.id)
            detail.favorited = true
            detail.favoriteCount = Number(detail.favoriteCount || 0) + 1
        }
    } catch (error) {
        message.error(error instanceof Error ? error.message : '收藏操作失败')
    } finally {
        favoriteSubmitting.value = false
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

watch([totalAmount, selectedSkuId], () => {
    if (selectedCouponReceiveId.value && !availableOrderCoupons.value.some((coupon) => coupon.receiveId === selectedCouponReceiveId.value)) {
        selectedCouponReceiveId.value = undefined
    }
})
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
                        <a-image :preview="false" :src="resolveUploadUrl(selectedGalleryImage)" :alt="productDetail.productName" />
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
                        <a-tag>{{ productDetail.craftType }}</a-tag>
                        <a-tag>{{ productDetail.originPlace }}</a-tag>
                        <a-tag>{{ productDetail.handmadeCycleDays }} 天手作周期</a-tag>
                    </a-space>

                    <div class="price-row">
                        <strong>{{ getPriceRange(productDetail.minPrice, productDetail.maxPrice) }}</strong>
                        <span>已售 {{ productDetail.soldQuantity }} 件 / 收藏 {{ productDetail.favoriteCount }}</span>
                    </div>

                    <a-button
                        class="manual-ant-btn favorite-btn"
                        :class="productDetail.favorited ? 'manual-ant-btn-primary' : 'manual-ant-btn-ghost'"
                        :loading="favoriteSubmitting"
                        @click="toggleFavorite"
                    >
                        {{ productDetail.favorited ? '已收藏' : '收藏作品' }}
                    </a-button>

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
                </div>
            </div>
        </section>

        <section v-if="productDetail" class="shell section">
            <a-row :gutter="[24, 24]">
                <a-col :span="24">
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
                        <a-empty v-else-if="!productDetail.materialDesc" description="暂无材料明细" />
                    </a-card>
                </a-col>
            </a-row>
        </section>

        <section v-if="similarProducts.length" class="shell section">
            <div class="section-head">
                <div>
                    <p class="eyebrow">相似推荐</p>
                    <h2>看过这件作品的用户，也常对这些作品感兴趣。</h2>
                </div>
            </div>
            <a-row :gutter="[22, 22]">
                <a-col v-for="product in similarProducts" :key="product.id" :xs="24" :lg="8">
                    <a-card class="soft-card similar-card" hoverable :bordered="false" @click="goToProductDetail(product.id)">
                        <template #cover>
                            <a-image :preview="false" :src="resolveUploadUrl(product.productCover)" :alt="product.productName" />
                        </template>
                        <div class="product-top">
                            <a-tag color="orange">{{ product.categoryName }}</a-tag>
                            <a-tag v-if="product.similarityScore">相似度 {{ Number(product.similarityScore).toFixed(2) }}</a-tag>
                        </div>
                        <h3>{{ product.productName }}</h3>
                        <p>{{ product.productSubtitle }}</p>
                        <div class="product-foot">
                            <span>{{ product.craftType }}</span>
                            <strong>{{ getPriceRange(product.minPrice, product.maxPrice) }}</strong>
                        </div>
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
                    <div class="checkout-tools">
                        <span>选择优惠券</span>
                        <a-button type="link" :loading="couponLoading" @click="refreshCoupons">刷新优惠券</a-button>
                    </div>
                    <a-spin :spinning="couponLoading">
                        <a-empty v-if="!availableOrderCoupons.length" description="暂无可用优惠券" />
                        <a-radio-group v-else v-model:value="selectedCouponReceiveId" class="coupon-list">
                            <a-radio :value="undefined" class="coupon-option">
                                <strong>不使用优惠券</strong>
                                <span>保持订单原价</span>
                            </a-radio>
                            <a-radio
                                v-for="coupon in availableOrderCoupons"
                                :key="coupon.receiveId || coupon.id"
                                :value="coupon.receiveId"
                                class="coupon-option"
                            >
                                <strong>{{ coupon.couponName }} / {{ formatCouponDiscount(coupon) }}</strong>
                                <span>{{ formatCouponThreshold(coupon) }}，有效期至 {{ coupon.endTime }}</span>
                            </a-radio>
                        </a-radio-group>
                    </a-spin>
                    <div v-if="selectedSku" class="checkout-summary">
                        <img :src="getSkuCover(selectedSku)" :alt="selectedSku.skuName" />
                        <div>
                            <strong>{{ selectedSku.skuName }}</strong>
                            <span>{{ selectedSku.specText || selectedSku.materialType || '标准规格' }} x {{ quantity }}</span>
                            <span v-if="orderDiscountAmount > 0">优惠 {{ formatMoney(orderDiscountAmount) }}</span>
                            <b>应付 {{ formatMoney(orderPayAmount) }}</b>
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

.section-head {
    margin-bottom: 22px;
}

.section-head h2 {
    margin: 8px 0 0;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: clamp(2rem, 4vw, 3.4rem);
    line-height: 1;
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

h3 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: 1.3rem;
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

.favorite-btn {
    width: fit-content;
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

.checkout-body,
.address-list,
.coupon-list {
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

.address-option,
.coupon-option {
    width: 100%;
    padding: 14px;
    border: 1px solid rgba(171, 118, 69, 0.18);
    border-radius: 12px;
    white-space: normal;
}

.address-option strong,
.address-option span,
.address-option small,
.coupon-option strong,
.coupon-option span {
    display: block;
}

.coupon-option span {
    color: var(--text-muted);
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

.similar-card {
    cursor: pointer;
    transition: all 0.3s ease;
}

.similar-card:hover {
    background: rgba(255, 253, 248, 0.95);
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(126, 69, 47, 0.08);
}

.similar-card :deep(.ant-card-cover img) {
    height: 220px;
    object-fit: cover;
}

.similar-card :deep(.ant-card-body) {
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
    .detail-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 760px) {
    .page {
        padding: 16px 16px 56px;
    }

    .thumb-grid {
        grid-template-columns: 1fr;
    }

    .price-row,
    .pay-row,
    .quantity-row {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
