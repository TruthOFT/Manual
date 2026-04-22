<script setup lang="ts">
import { CheckOutlined, EyeOutlined, ReloadOutlined, StopOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { computed, onMounted, ref } from 'vue'

import {
    approveAdminArtisanApplication,
    getAdminArtisanApplicationDetail,
    getAdminArtisanApplications,
    rejectAdminArtisanApplication,
} from '@/api/artisan-application'
import { API_CONTEXT_PATH, BASE_URL } from '@/api/request'
import type {
    AdminArtisanApplicationDetail,
    AdminArtisanApplicationItem,
} from '@/types/artisan-application'

const columns = [
    { title: '申请账号', dataIndex: 'userAccount', key: 'userAccount' },
    { title: '申请人昵称', dataIndex: 'userName', key: 'userName' },
    { title: '匠人姓名', dataIndex: 'artisanName', key: 'artisanName' },
    { title: '店铺名称', dataIndex: 'shopName', key: 'shopName' },
    { title: '联系电话', dataIndex: 'contactPhone', key: 'contactPhone' },
    { title: '保证金', dataIndex: 'depositAmount', key: 'depositAmount' },
    { title: '审核状态', dataIndex: 'auditStatus', key: 'auditStatus' },
    { title: '申请时间', dataIndex: 'applyTime', key: 'applyTime' },
    { title: '操作', key: 'action' },
]

const loading = ref(false)
const detailLoading = ref(false)
const actionLoading = ref(false)
const drawerVisible = ref(false)
const rejectModalVisible = ref(false)
const keyword = ref('')
const auditStatus = ref<number | undefined>()
const applications = ref<AdminArtisanApplicationItem[]>([])
const currentDetail = ref<AdminArtisanApplicationDetail | null>(null)
const rejectRemark = ref('')

const currentTitle = computed(
    () => currentDetail.value?.shopName || currentDetail.value?.artisanName || '匠人申请详情',
)

function getStatusText(status: number) {
    if (status === 1) {
        return '审核通过'
    }
    if (status === 2) {
        return '审核驳回'
    }
    return '待审核'
}

function getStatusColor(status: number) {
    if (status === 1) {
        return 'green'
    }
    if (status === 2) {
        return 'red'
    }
    return 'gold'
}

function formatMoney(value: number | string | null | undefined) {
    return `¥${Number(value || 0).toFixed(2)}`
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
            // ignore malformed JSON-like legacy data
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

async function loadApplications() {
    loading.value = true
    try {
        applications.value = await getAdminArtisanApplications({
            auditStatus: auditStatus.value,
            keyword: keyword.value || undefined,
        })
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载匠人申请列表失败')
    } finally {
        loading.value = false
    }
}

async function openDetail(id: string) {
    drawerVisible.value = true
    detailLoading.value = true
    try {
        currentDetail.value = await getAdminArtisanApplicationDetail(id)
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载匠人申请详情失败')
        drawerVisible.value = false
    } finally {
        detailLoading.value = false
    }
}

async function handleApprove() {
    if (!currentDetail.value) {
        return
    }
    actionLoading.value = true
    try {
        await approveAdminArtisanApplication(currentDetail.value.id)
        currentDetail.value = await getAdminArtisanApplicationDetail(currentDetail.value.id)
        await loadApplications()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '审核通过失败')
    } finally {
        actionLoading.value = false
    }
}

function openRejectModal() {
    rejectRemark.value = ''
    rejectModalVisible.value = true
}

async function handleReject() {
    if (!currentDetail.value) {
        return
    }
    if (!rejectRemark.value.trim()) {
        message.error('请输入驳回备注')
        return
    }
    actionLoading.value = true
    try {
        await rejectAdminArtisanApplication(currentDetail.value.id, {
            auditRemark: rejectRemark.value.trim(),
        })
        rejectModalVisible.value = false
        currentDetail.value = await getAdminArtisanApplicationDetail(currentDetail.value.id)
        await loadApplications()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '驳回申请失败')
    } finally {
        actionLoading.value = false
    }
}

onMounted(() => {
    void loadApplications()
})
</script>

