<script setup lang="ts">
import { DeleteOutlined, EditOutlined, GiftOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { message, Modal } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'

import {
    createAdminCoupon,
    deleteAdminCoupon,
    getAdminCouponReceives,
    getAdminCoupons,
    updateAdminCoupon,
    updateAdminCouponStatus,
} from '@/api/coupon'
import type { AdminCoupon, AdminCouponReceive, AdminCouponSaveRequest } from '@/types/coupon'

const columns = [
    { title: '优惠券名称', dataIndex: 'couponName', key: 'couponName' },
    { title: '类型', dataIndex: 'couponType', key: 'couponType' },
    { title: '门槛', dataIndex: 'thresholdAmount', key: 'thresholdAmount' },
    { title: '优惠', key: 'discount' },
    { title: '发行/领取/使用', key: 'count' },
    { title: '状态', dataIndex: 'couponStatus', key: 'couponStatus' },
    { title: '有效期', key: 'time' },
    { title: '操作', key: 'action' },
]

const loading = ref(false)
const saving = ref(false)
const modalVisible = ref(false)
const receiveVisible = ref(false)
const receiveLoading = ref(false)
const isEditMode = ref(false)
const currentEditId = ref('')
const currentCouponId = ref('')
const keyword = ref('')
const couponStatus = ref<number | undefined>()
const coupons = ref<AdminCoupon[]>([])
const receives = ref<AdminCouponReceive[]>([])

const form = reactive<AdminCouponSaveRequest>({
    couponName: '',
    couponType: 1,
    thresholdAmount: 0,
    discountAmount: 0,
    discountRate: null,
    totalCount: 0,
    startTime: '',
    endTime: '',
    couponStatus: 1,
})

const modalTitle = computed(() => (isEditMode.value ? '编辑优惠券' : '新增优惠券'))

function resetForm() {
    Object.assign(form, {
        couponName: '',
        couponType: 1,
        thresholdAmount: 0,
        discountAmount: 0,
        discountRate: null,
        totalCount: 0,
        startTime: '',
        endTime: '',
        couponStatus: 1,
    })
    currentEditId.value = ''
    isEditMode.value = false
}

function getRemainingUseCount(coupon: AdminCoupon) {
    return Math.max(0, Number(coupon.totalCount || 0) - Number(coupon.usedCount || 0))
}

function normalizeCouponPayload(): AdminCouponSaveRequest {
    return {
        ...form,
        discountAmount: form.couponType === 1 ? form.discountAmount : 0,
        discountRate: form.couponType === 2 ? form.discountRate : null,
    }
}

async function loadCoupons() {
    loading.value = true
    try {
        coupons.value = await getAdminCoupons({
            keyword: keyword.value || undefined,
            couponStatus: couponStatus.value,
        })
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载优惠券失败')
    } finally {
        loading.value = false
    }
}

function openCreateModal() {
    resetForm()
    modalVisible.value = true
}

function openEditModal(item: AdminCoupon) {
    isEditMode.value = true
    currentEditId.value = item.id
    form.couponName = item.couponName
    form.couponType = item.couponType
    form.thresholdAmount = Number(item.thresholdAmount || 0)
    form.discountAmount = Number(item.discountAmount || 0)
    form.discountRate = item.discountRate == null ? null : Number(item.discountRate)
    form.totalCount = item.totalCount
    form.startTime = item.startTime
    form.endTime = item.endTime
    form.couponStatus = item.couponStatus
    modalVisible.value = true
}

async function handleSave() {
    saving.value = true
    try {
        const payload = normalizeCouponPayload()
        if (isEditMode.value) {
            await updateAdminCoupon(currentEditId.value, payload)
        } else {
            await createAdminCoupon(payload)
        }
        modalVisible.value = false
        resetForm()
        await loadCoupons()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '保存优惠券失败')
    } finally {
        saving.value = false
    }
}

async function toggleStatus(item: AdminCoupon) {
    await updateAdminCouponStatus(item.id, item.couponStatus === 1 ? 0 : 1)
    await loadCoupons()
}

function handleDelete(couponId: string) {
    Modal.confirm({
        title: '确认删除该优惠券？',
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
            await deleteAdminCoupon(couponId)
            await loadCoupons()
        },
    })
}

async function openReceiveModal(couponId: string) {
    currentCouponId.value = couponId
    receiveVisible.value = true
    await loadReceives(couponId)
}

async function loadReceives(couponId = currentCouponId.value) {
    if (!couponId) return
    receiveLoading.value = true
    try {
        receives.value = await getAdminCouponReceives(couponId)
    } finally {
        receiveLoading.value = false
    }
}

onMounted(() => {
    void loadCoupons()
})
</script>

