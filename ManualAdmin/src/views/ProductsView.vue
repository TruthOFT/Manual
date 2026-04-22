<script setup lang="ts">
import {
    CheckOutlined,
    CloseOutlined,
    DeleteOutlined,
    EditOutlined,
    EyeOutlined,
    PlusOutlined,
    ReloadOutlined,
} from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'

import {
    approveAdminProduct,
    createAdminProduct,
    deleteAdminProduct,
    getAdminProductDetail,
    getAdminProductMeta,
    getAdminProducts,
    rejectAdminProduct,
    updateAdminProduct,
} from '@/api/product'
import { API_CONTEXT_PATH, BASE_URL } from '@/api/request'
import type {
    AdminProductDetail,
    AdminProductListItem,
    AdminProductMeta,
    AdminProductSaveRequest,
} from '@/types/product'

const columns = [
    { title: '商品名称', dataIndex: 'productName', key: 'productName' },
    { title: '分类', dataIndex: 'categoryName', key: 'categoryName' },
    { title: '匠人', dataIndex: 'artisanName', key: 'artisanName' },
    { title: '库存', dataIndex: 'inventory', key: 'inventory' },
    { title: '审核状态', dataIndex: 'auditStatus', key: 'auditStatus' },
    { title: '上架状态', dataIndex: 'status', key: 'status' },
    { title: '价格区间', key: 'priceRange' },
    { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime' },
    { title: '操作', key: 'action' },
]

const loading = ref(false)
const metaLoading = ref(false)
const drawerLoading = ref(false)
const actionLoading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)
const modalVisible = ref(false)
const isEditMode = ref(false)
const currentEditId = ref('')
const keyword = ref('')
const auditStatus = ref<number | undefined>()
const status = ref<number | undefined>()
const products = ref<AdminProductListItem[]>([])
const currentProduct = ref<AdminProductDetail | null>(null)
const meta = ref<AdminProductMeta>({
    categories: [],
    artisans: [],
})

const form = reactive<AdminProductSaveRequest>({
    categoryId: '',
    artisanId: '',
    productName: '',
    productSubtitle: '',
    productCover: '',
    productDesc: '',
    craftType: '',
    materialDesc: '',
    originPlace: '',
    handmadeCycleDays: 0,
    supportCustom: 0,
    inventory: 0,
    minPrice: 0,
    maxPrice: 0,
    auditStatus: 0,
    status: 0,
    sortOrder: 0,
})

const pendingCount = computed(() => products.value.filter((item) => item.auditStatus === 0).length)
const onShelfCount = computed(() => products.value.filter((item) => item.status === 1).length)
const totalCount = computed(() => products.value.length)
const modalTitle = computed(() => (isEditMode.value ? '编辑商品' : '新增商品'))

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
    if (value === 1) return '上架中'
    if (value === 2) return '已下架'
    return '未知'
}

function getStatusColor(value: number) {
    if (value === 1) return 'blue'
    if (value === 2) return 'default'
    return 'purple'
}

function formatCategoryLabel(item: AdminProductMeta['categories'][number]) {
    if (item.categoryLevel === 2 && item.parentName) {
        return `${item.parentName} / ${item.categoryName}`
    }
    return item.categoryName
}

function formatArtisanLabel(item: AdminProductMeta['artisans'][number]) {
    const artisanName = item.artisanName || '-'
    const shopName = item.shopName || '-'
    const userName = item.userName || item.userAccount
    return `${artisanName} (${shopName}) - ${userName}`
}

