<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

import { getMyRecommendations } from '@/api/recommendation'
import { resolveUploadUrl } from '@/api/upload'
import LandingNav from '@/components/layout/LandingNav.vue'
import { useHomePage } from '@/composables/useHomePage'
import { useUserStore } from '@/stores/user'
import type { RecommendationProduct } from '@/types/recommendation'

const router = useRouter()
const userStore = useUserStore()
const { errorMessage, homeData, loadHomePage, loading } = useHomePage()

const recommendationLoading = ref(false)
const recommendedProducts = ref<RecommendationProduct[]>([])

const categories = computed(() => homeData.value?.categories ?? [])
const featuredProducts = computed(() => homeData.value?.products.slice(0, 3) ?? [])
const signalProducts = computed(() => {
    const products = recommendedProducts.value.length ? recommendedProducts.value : featuredProducts.value
    return products.slice(0, 2)
})

function getPriceRange(minPrice: number, maxPrice: number) {
    return minPrice === maxPrice ? `CNY ${minPrice}` : `CNY ${minPrice} - ${maxPrice}`
}

function getSupportLabel(supportCustom: number) {
    return supportCustom ? '支持定制' : '现货零售'
}

async function loadRecommendations() {
    if (!userStore.isLoggedIn) {
        recommendedProducts.value = []
        return
    }
    recommendationLoading.value = true
    try {
        recommendedProducts.value = await getMyRecommendations(2)
    } catch {
        recommendedProducts.value = []
    } finally {
        recommendationLoading.value = false
    }
}

function goToProductDetail(productId: string) {
    void router.push({
        name: 'product-detail',
        params: {
            id: productId,
        },
    })
}

function goToCategoryProducts(categoryId: string) {
    void router.push({
        name: 'products',
        query: {
            categoryId,
        },
    })
}

onMounted(() => {
    void loadRecommendations()
})

watch(() => userStore.currentUser?.id, () => {
    void loadRecommendations()
})
</script>

