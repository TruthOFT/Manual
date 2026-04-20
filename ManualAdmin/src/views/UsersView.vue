<script setup lang="ts">
import { EditOutlined, PlusOutlined, ReloadOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'

import { createAdminUser, deleteAdminUser, getAdminUserDetail, getAdminUsers, updateAdminUser } from '@/api/user'
import type { AdminUser, AdminUserCreateRequest, AdminUserUpdateRequest } from '@/types/user'

const columns = [
    { title: '用户账号', dataIndex: 'userAccount', key: 'userAccount' },
    { title: '昵称', dataIndex: 'userName', key: 'userName' },
    { title: '角色', dataIndex: 'userRole', key: 'userRole' },
    { title: '状态', dataIndex: 'userStatus', key: 'userStatus' },
    { title: '手机号', dataIndex: 'phone', key: 'phone' },
    { title: '最近登录', dataIndex: 'lastLoginTime', key: 'lastLoginTime' },
    { title: '操作', key: 'action' },
]

const loading = ref(false)
const saving = ref(false)
const modalVisible = ref(false)
const isEditMode = ref(false)
const currentEditId = ref('')
const keyword = ref('')
const userRole = ref<string | undefined>()
const userStatus = ref<number | undefined>()
const users = ref<AdminUser[]>([])

const form = reactive({
    userAccount: '',
    userPassword: '',
    userName: '',
    avatarUrl: '',
    phone: '',
    email: '',
    gender: 0,
    userRole: 'user',
    userStatus: 0,
    balance: 0,
})

const modalTitle = computed(() => (isEditMode.value ? '编辑用户' : '新增用户'))

function resetForm() {
    form.userAccount = ''
    form.userPassword = ''
    form.userName = ''
    form.avatarUrl = ''
    form.phone = ''
    form.email = ''
    form.gender = 0
    form.userRole = 'user'
    form.userStatus = 0
    form.balance = 0
    currentEditId.value = ''
    isEditMode.value = false
}

function getRoleText(role: string) {
    if (role === 'admin') return '管理员'
    if (role === 'artisan') return '匠人'
    return '普通用户'
}

function getRoleColor(role: string) {
    if (role === 'admin') return 'geekblue'
    if (role === 'artisan') return 'gold'
    return 'green'
}

function getStatusText(status: number) {
    return status === 1 ? '禁用' : '正常'
}

function getStatusColor(status: number) {
    return status === 1 ? 'red' : 'green'
}

async function loadUsers() {
    loading.value = true
    try {
        users.value = await getAdminUsers({
            keyword: keyword.value || undefined,
            userRole: userRole.value || undefined,
            userStatus: userStatus.value,
        })
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载用户列表失败')
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
        const detail = await getAdminUserDetail(id)
        isEditMode.value = true
        currentEditId.value = id
        form.userAccount = detail.userAccount
        form.userPassword = ''
        form.userName = detail.userName || ''
        form.avatarUrl = detail.avatarUrl || ''
        form.phone = detail.phone || ''
        form.email = detail.email || ''
        form.gender = detail.gender ?? 0
        form.userRole = detail.userRole
        form.userStatus = detail.userStatus
        form.balance = Number(detail.balance || 0)
        modalVisible.value = true
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载用户详情失败')
    }
}

async function handleSave() {
    saving.value = true
    try {
        if (isEditMode.value) {
            const payload: AdminUserUpdateRequest = {
                userPassword: form.userPassword || undefined,
                userName: form.userName,
                avatarUrl: form.avatarUrl || undefined,
                phone: form.phone || undefined,
                email: form.email || undefined,
                gender: form.gender,
                userRole: form.userRole,
                userStatus: form.userStatus,
                balance: form.balance,
            }
            await updateAdminUser(currentEditId.value, payload)
            message.success('用户已更新')
        } else {
            const payload: AdminUserCreateRequest = {
                userAccount: form.userAccount,
                userPassword: form.userPassword,
                userName: form.userName,
                avatarUrl: form.avatarUrl || undefined,
                phone: form.phone || undefined,
                email: form.email || undefined,
                gender: form.gender,
                userRole: form.userRole,
                userStatus: form.userStatus,
                balance: form.balance,
            }
            await createAdminUser(payload)
            message.success('用户已创建')
        }
        modalVisible.value = false
        resetForm()
        await loadUsers()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '保存用户失败')
    } finally {
        saving.value = false
    }
}

