<script setup lang="ts">
import { message } from 'ant-design-vue'
import { computed } from 'vue'
import { useRouter } from 'vue-router'

import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginUser = computed(() => userStore.currentUser)

function handleEditProfile() {
    message.info('后续可在这里接入资料编辑表单。')
}

function handleChangePassword() {
    message.info('后续可在这里接入密码修改能力。')
}

async function handleLogout() {
    await userStore.logout()
    await router.push('/')
}

function formatGender(gender: number | null | undefined) {
    if (gender === 1) {
        return '男'
    }
    if (gender === 0) {
        return '女'
    }
    return '未填写'
}

function formatUserStatus(userStatus: number | null | undefined) {
    if (userStatus === 1) {
        return '已禁用'
    }
    if (userStatus === 0) {
        return '正常'
    }
    return '未知'
}

function getUserRoleTag(role: string | null | undefined) {
    if (role === 'admin') {
        return {
            color: 'volcano',
            text: '管理员',
        }
    }
    if (role === 'artisan') {
        return {
            color: 'geekblue',
            text: '匠人',
        }
    }
    return {
        color: 'green',
        text: '普通用户',
    }
}

function formatBalance(balance: number | string | null | undefined) {
    if (balance === null || balance === undefined || balance === '') {
        return 'CNY 0.00'
    }
    return `CNY ${balance}`
}

function formatDateTime(value: string | number | null | undefined) {
    if (value === null || value === undefined || value === '') {
        return '暂无'
    }
    if (typeof value === 'number') {
        return new Date(value).toLocaleString('zh-CN', { hour12: false })
    }
    return value
}
</script>

<template>
    <a-card class="profile-panel settings-panel" :bordered="false" title="账号设置">
        <a-descriptions :column="{ xs: 1, sm: 1, md: 2 }" bordered>
            <a-descriptions-item label="用户 ID">
                {{ loginUser?.id ?? '暂无' }}
            </a-descriptions-item>
            <a-descriptions-item label="昵称">
                {{ loginUser?.username || '未填写' }}
            </a-descriptions-item>
            <a-descriptions-item label="登录账号">
                {{ loginUser?.userAccount || '暂无' }}
            </a-descriptions-item>
            <a-descriptions-item label="用户角色">
                <a-tag :color="getUserRoleTag(loginUser?.userRole).color">
                    {{ getUserRoleTag(loginUser?.userRole).text }}
                </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="手机号">
                {{ loginUser?.phone || '未填写' }}
            </a-descriptions-item>
            <a-descriptions-item label="邮箱">
                {{ loginUser?.email || '未填写' }}
            </a-descriptions-item>
            <a-descriptions-item label="性别">
                {{ formatGender(loginUser?.gender) }}
            </a-descriptions-item>
            <a-descriptions-item label="账号状态">
                {{ formatUserStatus(loginUser?.userStatus) }}
            </a-descriptions-item>
            <a-descriptions-item label="账户余额">
                {{ formatBalance(loginUser?.balance) }}
            </a-descriptions-item>
            <a-descriptions-item label="最近登录">
                {{ formatDateTime(loginUser?.lastLoginTime) }}
            </a-descriptions-item>
            <a-descriptions-item label="创建时间">
                {{ formatDateTime(loginUser?.createTime) }}
            </a-descriptions-item>
            <a-descriptions-item label="更新时间">
                {{ formatDateTime(loginUser?.updateTime) }}
            </a-descriptions-item>
            <a-descriptions-item label="头像地址" :span="2">
                <div class="settings-avatar-row">
                    <a-avatar :size="56" :src="loginUser?.avatarUrl">
                        {{ (loginUser?.username || loginUser?.userAccount || 'U').slice(0, 1) }}
                    </a-avatar>
                    <span class="settings-avatar-text">
                        {{ loginUser?.avatarUrl || '当前为默认头像，后续可接入上传能力。' }}
                    </span>
                </div>
            </a-descriptions-item>
        </a-descriptions>

        <a-space wrap class="settings-actions">
            <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" @click="handleEditProfile">
                编辑资料
            </a-button>
            <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" @click="handleChangePassword">
                修改密码
            </a-button>
            <a-button class="manual-ant-btn manual-ant-btn-ghost" size="large" @click="handleLogout">
                退出登录
            </a-button>
        </a-space>
    </a-card>
</template>

<style scoped>
.settings-panel :deep(.ant-card-body) {
    display: grid;
    gap: 24px;
}

.settings-panel :deep(.ant-descriptions-view) {
    overflow: hidden;
    border-radius: 22px;
}

.settings-panel :deep(.ant-descriptions-row > th) {
    width: 140px;
    background: rgba(255, 247, 238, 0.92);
    color: var(--text-strong);
    font-weight: 800;
}

.settings-panel :deep(.ant-descriptions-row > td) {
    color: var(--text);
    background: rgba(255, 253, 248, 0.9);
}

.settings-avatar-row {
    display: flex;
    align-items: center;
    gap: 14px;
}

.settings-avatar-text {
    word-break: break-all;
}

.settings-actions {
    display: flex;
    justify-content: flex-start;
}

@media (max-width: 760px) {
    .settings-actions {
        width: 100%;
    }

    .settings-actions :deep(.ant-btn) {
        width: 100%;
    }

    .settings-avatar-row {
        align-items: flex-start;
        flex-direction: column;
    }
}
</style>
