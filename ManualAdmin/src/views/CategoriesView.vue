<script setup lang="ts">
import { DeleteOutlined, EditOutlined, EyeOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { message, Modal, type FormInstance } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'

import {
    createAdminCategory,
    deleteAdminCategory,
    getAdminCategories,
    getAdminCategoryDetail,
    updateAdminCategory,
} from '@/api/category'
import { API_CONTEXT_PATH, BASE_URL } from '@/api/request'
import { uploadFile } from '@/api/upload'
import type { AdminCategory, AdminCategorySaveRequest } from '@/types/category'

const columns = [
    { title: '分类名称', dataIndex: 'categoryName', key: 'categoryName' },
    { title: '层级', dataIndex: 'categoryLevel', key: 'categoryLevel' },
    { title: '父分类', dataIndex: 'parentName', key: 'parentName' },
    { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder' },
    { title: '首页展示', dataIndex: 'isEnable', key: 'isEnable' },
    { title: '商品数', dataIndex: 'productCount', key: 'productCount' },
    { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime' },
    { title: '操作', key: 'action' },
]

const formRef = ref<FormInstance>()
const loading = ref(false)
const saving = ref(false)
const modalVisible = ref(false)
const detailModalVisible = ref(false)
const detailLoading = ref(false)
const isEditMode = ref(false)
const currentEditId = ref('')
const currentCategory = ref<AdminCategory | null>(null)
const categories = ref<AdminCategory[]>([])
const displaySavingIds = ref(new Set<string>())
const iconFileInputRef = ref<HTMLInputElement>()
const pendingIconFile = ref<File | null>(null)
const iconPreviewUrl = ref('')

const form = reactive({
    parentId: undefined as string | undefined,
    categoryName: '',
    categoryIcon: '',
    categoryDesc: '',
    categoryLevel: 1,
    sortOrder: 0,
    isEnable: 1,
})

const modalTitle = computed(() => (isEditMode.value ? '编辑分类' : '新增分类'))
const parentOptions = computed(() =>
    categories.value.filter((item) => item.categoryLevel === 1 && item.id !== currentEditId.value),
)
const displayIconUrl = computed(() => iconPreviewUrl.value || resolveImageUrl(form.categoryIcon))

function resetForm() {
    revokeIconPreview()
    form.parentId = undefined
    form.categoryName = ''
    form.categoryIcon = ''
    form.categoryDesc = ''
    form.categoryLevel = 1
    form.sortOrder = 0
    form.isEnable = 1
    currentEditId.value = ''
    isEditMode.value = false
    formRef.value?.clearValidate()
}

function resolveImageUrl(url?: string | null) {
    if (!url) {
        return ''
    }
    const value = String(url).trim()
    if (!value) {
        return ''
    }
    if (value.startsWith('blob:') || value.startsWith('data:')) {
        return value
    }
    if (value.startsWith('http://') || value.startsWith('https://')) {
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

function revokeIconPreview() {
    if (iconPreviewUrl.value) {
        URL.revokeObjectURL(iconPreviewUrl.value)
    }
    iconPreviewUrl.value = ''
    pendingIconFile.value = null
    if (iconFileInputRef.value) {
        iconFileInputRef.value.value = ''
    }
}

function openIconPicker() {
    iconFileInputRef.value?.click()
}

function handleIconFileChange(event: Event) {
    const input = event.target as HTMLInputElement
    const file = input.files?.[0]
    if (!file) {
        return
    }
    if (!file.type.startsWith('image/')) {
        message.warning('请选择图片文件')
        input.value = ''
        return
    }
    if (file.size > 100 * 1024 * 1024) {
        message.warning('图片不能超过 100M')
        input.value = ''
        return
    }
    if (iconPreviewUrl.value) {
        URL.revokeObjectURL(iconPreviewUrl.value)
    }
    pendingIconFile.value = file
    iconPreviewUrl.value = URL.createObjectURL(file)
}

async function resolveCategoryIcon() {
    if (!pendingIconFile.value) {
        return form.categoryIcon || undefined
    }
    return uploadFile('category', pendingIconFile.value)
}

function getLevelText(level: number) {
    return level === 2 ? '二级分类' : '一级分类'
}

function getLevelColor(level: number) {
    return level === 2 ? 'gold' : 'blue'
}

function getStatusText(status: number) {
    return status === 1 ? '展示' : '隐藏'
}

function getStatusColor(status: number) {
    return status === 1 ? 'green' : 'red'
}

function getParentText(category: AdminCategory) {
    return category.parentName || (category.categoryLevel === 1 ? '无' : '-')
}

function setDisplaySaving(id: string, savingStatus: boolean) {
    const nextIds = new Set(displaySavingIds.value)
    if (savingStatus) {
        nextIds.add(id)
    } else {
        nextIds.delete(id)
    }
    displaySavingIds.value = nextIds
}

function buildSavePayload(category: AdminCategory, isEnable = category.isEnable): AdminCategorySaveRequest {
    return {
        parentId: category.categoryLevel === 2 ? category.parentId || undefined : undefined,
        categoryName: category.categoryName,
        categoryIcon: category.categoryIcon || undefined,
        categoryDesc: category.categoryDesc || undefined,
        categoryLevel: category.categoryLevel,
        sortOrder: category.sortOrder ?? 0,
        isEnable,
    }
}

function handleLevelChange(level: number) {
    if (level === 1) {
        form.parentId = undefined
    }
}

async function loadCategories() {
    loading.value = true
    try {
        categories.value = await getAdminCategories()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载分类列表失败')
    } finally {
        loading.value = false
    }
}

function openCreateModal() {
    resetForm()
    modalVisible.value = true
}

async function openEditModal(id: string) {
    try {
        const detail = await getAdminCategoryDetail(id)
        isEditMode.value = true
        currentEditId.value = id
        form.parentId = detail.parentId || undefined
        form.categoryName = detail.categoryName
        form.categoryIcon = detail.categoryIcon || ''
        revokeIconPreview()
        form.categoryDesc = detail.categoryDesc || ''
        form.categoryLevel = detail.categoryLevel
        form.sortOrder = detail.sortOrder ?? 0
        form.isEnable = detail.isEnable ?? 1
        modalVisible.value = true
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载分类详情失败')
    }
}

async function openDetailModal(id: string) {
    detailModalVisible.value = true
    detailLoading.value = true
    currentCategory.value = null
    try {
        currentCategory.value = await getAdminCategoryDetail(id)
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载分类详情失败')
        detailModalVisible.value = false
    } finally {
        detailLoading.value = false
    }
}

async function handleDisplayChange(record: AdminCategory, checked: boolean) {
    const nextStatus = checked ? 1 : 0
    setDisplaySaving(record.id, true)
    try {
        await updateAdminCategory(record.id, buildSavePayload(record, nextStatus))
        record.isEnable = nextStatus
        if (currentCategory.value?.id === record.id) {
            currentCategory.value = {
                ...currentCategory.value,
                isEnable: nextStatus,
            }
        }
    } catch (error) {
        message.error(error instanceof Error ? error.message : '更新展示状态失败')
    } finally {
        setDisplaySaving(record.id, false)
    }
}

async function handleSave() {
    try {
        await formRef.value?.validate()
    } catch {
        return
    }
    saving.value = true
    try {
        const payload: AdminCategorySaveRequest = {
            parentId: form.categoryLevel === 2 ? form.parentId : undefined,
            categoryName: form.categoryName,
            categoryIcon: await resolveCategoryIcon(),
            categoryDesc: form.categoryDesc || undefined,
            categoryLevel: form.categoryLevel,
            sortOrder: form.sortOrder,
            isEnable: form.isEnable,
        }
        if (isEditMode.value) {
            await updateAdminCategory(currentEditId.value, payload)
        } else {
            await createAdminCategory(payload)
        }
        modalVisible.value = false
        resetForm()
        await loadCategories()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '保存分类失败')
    } finally {
        saving.value = false
    }
}

function handleDelete(id: string) {
    Modal.confirm({
        title: '确认删除该分类？',
        content: '如果该分类下还有子分类或商品，后端会阻止删除。',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
            try {
                await deleteAdminCategory(id)
                await loadCategories()
            } catch (error) {
                message.error(error instanceof Error ? error.message : '删除分类失败')
            }
        },
    })
}

onMounted(() => {
    void loadCategories()
})
</script>

<template>
    <a-card class="panel" :bordered="false" title="分类管理">
        <template #extra>
            <a-space>
                <a-button @click="loadCategories">
                    <ReloadOutlined />
                    刷新
                </a-button>
                <a-button type="primary" @click="openCreateModal">
                    <PlusOutlined />
                    新增分类
                </a-button>
            </a-space>
        </template>

        <a-table
            :columns="columns"
            :data-source="categories"
            :loading="loading"
            :pagination="{ pageSize: 10 }"
            row-key="id"
        >
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'categoryName'">
                    <div class="name-cell">
                        <strong>{{ record.categoryName }}</strong>
                        <span>{{ record.categoryDesc || '暂无描述' }}</span>
                    </div>
                </template>
                <template v-else-if="column.key === 'categoryLevel'">
                    <a-tag :color="getLevelColor(record.categoryLevel)">
                        {{ getLevelText(record.categoryLevel) }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'parentName'">
                    {{ record.parentName || '-' }}
                </template>
                <template v-else-if="column.key === 'isEnable'">
                    <a-switch
                        :checked="record.isEnable === 1"
                        :loading="displaySavingIds.has(record.id)"
                        checked-children="展示"
                        un-checked-children="隐藏"
                        @change="(checked: boolean) => handleDisplayChange(record, checked)"
                    />
                </template>
                <template v-else-if="column.key === 'productCount'">
                    {{ record.productCount ?? 0 }}
                </template>
                <template v-else-if="column.key === 'updateTime'">
                    {{ record.updateTime || '-' }}
                </template>
                <template v-else-if="column.key === 'action'">
                    <a-space>
                        <a-button size="small" @click="openDetailModal(record.id)">
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
                    </a-space>
                </template>
            </template>
        </a-table>
    </a-card>

    <a-modal
        v-model:open="modalVisible"
        :title="modalTitle"
        :confirm-loading="saving"
        ok-text="保存"
        cancel-text="取消"
        width="720px"
        @ok="handleSave"
        @cancel="resetForm"
    >
        <a-form ref="formRef" :model="form" layout="vertical" class="form-grid">
            <a-form-item label="分类名称" name="categoryName" :rules="[{ required: true, message: '请输入分类名称' }]">
                <a-input v-model:value="form.categoryName" size="large" />
            </a-form-item>

            <a-form-item label="分类层级" name="categoryLevel" :rules="[{ required: true, message: '请选择分类层级' }]">
                <a-select v-model:value="form.categoryLevel" size="large" @change="handleLevelChange">
                    <a-select-option :value="1">一级分类</a-select-option>
                    <a-select-option :value="2">二级分类</a-select-option>
                </a-select>
            </a-form-item>

            <a-form-item
                label="父分类"
                name="parentId"
                :rules="form.categoryLevel === 2 ? [{ required: true, message: '请选择父分类' }] : []"
            >
                <a-select
                    v-model:value="form.parentId"
                    size="large"
                    placeholder="一级分类无需选择父分类"
                    :disabled="form.categoryLevel === 1"
                    allow-clear
                >
                    <a-select-option v-for="item in parentOptions" :key="item.id" :value="item.id">
                        {{ item.categoryName }}
                    </a-select-option>
                </a-select>
            </a-form-item>

            <a-form-item label="排序值" name="sortOrder">
                <a-input-number v-model:value="form.sortOrder" size="large" :controls="false" class="full-width" />
            </a-form-item>

            <a-form-item label="首页展示" name="isEnable">
                <a-select v-model:value="form.isEnable" size="large">
                    <a-select-option :value="1">展示</a-select-option>
                    <a-select-option :value="0">隐藏</a-select-option>
                </a-select>
            </a-form-item>

            <a-form-item label="分类图标" name="categoryIcon" class="span-2">
                <div class="icon-uploader">
                    <a-image
                        v-if="displayIconUrl"
                        class="category-icon-edit-preview"
                        :src="displayIconUrl"
                        :alt="form.categoryName || '分类图标'"
                    />
                    <div v-else class="empty-icon-preview">暂无图标</div>
                    <div class="icon-upload-actions">
                        <input
                            ref="iconFileInputRef"
                            type="file"
                            accept="image/*"
                            hidden
                            aria-hidden="true"
                            tabindex="-1"
                            class="hidden-file-input"
                            @change="handleIconFileChange"
                        />
                        <a-space wrap>
                            <a-button @click="openIconPicker">选择图片</a-button>
                            <a-button v-if="pendingIconFile" @click="revokeIconPreview">取消选择</a-button>
                        </a-space>
                        <span v-if="pendingIconFile">{{ pendingIconFile.name }}</span>
                        <span v-else-if="form.categoryIcon">{{ form.categoryIcon }}</span>
                    </div>
                </div>
            </a-form-item>

            <a-form-item label="分类描述" name="categoryDesc" class="span-2">
                <a-textarea v-model:value="form.categoryDesc" :rows="4" />
            </a-form-item>
        </a-form>
    </a-modal>

    <a-modal
        v-model:open="detailModalVisible"
        title="分类详情"
        :footer="null"
        width="680px"
    >
        <a-skeleton v-if="detailLoading" active :paragraph="{ rows: 8 }" />

        <a-descriptions v-else-if="currentCategory" bordered :column="1" size="middle">
            <a-descriptions-item label="分类名称">
                {{ currentCategory.categoryName }}
            </a-descriptions-item>
            <a-descriptions-item label="分类层级">
                <a-tag :color="getLevelColor(currentCategory.categoryLevel)">
                    {{ getLevelText(currentCategory.categoryLevel) }}
                </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="父分类">
                {{ getParentText(currentCategory) }}
            </a-descriptions-item>
            <a-descriptions-item label="首页展示">
                <a-tag :color="getStatusColor(currentCategory.isEnable)">
                    {{ getStatusText(currentCategory.isEnable) }}
                </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="排序值">
                {{ currentCategory.sortOrder ?? 0 }}
            </a-descriptions-item>
            <a-descriptions-item label="商品数">
                {{ currentCategory.productCount ?? 0 }}
            </a-descriptions-item>
            <a-descriptions-item label="分类图标">
                <a-image
                    v-if="currentCategory.categoryIcon"
                    class="category-icon-preview"
                    :src="resolveImageUrl(currentCategory.categoryIcon)"
                    :alt="currentCategory.categoryName"
                />
                <span v-else>-</span>
            </a-descriptions-item>
            <a-descriptions-item label="分类描述">
                {{ currentCategory.categoryDesc || '暂无描述' }}
            </a-descriptions-item>
            <a-descriptions-item label="创建时间">
                {{ currentCategory.createTime || '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="更新时间">
                {{ currentCategory.updateTime || '-' }}
            </a-descriptions-item>
        </a-descriptions>
    </a-modal>
</template>

<style scoped>
.panel {
    border-radius: 28px;
    background: var(--surface);
    box-shadow: var(--shadow);
}

.name-cell {
    display: grid;
    gap: 4px;
}

.name-cell strong {
    color: var(--text-strong);
}

.name-cell span {
    color: var(--text-muted);
    font-size: 0.92rem;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
}

.span-2 {
    grid-column: 1 / -1;
}

.full-width {
    width: 100%;
}

.category-icon-preview {
    max-width: 180px;
    max-height: 120px;
    object-fit: cover;
    border-radius: 8px;
}

.icon-uploader {
    display: grid;
    grid-template-columns: 140px minmax(0, 1fr);
    gap: 16px;
    align-items: center;
}

.category-icon-edit-preview {
    width: 140px;
    height: 100px;
    object-fit: cover;
    border-radius: 10px;
    overflow: hidden;
}

.empty-icon-preview {
    display: grid;
    place-items: center;
    width: 140px;
    height: 100px;
    border: 1px dashed rgba(30, 64, 175, 0.24);
    border-radius: 10px;
    color: var(--text-muted);
    background: #f8fafc;
}

.icon-upload-actions {
    display: grid;
    gap: 8px;
}

.icon-upload-actions span {
    color: var(--text-muted);
    word-break: break-all;
}

.hidden-file-input {
    position: absolute;
    width: 1px;
    height: 1px;
    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    clip-path: inset(50%);
    white-space: nowrap;
}

@media (max-width: 900px) {
    .form-grid {
        grid-template-columns: 1fr;
    }

    .span-2 {
        grid-column: auto;
    }

    .icon-uploader {
        grid-template-columns: 1fr;
    }
}
</style>
