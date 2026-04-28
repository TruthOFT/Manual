<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
    PlusOutlined,
    SearchOutlined,
    EditOutlined,
    DeleteOutlined,
    EyeOutlined,
} from '@ant-design/icons-vue'

import {
    approveAdminProduct,
    createAdminProduct,
    deleteAdminProduct,
    getAdminProductDetail,
    getAdminProductMeta,
    getAdminProducts,
    updateAdminProduct,
} from '@/api/product'
import { API_CONTEXT_PATH, BASE_URL } from '@/api/request'
import { resolveUploadUrl, uploadFile } from '@/api/upload'
import type {
    AdminProductDetail,
    AdminProductListItem,
    AdminProductMeta,
    AdminProductSaveRequest,
    AdminProductSkuSaveRequest,
} from '@/types/product'

type AdminProductSkuForm = AdminProductSkuSaveRequest & {
    pendingCoverFile?: File | null
    coverPreviewUrl?: string
}

type AdminProductForm = Omit<AdminProductSaveRequest, 'skus'> & {
    skus: AdminProductSkuForm[]
}

const columns = [
    { title: '商品封面', dataIndex: 'productCover', key: 'productCover' },
    { title: '商品名称', dataIndex: 'productName', key: 'productName' },
    { title: '分类', dataIndex: 'categoryName', key: 'categoryName' },
    { title: '价格', dataIndex: 'price', key: 'price' },
    { title: '上架状态', dataIndex: 'status', key: 'status' },
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
const searchKeyword = ref('')
const selectedCategory = ref('')
const products = ref<AdminProductListItem[]>([])
const currentProduct = ref<AdminProductDetail | null>(null)
const meta = ref<AdminProductMeta>({
    categories: [],
})
const coverFileInputRef = ref<HTMLInputElement>()
const pendingCoverFile = ref<File | null>(null)
const coverPreviewUrl = ref('')
const skuCoverFileInputRefs = ref<Array<HTMLInputElement | null>>([])

function createDefaultSkuRow(): AdminProductSkuForm {
    return {
        skuCode: '',
        skuName: '默认规格',
        skuCover: '',
        specText: '默认规格',
        materialType: '',
        weight: 0,
        price: 0,
        originalPrice: 0,
        stock: 0,
        status: 1,
        pendingCoverFile: null,
        coverPreviewUrl: '',
    }
}

const form = reactive<AdminProductForm>({
    categoryId: '',
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
    auditStatus: 1,
    status: 1,
    sortOrder: 0,
    skus: [createDefaultSkuRow()],
})

const pagination = computed(() => ({
    pageSize: 10,
    total: products.value.length,
    showSizeChanger: false,
    showQuickJumper: false,
    showTotal: (total: number) => `共 ${total} 条记录`,
}))

const categories = computed(() => meta.value.categories)
const modalTitle = computed(() => (isEditMode.value ? '编辑商品' : '新增商品'))
const displayProductCoverUrl = computed(() => coverPreviewUrl.value || resolveImageUrl(form.productCover))

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
    if (value === 1) return '上架'
    if (value === 0 || value === 2) return '下架'
    return '未知'
}

function getStatusColor(value: number) {
    if (value === 1) return 'blue'
    if (value === 0 || value === 2) return 'default'
    return 'purple'
}

function normalizeProductStatus(value: number) {
    return value === 1 ? 1 : 0
}

