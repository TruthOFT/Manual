<script setup lang="ts">
import { DeleteOutlined, EditOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'

import { createAdminStaff, deleteAdminStaff, getAdminStaff, getAdminStaffDetail, updateAdminStaff } from '@/api/staff'
import type { AdminStaff, AdminStaffSaveRequest } from '@/types/staff'

const columns = [
    { title: '账号', dataIndex: 'userAccount', key: 'userAccount' },
    { title: '店员姓名', dataIndex: 'staffName', key: 'staffName' },
    { title: '员工编号', dataIndex: 'staffNo', key: 'staffNo' },
    { title: '职位', dataIndex: 'positionName', key: 'positionName' },
    { title: '工资', dataIndex: 'salary', key: 'salary' },
    { title: '手机', dataIndex: 'phone', key: 'phone' },
    { title: '在职状态', dataIndex: 'staffStatus', key: 'staffStatus' },
    { title: '账号状态', dataIndex: 'userStatus', key: 'userStatus' },
    { title: '操作', key: 'action' },
]

const loading = ref(false)
const saving = ref(false)
const modalVisible = ref(false)
const isEditMode = ref(false)
const currentEditId = ref('')
const keyword = ref('')
const userStatus = ref<number | undefined>()
const staffStatus = ref<number | undefined>()
const staffList = ref<AdminStaff[]>([])

const form = reactive<AdminStaffSaveRequest>({
    userAccount: '',
    userPassword: '',
    userName: '',
    phone: '',
    email: '',
    gender: 0,
    userStatus: 0,
    staffName: '',
    staffNo: '',
    positionName: '',
    salary: 0,
    entryTime: '',
    staffStatus: 1,
})

const modalTitle = computed(() => (isEditMode.value ? '编辑店员' : '新增店员'))

function resetForm() {
    Object.assign(form, {
        userAccount: '',
        userPassword: '',
        userName: '',
        phone: '',
        email: '',
        gender: 0,
        userStatus: 0,
        staffName: '',
        staffNo: '',
        positionName: '',
        salary: 0,
        entryTime: '',
        staffStatus: 1,
    })
    currentEditId.value = ''
    isEditMode.value = false
}

function buildPayload(): AdminStaffSaveRequest {
    const payload: AdminStaffSaveRequest = {
        userAccount: form.userAccount,
        userPassword: form.userPassword || undefined,
        userName: form.userName,
        phone: form.phone || undefined,
        email: form.email || undefined,
        gender: form.gender,
        userStatus: form.userStatus,
        staffName: form.staffName,
        positionName: form.positionName || undefined,
        salary: form.salary ?? 0,
        entryTime: form.entryTime || undefined,
        staffStatus: form.staffStatus,
    }
    return payload
}

async function loadStaff() {
    loading.value = true
    try {
        staffList.value = await getAdminStaff({
            keyword: keyword.value || undefined,
            userStatus: userStatus.value,
            staffStatus: staffStatus.value,
        })
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载店员列表失败')
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
        const detail = await getAdminStaffDetail(userId)
        isEditMode.value = true
        currentEditId.value = userId
        form.userAccount = detail.userAccount
        form.userPassword = ''
        form.userName = detail.userName || ''
        form.phone = detail.phone || ''
        form.email = detail.email || ''
        form.gender = detail.gender ?? 0
        form.userStatus = detail.userStatus
        form.staffName = detail.staffName
        form.staffNo = detail.staffNo
        form.positionName = detail.positionName || ''
        form.salary = detail.salary ?? 0
        form.entryTime = detail.entryTime || ''
        form.staffStatus = detail.staffStatus
        modalVisible.value = true
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载店员详情失败')
    }
}

async function handleSave() {
    saving.value = true
    try {
        const payload = buildPayload()
        if (isEditMode.value) {
            await updateAdminStaff(currentEditId.value, payload)
        } else {
            await createAdminStaff(payload)
        }
        modalVisible.value = false
        resetForm()
        await loadStaff()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '保存店员失败')
    } finally {
        saving.value = false
    }
}

