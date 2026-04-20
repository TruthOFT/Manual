<script setup lang="ts">
import { DeleteOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons-vue'
import { message, type UploadProps } from 'ant-design-vue'
import { computed, onMounted, reactive, ref, type Ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
    createArtisanProduct,
    getArtisanProductCategories,
    getArtisanProductDetail,
    updateArtisanProduct,
} from '@/api/artisan-center'
import { uploadFile } from '@/api/upload'
import type {
    ArtisanCategoryOption,
    ArtisanProductDetail,
    ArtisanProductSaveRequest,
} from '@/types/artisan-center'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const saving = ref(false)
const uploadingCover = ref(false)
const errorMessage = ref('')
const categories = ref<ArtisanCategoryOption[]>([])

const isEditMode = computed(() => typeof route.params.id === 'string')
const noCategoryAvailable = computed(() => categories.value.length === 0)

const form = reactive<ArtisanProductSaveRequest>({
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
    sortOrder: 0,
    images: [{ imageUrl: '', imageType: 'detail', sortOrder: 1 }],
    materials: [{ materialName: '', materialOrigin: '', materialRatio: '', sortOrder: 1 }],
    skus: [
        {
            skuName: '',
            skuCover: '',
            specText: '',
            materialType: '',
            weight: 0,
            price: 0,
            originalPrice: 0,
            stock: 0,
            status: 1,
        },
    ],
})

function formatCategoryLabel(item: ArtisanCategoryOption) {
    if (item.categoryLevel === 2 && item.parentName) {
        return `${item.parentName} / ${item.categoryName}`
    }
    return item.categoryName
}

function applyProduct(detail: ArtisanProductDetail) {
    form.categoryId = detail.categoryId
    form.productName = detail.productName
    form.productSubtitle = detail.productSubtitle || ''
    form.productCover = detail.productCover
    form.productDesc = detail.productDesc
    form.craftType = detail.craftType || ''
    form.materialDesc = detail.materialDesc || ''
    form.originPlace = detail.originPlace || ''
    form.handmadeCycleDays = detail.handmadeCycleDays
    form.supportCustom = detail.supportCustom
    form.inventory = detail.inventory
    form.minPrice = Number(detail.minPrice || 0)
    form.maxPrice = Number(detail.maxPrice || 0)
    form.sortOrder = detail.sortOrder
    form.images = detail.images.length
        ? detail.images.map((item, index) => ({
              imageUrl: item.imageUrl,
              imageType: item.imageType || 'detail',
              sortOrder: index + 1,
          }))
        : [{ imageUrl: '', imageType: 'detail', sortOrder: 1 }]
    form.materials = detail.materials.length
        ? detail.materials.map((item, index) => ({
              materialName: item.materialName,
              materialOrigin: item.materialOrigin || '',
              materialRatio: item.materialRatio || '',
              sortOrder: index + 1,
          }))
        : [{ materialName: '', materialOrigin: '', materialRatio: '', sortOrder: 1 }]
    form.skus = detail.skus.length
        ? detail.skus.map((item) => ({
              skuName: item.skuName,
              skuCover: item.skuCover || detail.productCover,
              specText: item.specText || '',
              materialType: item.materialType || '',
              weight: Number(item.weight || 0),
              price: Number(item.price || 0),
              originalPrice: Number(item.originalPrice || 0),
              stock: item.stock,
              status: item.status,
          }))
        : [
              {
                  skuName: '',
                  skuCover: detail.productCover,
                  specText: '',
                  materialType: '',
                  weight: 0,
                  price: 0,
                  originalPrice: 0,
                  stock: 0,
                  status: 1,
              },
          ]
}

async function loadEditorData() {
    loading.value = true
    errorMessage.value = ''
    try {
        categories.value = await getArtisanProductCategories()
        if (isEditMode.value && typeof route.params.id === 'string') {
            applyProduct(await getArtisanProductDetail(route.params.id))
        } else if (categories.value.length > 0) {
            const firstCategory = categories.value[0]
            if (firstCategory) {
                form.categoryId = firstCategory.id
            }
        }
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载商品编辑数据失败'
    } finally {
        loading.value = false
    }
}

function addImage() {
    form.images.push({ imageUrl: '', imageType: 'detail', sortOrder: form.images.length + 1 })
}

function removeImage(index: number) {
    if (form.images.length === 1) {
        form.images[0] = { imageUrl: '', imageType: 'detail', sortOrder: 1 }
        return
    }
    form.images.splice(index, 1)
}

function addMaterial() {
    form.materials.push({
        materialName: '',
        materialOrigin: '',
        materialRatio: '',
        sortOrder: form.materials.length + 1,
    })
}

function removeMaterial(index: number) {
    if (form.materials.length === 1) {
        form.materials[0] = { materialName: '', materialOrigin: '', materialRatio: '', sortOrder: 1 }
        return
    }
    form.materials.splice(index, 1)
}

function addSku() {
    form.skus.push({
        skuName: '',
        skuCover: form.productCover,
        specText: '',
        materialType: '',
        weight: 0,
        price: 0,
        originalPrice: 0,
        stock: 0,
        status: 1,
    })
}

function removeSku(index: number) {
    if (form.skus.length === 1) {
        form.skus[0] = {
            skuName: '',
            skuCover: form.productCover,
            specText: '',
            materialType: '',
            weight: 0,
            price: 0,
            originalPrice: 0,
            stock: 0,
            status: 1,
        }
        return
    }
    form.skus.splice(index, 1)
}

async function uploadProductImage(
    file: File,
    onSuccess: (url: string) => void,
    loadingRef?: Ref<boolean>,
) {
    if (loadingRef) {
        loadingRef.value = true
    }
    try {
        const url = await uploadFile('product', file)
        onSuccess(url)
        message.success('图片上传成功')
    } catch (error) {
        message.error(error instanceof Error ? error.message : '图片上传失败')
    } finally {
        if (loadingRef) {
            loadingRef.value = false
        }
    }
    return false
}

const beforeUploadCover: UploadProps['beforeUpload'] = async (file) => {
    return uploadProductImage(file as File, (url) => {
        form.productCover = url
    }, uploadingCover)
}

function beforeUploadDetailImage(index: number): UploadProps['beforeUpload'] {
    return async (file) => {
        return uploadProductImage(file as File, (url) => {
            const image = form.images[index]
            if (image) {
                image.imageUrl = url
            }
        })
    }
}

function beforeUploadSkuCover(index: number): UploadProps['beforeUpload'] {
    return async (file) => {
        return uploadProductImage(file as File, (url) => {
            const sku = form.skus[index]
            if (sku) {
                sku.skuCover = url
            }
        })
    }
}

function normalizeRequest(): ArtisanProductSaveRequest {
    return {
        ...form,
        images: form.images
            .filter((item) => item.imageUrl)
            .map((item, index) => ({ ...item, sortOrder: index + 1 })),
        materials: form.materials
            .filter((item) => item.materialName.trim())
            .map((item, index) => ({ ...item, sortOrder: index + 1 })),
        skus: form.skus.map((item) => ({
            ...item,
            skuCover: item.skuCover || form.productCover,
        })),
    }
}

async function handleSave() {
    if (noCategoryAvailable.value) {
        errorMessage.value = '当前没有可用分类，请先在管理员端启用分类'
        return
    }
    saving.value = true
    errorMessage.value = ''
    try {
        if (isEditMode.value && typeof route.params.id === 'string') {
            await updateArtisanProduct(route.params.id, normalizeRequest())
            message.success('商品已更新')
        } else {
            const productId = await createArtisanProduct(normalizeRequest())
            message.success('商品草稿已创建')
            await router.replace(`/artisan/products/${productId}/edit`)
        }
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '保存商品失败'
    } finally {
        saving.value = false
    }
}

onMounted(() => {
    void loadEditorData()
})
</script>

<template>
    <div class="artisan-view">
        <a-card class="artisan-hero-card" :bordered="false">
            <p class="eyebrow">{{ isEditMode ? '编辑商品' : '新建商品' }}</p>
            <h1>先上传图片，再维护商品资料、材质和 SKU</h1>
            <p class="lead">
                所有图片会先上传到后端 `upload/product`，接口返回 URL 后再写入商品表单。
            </p>
        </a-card>

        <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage" />
        <a-alert
            v-else-if="noCategoryAvailable"
            type="warning"
            show-icon
            message="当前没有可用分类，请先在管理员端启用分类"
        />
        <a-skeleton v-if="loading" active :paragraph="{ rows: 12 }" />

        <a-card v-else class="artisan-panel" :bordered="false">
            <a-form layout="vertical" class="form-grid">
                <a-form-item label="分类">
                    <a-select v-model:value="form.categoryId" size="large" :disabled="noCategoryAvailable">
                        <a-select-option v-for="item in categories" :key="item.id" :value="item.id">
                            {{ formatCategoryLabel(item) }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
                <a-form-item label="商品名称">
                    <a-input v-model:value="form.productName" size="large" />
                </a-form-item>
                <a-form-item label="商品副标题">
                    <a-input v-model:value="form.productSubtitle" size="large" />
                </a-form-item>
                <a-form-item label="工艺类型">
                    <a-input v-model:value="form.craftType" size="large" />
                </a-form-item>
                <a-form-item label="产地">
                    <a-input v-model:value="form.originPlace" size="large" />
                </a-form-item>
                <a-form-item label="制作周期（天）">
                    <a-input-number
                        v-model:value="form.handmadeCycleDays"
                        size="large"
                        :min="0"
                        :controls="false"
                        class="full-width"
                    />
                </a-form-item>
                <a-form-item label="总库存">
                    <a-input-number
                        v-model:value="form.inventory"
                        size="large"
                        :min="0"
                        :controls="false"
                        class="full-width"
                    />
                </a-form-item>
                <a-form-item label="排序值">
                    <a-input-number
                        v-model:value="form.sortOrder"
                        size="large"
                        :controls="false"
                        class="full-width"
                    />
                </a-form-item>
                <a-form-item label="最低价">
                    <a-input-number
                        v-model:value="form.minPrice"
                        size="large"
                        :min="0"
                        :controls="false"
                        class="full-width"
                    />
                </a-form-item>
                <a-form-item label="最高价">
                    <a-input-number
                        v-model:value="form.maxPrice"
                        size="large"
                        :min="0"
                        :controls="false"
                        class="full-width"
                    />
                </a-form-item>
                <a-form-item label="支持定制">
                    <a-switch v-model:checked="form.supportCustom" :checked-value="1" :un-checked-value="0" />
                </a-form-item>
                <a-form-item label="材质说明" class="full-span">
                    <a-input v-model:value="form.materialDesc" size="large" />
                </a-form-item>
                <a-form-item label="商品描述" class="full-span">
                    <a-textarea v-model:value="form.productDesc" :rows="5" />
                </a-form-item>
            </a-form>

            <section class="editor-section">
                <div class="section-head">
                    <h2>商品封面</h2>
                    <a-upload :show-upload-list="false" accept="image/*" :before-upload="beforeUploadCover">
                        <a-button>
                            <UploadOutlined />
                            上传封面
                        </a-button>
                    </a-upload>
                </div>
                <div class="cover-panel">
                    <a-image v-if="form.productCover" :src="form.productCover" :width="220" />
                    <a-empty v-else description="请先上传商品封面" />
                </div>
            </section>

            <section class="editor-section">
                <div class="section-head">
                    <h2>商品图片</h2>
                    <a-button class="manual-ant-btn manual-ant-btn-soft" @click="addImage">
                        <PlusOutlined />
                        新增图片
                    </a-button>
                </div>
                <div class="list-grid">
                    <div v-for="(item, index) in form.images" :key="`image-${index}`" class="line-card image-card">
                        <div class="image-preview">
                            <a-image v-if="item.imageUrl" :src="item.imageUrl" :width="120" />
                            <a-empty v-else description="未上传" />
                        </div>
                        <div class="image-fields">
                            <a-input v-model:value="item.imageType" size="large" placeholder="例如 detail" />
                            <a-space wrap>
                                <a-upload
                                    :show-upload-list="false"
                                    accept="image/*"
                                    :before-upload="beforeUploadDetailImage(index)"
                                >
                                    <a-button>
                                        <UploadOutlined />
                                        上传图片
                                    </a-button>
                                </a-upload>
                                <a-button danger @click="removeImage(index)">
                                    <DeleteOutlined />
                                    删除
                                </a-button>
                            </a-space>
                        </div>
                    </div>
                </div>
            </section>

            <section class="editor-section">
                <div class="section-head">
                    <h2>材质明细</h2>
                    <a-button class="manual-ant-btn manual-ant-btn-soft" @click="addMaterial">
                        <PlusOutlined />
                        新增材质
                    </a-button>
                </div>
                <div class="list-grid">
                    <div
                        v-for="(item, index) in form.materials"
                        :key="`material-${index}`"
                        class="line-card triple"
                    >
                        <a-input v-model:value="item.materialName" size="large" placeholder="材质名称" />
                        <a-input v-model:value="item.materialOrigin" size="large" placeholder="材质来源" />
                        <a-input v-model:value="item.materialRatio" size="large" placeholder="占比说明" />
                        <a-button danger @click="removeMaterial(index)">
                            <DeleteOutlined />
                            删除
                        </a-button>
                    </div>
                </div>
            </section>

            <section class="editor-section">
                <div class="section-head">
                    <h2>SKU 列表</h2>
                    <a-button class="manual-ant-btn manual-ant-btn-soft" @click="addSku">
                        <PlusOutlined />
                        新增 SKU
                    </a-button>
                </div>
                <div class="list-grid">
                    <div v-for="(item, index) in form.skus" :key="`sku-${index}`" class="line-card sku-card">
                        <a-input v-model:value="item.skuName" size="large" placeholder="SKU 名称" />
                        <div class="sku-upload">
                            <a-image v-if="item.skuCover" :src="item.skuCover" :width="108" />
                            <a-upload
                                :show-upload-list="false"
                                accept="image/*"
                                :before-upload="beforeUploadSkuCover(index)"
                            >
                                <a-button>
                                    <UploadOutlined />
                                    上传 SKU 图
                                </a-button>
                            </a-upload>
                        </div>
                        <a-input v-model:value="item.specText" size="large" placeholder="规格描述" />
                        <a-input v-model:value="item.materialType" size="large" placeholder="材质类型" />
                        <a-input-number
                            v-model:value="item.weight"
                            size="large"
                            :min="0"
                            :controls="false"
                            placeholder="重量"
                            class="full-width"
                        />
                        <a-input-number
                            v-model:value="item.price"
                            size="large"
                            :min="0"
                            :controls="false"
                            placeholder="售价"
                            class="full-width"
                        />
                        <a-input-number
                            v-model:value="item.originalPrice"
                            size="large"
                            :min="0"
                            :controls="false"
                            placeholder="原价"
                            class="full-width"
                        />
                        <a-input-number
                            v-model:value="item.stock"
                            size="large"
                            :min="0"
                            :controls="false"
                            placeholder="库存"
                            class="full-width"
                        />
                        <a-button danger @click="removeSku(index)">
                            <DeleteOutlined />
                            删除
                        </a-button>
                    </div>
                </div>
            </section>

            <div class="action-row">
                <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" @click="router.push('/artisan/products')">
                    返回列表
                </a-button>
                <a-button
                    class="manual-ant-btn manual-ant-btn-primary"
                    size="large"
                    :loading="saving || uploadingCover"
                    :disabled="noCategoryAvailable"
                    @click="handleSave"
                >
                    保存商品
                </a-button>
            </div>
        </a-card>
    </div>
</template>

<style scoped>
.artisan-view {
    display: grid;
    gap: 24px;
}

.eyebrow {
    color: var(--coral-deep);
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

h1,
h2 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
}

h1 {
    font-size: clamp(2rem, 4vw, 3.2rem);
}

.lead {
    color: var(--text);
}

.form-grid,
.list-grid {
    display: grid;
    gap: 0 18px;
}

.form-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.full-span {
    grid-column: 1 / -1;
}

.full-width {
    width: 100%;
}

.editor-section {
    margin-top: 28px;
}

.section-head {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    align-items: center;
    margin-bottom: 14px;
}

.list-grid {
    gap: 12px;
}

.line-card,
.cover-panel {
    display: grid;
    gap: 12px;
    padding: 16px;
    border-radius: 18px;
    background: rgba(255, 247, 238, 0.92);
}

.image-card {
    grid-template-columns: auto 1fr;
}

.image-fields,
.sku-upload {
    display: grid;
    gap: 12px;
}

.line-card.triple {
    grid-template-columns: repeat(3, minmax(0, 1fr)) auto;
}

.sku-card {
    grid-template-columns: repeat(4, minmax(0, 1fr)) auto;
    align-items: start;
}

.action-row {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 28px;
}

@media (max-width: 1100px) {
    .form-grid,
    .image-card,
    .line-card.triple,
    .sku-card {
        grid-template-columns: 1fr;
    }

    .section-head,
    .action-row {
        flex-direction: column;
        align-items: stretch;
    }
}
</style>
