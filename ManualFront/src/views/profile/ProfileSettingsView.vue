<script setup lang="ts">
import { message } from 'ant-design-vue'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'

import { rechargeUser } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginUser = computed(() => userStore.currentUser)
const rechargeAmount = ref<number>(100)
const recharging = ref(false)

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

async function handleRecharge() {
    if (!rechargeAmount.value || rechargeAmount.value <= 0) {
        message.warning('请输入大于 0 的充值金额')
        return
    }
    recharging.value = true
    try {
        const nextUser = await rechargeUser({ amount: rechargeAmount.value })
        userStore.setCurrentUser(nextUser)
    } catch (error) {
        message.error(error instanceof Error ? error.message : '充值失败')
    } finally {
        recharging.value = false
    }
}

function formatGender(gender: number | null | undefined) {
    if (gender === 1) {
        return '男'
    }
    if (gender === 2) {
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
    return `CNY ${Number(balance || 0).toFixed(2)}`
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
        <a-card class="balance-card" :bordered="false">
            <div class="balance-copy">
                <span>账户余额</span>
                <strong>{{ formatBalance(loginUser?.balance) }}</strong>
                <p>余额可用于支付待支付订单。</p>
            </div>
            <div class="recharge-box">
                <a-input-number
                    v-model:value="rechargeAmount"
                    size="large"
                    :min="1"
                    :precision="2"
                    :controls="false"
                    class="recharge-input"
                />
                <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" :loading="recharging" @click="handleRecharge">
                    充值
                </a-button>
            </div>
        </a-card>

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
            <a-descriptions-item label="最近登录">
                {{ formatDateTime(loginUser?.lastLoginTime) }}
            </a-descriptions-item>
            <a-descriptions-item label="创建时间">
                {{ formatDateTime(loginUser?.createTime) }}
            </a-descriptions-item>
            <a-descriptions-item label="更新时间">
                {{ formatDateTime(loginUser?.updateTime) }}
            </a-descriptions-item>
            <a-descriptions-item label="头像地址">
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

.balance-card {
    border-radius: 16px;
    background: rgba(255, 247, 238, 0.92);
}

.balance-card :deep(.ant-card-body) {
    display: flex;
    justify-content: space-between;
    gap: 18px;
    align-items: center;
}

.balance-copy {
    display: grid;
    gap: 6px;
}

.balance-copy span,
.balance-copy p {
    color: var(--text-muted);
}

.balance-copy strong {
    color: var(--coral-deep);
    font-size: 1.8rem;
}

.recharge-box {
    display: flex;
    gap: 12px;
    align-items: center;
}

.recharge-input {
    width: 180px;
}

.settings-panel :deep(.ant-descriptions-view) {
    overflow: hidden;
    border-radius: 16px;
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
    .balance-card :deep(.ant-card-body),
    .recharge-box {
        align-items: flex-start;
        flex-direction: column;
    }

    .recharge-box,
    .recharge-input,
    .settings-actions {
        width: 100%;
    }

    .settings-actions :deep(.ant-btn),
    .recharge-box :deep(.ant-btn) {
        width: 100%;
    }

    .settings-avatar-row {
        align-items: flex-start;
        flex-direction: column;
    }
}
</style>
