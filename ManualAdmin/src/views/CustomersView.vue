<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
    PlusOutlined,
    SearchOutlined,
    EditOutlined,
    DeleteOutlined,
    EyeOutlined,
} from '@ant-design/icons-vue'

import {
    createAdminCustomer,
    deleteAdminCustomer,
    getAdminCustomerDetail,
    getAdminCustomers,
    updateAdminCustomer,
} from '@/api/customer'
import type { AdminCustomer, AdminCustomerSaveRequest } from '@/types/customer'

const columns = [
    { title: '头像', dataIndex: 'avatarUrl', key: 'avatar' },
    { title: '顾客信息', dataIndex: 'userName', key: 'userInfo' },
    { title: '等级', dataIndex: 'customerLevel', key: 'customerLevel' },
    { title: '积分', dataIndex: 'points', key: 'points' },
    { title: '累计消费', dataIndex: 'totalAmount', key: 'totalAmount' },
    { title: '订单数', dataIndex: 'orderCount', key: 'orderCount' },
    { title: '状态', dataIndex: 'userStatus', key: 'status' },
    { title: '操作', key: 'action' },
]

const loading = ref(false)
const saving = ref(false)
const modalVisible = ref(false)
const detailVisible = ref(false)
const detailLoading = ref(false)
const isEditMode = ref(false)
const currentEditId = ref('')
const searchKeyword = ref('')
const userStatus = ref<number | undefined>()
const customers = ref<AdminCustomer[]>([])
const currentCustomer = ref<AdminCustomer | null>(null)

const form = reactive<AdminCustomerSaveRequest>({
    userAccount: '',
    userPassword: '',
    userName: '',
    phone: '',
    email: '',
    gender: 0,
    userStatus: 0,
    customerLevel: 1,
    points: 0,
    totalAmount: 0,
    orderCount: 0,
    preferenceTags: '',
    lastPurchaseTime: '',
})

const modalTitle = computed(() => (isEditMode.value ? '编辑顾客' : '新增顾客'))

const pagination = computed(() => ({
    pageSize: 10,
    total: customers.value.length,
    showSizeChanger: false,
    showQuickJumper: false,
    showTotal: (total: number) => `共 ${total} 条记录`,
}))

function formatAmount(value: number | string | null | undefined) {
    return Number(value ?? 0).toFixed(2)
}

function handleSearch() {
    void loadCustomers()
}

function resetForm() {
    Object.assign(form, {
        userAccount: '',
        userPassword: '',
        userName: '',
        phone: '',
        email: '',
        gender: 0,
        userStatus: 0,
        customerLevel: 1,
        points: 0,
        totalAmount: 0,
        orderCount: 0,
        preferenceTags: '',
        lastPurchaseTime: '',
    })
    currentEditId.value = ''
    isEditMode.value = false
}

function getStatusText(status: number) {
    return status === 1 ? '禁用' : '正常'
}

function getStatusColor(status: number) {
    return status === 1 ? 'red' : 'green'
}

function buildPayload(): AdminCustomerSaveRequest {
    return {
        userAccount: form.userAccount,
        userPassword: form.userPassword || undefined,
        userName: form.userName,
        phone: form.phone || undefined,
        email: form.email || undefined,
        gender: form.gender,
        userStatus: form.userStatus,
        customerLevel: Number(form.customerLevel || 1),
        points: Number(form.points || 0),
        totalAmount: Number(form.totalAmount || 0),
        orderCount: Number(form.orderCount || 0),
        preferenceTags: form.preferenceTags || undefined,
        lastPurchaseTime: form.lastPurchaseTime || undefined,
    }
}

async function loadCustomers() {
    loading.value = true
    try {
        customers.value = await getAdminCustomers({
            keyword: searchKeyword.value || undefined,
            userStatus: userStatus.value,
        })
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载顾客列表失败')
    } finally {
        loading.value = false
    }
}

function openCreateModal() {
    resetForm()
    modalVisible.value = true
}

async function openDetailModal(userId: string) {
    detailVisible.value = true
    detailLoading.value = true
    try {
        currentCustomer.value = await getAdminCustomerDetail(userId)
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载顾客详情失败')
        detailVisible.value = false
    } finally {
        detailLoading.value = false
    }
}