function resolveImageUrl(url?: string | null) {
    if (!url) {
        return ''
    }
    let value = String(url).trim()
    if ((value.startsWith('"') && value.endsWith('"')) || (value.startsWith("'") && value.endsWith("'"))) {
        value = value.slice(1, -1).trim()
    }
    if (value.startsWith('[')) {
        try {
            const parsed = JSON.parse(value)
            if (Array.isArray(parsed) && typeof parsed[0] === 'string') {
                value = parsed[0].trim()
            }
        } catch {
            // ignore malformed legacy data
        }
    }
    value = value.replaceAll('\\', '/')
    if (!value) {
        return ''
    }
    if (value.startsWith('blob:') || value.startsWith('data:')) {
        return value
    }
    if (value.startsWith('http://') || value.startsWith('https://')) {
        try {
            const parsedUrl = new URL(value)
            if ((parsedUrl.hostname === 'localhost' || parsedUrl.hostname === '127.0.0.1')
                && parsedUrl.pathname.startsWith('/upload/')) {
                return `${BASE_URL}${API_CONTEXT_PATH}${parsedUrl.pathname}${parsedUrl.search}${parsedUrl.hash}`
            }
        } catch {
            // keep original value
        }
        return value
    }
    const normalizedPath = value.startsWith('/') ? value : `/${value}`
    if (normalizedPath.startsWith(`${API_CONTEXT_PATH}/`)) {
        return `${BASE_URL}${normalizedPath}`
    }
    if (normalizedPath.startsWith('/upload/')) {
        return `${BASE_URL}${API_CONTEXT_PATH}${normalizedPath}`
    }
    return `${BASE_URL}${normalizedPath}`
}

function resetForm() {
    form.categoryId = ''
    form.artisanId = ''
    form.productName = ''
    form.productSubtitle = ''
    form.productCover = ''
    form.productDesc = ''
    form.craftType = ''
    form.materialDesc = ''
    form.originPlace = ''
    form.handmadeCycleDays = 0
    form.supportCustom = 0
    form.inventory = 0
    form.minPrice = 0
    form.maxPrice = 0
    form.auditStatus = 0
    form.status = 0
    form.sortOrder = 0
    currentEditId.value = ''
    isEditMode.value = false
}

function validateForm() {
    if (!form.categoryId) {
        throw new Error('请选择分类')
    }
    if (!form.artisanId) {
        throw new Error('请选择匠人')
    }
    if (!form.productName.trim()) {
        throw new Error('请输入商品名称')
    }
    const minPrice = Number(form.minPrice || 0)
    const maxPrice = Number(form.maxPrice || 0)
    const inventory = Number(form.inventory || 0)
    const handmadeCycleDays = Number(form.handmadeCycleDays || 0)
    if (Number.isNaN(minPrice) || minPrice < 0) {
        throw new Error('最低价不能小于 0')
    }
    if (Number.isNaN(maxPrice) || maxPrice < 0) {
        throw new Error('最高价不能小于 0')
    }
    if (minPrice > maxPrice) {
        throw new Error('最低价不能大于最高价')
    }
    if (!Number.isInteger(inventory) || inventory < 0) {
        throw new Error('库存必须是大于等于 0 的整数')
    }
    if (!Number.isInteger(handmadeCycleDays) || handmadeCycleDays < 0) {
        throw new Error('制作周期必须是大于等于 0 的整数')
    }
    if (form.status === 1 && form.auditStatus !== 1) {
        throw new Error('仅审核通过的商品才可以上架')
    }
}

function buildSavePayload(): AdminProductSaveRequest {
    return {
        categoryId: form.categoryId,
        artisanId: form.artisanId,
        productName: form.productName.trim(),
        productSubtitle: form.productSubtitle.trim(),
        productCover: form.productCover.trim(),
        productDesc: form.productDesc.trim(),
        craftType: form.craftType.trim(),
        materialDesc: form.materialDesc.trim(),
        originPlace: form.originPlace.trim(),
        handmadeCycleDays: Number(form.handmadeCycleDays || 0),
        supportCustom: form.supportCustom === 1 ? 1 : 0,
        inventory: Number(form.inventory || 0),
        minPrice: Number(form.minPrice || 0),
        maxPrice: Number(form.maxPrice || 0),
        auditStatus: form.auditStatus,
        status: form.status,
        sortOrder: Number(form.sortOrder || 0),
    }
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
        message.error(error instanceof Error ? error.message : '加载商品列表失败')
    } finally {
        loading.value = false
    }
}