<template>
    <a-card class="panel" :bordered="false" title="匠人审核">
        <template #extra>
            <a-button @click="loadApplications">
                <ReloadOutlined />
                刷新
            </a-button>
        </template>

        <div class="toolbar">
            <a-input
                v-model:value="keyword"
                size="large"
                placeholder="搜索账号、昵称、匠人名、店铺名"
                @press-enter="loadApplications"
            />
            <a-select
                v-model:value="auditStatus"
                allow-clear
                size="large"
                placeholder="筛选审核状态"
            >
                <a-select-option :value="0">待审核</a-select-option>
                <a-select-option :value="1">审核通过</a-select-option>
                <a-select-option :value="2">审核驳回</a-select-option>
            </a-select>
            <a-button type="primary" size="large" @click="loadApplications">查询</a-button>
        </div>

        <a-table
            :columns="columns"
            :data-source="applications"
            :loading="loading"
            :pagination="{ pageSize: 8 }"
            :scroll="{ x: 1080 }"
            row-key="id"
        >
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'userName'">
                    {{ record.userName || '-' }}
                </template>
                <template v-else-if="column.key === 'contactPhone'">
                    {{ record.contactPhone || '-' }}
                </template>
                <template v-else-if="column.key === 'depositAmount'">
                    {{ formatMoney(record.depositAmount) }}
                </template>
                <template v-else-if="column.key === 'auditStatus'">
                    <a-tag :color="getStatusColor(record.auditStatus)">
                        {{ getStatusText(record.auditStatus) }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'action'">
                    <a-button size="small" @click="openDetail(record.id)">
                        <EyeOutlined />
                        查看详情
                    </a-button>
                </template>
            </template>
        </a-table>
    </a-card>

    <a-drawer v-model:open="drawerVisible" :title="currentTitle" width="720" destroy-on-close>
        <a-skeleton v-if="detailLoading" active :paragraph="{ rows: 12 }" />
        <div v-else-if="currentDetail" class="detail-stack">
            <div class="detail-head">
                <a-avatar :size="68" :src="resolveImageUrl(currentDetail.artisanAvatar || currentDetail.userAvatarUrl)">
                    {{ (currentDetail.artisanName || '匠').slice(0, 1) }}
                </a-avatar>
                <div>
                    <h2>{{ currentDetail.artisanName }}</h2>
                    <p>{{ currentDetail.shopName }}</p>
                    <a-tag :color="getStatusColor(currentDetail.auditStatus)">
                        {{ getStatusText(currentDetail.auditStatus) }}
                    </a-tag>
                </div>
            </div>

            <a-descriptions bordered :column="1">
                <a-descriptions-item label="申请账号">{{ currentDetail.userAccount }}</a-descriptions-item>
                <a-descriptions-item label="申请人昵称">
                    {{ currentDetail.userName || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="联系电话">
                    {{ currentDetail.contactPhone || currentDetail.phone || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="邮箱">{{ currentDetail.email || '-' }}</a-descriptions-item>
                <a-descriptions-item label="保证金">
                    {{ formatMoney(currentDetail.depositAmount) }}
                </a-descriptions-item>
                <a-descriptions-item label="擅长工艺">
                    {{ currentDetail.craftCategory || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="所在地">
                    {{ currentDetail.originPlace || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="从业年限">
                    {{ currentDetail.experienceYears }} 年
                </a-descriptions-item>
                <a-descriptions-item label="支持定制">
                    {{ currentDetail.supportCustom ? '是' : '否' }}
                </a-descriptions-item>
                <a-descriptions-item label="匠人故事">
                    {{ currentDetail.artisanStory || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="资质说明">
                    {{ currentDetail.qualificationDesc || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="驳回备注">
                    {{ currentDetail.auditRemark || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="申请时间">
                    {{ currentDetail.applyTime || '-' }}
                </a-descriptions-item>
                <a-descriptions-item label="审核时间">
                    {{ currentDetail.auditTime || '-' }}
                </a-descriptions-item>
            </a-descriptions>

            <div class="image-block">
                <h3>店铺封面</h3>
                <a-image v-if="currentDetail.coverUrl" :src="resolveImageUrl(currentDetail.coverUrl)" :width="220" />
                <a-empty v-else description="未上传封面" />
            </div>

            <div class="image-block">
                <h3>资质图片</h3>
                <div class="qualification-grid">
                    <a-image
                        v-for="item in currentDetail.qualificationImages"
                        :key="item"
                        :src="resolveImageUrl(item)"
                        :width="140"
                    />
                    <a-empty
                        v-if="!currentDetail.qualificationImages.length"
                        description="未上传资质图"
                    />
                </div>
            </div>

            <div class="action-row" v-if="currentDetail.auditStatus === 0">
                <a-button type="primary" :loading="actionLoading" @click="handleApprove">
                    <CheckOutlined />
                    审核通过
                </a-button>
                <a-button danger :loading="actionLoading" @click="openRejectModal">
                    <StopOutlined />
                    驳回申请
                </a-button>
            </div>
        </div>
    </a-drawer>

    <a-modal
        v-model:open="rejectModalVisible"
        title="驳回匠人申请"
        ok-text="确认驳回"
        cancel-text="取消"
        :confirm-loading="actionLoading"
        @ok="handleReject"
    >
        <a-form layout="vertical">
            <a-form-item label="驳回备注">
                <a-textarea
                    v-model:value="rejectRemark"
                    :rows="4"
                    placeholder="请输入驳回原因，用户会在申请页看到该备注"
                />
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

.toolbar {
    display: grid;
    grid-template-columns: minmax(0, 1.6fr) minmax(0, 1fr) auto;
    gap: 16px;
    margin-bottom: 18px;
}

.detail-stack {
    display: grid;
    gap: 20px;
}

.detail-head {
    display: flex;
    gap: 16px;
    align-items: center;
}

.detail-head h2,
.detail-head p,
.image-block h3 {
    margin: 0;
}

.detail-head p {
    color: var(--text-muted);
}

.image-block {
    display: grid;
    gap: 12px;
}

.qualification-grid {
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
}

.action-row {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

@media (max-width: 980px) {
    .toolbar {
        grid-template-columns: 1fr;
    }

    .detail-head,
    .action-row {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
