<script setup lang="ts">
import { message } from 'ant-design-vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import {
    acceptArtisanCustomRequirement,
    completeArtisanCustomRequirement,
    getArtisanCustomRequirementDetail,
    processingArtisanCustomRequirement,
    rejectArtisanCustomRequirement,
} from '@/api/artisan-center'
import type { ArtisanCustomRequirementDetail } from '@/types/artisan-center'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const acting = ref(false)
const errorMessage = ref('')
const remark = ref('')
const detail = ref<ArtisanCustomRequirementDetail | null>(null)

const imageList = computed(() => detail.value?.referenceImages?.split(',').map((item) => item.trim()).filter(Boolean) ?? [])

async function loadDetail() {
    if (typeof route.params.id !== 'string') {
        errorMessage.value = '定制需求参数无效'
        return
    }
    loading.value = true
    errorMessage.value = ''
    try {
        detail.value = await getArtisanCustomRequirementDetail(route.params.id)
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载定制需求详情失败'
    } finally {
        loading.value = false
    }
}

async function runAction(action: (id: string, data: { confirmRemark?: string }) => Promise<boolean>) {
    if (typeof route.params.id !== 'string') {
        return
    }
    acting.value = true
    try {
        await action(route.params.id, {
            confirmRemark: remark.value || undefined,
        })
        await loadDetail()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '操作失败')
    } finally {
        acting.value = false
    }
}

onMounted(() => {
    void loadDetail()
})
</script>

<template>
    <div class="artisan-view">
        <a-button class="manual-ant-btn manual-ant-btn-soft back-btn" @click="router.push('/artisan/custom-requirements')">
            返回需求列表
        </a-button>

        <a-alert v-if="errorMessage" type="error" show-icon :message="errorMessage" />
        <a-skeleton v-if="loading" active :paragraph="{ rows: 10 }" />

        <template v-else-if="detail">
            <a-card class="artisan-panel" :bordered="false" title="需求详情">
                <div class="detail-grid">
                    <div><strong>需求标题</strong><span>{{ detail.requirementTitle }}</span></div>
                    <div><strong>关联订单</strong><span>{{ detail.orderNo }}</span></div>
                    <div><strong>关联商品</strong><span>{{ detail.productName }}</span></div>
                    <div><strong>买家</strong><span>{{ detail.buyerName }}</span></div>
                    <div class="full-span"><strong>需求内容</strong><span>{{ detail.requirementContent }}</span></div>
                    <div><strong>当前状态</strong><span>{{ detail.confirmStatus }}</span></div>
                    <div><strong>确认备注</strong><span>{{ detail.confirmRemark || '无' }}</span></div>
                </div>
            </a-card>

            <a-card class="artisan-panel" :bordered="false" title="参考图片">
                <a-empty v-if="!imageList.length" description="未上传参考图片" />
                <a-image-preview-group v-else>
                    <div class="image-grid">
                        <a-image v-for="item in imageList" :key="item" :src="item" :preview="true" />
                    </div>
                </a-image-preview-group>
            </a-card>

            <a-card class="artisan-panel" :bordered="false" title="处理动作">
                <a-textarea v-model:value="remark" :rows="4" placeholder="可选填写处理备注" />
                <div class="action-row">
                    <a-button
                        v-if="detail.confirmStatus === 0"
                        class="manual-ant-btn manual-ant-btn-primary"
                        :loading="acting"
                        @click="runAction(acceptArtisanCustomRequirement)"
                    >
                        接单
                    </a-button>
                    <a-button
                        v-if="detail.confirmStatus === 0"
                        class="manual-ant-btn manual-ant-btn-soft"
                        :loading="acting"
                        @click="runAction(rejectArtisanCustomRequirement)"
                    >
                        拒绝
                    </a-button>
                    <a-button
                        v-if="detail.confirmStatus === 1"
                        class="manual-ant-btn manual-ant-btn-cream"
                        :loading="acting"
                        @click="runAction(processingArtisanCustomRequirement)"
                    >
                        标记处理中
                    </a-button>
                    <a-button
                        v-if="detail.confirmStatus === 1 || detail.confirmStatus === 3"
                        class="manual-ant-btn manual-ant-btn-primary"
                        :loading="acting"
                        @click="runAction(completeArtisanCustomRequirement)"
                    >
                        标记完成
                    </a-button>
                </div>
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

.detail-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
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

.full-span {
    grid-column: 1 / -1;
}

.image-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 14px;
}

.action-row {
    display: flex;
    gap: 12px;
    margin-top: 18px;
    flex-wrap: wrap;
}

@media (max-width: 900px) {
    .detail-grid,
    .image-grid {
        grid-template-columns: 1fr;
    }
}
</style>
