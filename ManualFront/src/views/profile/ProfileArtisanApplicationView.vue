<script setup lang="ts">
import { DeleteOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons-vue'
import { message, type UploadProps } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getArtisanApplication, submitArtisanApplication } from '@/api/artisan-application'
import { uploadFile } from '@/api/upload'
import { rechargeUser } from '@/api/user'
import { useUserStore } from '@/stores/user'
import type {
    ArtisanApplication,
    ArtisanApplicationSubmitRequest,
} from '@/types/artisan-application'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const recharging = ref(false)
const uploadingAvatar = ref(false)
const uploadingCover = ref(false)
const uploadingQualification = ref(false)
const application = ref<ArtisanApplication | null>(null)
const rechargeAmount = ref<number>(1000)

const form = reactive<ArtisanApplicationSubmitRequest>({
    artisanName: '',
    shopName: '',
    artisanAvatar: '',
    coverUrl: '',
    artisanStory: '',
    craftCategory: '',
    originPlace: '',
    experienceYears: 0,
    supportCustom: 1,
    contactPhone: '',
    qualificationDesc: '',
    qualificationImages: [],
})

const currentUser = computed(() => userStore.currentUser)
const auditStatus = computed(() => application.value?.auditStatus ?? null)
const isNormalUser = computed(() => currentUser.value?.userRole === 'user')
const canSubmit = computed(() => isNormalUser.value && auditStatus.value !== 0)
const isApproved = computed(
    () => currentUser.value?.userRole === 'artisan' || auditStatus.value === 1,
)
const depositAmount = computed(() => Number(application.value?.depositAmount ?? 1000))
const disableReason = computed(() => {
    if (currentUser.value?.userRole === 'artisan') {
        return '当前账号已是匠人，无需重复申请。'
    }
    if (!isNormalUser.value) {
        return '仅普通用户可以提交匠人申请。'
    }
    if (auditStatus.value === 0) {
        return '当前申请正在审核中，暂时不能重复提交。'
    }
    return ''
})

function applyApplication(detail: ArtisanApplication) {
    application.value = detail
    form.artisanName = detail.artisanName || ''
    form.shopName = detail.shopName || ''
    form.artisanAvatar = detail.artisanAvatar || ''
    form.coverUrl = detail.coverUrl || ''
    form.artisanStory = detail.artisanStory || ''
    form.craftCategory = detail.craftCategory || ''
    form.originPlace = detail.originPlace || ''
    form.experienceYears = detail.experienceYears || 0
    form.supportCustom = detail.supportCustom ?? 1
    form.contactPhone = detail.contactPhone || currentUser.value?.phone || ''
    form.qualificationDesc = detail.qualificationDesc || ''
    form.qualificationImages = detail.qualificationImages ? [...detail.qualificationImages] : []
}

async function loadApplication() {
    loading.value = true
    try {
        await userStore.fetchCurrentUser()
        applyApplication(await getArtisanApplication())
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载匠人申请信息失败')
    } finally {
        loading.value = false
    }
}

async function handleRecharge() {
    recharging.value = true
    try {
        const user = await rechargeUser({ amount: rechargeAmount.value })
        userStore.setCurrentUser(user)
        message.success('余额已充值，可以继续提交匠人申请')
    } catch (error) {
        message.error(error instanceof Error ? error.message : '充值失败')
    } finally {
        recharging.value = false
    }
}

async function handleSubmit() {
    if (!canSubmit.value) {
        if (disableReason.value) {
            message.warning(disableReason.value)
        }
        return
    }
    submitting.value = true
    try {
        const result = await submitArtisanApplication({
            ...form,
            qualificationImages: [...form.qualificationImages],
        })
        applyApplication(result)
        await userStore.fetchCurrentUser()
        message.success('匠人申请已提交，请等待管理员审核')
    } catch (error) {
        message.error(error instanceof Error ? error.message : '提交匠人申请失败')
    } finally {
        submitting.value = false
    }
}

async function uploadUserAsset(
    file: File,
    onSuccess: (url: string) => void,
    mode: 'avatar' | 'cover' | 'qualification',
) {
    if (mode === 'avatar') {
        uploadingAvatar.value = true
    } else if (mode === 'cover') {
        uploadingCover.value = true
    } else {
        uploadingQualification.value = true
    }
    try {
        const url = await uploadFile('user', file)
        onSuccess(url)
        message.success('图片上传成功')
    } catch (error) {
        message.error(error instanceof Error ? error.message : '图片上传失败')
    } finally {
        if (mode === 'avatar') {
            uploadingAvatar.value = false
        } else if (mode === 'cover') {
            uploadingCover.value = false
        } else {
            uploadingQualification.value = false
        }
    }
    return false
}

const beforeUploadAvatar: UploadProps['beforeUpload'] = async (file) => {
    return uploadUserAsset(file as File, (url) => {
        form.artisanAvatar = url
    }, 'avatar')
}

const beforeUploadCover: UploadProps['beforeUpload'] = async (file) => {
    return uploadUserAsset(file as File, (url) => {
        form.coverUrl = url
    }, 'cover')
}