<template>
    <a-card class="panel" :bordered="false" title="优惠券管理">
        <template #extra>
            <a-space>
                <a-button @click="loadCoupons">
                    <ReloadOutlined />
                    刷新
                </a-button>
                <a-button type="primary" @click="openCreateModal">
                    <PlusOutlined />
                    新增优惠券
                </a-button>
            </a-space>
        </template>

        <div class="toolbar">
            <a-input v-model:value="keyword" size="large" placeholder="搜索优惠券名称" @press-enter="loadCoupons" />
            <a-select v-model:value="couponStatus" allow-clear size="large" placeholder="状态">
                <a-select-option :value="1">启用</a-select-option>
                <a-select-option :value="0">停用</a-select-option>
            </a-select>
            <a-button type="primary" size="large" @click="loadCoupons">查询</a-button>
        </div>

        <a-table :columns="columns" :data-source="coupons" :loading="loading" :pagination="{ pageSize: 8 }" row-key="id">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'couponType'">
                    {{ record.couponType === 2 ? '折扣' : '满减' }}
                </template>
                <template v-else-if="column.key === 'discount'">
                    {{ record.couponType === 2 ? `${record.discountRate || '-'} 折` : `￥${record.discountAmount}` }}
                </template>
                <template v-else-if="column.key === 'thresholdAmount'">
                    ￥{{ record.thresholdAmount }}
                </template>
                <template v-else-if="column.key === 'count'">
                    <div class="count-stack">
                        <span>发行 {{ record.totalCount }}</span>
                        <span>领取 {{ record.receiveCount }}</span>
                        <span>使用 {{ record.usedCount }}</span>
                        <strong>剩余 {{ getRemainingUseCount(record) }}</strong>
                    </div>
                </template>
                <template v-else-if="column.key === 'couponStatus'">
                    <a-tag :color="record.couponStatus === 1 ? 'green' : 'default'">
                        {{ record.couponStatus === 1 ? '启用' : '停用' }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'time'">
                    {{ record.startTime }} 至 {{ record.endTime }}
                </template>
                <template v-else-if="column.key === 'action'">
                    <a-space wrap>
                        <a-button size="small" @click="openEditModal(record)">
                            <EditOutlined />
                            编辑
                        </a-button>
                        <a-button size="small" @click="toggleStatus(record)">
                            {{ record.couponStatus === 1 ? '停用' : '启用' }}
                        </a-button>
                        <a-button size="small" @click="openReceiveModal(record.id)">
                            <GiftOutlined />
                            领取记录
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
        width="760px"
        ok-text="保存"
        cancel-text="取消"
        @ok="handleSave"
        @cancel="resetForm"
    >
        <a-form layout="vertical" class="form-grid">
            <a-form-item label="优惠券名称">
                <a-input v-model:value="form.couponName" size="large" />
            </a-form-item>
            <a-form-item label="类型">
                <a-select v-model:value="form.couponType" size="large">
                    <a-select-option :value="1">满减</a-select-option>
                    <a-select-option :value="2">折扣</a-select-option>
                </a-select>
            </a-form-item>
            <a-form-item label="使用门槛">
                <a-input-number v-model:value="form.thresholdAmount" :min="0" size="large" class="full-width" />
            </a-form-item>
            <a-form-item v-if="form.couponType === 1" label="优惠金额">
                <a-input-number v-model:value="form.discountAmount" :min="0" size="large" class="full-width" />
            </a-form-item>
            <a-form-item v-if="form.couponType === 2" label="折扣比例">
                <a-input-number v-model:value="form.discountRate" :min="0" :max="9.99" size="large" class="full-width" />
            </a-form-item>
            <a-form-item label="发行数量">
                <a-input-number v-model:value="form.totalCount" :min="0" size="large" class="full-width" />
            </a-form-item>
            <a-form-item label="开始时间">
                <a-date-picker
                    v-model:value="form.startTime"
                    show-time
                    value-format="YYYY-MM-DD HH:mm:ss"
                    format="YYYY-MM-DD HH:mm:ss"
                    size="large"
                    class="full-width"
                    placeholder="请选择开始时间"
                />
            </a-form-item>
            <a-form-item label="结束时间">
                <a-date-picker
                    v-model:value="form.endTime"
                    show-time
                    value-format="YYYY-MM-DD HH:mm:ss"
                    format="YYYY-MM-DD HH:mm:ss"
                    size="large"
                    class="full-width"
                    placeholder="请选择结束时间"
                />
            </a-form-item>
            <a-form-item label="状态">
                <a-select v-model:value="form.couponStatus" size="large">
                    <a-select-option :value="1">启用</a-select-option>
                    <a-select-option :value="0">停用</a-select-option>
                </a-select>
            </a-form-item>
        </a-form>
    </a-modal>

    <a-modal
        v-model:open="receiveVisible"
        title="优惠券领取记录"
        width="820px"
        :footer="null"
    >
        <a-space class="receive-bar">
            <a-button @click="loadReceives()">刷新记录</a-button>
        </a-space>
        <a-table :data-source="receives" :loading="receiveLoading" :pagination="{ pageSize: 6 }" row-key="id" size="small">
            <a-table-column title="顾客" key="customer">
                <template #default="{ record }">
                    {{ record.userName || record.userAccount }}（{{ record.userId }}）
                </template>
            </a-table-column>
            <a-table-column title="手机" data-index="phone" />
            <a-table-column title="领取时间" data-index="receiveTime" />
            <a-table-column title="状态" key="useStatus">
                <template #default="{ record }">
                    {{ record.useStatus === 1 ? '已使用' : record.useStatus === 2 ? '已过期' : '未使用' }}
                </template>
            </a-table-column>
        </a-table>
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

.count-stack {
    display: grid;
    gap: 2px;
    color: var(--text-muted);
}

.count-stack strong {
    color: var(--text-strong);
}

.receive-bar {
    width: 100%;
    margin-bottom: 16px;
}

@media (max-width: 980px) {
    .toolbar,
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>