async function openEditModal(userId: string) {
    try {
        const detail = await getAdminCustomerDetail(userId)
        isEditMode.value = true
        currentEditId.value = userId
        form.userAccount = detail.userAccount
        form.userPassword = ''
        form.userName = detail.userName || ''
        form.phone = detail.phone || ''
        form.email = detail.email || ''
        form.gender = detail.gender ?? 0
        form.userStatus = detail.userStatus
        form.customerLevel = detail.customerLevel
        form.points = detail.points
        form.totalAmount = detail.totalAmount
        form.orderCount = detail.orderCount
        form.preferenceTags = detail.preferenceTags || ''
        form.lastPurchaseTime = detail.lastPurchaseTime || ''
        modalVisible.value = true
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载顾客详情失败')
    }
}

async function handleSave() {
    saving.value = true
    try {
        const payload = buildPayload()
        if (isEditMode.value) {
            await updateAdminCustomer(currentEditId.value, payload)
        } else {
            await createAdminCustomer(payload)
        }
        modalVisible.value = false
        resetForm()
        await loadCustomers()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '保存顾客失败')
    } finally {
        saving.value = false
    }
}

async function handleDelete(userId: string) {
    try {
        await deleteAdminCustomer(userId)
        if (currentCustomer.value?.userId === userId) {
            currentCustomer.value = null
            detailVisible.value = false
        }
        await loadCustomers()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '删除顾客失败')
    }
}

onMounted(() => {
    void loadCustomers()
})
</script>