<template>
    <main class="page">
        <section class="hero shell">
            <LandingNav />

            <div class="hero-grid">
                <div class="hero-copy">
                    <p class="eyebrow">手作品牌展示、精选作品、门店商品</p>
                    <h1>探索匠心手作，感受独具温度的品牌美学。</h1>

                    <a-space size="middle" wrap>
                        <RouterLink to="/products">
                            <a-button class="manual-ant-btn manual-ant-btn-primary" size="large">查看精选作品</a-button>
                        </RouterLink>
                    </a-space>

                    <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage">
                        <template #action>
                            <a-button type="link" @click="loadHomePage(true)">重新加载</a-button>
                        </template>
                    </a-alert>
                </div>

                <a-card class="hero-panel soft-card" :bordered="false">
                    <template #cover>
                        <a-image
                            :preview="false"
                            src="https://images.unsplash.com/photo-1517685352821-92cf88aee5a5?auto=format&fit=crop&w=1200&q=80"
                            alt="handmade studio"
                        />
                    </template>

                    <template #title>
                        <span class="card-title">今日门店推荐</span>
                    </template>

                    <a-skeleton v-if="loading || recommendationLoading" active :paragraph="{ rows: 4 }" />

                    <template v-else>
                        <div v-if="signalProducts.length" class="signal-list">
                            <div
                                v-for="product in signalProducts"
                                :key="product.id"
                                class="signal-item signal-item-clickable"
                                @click="goToProductDetail(product.id)"
                            >
                                <a-avatar shape="square" :size="56" :src="resolveUploadUrl(product.productCover)" />
                                <div class="signal-body">
                                    <strong>{{ product.productName }}</strong>
                                    <small>{{ product.categoryName }} / {{ product.craftType }}</small>
                                </div>
                                <span>{{ getPriceRange(product.minPrice, product.maxPrice) }}</span>
                            </div>
                        </div>
                        <a-empty v-else description="暂无精选作品" />
                    </template>
                </a-card>
            </div>
        </section>

        <section class="shell section">
            <p class="eyebrow">精选分类</p>

            <a-skeleton v-if="loading" active :paragraph="{ rows: 6 }" />

            <a-row v-else-if="categories.length" :gutter="[20, 20]">
                <a-col v-for="category in categories" :key="category.id" :xs="24" :sm="12" :xl="6">
                    <a-card
                        class="soft-card image-card category-card"
                        hoverable
                        :bordered="false"
                        role="button"
                        tabindex="0"
                        @click="goToCategoryProducts(category.id)"
                        @keydown.enter="goToCategoryProducts(category.id)"
                        @keydown.space.prevent="goToCategoryProducts(category.id)"
                    >
                        <template #cover>
                            <a-image :preview="false" :src="resolveUploadUrl(category.categoryIcon)" :alt="category.categoryName" />
                        </template>
                        <a-tag color="gold">level {{ category.categoryLevel }}</a-tag>
                        <h3>{{ category.categoryName }}</h3>
                        <p>{{ category.categoryDesc }}</p>
                    </a-card>
                </a-col>
            </a-row>
            <a-empty v-else description="暂无分类数据" />
        </section>

        <section class="shell section">
            <div class="section-head">
                <div>
                    <p class="eyebrow">作品预览</p>
                </div>
                <RouterLink to="/products">
                    <a-button class="manual-ant-btn manual-ant-btn-ghost" size="large">进入作品页</a-button>
                </RouterLink>
            </div>

            <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />

            <a-row v-else-if="featuredProducts.length" :gutter="[22, 22]">
                <a-col v-for="product in featuredProducts" :key="product.id" :xs="24" :lg="8">
                    <a-card class="soft-card image-card product-card" hoverable :bordered="false" @click="goToProductDetail(product.id)">
                        <template #cover>
                            <a-image :preview="false" :src="resolveUploadUrl(product.productCover)" :alt="product.productName" />
                        </template>

                        <div class="product-top">
                            <a-tag color="orange">{{ product.categoryName }}</a-tag>
                            <a-tag :color="product.supportCustom ? 'green' : 'default'">
                                {{ getSupportLabel(product.supportCustom) }}
                            </a-tag>
                        </div>

                        <h3>{{ product.productName }}</h3>
                        <p>{{ product.productSubtitle }}</p>

                        <div class="product-foot">
                            <span class="text-link">{{ product.categoryName }}</span>
                            <strong>{{ getPriceRange(product.minPrice, product.maxPrice) }}</strong>
                        </div>
                    </a-card>
                </a-col>
            </a-row>
            <a-empty v-else description="暂无作品数据" />
        </section>

        <section class="shell section features-section">
            <div class="section-head text-center">
                <p class="eyebrow">我们的优势</p>
                <h2>为什么选择我们</h2>
            </div>
            <a-row :gutter="[32, 32]" class="features-grid">
                <a-col :xs="24" :md="8">
                    <div class="feature-item">
                        <div class="feature-icon">✨</div>
                        <h3>纯手工制作</h3>
                        <p>每一件作品都由经验丰富的匠人亲手打造，倾注心血与温度。</p>
                    </div>
                </a-col>
                <a-col :xs="24" :md="8">
                    <div class="feature-item">
                        <div class="feature-icon">💎</div>
                        <h3>严选优质材料</h3>
                        <p>我们在全球范围内甄选高品质原材料，确保作品的质感与耐久度。</p>
                    </div>
                </a-col>
                <a-col :xs="24" :md="8">
                    <div class="feature-item">
                        <div class="feature-icon">🎨</div>
                        <h3>原创设计作品</h3>
                        <p>汇聚匠人原创手作，让每一件作品都有独特的设计感与温度。</p>
                    </div>
                </a-col>
            </a-row>
        </section>

        <section class="shell section banner-section">
            <div class="cta-banner">
                <h2>准备好探索您的专属手作了吗？</h2>
                <p>开启一段关于匠心与美学的旅程，发现属于你的独特好物。</p>
                <RouterLink to="/products">
                    <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" style="background: white; color: var(--coral-deep);">立即探索</a-button>
                </RouterLink>
            </div>
        </section>

        <footer class="shell section site-footer">
            <a-row :gutter="[32, 32]">
                <a-col :xs="24" :md="8">
                    <h3 class="footer-title">关于我们</h3>
                    <p class="footer-text">我们致力于传承手工技艺，创造独一无二的艺术品。用心呈现每一件手作，为您传递充满匠心与温度的品牌美学。</p>
                </a-col>
                <a-col :xs="24" :md="8">
                    <h3 class="footer-title">快速链接</h3>
                    <ul class="footer-links">
                        <li><RouterLink to="/products">所有作品</RouterLink></li>
                        <li><RouterLink to="/login">用户登录</RouterLink></li>
                        <li><RouterLink to="/register">注册账号</RouterLink></li>
                    </ul>
                </a-col>
                <a-col :xs="24" :md="8">
                    <h3 class="footer-title">联系方式</h3>
                    <ul class="footer-contact">
                        <li>📍 地址：xx市xx区xx创意园x栋</li>
                        <li>📞 电话：138-xxxx-xxxx</li>
                        <li>✉️ 邮箱：contact@manual.com</li>
                    </ul>
                </a-col>
            </a-row>
            <div class="footer-bottom">
                <p>&copy; 2026 手工门店展示站. All Rights Reserved.</p>
            </div>
        </footer>
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

.hero-grid {
    display: grid;
    gap: 24px;
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
    font-family: 'Nunito', sans-serif;
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
    font-weight: 600;
}