async function loadMeta() {
    metaLoading.value = true
    try {
        meta.value = await getAdminProductMeta()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载商品元数据失败')
    } finally {
        metaLoading.value = false
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

function openCreateModal() {
    resetForm()
    modalVisible.value = true
}

async function openEditModal(productId: string) {
    saving.value = true
    try {
        const detail = await getAdminProductDetail(productId)
        isEditMode.value = true
        currentEditId.value = productId
        form.categoryId = detail.categoryId
        form.artisanId = detail.artisanId
        form.productName = detail.productName
        form.productSubtitle = detail.productSubtitle || ''
        form.productCover = detail.productCover || ''
        form.productDesc = detail.productDesc || ''
        form.craftType = detail.craftType || ''
        form.materialDesc = detail.materialDesc || ''
        form.originPlace = detail.originPlace || ''
        form.handmadeCycleDays = detail.handmadeCycleDays || 0
        form.supportCustom = detail.supportCustom === 1 ? 1 : 0
        form.inventory = detail.inventory || 0
        form.minPrice = Number(detail.minPrice || 0)
        form.maxPrice = Number(detail.maxPrice || 0)
        form.auditStatus = detail.auditStatus
        form.status = detail.status
        form.sortOrder = detail.sortOrder || 0
        modalVisible.value = true
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载商品编辑数据失败')
    } finally {
        saving.value = false
    }
}

async function handleSave() {
    try {
        validateForm()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '表单校验失败')
        return
    }
    saving.value = true
    try {
        const payload = buildSavePayload()
        if (isEditMode.value) {
            await updateAdminProduct(currentEditId.value, payload)
        } else {
            await createAdminProduct(payload)
        }
        modalVisible.value = false
        resetForm()
        await loadProducts()
        await loadMeta()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '保存商品失败')
    } finally {
        saving.value = false
    }
}

function handleDelete(productId: string) {
    Modal.confirm({
        title: '确认删除该商品？',
        content: '删除后该商品会被逻辑删除，商品图片、材质和 SKU 也会同步逻辑删除。',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
            try {
                await deleteAdminProduct(productId)
                if (currentProduct.value?.id === productId) {
                    drawerVisible.value = false
                    currentProduct.value = null
                }
                await loadProducts()
            } catch (error) {
                message.error(error instanceof Error ? error.message : '删除商品失败')
            }
        },
    })
}