const beforeUploadQualification: UploadProps['beforeUpload'] = async (file) => {
    return uploadUserAsset(file as File, (url) => {
        form.qualificationImages.push(url)
    }, 'qualification')
}

function removeQualification(index: number) {
    form.qualificationImages.splice(index, 1)
}

function getAuditStatusText(status: number | null) {
    if (status === 1) {
        return '审核通过'
    }
    if (status === 2) {
        return '审核驳回'
    }
    if (status === 0) {
        return '待审核'
    }
    return '未提交'
}

function getAuditStatusColor(status: number | null) {
    if (status === 1) {
        return 'green'
    }
    if (status === 2) {
        return 'red'
    }
    if (status === 0) {
        return 'gold'
    }
    return 'default'
}

function formatBalance(value: number | string | null | undefined) {
    return `￥${Number(value || 0).toFixed(2)}`
}

onMounted(() => {
    void loadApplication()
})
</script>

<template>
    <div class="profile-view">
        <a-card class="profile-hero-card" :bordered="false">
            <p class="eyebrow">申请成为匠人</p>
            <h1>提交资质资料与保证金，审核通过后即可进入匠人工作台</h1>
            <p class="lead">
                当前余额、申请状态、驳回备注和资料上传都在这里统一处理，提交后由管理员审核。
            </p>

            <div class="stats-grid">
                <a-card class="stat-card" :bordered="false">
                    <span>当前余额</span>
                    <strong>{{ formatBalance(currentUser?.balance) }}</strong>
                    <p>余额不足时可先进行模拟充值。</p>
                </a-card>
                <a-card class="stat-card" :bordered="false">
                    <span>保证金</span>
                    <strong>{{ formatBalance(depositAmount) }}</strong>
                    <p>提交申请时会直接扣减该金额。</p>
                </a-card>
                <a-card class="stat-card" :bordered="false">
                    <span>申请状态</span>
                    <strong>{{ getAuditStatusText(auditStatus) }}</strong>
                    <p>审核结果会同步影响是否可进入匠人工作台。</p>
                </a-card>
            </div>
        </a-card>

        <a-skeleton v-if="loading" active :paragraph="{ rows: 12 }" />

        <div v-else class="content-grid">
            <a-card class="profile-panel" :bordered="false" title="余额与审核状态">
                <div class="status-stack">
                    <div class="status-row">
                        <span>当前账号</span>
                        <strong>{{ currentUser?.userAccount }}</strong>
                    </div>
                    <div class="status-row">
                        <span>当前角色</span>
                        <a-tag :color="isApproved ? 'green' : 'blue'">
                            {{ currentUser?.userRole || '-' }}
                        </a-tag>
                    </div>
                    <div class="status-row">
                        <span>审核状态</span>
                        <a-tag :color="getAuditStatusColor(auditStatus)">
                            {{ getAuditStatusText(auditStatus) }}
                        </a-tag>
                    </div>
                    <div class="status-row">
                        <span>驳回备注</span>
                        <p>{{ application?.auditRemark || '暂无' }}</p>
                    </div>
                    <div class="status-row">
                        <span>申请时间</span>
                        <p>{{ application?.applyTime || '未提交' }}</p>
                    </div>
                    <div class="status-row">
                        <span>审核时间</span>
                        <p>{{ application?.auditTime || '暂无' }}</p>
                    </div>
                </div>

                <a-divider />

                <div class="recharge-box">
                    <a-input-number
                        v-model:value="rechargeAmount"
                        :min="1"
                        :precision="2"
                        size="large"
                        class="recharge-input"
                    />
                    <a-button
                        class="manual-ant-btn manual-ant-btn-primary"
                        size="large"
                        :loading="recharging"
                        @click="handleRecharge"
                    >
                        模拟充值
                    </a-button>
                </div>

                <a-alert
                    v-if="isApproved"
                    type="success"
                    show-icon
                    message="当前账号已通过匠人审核"
                    description="现在可以直接进入匠人工作台管理商品、订单和店铺资料。"
                >
                    <template #action>
                        <a-button type="link" @click="router.push('/artisan')">进入工作台</a-button>
                    </template>
                </a-alert>
            </a-card>

            <a-card class="profile-panel" :bordered="false" title="匠人申请资料">
                <a-form layout="vertical" class="form-grid">
                    <a-form-item label="匠人姓名">
                        <a-input v-model:value="form.artisanName" size="large" :disabled="!canSubmit" />
                    </a-form-item>
                    <a-form-item label="店铺名称">
                        <a-input v-model:value="form.shopName" size="large" :disabled="!canSubmit" />
                    </a-form-item>
                    <a-form-item label="擅长工艺">
                        <a-input v-model:value="form.craftCategory" size="large" :disabled="!canSubmit" />
                    </a-form-item>
                    <a-form-item label="所在地">
                        <a-input v-model:value="form.originPlace" size="large" :disabled="!canSubmit" />
                    </a-form-item>
                    <a-form-item label="从业年限">
                        <a-input-number
                            v-model:value="form.experienceYears"
                            size="large"
                            :min="0"
                            :controls="false"
                            class="full-width"
                            :disabled="!canSubmit"
                        />
                    </a-form-item>
                    <a-form-item label="联系电话">
                        <a-input v-model:value="form.contactPhone" size="large" :disabled="!canSubmit" />
                    </a-form-item>
                    <a-form-item label="支持定制" class="full-span">
                        <a-switch
                            v-model:checked="form.supportCustom"
                            :checked-value="1"
                            :un-checked-value="0"
                            :disabled="!canSubmit"
                        />
                    </a-form-item>
                    <a-form-item label="匠人故事" class="full-span">
                        <a-textarea v-model:value="form.artisanStory" :rows="4" :disabled="!canSubmit" />
                    </a-form-item>
                    <a-form-item label="资质说明" class="full-span">
                        <a-textarea v-model:value="form.qualificationDesc" :rows="4" :disabled="!canSubmit" />
                    </a-form-item>
                </a-form>

                <div class="upload-grid">
                    <div class="upload-panel">
                        <p>匠人头像</p>
                        <a-image v-if="form.artisanAvatar" :src="form.artisanAvatar" :width="120" />
                        <a-upload :show-upload-list="false" accept="image/*" :before-upload="beforeUploadAvatar">
                            <a-button :disabled="!canSubmit" :loading="uploadingAvatar">
                                <UploadOutlined />
                                上传头像
                            </a-button>
                        </a-upload>
                    </div>
                    <div class="upload-panel">
                        <p>店铺封面</p>
                        <a-image v-if="form.coverUrl" :src="form.coverUrl" :width="180" />
                        <a-upload :show-upload-list="false" accept="image/*" :before-upload="beforeUploadCover">
                            <a-button :disabled="!canSubmit" :loading="uploadingCover">
                                <UploadOutlined />
                                上传封面
                            </a-button>
                        </a-upload>
                    </div>
                </div>

                <section class="qualification-section">
                    <div class="section-head">
                        <h2>资质图片</h2>
                        <a-upload
                            :show-upload-list="false"
                            accept="image/*"
                            :before-upload="beforeUploadQualification"
                        >
                            <a-button :disabled="!canSubmit" :loading="uploadingQualification">
                                <PlusOutlined />
                                新增资质图
                            </a-button>
                        </a-upload>
                    </div>
                    <div class="qualification-grid">
                        <div
                            v-for="(item, index) in form.qualificationImages"
                            :key="`${item}-${index}`"
                            class="qualification-item"
                        >
                            <a-image :src="item" :preview="true" :width="132" />
                            <a-button danger type="link" :disabled="!canSubmit" @click="removeQualification(index)">
                                <DeleteOutlined />
                                删除
                            </a-button>
                        </div>
                        <a-empty v-if="!form.qualificationImages.length" description="请至少上传一张资质图片" />
                    </div>
                </section>

                <div class="action-row">
                    <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" @click="loadApplication">
                        刷新状态
                    </a-button>
                    <a-button
                        class="manual-ant-btn manual-ant-btn-primary"
                        size="large"
                        :disabled="!canSubmit"
                        :loading="submitting"
                        @click="handleSubmit"
                    >
                        提交匠人申请
                    </a-button>
                </div>
                <p v-if="!canSubmit && disableReason" class="footnote">
                    {{ disableReason }}
                </p>
            </a-card>
        </div>
    </div>