h1 {
    max-width: 12ch;
    font-size: clamp(2.4rem, 4.5vw, 4rem);
    line-height: 1.4;
    letter-spacing: 2px;
    color: #d4a555;
    font-family: 'KaiTi', 'STKaiti', 'SimSun', 'Georgia', serif;
    text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.15);
}

h2 {
    font-size: clamp(2rem, 4vw, 2.8rem);
    font-weight: 600;
}

h3 {
    font-size: 1.35rem;
    font-weight: 500;
}

.lead,
.product-card p {
    color: var(--text);
    font-family: 'Nunito', sans-serif;
}

.section-head {
    display: flex;
    align-items: end;
    justify-content: space-between;
    gap: 24px;
    margin-bottom: 24px;
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
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
    font-family: 'Nunito', sans-serif;
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

.signal-item-clickable,
.category-card,
.product-card {
    cursor: pointer;
}

.category-card,
.product-card,
.signal-item-clickable {
    transition: all 0.3s ease;
}

.category-card:hover,
.category-card:focus-visible,
.product-card:hover,
.signal-item-clickable:hover {
    background: rgba(255, 253, 248, 0.95);
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(126, 69, 47, 0.08);
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

.image-card :deep(.ant-card-body) {
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

.text-link {
    border: 0;
    padding: 0;
    background: transparent;
    color: var(--text-muted);
    cursor: pointer;
    text-align: left;
}

.text-link:hover {
    color: var(--coral-deep);
}

.text-center {
    text-align: center;
    justify-content: center;
    flex-direction: column;
    align-items: center;
}

.text-center h2 {
    margin-top: 12px;
}

.features-grid {
    margin-top: 48px;
}

.feature-item {
    text-align: center;
    padding: 32px 24px;
    border-radius: 24px;
    background: rgba(255, 253, 248, 0.6);
    transition: all 0.3s ease;
    height: 100%;
}

.feature-item:hover {
    background: rgba(255, 253, 248, 0.95);
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(126, 69, 47, 0.08);
}

.feature-icon {
    font-size: 48px;
    margin-bottom: 24px;
}

.feature-item h3 {
    margin-bottom: 16px;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-weight: 600;
    font-size: 1.5rem;
}

.feature-item p {
    color: var(--text-muted);
    line-height: 1.6;
    font-family: 'Nunito', sans-serif;
}

.cta-banner {
    background: linear-gradient(135deg, var(--coral) 0%, var(--gold) 100%);
    border-radius: 32px;
    padding: 64px 32px;
    text-align: center;
    color: white;
    box-shadow: 0 24px 48px rgba(255, 126, 103, 0.3);
}

.cta-banner h2 {
    color: white;
    margin-bottom: 16px;
    font-size: clamp(2rem, 4vw, 2.8rem);
    font-family: var(--font-display);
    font-weight: 600;
    font-style: italic;
}

.cta-banner p {
    color: rgba(255, 255, 255, 0.9);
    font-size: 1.2rem;
    margin-bottom: 32px;
    max-width: 600px;
    margin-left: auto;
    margin-right: auto;
    font-family: 'Nunito', sans-serif;
}

.cta-banner .manual-ant-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.site-footer {
    margin-top: 24px;
    padding-top: 64px;
    border-top: 1px solid rgba(126, 69, 47, 0.1);
}

.footer-title {
    margin-bottom: 24px;
    color: var(--text-strong);
    font-size: 1.2rem;
    font-family: var(--font-display);
    font-weight: 600;
}

.footer-text {
    color: var(--text-muted);
    line-height: 1.8;
    font-family: 'Nunito', sans-serif;
}

.footer-links,
.footer-contact {
    list-style: none;
    padding: 0;
    margin: 0;
}

.footer-links li,
.footer-contact li {
    margin-bottom: 16px;
    color: var(--text-muted);
}

.footer-links a {
    color: var(--text-muted);
    text-decoration: none;
    transition: color 0.2s ease;
}

.footer-links a:hover {
    color: var(--coral-deep);
}

.footer-bottom {
    margin-top: 64px;
    padding-top: 24px;
    border-top: 1px solid rgba(126, 69, 47, 0.1);
    text-align: center;
    color: var(--text-muted);
    font-size: 0.9rem;
}

@media (max-width: 1120px) {
    .hero-grid {
        grid-template-columns: 1fr;
    }

    .section-head {
        align-items: flex-start;
        flex-direction: column;
    }

    .text-center {
        align-items: center;
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
    .product-foot {
        flex-direction: column;
        align-items: flex-start;
    }

    .signal-item {
        grid-template-columns: 1fr;
    }

    :deep(.manual-ant-btn.ant-btn) {
        width: 100%;
    }

    .cta-banner {
        padding: 48px 24px;
    }
}
</style>
