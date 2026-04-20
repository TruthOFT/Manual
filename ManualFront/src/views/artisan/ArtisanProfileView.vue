<script setup lang="ts">
import { message } from 'ant-design-vue'
import { onMounted, reactive, ref } from 'vue'

import { getArtisanCenterProfile, updateArtisanCenterProfile } from '@/api/artisan-center'
import type { ArtisanCenterProfile } from '@/types/artisan-center'

const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')

const form = reactive<ArtisanCenterProfile>({
    id: '',
    userId: '',
    artisanName: '',
    shopName: '',
    artisanAvatar: '',
    coverUrl: '',
    artisanStory: '',
    craftCategory: '',
    originPlace: '',
    experienceYears: 0,
    supportCustom: 0,
    contactPhone: '',
    auditStatus: 0,
    shelfStatus: 0,
})

function applyProfile(profile: ArtisanCenterProfile) {
    Object.assign(form, profile)
}

async function loadProfile() {
    loading.value = true
    errorMessage.value = ''
    try {
        applyProfile(await getArtisanCenterProfile())
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '加载店铺资料失败'
    } finally {
        loading.value = false
    }
}

async function handleSave() {
    saving.value = true
    errorMessage.value = ''
    try {
        const nextProfile = await updateArtisanCenterProfile({
            artisanName: form.artisanName,
            shopName: form.shopName,
            artisanAvatar: form.artisanAvatar,
            coverUrl: form.coverUrl,
            artisanStory: form.artisanStory,
            craftCategory: form.craftCategory,
            originPlace: form.originPlace,
            experienceYears: form.experienceYears,
            supportCustom: form.supportCustom,
            contactPhone: form.contactPhone,
        })
        applyProfile(nextProfile)
        message.success('店铺资料已更新')
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '保存店铺资料失败'
    } finally {
        saving.value = false
    }
}

onMounted(() => {
    void loadProfile()
})
</script>

<template>
    <div class="artisan-view">
        <a-card class="artisan-hero-card" :bordered="false">
            <p class="eyebrow">店铺资料</p>
            <h1>维护匠人主页、门店封面与接单偏好。</h1>
            <p class="lead">这里保存的是匠人端基础对外形象，也会影响前台匠人故事页和商品作者信息展示。</p>
        </a-card>

        <a-card class="artisan-panel" :bordered="false" title="基础信息">
            <a-alert v-if="errorMessage" class="alert" type="error" show-icon :message="errorMessage" />
            <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />
            <a-form v-else layout="vertical" class="form-grid">
                <a-form-item label="匠人名称">
                    <a-input v-model:value="form.artisanName" size="large" />
                </a-form-item>
                <a-form-item label="店铺名称">
                    <a-input v-model:value="form.shopName" size="large" />
                </a-form-item>
                <a-form-item label="工艺分类">
                    <a-input v-model:value="form.craftCategory" size="large" />
                </a-form-item>
                <a-form-item label="所在地区">
                    <a-input v-model:value="form.originPlace" size="large" />
                </a-form-item>
                <a-form-item label="从业年限">
                    <a-input-number v-model:value="form.experienceYears" size="large" :min="0" :controls="false" />
                </a-form-item>
                <a-form-item label="联系电话">
                    <a-input v-model:value="form.contactPhone" size="large" />
                </a-form-item>
                <a-form-item label="头像地址" class="full-span">
                    <a-input v-model:value="form.artisanAvatar" size="large" />
                </a-form-item>
                <a-form-item label="封面地址" class="full-span">
                    <a-input v-model:value="form.coverUrl" size="large" />
                </a-form-item>
                <a-form-item label="匠人故事" class="full-span">
                    <a-textarea v-model:value="form.artisanStory" :rows="5" />
                </a-form-item>
                <a-form-item label="支持定制" class="full-span">
                    <a-switch v-model:checked="form.supportCustom" :checked-value="1" :un-checked-value="0" />
                </a-form-item>
            </a-form>

            <div class="status-row">
                <a-tag :color="form.auditStatus === 1 ? 'green' : form.auditStatus === 2 ? 'red' : 'gold'">
                    审核状态：{{ form.auditStatus === 1 ? '已通过' : form.auditStatus === 2 ? '已驳回' : '待审核' }}
                </a-tag>
                <a-tag :color="form.shelfStatus === 1 ? 'blue' : 'default'">
                    店铺状态：{{ form.shelfStatus === 1 ? '启用中' : '未启用' }}
                </a-tag>
            </div>

            <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" :loading="saving" @click="handleSave">
                保存资料
            </a-button>
        </a-card>
    </div>
</template>

<style scoped>
.artisan-view {
    display: grid;
    gap: 24px;
}

.artisan-hero-card :deep(.ant-card-body) {
    display: grid;
    gap: 18px;
}

.eyebrow {
    color: var(--coral-deep);
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

h1 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: clamp(2.2rem, 4vw, 3.6rem);
}

.lead {
    color: var(--text);
}

.alert {
    margin-bottom: 18px;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0 18px;
}

.full-span {
    grid-column: 1 / -1;
}

.status-row {
    display: flex;
    gap: 12px;
    margin: 8px 0 22px;
    flex-wrap: wrap;
}

@media (max-width: 860px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>