</template>

<style scoped>
.profile-view {
    display: grid;
    gap: 24px;
}

.eyebrow {
    color: var(--coral-deep);
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

h1,
h2 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
}

h1 {
    font-size: clamp(2.2rem, 4vw, 3.4rem);
}

.lead,
.stat-card p,
.footnote {
    color: var(--text);
}

.stats-grid,
.content-grid,
.form-grid,
.upload-grid,
.qualification-grid {
    display: grid;
    gap: 18px;
}

.stats-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
}

.content-grid {
    grid-template-columns: 0.9fr 1.1fr;
}

.stat-card {
    border-radius: 22px;
    background: rgba(255, 247, 238, 0.92);
}

.stat-card strong {
    display: block;
    margin: 10px 0 8px;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: 2rem;
}

.status-stack {
    display: grid;
    gap: 14px;
}

.status-row {
    display: grid;
    gap: 6px;
}

.status-row span {
    color: var(--text-muted);
}

.status-row strong,
.status-row p {
    color: var(--text-strong);
    margin: 0;
}

.recharge-box {
    display: flex;
    gap: 12px;
    margin-bottom: 18px;
}

.recharge-input,
.full-width {
    width: 100%;
}

.form-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.full-span {
    grid-column: 1 / -1;
}

.upload-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    margin-top: 12px;
}

.upload-panel,
.qualification-item {
    display: grid;
    gap: 12px;
    padding: 16px;
    border-radius: 20px;
    background: rgba(255, 247, 238, 0.92);
}

.section-head,
.action-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
}

.qualification-section {
    display: grid;
    gap: 14px;
    margin-top: 24px;
}

.qualification-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
}

.action-row {
    margin-top: 24px;
    justify-content: flex-end;
}

@media (max-width: 980px) {
    .stats-grid,
    .content-grid,
    .form-grid,
    .upload-grid {
        grid-template-columns: 1fr;
    }
}
</style>