async function runAuditAction(action: () => Promise<boolean>) {
    actionLoading.value = true
    try {
        await action()
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
    await runAuditAction(() => approveAdminProduct(currentProduct.value!.id))
}

async function rejectCurrentProduct() {
    if (!currentProduct.value) {
        return
    }
    await runAuditAction(() => rejectAdminProduct(currentProduct.value!.id))
}

onMounted(() => {
    void Promise.all([loadProducts(), loadMeta()])
})
</script>

<template>
    <div class="products-view">
        <a-card class="panel summary-panel" :bordered="false">
            <div class="summary-copy">
                <p class="eyebrow">商品管理</p>
                <h2>全量商品管理中心</h2>
                <p>支持商品查询、详情、审核、新增、编辑和删除，库存可在编辑表单中直接调整。</p>
            </div>
            <div class="summary-metrics">
                <div class="metric-item">
                    <strong>{{ totalCount }}</strong>
                    <span>商品总数</span>
                </div>
                <div class="metric-item">
                    <strong>{{ pendingCount }}</strong>
                    <span>待审核</span>
                </div>
                <div class="metric-item">
                    <strong>{{ onShelfCount }}</strong>
                    <span>上架中</span>
                </div>
            </div>
        </a-card>

        <a-card class="panel" :bordered="false" title="商品列表">
            <template #extra>
                <a-space>
                    <a-button :loading="metaLoading" @click="loadMeta">
                        <ReloadOutlined />
                        刷新元数据
                    </a-button>
                    <a-button @click="loadProducts">
                        <ReloadOutlined />
                        刷新列表
                    </a-button>
                    <a-button type="primary" @click="openCreateModal">
                        <PlusOutlined />
                        新增商品
                    </a-button>
                </a-space>
            </template>

            <div class="toolbar">
                <a-input
                    v-model:value="keyword"
                    size="large"
                    placeholder="搜索商品、分类或匠人"
                    @press-enter="loadProducts"
                />
                <a-select v-model:value="auditStatus" allow-clear size="large" placeholder="审核状态">
                    <a-select-option :value="-1">草稿</a-select-option>
                    <a-select-option :value="0">待审核</a-select-option>
                    <a-select-option :value="1">已通过</a-select-option>
                    <a-select-option :value="2">已驳回</a-select-option>
                </a-select>
                <a-select v-model:value="status" allow-clear size="large" placeholder="上架状态">
                    <a-select-option :value="0">草稿</a-select-option>
                    <a-select-option :value="1">上架中</a-select-option>
                    <a-select-option :value="2">已下架</a-select-option>
                </a-select>
                <a-button type="primary" size="large" @click="loadProducts">查询</a-button>
            </div>

            <a-table :columns="columns" :data-source="products" :loading="loading" :pagination="{ pageSize: 8 }" row-key="id">
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'productName'">
                        <div class="product-name-cell">
                            <a-avatar shape="square" :size="52" :src="resolveImageUrl(record.productCover)" />
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
                        ¥{{ record.minPrice }} - ¥{{ record.maxPrice }}
                    </template>
                    <template v-else-if="column.key === 'action'">
                        <a-space wrap>
                            <a-button size="small" @click="openDetail(record.id)">
                                <EyeOutlined />
                                查看
                            </a-button>
                            <a-button size="small" @click="openEditModal(record.id)">
                                <EditOutlined />
                                编辑
                            </a-button>
                            <a-button danger size="small" @click="handleDelete(record.id)">
                                <DeleteOutlined />
                                删除
                            </a-button>
                            <a-button
                                v-if="record.auditStatus === 0"
                                type="primary"
                                size="small"
                                @click="runAuditAction(() => approveAdminProduct(record.id))"
                            >
                                <CheckOutlined />
                                通过
                            </a-button>
                            <a-button
                                v-if="record.auditStatus === 0 || record.auditStatus === 1"
                                size="small"
                                @click="runAuditAction(() => rejectAdminProduct(record.id))"
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
        title="商品详情"
        width="900"
        :destroy-on-close="true"
    >
        <a-skeleton v-if="drawerLoading" active :paragraph="{ rows: 10 }" />
        <template v-else-if="currentProduct">
            <div class="detail-grid">
                <a-card class="detail-card" :bordered="false">
                    <div class="detail-head">
                        <a-avatar shape="square" :size="88" :src="resolveImageUrl(currentProduct.productCover)" />
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
                        <span>价格区间：¥{{ currentProduct.minPrice }} - ¥{{ currentProduct.maxPrice }}</span>
                        <span>支持定制：{{ currentProduct.supportCustom === 1 ? '是' : '否' }}</span>
                    </div>
                    <p class="detail-desc">{{ currentProduct.productDesc || '暂无商品描述' }}</p>
                </a-card>

                <a-card class="detail-card" :bordered="false" title="图片">
                    <div v-if="currentProduct.images.length" class="image-grid">
                        <a-image
                            v-for="item in currentProduct.images"
                            :key="item.id"
                            :src="resolveImageUrl(item.imageUrl)"
                            :alt="currentProduct.productName"
                            class="detail-image"
                        />
                    </div>
                    <a-image
                        v-else-if="currentProduct.productCover"
                        :src="resolveImageUrl(currentProduct.productCover)"
                        :alt="currentProduct.productName"
                        :width="260"
                    />
                    <a-empty v-else description="暂无图片" />
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
                <a-button @click="openEditModal(currentProduct.id)">
                    <EditOutlined />
                    编辑商品
                </a-button>
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
                    :loading="actionLoading"
                    @click="rejectCurrentProduct"
                >
                    驳回商品
                </a-button>
            </div>
        </template>
    </a-drawer>

    <a-modal
        v-model:open="modalVisible"
        :title="modalTitle"
        :confirm-loading="saving"
        ok-text="保存"
        cancel-text="取消"
        width="980px"
        @ok="handleSave"
        @cancel="resetForm"
    >
        <a-form layout="vertical" class="form-grid">
            <a-form-item label="分类">
                <a-select v-model:value="form.categoryId" size="large" placeholder="请选择分类">
                    <a-select-option v-for="item in meta.categories" :key="item.id" :value="item.id">
                        {{ formatCategoryLabel(item) }}
                    </a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="匠人">
                <a-select v-model:value="form.artisanId" size="large" placeholder="请选择匠人" show-search :filter-option="false">
                    <a-select-option v-for="item in meta.artisans" :key="item.id" :value="item.id">
                        {{ formatArtisanLabel(item) }}
                    </a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="商品名称">
                <a-input v-model:value="form.productName" size="large" />
            </a-form-item>
            <a-form-item label="副标题">
                <a-input v-model:value="form.productSubtitle" size="large" />
            </a-form-item>
            <a-form-item label="库存">
                <a-input-number v-model:value="form.inventory" size="large" :min="0" :controls="false" class="full-width" />
            </a-form-item>
            <a-form-item label="制作周期（天）">
                <a-input-number v-model:value="form.handmadeCycleDays" size="large" :min="0" :controls="false" class="full-width" />
            </a-form-item>
            <a-form-item label="最低价">
                <a-input-number v-model:value="form.minPrice" size="large" :min="0" :controls="false" class="full-width" />
            </a-form-item>
            <a-form-item label="最高价">
                <a-input-number v-model:value="form.maxPrice" size="large" :min="0" :controls="false" class="full-width" />
            </a-form-item>
            <a-form-item label="审核状态">
                <a-select v-model:value="form.auditStatus" size="large">
                    <a-select-option :value="-1">草稿</a-select-option>
                    <a-select-option :value="0">待审核</a-select-option>
                    <a-select-option :value="1">已通过</a-select-option>
                    <a-select-option :value="2">已驳回</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="上架状态">
                <a-select v-model:value="form.status" size="large">
                    <a-select-option :value="0">草稿</a-select-option>
                    <a-select-option :value="1">上架中</a-select-option>
                    <a-select-option :value="2">已下架</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="排序值">
                <a-input-number v-model:value="form.sortOrder" size="large" :controls="false" class="full-width" />
            </a-form-item>
            <a-form-item label="支持定制" class="full-span">
                <a-switch v-model:checked="form.supportCustom" :checked-value="1" :un-checked-value="0" />
            </a-form-item>
            <a-form-item label="封面地址" class="full-span">
                <a-input v-model:value="form.productCover" size="large" />
            </a-form-item>
            <a-form-item label="工艺类型">
                <a-input v-model:value="form.craftType" size="large" />
            </a-form-item>
            <a-form-item label="产地">
                <a-input v-model:value="form.originPlace" size="large" />
            </a-form-item>
            <a-form-item label="材质说明" class="full-span">
                <a-input v-model:value="form.materialDesc" size="large" />
            </a-form-item>
            <a-form-item label="商品描述" class="full-span">
                <a-textarea v-model:value="form.productDesc" :rows="5" />
            </a-form-item>
        </a-form>
    </a-modal>
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

.summary-metrics {
    display: flex;
    gap: 18px;
}

.metric-item {
    display: grid;
    justify-items: center;
    min-width: 88px;
}

.metric-item strong {
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: 2rem;
    line-height: 1;
}

.metric-item span {
    color: var(--text-muted);
    font-size: 0.86rem;
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

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0 16px;
}

.full-span {
    grid-column: 1 / -1;
}

.full-width {
    width: 100%;
}

@media (max-width: 980px) {
    .summary-panel {
        flex-direction: column;
        align-items: stretch;
    }

    .summary-metrics {
        justify-content: space-between;
    }

    .toolbar,
    .meta-grid,
    .image-grid,
    .form-grid {
        grid-template-columns: 1fr;
    }

    .drawer-actions {
        flex-direction: column;
        align-items: stretch;
    }
}
</style>
