<script setup lang="ts">
import { message } from 'ant-design-vue'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { getArtisanOrderDetail, shipArtisanOrder } from '@/api/artisan-center'
import type { ArtisanOrderDetail } from '@/types/artisan-center'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const shipping = ref(false)
const errorMessage = ref('')
const detail = ref<ArtisanOrderDetail | null>(null)
const shipForm = reactive({
    companyName: '',
    trackingNo: '',
    senderName: '',
    senderPhone: '',
    logisticsRemark: '',
})

async function loadDetail() {
    if (typeof route.params.id !== 'string') {
        errorMessage.value = '订单参数无效'
        return
    }
    loading.value = true
    errorMessage.value = ''
    try {
        detail.value = await getArtisanOrderDetail(route.params.id)
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载订单详情失败'
    } finally {
        loading.value = false
    }
}

async function handleShip() {
    if (!detail.value || typeof route.params.id !== 'string') {
        return
    }
    shipping.value = true
    try {
        await shipArtisanOrder(route.params.id, shipForm)
        await loadDetail()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '发货失败')
    } finally {
        shipping.value = false
    }
}

onMounted(() => {
    void loadDetail()
})
</script>

<template>
    <div class="artisan-view">
        <a-button class="manual-ant-btn manual-ant-btn-soft back-btn" @click="router.push('/artisan/orders')">返回订单列表</a-button>
        <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage" />
        <a-skeleton v-if="loading" active :paragraph="{ rows: 10 }" />

        <template v-else-if="detail">
            <a-card class="artisan-panel" :bordered="false" title="订单详情">
                <div class="detail-grid">
                    <div><strong>订单号</strong><span>{{ detail.orderNo }}</span></div>
                    <div><strong>买家</strong><span>{{ detail.buyerName }}</span></div>
                    <div><strong>商品</strong><span>{{ detail.productName }} / {{ detail.skuName }}</span></div>
                    <div><strong>成交金额</strong><span>¥ {{ detail.totalAmount }}</span></div>
                    <div><strong>收货信息</strong><span>{{ detail.receiverName }} {{ detail.receiverPhone }}</span></div>
                    <div><strong>收货地址</strong><span>{{ detail.province }}{{ detail.city }}{{ detail.district }}{{ detail.detailAddress }}</span></div>
                    <div><strong>买家备注</strong><span>{{ detail.buyerRemark || '无' }}</span></div>
                    <div><strong>物流状态</strong><span>{{ detail.deliveryStatus === 1 ? '已发货' : '待发货' }}</span></div>
                </div>
            </a-card>

            <a-card class="artisan-panel" :bordered="false" title="物流信息">
                <template v-if="detail.logistics">
                    <div class="detail-grid">
                        <div><strong>物流公司</strong><span>{{ detail.logistics.companyName || '-' }}</span></div>
                        <div><strong>物流单号</strong><span>{{ detail.logistics.trackingNo || '-' }}</span></div>
                        <div><strong>发货时间</strong><span>{{ detail.logistics.shipTime || '-' }}</span></div>
                        <div><strong>备注</strong><span>{{ detail.logistics.logisticsRemark || '无' }}</span></div>
                    </div>
                </template>
                <template v-else>
                    <a-empty description="暂未录入物流信息" />
                </template>
            </a-card>

            <a-card v-if="detail.orderStatus === 1" class="artisan-panel" :bordered="false" title="发货操作">
                <a-form layout="vertical" class="ship-grid">
                    <a-form-item label="物流公司">
                        <a-input v-model:value="shipForm.companyName" size="large" />
                    </a-form-item>
                    <a-form-item label="物流单号">
                        <a-input v-model:value="shipForm.trackingNo" size="large" />
                    </a-form-item>
                    <a-form-item label="发件人">
                        <a-input v-model:value="shipForm.senderName" size="large" />
                    </a-form-item>
                    <a-form-item label="发件电话">
                        <a-input v-model:value="shipForm.senderPhone" size="large" />
                    </a-form-item>
                    <a-form-item label="物流备注" class="full-span">
                        <a-textarea v-model:value="shipForm.logisticsRemark" :rows="4" />
                    </a-form-item>
                </a-form>
                <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" :loading="shipping" @click="handleShip">
                    提交发货
                </a-button>
            </a-card>
        </template>
    </div>
</template>

<style scoped>
.artisan-view {
    display: grid;
    gap: 24px;
}

.back-btn {
    width: max-content;
}

.detail-grid,
.ship-grid {
    display: grid;
    gap: 16px;
}

.detail-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.detail-grid div {
    display: grid;
    gap: 6px;
    padding: 16px;
    border-radius: 18px;
    background: rgba(255, 247, 238, 0.92);
}

.detail-grid strong {
    color: var(--text-strong);
}

.detail-grid span {
    color: var(--text);
}

.ship-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.full-span {
    grid-column: 1 / -1;
}

@media (max-width: 900px) {
    .detail-grid,
    .ship-grid {
        grid-template-columns: 1fr;
    }
}
</style>