function formatCategoryLabel(item: AdminProductMeta['categories'][number]) {
    if (item.categoryLevel === 2 && item.parentName) {
        return `${item.parentName} / ${item.categoryName}`
    }
    return item.categoryName
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

function validateImageFile(file: File, input: HTMLInputElement) {
    if (!file.type.startsWith('image/')) {
        message.warning('请选择图片文件')
        input.value = ''
        return false
    }
    if (file.size > 100 * 1024 * 1024) {
        message.warning('图片不能超过 100M')
        input.value = ''
        return false
    }
    return true
}

function revokeProductCoverPreview() {
    if (coverPreviewUrl.value) {
        URL.revokeObjectURL(coverPreviewUrl.value)
    }
    coverPreviewUrl.value = ''
    pendingCoverFile.value = null
    if (coverFileInputRef.value) {
        coverFileInputRef.value.value = ''
    }
}

function revokeSkuCoverPreview(sku: AdminProductSkuForm) {
    if (sku.coverPreviewUrl) {
        URL.revokeObjectURL(sku.coverPreviewUrl)
    }
    sku.coverPreviewUrl = ''
    sku.pendingCoverFile = null
}

function revokeAllSkuCoverPreviews() {
    form.skus.forEach(revokeSkuCoverPreview)
    skuCoverFileInputRefs.value.forEach((input) => {
        if (input) {
            input.value = ''
        }
    })
    skuCoverFileInputRefs.value = []
}

function openProductCoverPicker() {
    coverFileInputRef.value?.click()
}

function handleProductCoverFileChange(event: Event) {
    const input = event.target as HTMLInputElement
    const file = input.files?.[0]
    if (!file || !validateImageFile(file, input)) {
        return
    }
    revokeProductCoverPreview()
    pendingCoverFile.value = file
    coverPreviewUrl.value = URL.createObjectURL(file)
}

function clearProductCover() {
    revokeProductCoverPreview()
    form.productCover = ''
}

function setSkuCoverFileInputRef(el: unknown, index: number) {
    skuCoverFileInputRefs.value[index] = el instanceof HTMLInputElement ? el : null
}

function openSkuCoverPicker(index: number) {
    skuCoverFileInputRefs.value[index]?.click()
}

function handleSkuCoverFileChange(event: Event, index: number) {
    const input = event.target as HTMLInputElement
    const file = input.files?.[0]
    const sku = form.skus[index]
    if (!sku || !file || !validateImageFile(file, input)) {
        return
    }
    revokeSkuCoverPreview(sku)
    sku.pendingCoverFile = file
    sku.coverPreviewUrl = URL.createObjectURL(file)
}

function getSkuCoverDisplayUrl(sku: AdminProductSkuForm) {
    return sku.coverPreviewUrl || resolveImageUrl(sku.skuCover) || displayProductCoverUrl.value
}

function clearSkuCover(index: number) {
    const sku = form.skus[index]
    if (!sku) {
        return
    }
    revokeSkuCoverPreview(sku)
    sku.skuCover = ''
    const input = skuCoverFileInputRefs.value[index]
    if (input) {
        input.value = ''
    }
}

function resetForm() {
    revokeProductCoverPreview()
    revokeAllSkuCoverPreviews()
    form.categoryId = ''
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
    form.auditStatus = 1
    form.status = 1
    form.sortOrder = 0
    form.skus = [createDefaultSkuRow()]
    currentEditId.value = ''
    isEditMode.value = false
}

function addSkuRow() {
    form.skus.push(createDefaultSkuRow())
}

function removeSkuRow(index: number) {
    if (form.skus.length <= 1) {
        message.warning('至少保留一个商品规格')
        return
    }
    revokeSkuCoverPreview(form.skus[index])
    form.skus.splice(index, 1)
    skuCoverFileInputRefs.value.splice(index, 1)
}

function normalizeSkuRows() {
    form.skus = form.skus.length ? form.skus : [createDefaultSkuRow()]
    if (form.skus.length === 1 && form.skus[0].skuName === '默认规格') {
        if (Number(form.skus[0].price || 0) === 0 && Number(form.minPrice || 0) > 0) {
            form.skus[0].price = Number(form.minPrice || 0)
        }
        if (Number(form.skus[0].originalPrice || 0) === 0 && Number(form.maxPrice || 0) > 0) {
            form.skus[0].originalPrice = Number(form.maxPrice || 0)
        }
        if (Number(form.skus[0].stock || 0) === 0 && Number(form.inventory || 0) > 0) {
            form.skus[0].stock = Number(form.inventory || 0)
        }
    }
    const enabledSkus = form.skus.filter((sku) => sku.status === 1)
    const priceSkus = enabledSkus.length ? enabledSkus : form.skus
    const prices = priceSkus.map((sku) => Number(sku.price || 0))
    form.minPrice = Math.min(...prices)
    form.maxPrice = Math.max(...prices)
    form.inventory = enabledSkus.reduce((total, sku) => total + Number(sku.stock || 0), 0)
}

function validateForm() {
    if (!form.categoryId) {
        throw new Error('请选择分类')
    }
    if (!form.productName.trim()) {
        throw new Error('请输入商品名称')
    }
    normalizeSkuRows()
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
    if (!form.skus.length) {
        throw new Error('请至少添加一个商品规格')
    }
    const hasPurchasableSku = form.skus.some((sku) => sku.status === 1 && Number(sku.stock || 0) > 0)
    form.skus.forEach((sku, index) => {
        if (!sku.skuName.trim()) {
            throw new Error(`第 ${index + 1} 个规格名称不能为空`)
        }
        const price = Number(sku.price || 0)
        const originalPrice = Number(sku.originalPrice || 0)
        const stock = Number(sku.stock || 0)
        const weight = Number(sku.weight || 0)
        if (Number.isNaN(price) || price < 0) {
            throw new Error(`第 ${index + 1} 个规格售价不能小于 0`)
        }
        if (Number.isNaN(originalPrice) || originalPrice < 0) {
            throw new Error(`第 ${index + 1} 个规格原价不能小于 0`)
        }
        if (!Number.isInteger(stock) || stock < 0) {
            throw new Error(`第 ${index + 1} 个规格库存必须是大于等于 0 的整数`)
        }
        if (Number.isNaN(weight) || weight < 0) {
            throw new Error(`第 ${index + 1} 个规格重量不能小于 0`)
        }
    })
    if (form.status === 1 && !hasPurchasableSku) {
        throw new Error('上架商品至少需要一个启用且有库存的规格')
    }
}

async function resolveProductCover() {
    if (!pendingCoverFile.value) {
        return form.productCover.trim()
    }
    return uploadFile('product', pendingCoverFile.value)
}

async function resolveSkuCover(sku: AdminProductSkuForm) {
    if (sku.pendingCoverFile) {
        return uploadFile('product', sku.pendingCoverFile)
    }
    return sku.skuCover.trim()
}

async function buildSavePayload(): Promise<AdminProductSaveRequest> {
    const productCover = await resolveProductCover()
    const skus = await Promise.all(form.skus.map(async (sku) => ({
        skuCode: sku.skuCode || '',
        skuName: sku.skuName.trim(),
        skuCover: await resolveSkuCover(sku),
        specText: sku.specText.trim(),
        materialType: sku.materialType.trim(),
        weight: Number(sku.weight || 0),
        price: Number(sku.price || 0),
        originalPrice: Number(sku.originalPrice || 0),
        stock: Number(sku.stock || 0),
        status: sku.status,
    })))
    return {
        categoryId: form.categoryId,
        productName: form.productName.trim(),
        productSubtitle: form.productSubtitle.trim(),
        productCover,
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
        skus,
    }
}

async function loadProducts() {
    loading.value = true
    try {
        products.value = await getAdminProducts({
            keyword: searchKeyword.value || undefined,
            categoryId: selectedCategory.value || undefined,
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
        revokeProductCoverPreview()
        revokeAllSkuCoverPreviews()
        isEditMode.value = true
        currentEditId.value = productId
        form.categoryId = detail.categoryId
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
        form.status = normalizeProductStatus(detail.status)
        form.sortOrder = detail.sortOrder || 0
        form.skus = detail.skus.length
            ? detail.skus.map((sku) => ({
                skuCode: sku.skuCode,
                skuName: sku.skuName,
                skuCover: sku.skuCover || '',
                specText: sku.specText || '',
                materialType: sku.materialType || '',
                weight: Number(sku.weight || 0),
                price: Number(sku.price || 0),
                originalPrice: Number(sku.originalPrice || 0),
                stock: Number(sku.stock || 0),
                status: sku.status,
            }))
            : [createDefaultSkuRow()]
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
        const payload = await buildSavePayload()
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

async function handleDelete(productId: string) {
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

function handleSearch() {
    loadProducts()
}

function handleCategoryChange() {
    loadProducts()
}

function handleReset() {
    searchKeyword.value = ''
    selectedCategory.value = ''
    loadProducts()
}

onMounted(() => {
    void Promise.all([loadProducts(), loadMeta()])
})

onBeforeUnmount(() => {
    revokeProductCoverPreview()
    revokeAllSkuCoverPreviews()
})
</script>

<template>
    <div class="products-view">
        <div class="page-header">
            <div class="header-left">
                <span class="header-icon">📦</span>
                <div>
                    <h2>商品管理</h2>
                    <p>管理所有商品信息，支持分类筛选</p>
                </div>
            </div>
            <a-button type="primary" size="large" class="add-btn" @click="openCreateModal">
                <PlusOutlined />
                新增商品
            </a-button>
        </div>

        <a-card class="filter-card" :bordered="false">
            <a-space size="large" wrap>
                <a-input-search
                    v-model:value="searchKeyword"
                    placeholder="搜索商品名称..."
                    style="width: 280px"
                    @search="handleSearch"
                >
                    <template #prefix>
                        <SearchOutlined />
                    </template>
                </a-input-search>

                <a-select
                    v-model:value="selectedCategory"
                    placeholder="选择分类"
                    style="width: 160px"
                    @change="handleCategoryChange"
                >
                    <a-select-option value="">全部分类</a-select-option>
                    <a-select-option v-for="category in categories" :key="category.id" :value="category.id">
                        {{ category.categoryName }}
                    </a-select-option>
                </a-select>

                <a-button @click="handleReset">重置</a-button>
            </a-space>
        </a-card>

        <a-card class="table-card" :bordered="false" :loading="loading">
            <a-table
                :columns="columns"
                :data-source="products"
                :pagination="pagination"
                row-key="id"
            >
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'productCover'">
                        <a-image
                            :src="resolveUploadUrl(record.productCover)"
                            :width="60"
                            :height="60"
                            style="object-fit: cover; border-radius: 8px;"
                        />
                    </template>

                    <template v-if="column.key === 'productName'">
                        <strong>{{ record.productName }}</strong>
                    </template>

                    <template v-if="column.key === 'categoryName'">
                        <a-tag color="blue">{{ record.categoryName }}</a-tag>
                    </template>

                    <template v-if="column.key === 'price'">
                        <span style="color: #ef4444; font-weight: 600;">
                            ¥{{ record.minPrice }}
                        </span>
                    </template>

                    <template v-if="column.key === 'status'">
                        <a-tag :color="record.status === 1 ? 'green' : 'default'">
                            {{ record.status === 1 ? '上架' : '下架' }}
                        </a-tag>
                    </template>

                    <template v-if="column.key === 'action'">
                        <a-space>
                            <a-button type="link" size="small" @click="openDetail(record.id)">
                                <EyeOutlined />
                                查看
                            </a-button>
                            <a-button type="link" size="small" @click="openEditModal(record.id)">
                                <EditOutlined />
                                编辑
                            </a-button>
                            <a-popconfirm
                                title="确定删除该商品吗？"
                                @confirm="handleDelete(record.id)"
                            >
                                <a-button type="link" size="small" danger>
                                    <DeleteOutlined />
                                    删除
                                </a-button>
                            </a-popconfirm>
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
                                <a-tag :color="getAuditColor(currentProduct.auditStatus)">
                                    {{ getAuditText(currentProduct.auditStatus) }}
                                </a-tag>
                                <a-tag :color="getStatusColor(currentProduct.status)">
                                    {{ getStatusText(currentProduct.status) }}
                                </a-tag>
                            </a-space>
                        </div>
                    </div>
                    <div class="meta-grid">
                        <span>制作周期：{{ currentProduct.handmadeCycleDays }} 天</span>
                        <span>库存：{{ currentProduct.inventory }}</span>
                        <span>价格区间：¥{{ currentProduct.minPrice }} - ¥{{ currentProduct.maxPrice }}</span>
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
            <a-form-item label="上架状态">
                <a-select v-model:value="form.status" size="large">
                    <a-select-option :value="1">上架</a-select-option>
                    <a-select-option :value="0">下架</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="排序值">
                <a-input-number v-model:value="form.sortOrder" size="large" :controls="false" class="full-width" />
            </a-form-item>
            <div class="full-span sku-section">
                <div class="sku-section-head">
                    <strong>商品规格</strong>
                    <a-button type="primary" ghost @click="addSkuRow">
                        <PlusOutlined />
                        添加规格
                    </a-button>
                </div>
                <div v-for="(sku, index) in form.skus" :key="index" class="sku-row">
                    <a-form-item label="规格名称">
                        <a-input v-model:value="sku.skuName" size="large" placeholder="如 默认规格 / 礼盒装" />
                    </a-form-item>
                    <a-form-item label="规格描述">
                        <a-input v-model:value="sku.specText" size="large" placeholder="如 30cm x 18cm" />
                    </a-form-item>
                    <a-form-item label="材质">
                        <a-input v-model:value="sku.materialType" size="large" />
                    </a-form-item>
                    <a-form-item label="重量">
                        <a-input-number v-model:value="sku.weight" size="large" :min="0" :controls="false" class="full-width" />
                    </a-form-item>
                    <a-form-item label="售价">
                        <a-input-number v-model:value="sku.price" size="large" :min="0" :controls="false" class="full-width" />
                    </a-form-item>
                    <a-form-item label="原价">
                        <a-input-number v-model:value="sku.originalPrice" size="large" :min="0" :controls="false" class="full-width" />
                    </a-form-item>
                    <a-form-item label="库存">
                        <a-input-number v-model:value="sku.stock" size="large" :min="0" :precision="0" :controls="false" class="full-width" />
                    </a-form-item>
                    <a-form-item label="状态">
                        <a-select v-model:value="sku.status" size="large">
                            <a-select-option :value="1">启用</a-select-option>
                            <a-select-option :value="0">停用</a-select-option>
                        </a-select>
                    </a-form-item>
                    <a-form-item label="规格图片" class="sku-cover-field">
                        <div class="image-uploader compact-uploader">
                            <a-image
                                v-if="getSkuCoverDisplayUrl(sku)"
                                class="upload-image-preview small-preview"
                                :src="getSkuCoverDisplayUrl(sku)"
                                :alt="sku.skuName || '规格图片'"
                            />
                            <div v-else class="empty-image-preview small-preview">暂无图片</div>
                            <div class="image-upload-actions">
                                <input
                                    :ref="(el) => setSkuCoverFileInputRef(el, index)"
                                    type="file"
                                    accept="image/*"
                                    hidden
                                    aria-hidden="true"
                                    tabindex="-1"
                                    class="hidden-file-input"
                                    @change="handleSkuCoverFileChange($event, index)"
                                />
                                <a-space wrap>
                                    <a-button @click="openSkuCoverPicker(index)">选择图片</a-button>
                                    <a-button v-if="getSkuCoverDisplayUrl(sku)" danger @click="clearSkuCover(index)">
                                        <DeleteOutlined />
                                        清空图片
                                    </a-button>
                                </a-space>
                                <span v-if="sku.pendingCoverFile">{{ sku.pendingCoverFile.name }}</span>
                                <span v-else-if="sku.skuCover">{{ sku.skuCover }}</span>
                                <span v-else-if="form.productCover">默认使用商品封面</span>
                            </div>
                        </div>
                    </a-form-item>
                    <div class="sku-row-action">
                        <a-button danger @click="removeSkuRow(index)">
                            <DeleteOutlined />
                            删除
                        </a-button>
                    </div>
                </div>
            </div>
            <a-form-item label="商品封面" class="full-span">
                <div class="image-uploader">
                    <a-image
                        v-if="displayProductCoverUrl"
                        class="upload-image-preview"
                        :src="displayProductCoverUrl"
                        :alt="form.productName || '商品封面'"
                    />
                    <div v-else class="empty-image-preview">暂无封面</div>
                    <div class="image-upload-actions">
                        <input
                            ref="coverFileInputRef"
                            type="file"
                            accept="image/*"
                            hidden
                            aria-hidden="true"
                            tabindex="-1"
                            class="hidden-file-input"
                            @change="handleProductCoverFileChange"
                        />
                        <a-space wrap>
                            <a-button @click="openProductCoverPicker">选择图片</a-button>
                            <a-button v-if="displayProductCoverUrl" danger @click="clearProductCover">
                                <DeleteOutlined />
                                清空封面
                            </a-button>
                        </a-space>
                        <span v-if="pendingCoverFile">{{ pendingCoverFile.name }}</span>
                        <span v-else-if="form.productCover">{{ form.productCover }}</span>
                    </div>
                </div>
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

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 16px;
}

.header-icon {
    font-size: 2.5rem;
    line-height: 1;
}

.page-header h2 {
    margin: 0;
    font-size: 1.5rem;
    color: #1e293b;
    font-weight: 700;
}

.page-header p {
    margin: 4px 0 0;
    color: #64748b;
    font-size: 0.9rem;
}

.add-btn {
    border-radius: 12px;
    height: 44px;
    padding: 0 24px;
    font-weight: 600;
}

.filter-card {
    border-radius: 16px;
    background: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.filter-card :deep(.ant-card-body) {
    padding: 20px;
}

.table-card {
    border-radius: 16px;
    background: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table-card :deep(.ant-card-body) {
    padding: 24px;
}

:deep(.ant-table-thead > tr > th) {
    background: #f8fafc;
    font-weight: 600;
    color: #475569;
}

:deep(.ant-table-tbody > tr:hover > td) {
    background: #f1f5f9;
}

:deep(.ant-pagination) {
    margin-top: 20px;
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

.image-uploader {
    display: grid;
    grid-template-columns: 180px minmax(0, 1fr);
    gap: 16px;
    align-items: center;
}

.compact-uploader {
    grid-template-columns: 104px minmax(0, 1fr);
}

.upload-image-preview,
.empty-image-preview {
    width: 180px;
    height: 128px;
    border-radius: 12px;
    overflow: hidden;
}

.upload-image-preview {
    object-fit: cover;
}

.small-preview {
    width: 104px;
    height: 76px;
}

.empty-image-preview {
    display: grid;
    place-items: center;
    border: 1px dashed rgba(30, 64, 175, 0.24);
    color: var(--text-muted);
    background: #f8fafc;
}

.image-upload-actions {
    display: grid;
    gap: 8px;
}

.image-upload-actions span {
    color: var(--text-muted);
    word-break: break-all;
}

.hidden-file-input {
    position: absolute;
    width: 1px;
    height: 1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
}

.sku-section {
    display: grid;
    gap: 14px;
    margin: 4px 0 18px;
}

.sku-section-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
}

.sku-section-head strong {
    color: var(--text-strong);
}

.sku-row {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 0 12px;
    padding: 16px;
    border: 1px solid #e5e7eb;
    border-radius: 16px;
    background: #f8fafc;
}

.sku-cover-field {
    grid-column: span 3;
}

.sku-row-action {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding-top: 30px;
}

@media (max-width: 980px) {
    .meta-grid,
    .image-grid,
    .form-grid,
    .sku-row {
        grid-template-columns: 1fr;
    }

    .sku-cover-field {
        grid-column: auto;
    }

    .image-uploader,
    .compact-uploader {
        grid-template-columns: 1fr;
    }

    .drawer-actions {
        flex-direction: column;
        align-items: stretch;
    }
}
</style>