<template>
    <div class="customers-view">
        <div class="page-header">
            <div class="header-left">
                <span class="header-icon">👥</span>
                <div>
                    <h2>顾客管理</h2>
                    <p>管理顾客信息，分析顾客需求</p>
                </div>
            </div>
            <a-button type="primary" size="large" class="add-btn" @click="openCreateModal">
                <PlusOutlined />
                新增顾客
            </a-button>
        </div>

        <a-card class="filter-card" :bordered="false">
            <a-space size="large" wrap>
                <a-input-search
                    v-model:value="searchKeyword"
                    placeholder="搜索顾客名称或手机号..."
                    style="width: 320px"
                    @search="handleSearch"
                >
                    <template #prefix>
                        <SearchOutlined />
                    </template>
                </a-input-search>
            </a-space>
        </a-card>

        <a-card class="table-card" :bordered="false" :loading="loading">
            <a-table
                :columns="columns"
                :data-source="customers"
                :pagination="pagination"
                row-key="userId"
            >
                <template #bodyCell="{ column, record }">
                    <template v-if="column.key === 'avatar'">
                        <a-avatar :size="48">
                            {{ (record.userName || record.userAccount)?.slice(0, 1) }}
                        </a-avatar>
                    </template>

                    <template v-if="column.key === 'userInfo'">
                        <div>
                            <strong>{{ record.userName || record.userAccount }}</strong>
                            <p style="margin: 4px 0 0; color: #64748b; font-size: 0.85rem;">
                                {{ record.phone || '暂无手机号' }}
                            </p>
                        </div>
                    </template>

                    <template v-if="column.key === 'status'">
                        <a-tag :color="getStatusColor(record.userStatus)">
                            {{ getStatusText(record.userStatus) }}
                        </a-tag>
                    </template>

                    <template v-if="column.key === 'totalAmount'">
                        ￥{{ formatAmount(record.totalAmount) }}
                    </template>

                    <template v-if="column.key === 'action'">
                        <a-space>
                            <a-button type="link" size="small" @click="openDetailModal(record.userId)">
                                <EyeOutlined />
                                查看
                            </a-button>
                            <a-button type="link" size="small" @click="openEditModal(record.userId)">
                                <EditOutlined />
                                编辑
                            </a-button>
                            <a-popconfirm
                                title="确定删除该顾客吗？"
                                @confirm="handleDelete(record.userId)"
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

    <a-modal
        v-model:open="detailVisible"
        title="顾客详情"
        :footer="null"
        width="760px"
    >
        <a-skeleton v-if="detailLoading" active :paragraph="{ rows: 8 }" />
        <div v-else-if="currentCustomer" class="detail-grid">
            <div class="detail-head">
                <a-avatar :size="64">
                    {{ (currentCustomer.userName || currentCustomer.userAccount)?.slice(0, 1) }}
                </a-avatar>
                <div>
                    <h3>{{ currentCustomer.userName || currentCustomer.userAccount }}</h3>
                    <p>{{ currentCustomer.phone || '暂无手机号' }}</p>
                    <a-tag :color="getStatusColor(currentCustomer.userStatus)">
                        {{ getStatusText(currentCustomer.userStatus) }}
                    </a-tag>
                </div>
            </div>
            <a-descriptions bordered :column="2" size="small">
                <a-descriptions-item label="账号">{{ currentCustomer.userAccount }}</a-descriptions-item>
                <a-descriptions-item label="邮箱">{{ currentCustomer.email || '-' }}</a-descriptions-item>
                <a-descriptions-item label="等级">{{ currentCustomer.customerLevel }}</a-descriptions-item>
                <a-descriptions-item label="积分">{{ currentCustomer.points }}</a-descriptions-item>
                <a-descriptions-item label="累计消费">￥{{ formatAmount(currentCustomer.totalAmount) }}</a-descriptions-item>
                <a-descriptions-item label="订单数">{{ currentCustomer.orderCount }}</a-descriptions-item>
                <a-descriptions-item label="偏好标签" :span="2">
                    {{ currentCustomer.preferenceTags || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="最近购买" :span="2">
                    {{ currentCustomer.lastPurchaseTime || '-' }}
                </a-descriptions-item>
            </a-descriptions>
            <div class="detail-actions">
                <a-button @click="openEditModal(currentCustomer.userId)">
                    <EditOutlined />
                    编辑
                </a-button>
            </div>
        </div>
    </a-modal>

    <a-modal
        v-model:open="modalVisible"
        :title="modalTitle"
        :confirm-loading="saving"
        width="820px"
        ok-text="保存"
        cancel-text="取消"
        @ok="handleSave"
        @cancel="resetForm"
    >
        <a-form layout="vertical" class="form-grid">
            <a-form-item label="登录账号">
                <a-input v-model:value="form.userAccount" :disabled="isEditMode" size="large" />
            </a-form-item>
            <a-form-item :label="isEditMode ? '重置密码（可选）' : '登录密码'">
                <a-input-password v-model:value="form.userPassword" size="large" />
            </a-form-item>
            <a-form-item label="顾客昵称">
                <a-input v-model:value="form.userName" size="large" />
            </a-form-item>
            <a-form-item label="手机号">
                <a-input v-model:value="form.phone" size="large" />
            </a-form-item>
            <a-form-item label="邮箱">
                <a-input v-model:value="form.email" size="large" />
            </a-form-item>
            <a-form-item label="顾客等级">
                <a-input-number v-model:value="form.customerLevel" :min="1" size="large" class="full-width" />
            </a-form-item>
            <a-form-item label="积分">
                <a-input-number v-model:value="form.points" :min="0" size="large" class="full-width" />
            </a-form-item>
            <a-form-item label="累计消费">
                <a-input-number v-model:value="form.totalAmount" :min="0" size="large" class="full-width" />
            </a-form-item>
            <a-form-item label="订单数">
                <a-input-number v-model:value="form.orderCount" :min="0" size="large" class="full-width" />
            </a-form-item>
            <a-form-item label="账号状态">
                <a-select v-model:value="form.userStatus" size="large">
                    <a-select-option :value="0">正常</a-select-option>
                    <a-select-option :value="1">禁用</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="偏好标签" class="full-span">
                <a-input v-model:value="form.preferenceTags" size="large" />
            </a-form-item>
            <a-form-item label="最近购买时间" class="full-span">
                <a-input v-model:value="form.lastPurchaseTime" size="large" placeholder="yyyy-MM-dd HH:mm:ss" />
            </a-form-item>
        </a-form>
    </a-modal>
</template>

<style scoped>
.customers-view {
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
    gap: 18px;
}

.detail-head {
    display: grid;
    grid-template-columns: auto 1fr;
    gap: 14px;
    align-items: center;
}

.detail-head h3 {
    margin: 0 0 4px;
    color: #1e293b;
}

.detail-head p {
    margin: 0 0 8px;
    color: #64748b;
}

.detail-actions {
    display: flex;
    justify-content: flex-end;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0 16px;
}

.full-width {
    width: 100%;
}

.full-span {
    grid-column: 1 / -1;
}

@media (max-width: 980px) {
    .form-grid {
        grid-template-columns: 1fr;
    }

    .full-span {
        grid-column: auto;
    }
}
</style>