function handleDelete(id: string) {
    Modal.confirm({
        title: '确认删除该用户？',
        content: '删除后会执行逻辑删除，用户将不再出现在管理列表中。',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
            try {
                await deleteAdminUser(id)
                message.success('用户已删除')
                await loadUsers()
            } catch (error) {
                message.error(error instanceof Error ? error.message : '删除用户失败')
            }
        },
    })
}

onMounted(() => {
    void loadUsers()
})
</script>

<template>
    <a-card class="panel" :bordered="false" title="用户管理">
        <template #extra>
            <a-space>
                <a-button @click="loadUsers">
                    <ReloadOutlined />
                    刷新
                </a-button>
                <a-button type="primary" @click="openCreateModal">
                    <PlusOutlined />
                    新增用户
                </a-button>
            </a-space>
        </template>

        <div class="toolbar">
            <a-input v-model:value="keyword" size="large" placeholder="搜索账号或昵称" @press-enter="loadUsers" />
            <a-select v-model:value="userRole" allow-clear size="large" placeholder="筛选角色">
                <a-select-option value="admin">管理员</a-select-option>
                <a-select-option value="artisan">匠人</a-select-option>
                <a-select-option value="user">普通用户</a-select-option>
            </a-select>
            <a-select v-model:value="userStatus" allow-clear size="large" placeholder="筛选状态">
                <a-select-option :value="0">正常</a-select-option>
                <a-select-option :value="1">禁用</a-select-option>
            </a-select>
            <a-button type="primary" size="large" @click="loadUsers">查询</a-button>
        </div>

        <a-table :columns="columns" :data-source="users" :loading="loading" :pagination="{ pageSize: 8 }" row-key="id">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'userRole'">
                    <a-tag :color="getRoleColor(record.userRole)">
                        {{ getRoleText(record.userRole) }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'userStatus'">
                    <a-tag :color="getStatusColor(record.userStatus)">
                        {{ getStatusText(record.userStatus) }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'phone'">
                    {{ record.phone || '-' }}
                </template>
                <template v-else-if="column.key === 'lastLoginTime'">
                    {{ record.lastLoginTime || '-' }}
                </template>
                <template v-else-if="column.key === 'action'">
                    <a-space>
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
        width="760px"
        @ok="handleSave"
        @cancel="resetForm"
    >
        <a-form layout="vertical" class="form-grid">
            <a-form-item label="用户账号">
                <a-input v-model:value="form.userAccount" :disabled="isEditMode" size="large" />
            </a-form-item>
            <a-form-item :label="isEditMode ? '重置密码（可选）' : '登录密码'">
                <a-input-password v-model:value="form.userPassword" size="large" />
            </a-form-item>
            <a-form-item label="用户昵称">
                <a-input v-model:value="form.userName" size="large" />
            </a-form-item>
            <a-form-item label="头像地址">
                <a-input v-model:value="form.avatarUrl" size="large" />
            </a-form-item>
            <a-form-item label="手机号">
                <a-input v-model:value="form.phone" size="large" />
            </a-form-item>
            <a-form-item label="邮箱">
                <a-input v-model:value="form.email" size="large" />
            </a-form-item>
            <a-form-item label="性别">
                <a-select v-model:value="form.gender" size="large">
                    <a-select-option :value="0">未知</a-select-option>
                    <a-select-option :value="1">男</a-select-option>
                    <a-select-option :value="2">女</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="角色">
                <a-select v-model:value="form.userRole" size="large">
                    <a-select-option value="admin">管理员</a-select-option>
                    <a-select-option value="artisan">匠人</a-select-option>
                    <a-select-option value="user">普通用户</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="状态">
                <a-select v-model:value="form.userStatus" size="large">
                    <a-select-option :value="0">正常</a-select-option>
                    <a-select-option :value="1">禁用</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="账户余额">
                <a-input-number v-model:value="form.balance" size="large" :min="0" :controls="false" class="full-width" />
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
    grid-template-columns: minmax(0, 1.6fr) repeat(2, minmax(0, 1fr)) auto;
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