function handleDelete(userId: string) {
    Modal.confirm({
        title: '确认删除该店员？',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
            await deleteAdminStaff(userId)
            await loadStaff()
        },
    })
}

function formatSalary(value: number | null | undefined) {
    return Number(value ?? 0).toFixed(2)
}

onMounted(() => {
    void loadStaff()
})
</script>

<template>
    <a-card class="panel" :bordered="false" title="店员管理">
        <template #extra>
            <a-space>
                <a-button @click="loadStaff">
                    <ReloadOutlined />
                    刷新
                </a-button>
                <a-button type="primary" @click="openCreateModal">
                    <PlusOutlined />
                    新增店员
                </a-button>
            </a-space>
        </template>

        <div class="toolbar">
            <a-input v-model:value="keyword" size="large" placeholder="搜索账号、姓名、编号或职位" @press-enter="loadStaff" />
            <a-select v-model:value="staffStatus" allow-clear size="large" placeholder="在职状态">
                <a-select-option :value="1">在职</a-select-option>
                <a-select-option :value="0">离职</a-select-option>
            </a-select>
            <a-button type="primary" size="large" @click="loadStaff">查询</a-button>
        </div>

        <a-table :columns="columns" :data-source="staffList" :loading="loading" :pagination="{ pageSize: 8 }" row-key="userId">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'staffStatus'">
                    <a-tag :color="record.staffStatus === 1 ? 'green' : 'default'">
                        {{ record.staffStatus === 1 ? '在职' : '离职' }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'userStatus'">
                    <a-tag :color="record.userStatus === 1 ? 'red' : 'green'">
                        {{ record.userStatus === 1 ? '禁用' : '正常' }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'phone'">
                    {{ record.phone || '-' }}
                </template>
                <template v-else-if="column.key === 'positionName'">
                    {{ record.positionName || '-' }}
                </template>
                <template v-else-if="column.key === 'salary'">
                    {{ formatSalary(record.salary) }}
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
            <a-form-item label="店员姓名">
                <a-input v-model:value="form.staffName" size="large" />
            </a-form-item>
            <a-form-item v-if="isEditMode" label="员工编号">
                <a-input v-model:value="form.staffNo" disabled size="large" />
            </a-form-item>
            <a-form-item label="账号昵称">
                <a-input v-model:value="form.userName" size="large" />
            </a-form-item>
            <a-form-item label="手机号">
                <a-input v-model:value="form.phone" size="large" />
            </a-form-item>
            <a-form-item label="邮箱">
                <a-input v-model:value="form.email" size="large" />
            </a-form-item>
            <a-form-item label="职位">
                <a-input v-model:value="form.positionName" size="large" />
            </a-form-item>
            <a-form-item label="工资">
                <a-input-number
                    v-model:value="form.salary"
                    :min="0"
                    :precision="2"
                    size="large"
                    class="full-width"
                />
            </a-form-item>
            <a-form-item label="入职时间">
                <a-date-picker
                    v-model:value="form.entryTime"
                    show-time
                    value-format="YYYY-MM-DD HH:mm:ss"
                    format="YYYY-MM-DD HH:mm:ss"
                    size="large"
                    class="full-width"
                    placeholder="请选择入职时间"
                />
            </a-form-item>
            <a-form-item label="在职状态">
                <a-select v-model:value="form.staffStatus" size="large">
                    <a-select-option :value="1">在职</a-select-option>
                    <a-select-option :value="0">离职</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="账号状态">
                <a-select v-model:value="form.userStatus" size="large">
                    <a-select-option :value="0">正常</a-select-option>
                    <a-select-option :value="1">禁用</a-select-option>
                </a-select>
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

@media (max-width: 980px) {
    .toolbar,
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>
