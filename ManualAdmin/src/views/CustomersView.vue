<script setup lang="ts">
import { DeleteOutlined, EditOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'

import {
    createAdminCustomer,
    deleteAdminCustomer,
    getAdminCustomerDetail,
    getAdminCustomers,
    updateAdminCustomer,
} from '@/api/customer'
import type { AdminCustomer, AdminCustomerSaveRequest } from '@/types/customer'

const columns = [
    { title: '账号', dataIndex: 'userAccount', key: 'userAccount' },
    { title: '昵称', dataIndex: 'userName', key: 'userName' },
    { title: '手机', dataIndex: 'phone', key: 'phone' },
    { title: '等级', dataIndex: 'customerLevel', key: 'customerLevel' },
    { title: '积分', dataIndex: 'points', key: 'points' },
    { title: '累计消费', dataIndex: 'totalAmount', key: 'totalAmount' },
    { title: '订单数', dataIndex: 'orderCount', key: 'orderCount' },
    { title: '状态', dataIndex: 'userStatus', key: 'userStatus' },
    { title: '操作', key: 'action' },
]

const loading = ref(false)
const saving = ref(false)
const modalVisible = ref(false)
const isEditMode = ref(false)
const currentEditId = ref('')
const keyword = ref('')
const userStatus = ref<number | undefined>()
const customers = ref<AdminCustomer[]>([])

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
            keyword: keyword.value || undefined,
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

function handleDelete(userId: string) {
    Modal.confirm({
        title: '确认删除该顾客？',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
            await deleteAdminCustomer(userId)
            await loadCustomers()
        },
    })
}

onMounted(() => {
    void loadCustomers()
})
</script>

<template>
    <a-card class="panel" :bordered="false" title="顾客管理">
        <template #extra>
            <a-space>
                <a-button @click="loadCustomers">
                    <ReloadOutlined />
                    刷新
                </a-button>
                <a-button type="primary" @click="openCreateModal">
                    <PlusOutlined />
                    新增顾客
                </a-button>
            </a-space>
        </template>

        <div class="toolbar">
            <a-input v-model:value="keyword" size="large" placeholder="搜索账号、昵称、手机或标签" @press-enter="loadCustomers" />
            <a-select v-model:value="userStatus" allow-clear size="large" placeholder="账号状态">
                <a-select-option :value="0">正常</a-select-option>
                <a-select-option :value="1">禁用</a-select-option>
            </a-select>
            <a-button type="primary" size="large" @click="loadCustomers">查询</a-button>
        </div>

        <a-table
            :columns="columns"
            :data-source="customers"
            :loading="loading"
            :pagination="{ pageSize: 8 }"
            row-key="userId"
        >
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'userStatus'">
                    <a-tag :color="record.userStatus === 1 ? 'red' : 'green'">
                        {{ getStatusText(record.userStatus) }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'phone'">
                    {{ record.phone || '-' }}
                </template>
                <template v-else-if="column.key === 'totalAmount'">
                    ￥{{ record.totalAmount }}
                </template>
                <template v-else-if="column.key === 'action'">
                    <a-space>
                        <a-button size="small" @click="openEditModal(record.userId)">
                            <EditOutlined />
                            编辑
                        </a-button>
                        <a-button danger size="small" @click="handleDelete(record.userId)">
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
.panel {
    border-radius: 28px;
    background: var(--surface);
    box-shadow: var(--shadow);
}

.toolbar,
.form-grid {
    display: grid;
    gap: 16px;
}

.toolbar {
    grid-template-columns: minmax(0, 1fr) 220px auto;
    margin-bottom: 18px;
}

.form-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.full-width {
    width: 100%;
}

.full-span {
    grid-column: 1 / -1;
}

@media (max-width: 980px) {
    .toolbar,
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>
