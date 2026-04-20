<script setup lang="ts">
import { CheckOutlined, CloseOutlined, EyeOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { computed, onMounted, ref } from 'vue'

import {
    approveAdminProduct,
    getAdminProductDetail,
    getAdminProducts,
    rejectAdminProduct,
} from '@/api/product'
import type { AdminProductDetail, AdminProductListItem } from '@/types/product'

const columns = [
    { title: '商品名称', dataIndex: 'productName', key: 'productName' },
    { title: '分类', dataIndex: 'categoryName', key: 'categoryName' },
    { title: '匠人', dataIndex: 'artisanName', key: 'artisanName' },
    { title: '审核状态', dataIndex: 'auditStatus', key: 'auditStatus' },
    { title: '上架状态', dataIndex: 'status', key: 'status' },
    { title: '价格区间', key: 'priceRange' },
    { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime' },
    { title: '操作', key: 'action' },
]

const loading = ref(false)
const drawerLoading = ref(false)
const actionLoading = ref(false)
const drawerVisible = ref(false)
const keyword = ref('')
const auditStatus = ref<number | undefined>(0)
const status = ref<number | undefined>()
const products = ref<AdminProductListItem[]>([])
const currentProduct = ref<AdminProductDetail | null>(null)

const pendingCount = computed(() => products.value.filter((item) => item.auditStatus === 0).length)

function getAuditText(value: number) {
    if (value === -1) return '草稿'
    if (value === 0) return '待审核'
    if (value === 1) return '已通过'
    if (value === 2) return '已驳回'
    return '未知'
}

function getAuditColor(value: number) {
    if (value === 1) return 'green'
    if (value === 2) return 'red'
    return 'gold'
}

function getStatusText(value: number) {
    if (value === 0) return '草稿'
    if (value === 1) return '已上架'
    if (value === 2) return '已下架'
    return '未知'
}

function getStatusColor(value: number) {
    if (value === 1) return 'blue'
    if (value === 2) return 'default'
    return 'purple'
}

async function loadProducts() {
    loading.value = true
    try {
        products.value = await getAdminProducts({
            keyword: keyword.value || undefined,
            auditStatus: auditStatus.value,
            status: status.value,
        })
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载商品审核列表失败')
    } finally {
        loading.value = false
    }
}

async function openDetail(productId: string) {
    drawerLoading.value = true
    drawerVisible.value = true
    try {
        currentProduct.value = await getAdminProductDetail(productId)
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载商品详情失败')
        drawerVisible.value = false
    } finally {
        drawerLoading.value = false
    }
}

async function runAuditAction(action: () => Promise<boolean>, successText: string) {
    actionLoading.value = true
    try {
        await action()
        message.success(successText)
        if (currentProduct.value) {
            currentProduct.value = await getAdminProductDetail(currentProduct.value.id)
        }
        await loadProducts()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '审核操作失败')
    } finally {
        actionLoading.value = false
    }
}

async function approveCurrentProduct() {
    if (!currentProduct.value) {
        return
    }
    const productId = currentProduct.value.id
    await runAuditAction(() => approveAdminProduct(productId), '商品已审核通过')
}

async function rejectCurrentProduct() {
    if (!currentProduct.value) {
        return
    }
    const productId = currentProduct.value.id
    await runAuditAction(() => rejectAdminProduct(productId), '商品已驳回')
}

onMounted(() => {
    void loadProducts()
})
</script>

<template>
    <div class="products-view">
        <a-card class="panel summary-panel" :bordered="false">
            <div class="summary-copy">
                <p class="eyebrow">审核中心</p>
                <h2>商品审核中心</h2>
                <p>集中处理匠人提交的商品审核，优先展示待审核商品。</p>
            </div>
            <div class="summary-metric">
                <strong>{{ pendingCount }}</strong>
                <span>待审核商品</span>
            </div>
        </a-card>

        <a-card class="panel" :bordered="false" title="审核列表">
            <template #extra>
                <a-button @click="loadProducts">
                    <ReloadOutlined />
                    刷新
                </a-button>
            </template>

            <div class="toolbar">
                <a-input v-model:value="keyword" size="large" placeholder="搜索商品、分类或匠人" @press-enter="loadProducts" />
                <a-select v-model:value="auditStatus" allow-clear size="large" placeholder="审核状态">
                    <a-select-option :value="-1">草稿</a-select-option>
                    <a-select-option :value="0">待审核</a-select-option>
                    <a-select-option :value="1">已通过</a-select-option>
                    <a-select-option :value="2">已驳回</a-select-option>
                </a-select>
                <a-select v-model:value="status" allow-clear size="large" placeholder="上架状态">
                    <a-select-option :value="0">草稿</a-select-option>
                    <a-select-option :value="1">已上架</a-select-option>
                    <a-select-option :value="2">已下架</a-select-option>
                </a-select>
                <a-button type="primary" size="large" @click="loadProducts">查询</a-button>
            </div>

            <a-table :columns="columns" :data-source="products" :loading="loading" :pagination="{ pageSize: 8 }" row-key="id">
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'productName'">
                        <div class="product-name-cell">
                            <a-avatar shape="square" :size="52" :src="record.productCover || undefined" />
                            <div>
                                <strong>{{ record.productName }}</strong>
                                <p>{{ record.productSubtitle || '暂无副标题' }}</p>
                            </div>
                        </div>
                    </template>
                    <template v-else-if="column.key === 'auditStatus'">
                        <a-tag :color="getAuditColor(record.auditStatus)">
                            {{ getAuditText(record.auditStatus) }}
                        </a-tag>
                    </template>
                    <template v-else-if="column.key === 'status'">
                        <a-tag :color="getStatusColor(record.status)">
                            {{ getStatusText(record.status) }}
                        </a-tag>
                    </template>
                    <template v-else-if="column.key === 'priceRange'">
                        ￥{{ record.minPrice }} - ￥{{ record.maxPrice }}
                    </template>
                    <template v-else-if="column.key === 'action'">
                        <a-space wrap>
                            <a-button size="small" @click="openDetail(record.id)">
                                <EyeOutlined />
                                查看
                            </a-button>
                            <a-button
                                v-if="record.auditStatus === 0"
                                type="primary"
                                size="small"
                                @click="runAuditAction(() => approveAdminProduct(record.id), '商品已审核通过')"
                            >
                                <CheckOutlined />
                                通过
                            </a-button>
                            <a-button
                                v-if="record.auditStatus === 0 || record.auditStatus === 1"
                                danger
                                size="small"
                                @click="runAuditAction(() => rejectAdminProduct(record.id), '商品已驳回')"
                            >
                                <CloseOutlined />
                                驳回
                            </a-button>
                        </a-space>
                    </template>
                </template>
            </a-table>
        </a-card>
    </div>

    <a-drawer
        v-model:open="drawerVisible"
        title="商品审核详情"
        width="880"
        :destroy-on-close="true"
    >
        <a-skeleton v-if="drawerLoading" active :paragraph="{ rows: 10 }" />
        <template v-else-if="currentProduct">
            <div class="detail-grid">
                <a-card class="detail-card" :bordered="false">
                    <div class="detail-head">
                        <a-avatar shape="square" :size="88" :src="currentProduct.productCover || undefined" />
                        <div>
                            <h3>{{ currentProduct.productName }}</h3>
                            <p>{{ currentProduct.productSubtitle || '暂无副标题' }}</p>
                            <a-space wrap>
                                <a-tag>{{ currentProduct.categoryName || '未分类' }}</a-tag>
                                <a-tag :color="getAuditColor(currentProduct.auditStatus)">{{ getAuditText(currentProduct.auditStatus) }}</a-tag>
                                <a-tag :color="getStatusColor(currentProduct.status)">{{ getStatusText(currentProduct.status) }}</a-tag>
                            </a-space>
                        </div>
                    </div>
                    <div class="meta-grid">
                        <span>匠人：{{ currentProduct.artisanName || '-' }}</span>
                        <span>店铺：{{ currentProduct.shopName || '-' }}</span>
                        <span>制作周期：{{ currentProduct.handmadeCycleDays }} 天</span>
                        <span>库存：{{ currentProduct.inventory }}</span>
                        <span>价格区间：￥{{ currentProduct.minPrice }} - ￥{{ currentProduct.maxPrice }}</span>
                        <span>支持定制：{{ currentProduct.supportCustom === 1 ? '是' : '否' }}</span>
                    </div>
                    <p class="detail-desc">{{ currentProduct.productDesc || '暂无商品描述' }}</p>
                </a-card>

                <a-card class="detail-card" :bordered="false" title="图片">
                    <div class="image-grid">
                        <a-image
                            v-for="item in currentProduct.images"
                            :key="item.id"
                            :src="item.imageUrl"
                            :alt="currentProduct.productName"
                            class="detail-image"
                        />
                    </div>
                </a-card>

                <a-card class="detail-card" :bordered="false" title="材质">
                    <a-empty v-if="!currentProduct.materials.length" description="暂无材质信息" />
                    <div v-else class="material-grid">
                        <div v-for="item in currentProduct.materials" :key="item.id" class="material-item">
                            <strong>{{ item.materialName }}</strong>
                            <span>{{ item.materialOrigin || '来源未填写' }}</span>
                            <small>{{ item.materialRatio || '占比未填写' }}</small>
                        </div>
                    </div>
                </a-card>

                <a-card class="detail-card" :bordered="false" title="SKU">
                    <a-table :data-source="currentProduct.skus" :pagination="false" row-key="id" size="small">
                        <a-table-column key="skuName" title="SKU" data-index="skuName" />
                        <a-table-column key="specText" title="规格" data-index="specText" />
                        <a-table-column key="price" title="售价" data-index="price" />
                        <a-table-column key="stock" title="库存" data-index="stock" />
                    </a-table>
                </a-card>
            </div>

            <div class="drawer-actions">
                <a-button
                    v-if="currentProduct.auditStatus === 0"
                    type="primary"
                    :loading="actionLoading"
                    @click="approveCurrentProduct"
                >
                    审核通过
                </a-button>
                <a-button
                    v-if="currentProduct.auditStatus === 0 || currentProduct.auditStatus === 1"
                    danger
                    :loading="actionLoading"
                    @click="rejectCurrentProduct"
                >
                    驳回商品
                </a-button>
            </div>
        </template>
    </a-drawer>
</template>

<style scoped>
.products-view {
    display: grid;
    gap: 20px;
}

.panel {
    border-radius: 28px;
    background: var(--surface);
    box-shadow: var(--shadow);
}

.summary-panel {
    display: flex;
    justify-content: space-between;
    gap: 24px;
    align-items: center;
}

.eyebrow {
    margin: 0 0 8px;
    color: var(--blue);
    font-size: 0.82rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.summary-copy h2 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: 2rem;
}

.summary-copy p:last-child {
    margin: 10px 0 0;
    color: var(--text-muted);
}

.summary-metric {
    display: grid;
    justify-items: end;
}

.summary-metric strong {
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: 3rem;
    line-height: 1;
}

.summary-metric span {
    color: var(--text-muted);
}

.toolbar {
    display: grid;
    grid-template-columns: minmax(0, 2fr) repeat(2, minmax(0, 1fr)) auto;
    gap: 16px;
    margin-bottom: 18px;
}

.product-name-cell {
    display: grid;
    grid-template-columns: auto 1fr;
    gap: 12px;
    align-items: center;
}

.product-name-cell strong {
    display: block;
    color: var(--text-strong);
}

.product-name-cell p {
    margin: 4px 0 0;
    color: var(--text-muted);
}

.detail-grid {
    display: grid;
    gap: 16px;
}

.detail-card {
    border-radius: 20px;
    background: #f8fafc;
}

.detail-head {
    display: grid;
    grid-template-columns: auto 1fr;
    gap: 16px;
    align-items: center;
}

.detail-head h3 {
    margin: 0 0 6px;
    color: var(--text-strong);
}

.detail-head p {
    margin: 0 0 10px;
    color: var(--text-muted);
}

.meta-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
    margin-top: 18px;
    color: var(--text);
}

.detail-desc {
    margin: 18px 0 0;
    color: var(--text);
    line-height: 1.8;
}

.image-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
}

.detail-image {
    width: 100%;
    border-radius: 16px;
    overflow: hidden;
}

.material-grid {
    display: grid;
    gap: 12px;
}

.material-item {
    display: grid;
    gap: 4px;
    padding: 14px 16px;
    border-radius: 16px;
    background: #ffffff;
}

.material-item strong {
    color: var(--text-strong);
}

.material-item span,
.material-item small {
    color: var(--text-muted);
}

.drawer-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 20px;
}

@media (max-width: 980px) {
    .summary-panel,
    .toolbar,
    .meta-grid,
    .image-grid {
        grid-template-columns: 1fr;
    }

    .summary-panel,
    .drawer-actions {
        flex-direction: column;
        align-items: stretch;
    }
}
</style>
