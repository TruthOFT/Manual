<script setup lang="ts">
import { GiftOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { computed, onMounted, ref } from 'vue'

import { getAvailableCoupons, getMyCoupons, receiveCoupon } from '@/api/coupon'
import type { UserCoupon } from '@/types/coupon'

const activeTab = ref('available')
const availableCoupons = ref<UserCoupon[]>([])
const myCoupons = ref<UserCoupon[]>([])
const loadingAvailable = ref(false)
const loadingMine = ref(false)
const receivingIds = ref<Set<string>>(new Set())
const useStatus = ref<number | undefined>()

const currentLoading = computed(() => (activeTab.value === 'available' ? loadingAvailable.value : loadingMine.value))

function formatAmount(value: number | string | null | undefined) {
    const amount = Number(value ?? 0)
    return amount.toFixed(2)
}

function formatDiscount(coupon: UserCoupon) {
    if (coupon.couponType === 2) {
        return `${coupon.discountRate ?? '-'} 折`
    }
    return `¥${formatAmount(coupon.discountAmount)}`
}

function formatThreshold(coupon: UserCoupon) {
    const threshold = Number(coupon.thresholdAmount ?? 0)
    return threshold > 0 ? `满 ¥${threshold.toFixed(2)} 可用` : '无门槛'
}

function getUseStatusText(status: number | null | undefined) {
    if (status === 1) {
        return {
            text: '已使用',
            color: 'default',
        }
    }
    return {
        text: '未使用',
        color: 'green',
    }
}

async function loadAvailableCoupons() {
    loadingAvailable.value = true
    try {
        availableCoupons.value = await getAvailableCoupons()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载可领取优惠券失败')
    } finally {
        loadingAvailable.value = false
    }
}

async function loadMyCoupons() {
    loadingMine.value = true
    try {
        myCoupons.value = await getMyCoupons({
            useStatus: useStatus.value,
        })
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载我的优惠券失败')
    } finally {
        loadingMine.value = false
    }
}

async function handleReceive(couponId: string) {
    const nextIds = new Set(receivingIds.value)
    nextIds.add(couponId)
    receivingIds.value = nextIds
    try {
        await receiveCoupon(couponId)
        await Promise.all([loadAvailableCoupons(), loadMyCoupons()])
    } catch (error) {
        message.error(error instanceof Error ? error.message : '领取优惠券失败')
    } finally {
        const restIds = new Set(receivingIds.value)
        restIds.delete(couponId)
        receivingIds.value = restIds
    }
}

async function refreshCurrentTab() {
    if (activeTab.value === 'available') {
        await loadAvailableCoupons()
        return
    }
    await loadMyCoupons()
}

function handleTabChange(tabKey: string) {
    activeTab.value = tabKey
    void refreshCurrentTab()
}

onMounted(() => {
    void Promise.all([loadAvailableCoupons(), loadMyCoupons()])
})
</script>

<template>
    <a-card class="profile-panel coupon-panel" :bordered="false" title="我的优惠券">
        <template #extra>
            <a-button :loading="currentLoading" @click="refreshCurrentTab">
                <ReloadOutlined />
                刷新
            </a-button>
        </template>

        <a-tabs :active-key="activeTab" @change="handleTabChange">
            <a-tab-pane key="available" tab="可领取">
                <a-empty v-if="!loadingAvailable && availableCoupons.length === 0" description="暂无可领取优惠券" />
                <a-spin :spinning="loadingAvailable">
                    <div class="coupon-grid">
                        <article v-for="coupon in availableCoupons" :key="coupon.id" class="coupon-ticket">
                            <div class="ticket-main">
                                <div class="ticket-icon">
                                    <GiftOutlined />
                                </div>
                                <div>
                                    <h3>{{ coupon.couponName }}</h3>
                                    <p>{{ formatThreshold(coupon) }}</p>
                                    <span>{{ coupon.startTime }} 至 {{ coupon.endTime }}</span>
                                </div>
                            </div>
                            <div class="ticket-side">
                                <strong>{{ formatDiscount(coupon) }}</strong>
                                <a-button
                                    type="primary"
                                    :loading="receivingIds.has(coupon.id)"
                                    @click="handleReceive(coupon.id)"
                                >
                                    领取
                                </a-button>
                            </div>
                        </article>
                    </div>
                </a-spin>
            </a-tab-pane>

            <a-tab-pane key="mine" tab="已领取">
                <div class="filter-row">
                    <a-select v-model:value="useStatus" allow-clear placeholder="使用状态" @change="loadMyCoupons">
                        <a-select-option :value="0">未使用</a-select-option>
                        <a-select-option :value="1">已使用</a-select-option>
                    </a-select>
                </div>
                <a-empty v-if="!loadingMine && myCoupons.length === 0" description="暂无已领取优惠券" />
                <a-spin :spinning="loadingMine">
                    <div class="coupon-grid">
                        <article v-for="coupon in myCoupons" :key="coupon.receiveId || coupon.id" class="coupon-ticket">
                            <div class="ticket-main">
                                <div class="ticket-icon soft">
                                    <GiftOutlined />
                                </div>
                                <div>
                                    <h3>{{ coupon.couponName }}</h3>
                                    <p>{{ formatThreshold(coupon) }}</p>
                                    <span>领取时间：{{ coupon.receiveTime || '-' }}</span>
                                    <span>有效期：{{ coupon.startTime }} 至 {{ coupon.endTime }}</span>
                                </div>
                            </div>
                            <div class="ticket-side">
                                <strong>{{ formatDiscount(coupon) }}</strong>
                                <a-tag :color="getUseStatusText(coupon.useStatus).color">
                                    {{ getUseStatusText(coupon.useStatus).text }}
                                </a-tag>
                            </div>
                        </article>
                    </div>
                </a-spin>
            </a-tab-pane>
        </a-tabs>
    </a-card>
</template>

<style scoped>
.coupon-panel :deep(.ant-card-body) {
    display: grid;
    gap: 18px;
}

.filter-row {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 16px;
}

.filter-row :deep(.ant-select) {
    width: 180px;
}

.coupon-grid {
    display: grid;
    gap: 16px;
}

.coupon-ticket {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 180px;
    gap: 16px;
    align-items: center;
    padding: 18px;
    border: 1px solid rgba(212, 149, 115, 0.18);
    border-radius: 22px;
    background: rgba(255, 247, 238, 0.92);
}

.ticket-main {
    display: grid;
    grid-template-columns: auto minmax(0, 1fr);
    gap: 14px;
    align-items: center;
}

.ticket-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 52px;
    height: 52px;
    border-radius: 18px;
    background: linear-gradient(135deg, #ffbf8f, #f28a67);
    color: var(--text-strong);
    font-size: 1.35rem;
}

.ticket-icon.soft {
    background: rgba(255, 232, 213, 0.92);
}

.ticket-main h3 {
    margin: 0 0 6px;
    color: var(--text-strong);
    font-size: 1.05rem;
}

.ticket-main p {
    margin: 0 0 4px;
    color: var(--text);
    font-weight: 800;
}

.ticket-main span {
    display: block;
    color: var(--text-muted);
    font-size: 0.9rem;
}

.ticket-side {
    display: grid;
    gap: 10px;
    justify-items: end;
    text-align: right;
}

.ticket-side strong {
    color: var(--coral-deep);
    font-family: var(--font-display);
    font-size: 1.8rem;
}

.ticket-side small {
    color: var(--text-muted);
}

@media (max-width: 760px) {
    .coupon-ticket {
        grid-template-columns: 1fr;
    }

    .ticket-side {
        justify-items: start;
        text-align: left;
    }
}
</style>
