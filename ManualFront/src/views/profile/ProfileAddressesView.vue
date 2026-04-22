<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons-vue'

import { createOrderAddress, deleteOrderAddress, getOrderAddresses, updateOrderAddress } from '@/api/order'
import type { UserAddress, UserAddressSaveRequest } from '@/types/order'

const loading = ref(false)
const submitting = ref(false)
const modalVisible = ref(false)
const editingId = ref<string>()
const addresses = ref<UserAddress[]>([])

const form = reactive<UserAddressSaveRequest>({
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    postalCode: '',
    tagName: '',
    isDefault: 0,
})

const modalTitle = computed(() => (editingId.value ? '编辑收货地址' : '新增收货地址'))

function resetForm() {
    editingId.value = undefined
    form.receiverName = ''
    form.receiverPhone = ''
    form.province = ''
    form.city = ''
    form.district = ''
    form.detailAddress = ''
    form.postalCode = ''
    form.tagName = ''
    form.isDefault = addresses.value.length ? 0 : 1
}

function fillForm(address: UserAddress) {
    editingId.value = address.id
    form.receiverName = address.receiverName
    form.receiverPhone = address.receiverPhone
    form.province = address.province
    form.city = address.city
    form.district = address.district
    form.detailAddress = address.detailAddress
    form.postalCode = address.postalCode || ''
    form.tagName = address.tagName || ''
    form.isDefault = address.isDefault
}

function openCreateModal() {
    resetForm()
    modalVisible.value = true
}

function openEditModal(address: UserAddress) {
    fillForm(address)
    modalVisible.value = true
}

function validateForm() {
    if (!form.receiverName.trim()) return '请填写收货人'
    if (!form.receiverPhone.trim()) return '请填写手机号'
    if (!form.province.trim()) return '请填写省份'
    if (!form.city.trim()) return '请填写城市'
    if (!form.district.trim()) return '请填写区县'
    if (!form.detailAddress.trim()) return '请填写详细地址'
    return ''
}

async function loadAddresses() {
    loading.value = true
    try {
        addresses.value = await getOrderAddresses()
    } catch (error) {
        message.error(error instanceof Error ? error.message : '加载地址失败')
    } finally {
        loading.value = false
    }
}

async function submitAddress() {
    const error = validateForm()
    if (error) {
        message.warning(error)
        return
    }
    submitting.value = true
    try {
        const payload: UserAddressSaveRequest = {
            receiverName: form.receiverName.trim(),
            receiverPhone: form.receiverPhone.trim(),
            province: form.province.trim(),
            city: form.city.trim(),
            district: form.district.trim(),
            detailAddress: form.detailAddress.trim(),
            postalCode: form.postalCode?.trim() || undefined,
            tagName: form.tagName?.trim() || undefined,
            isDefault: form.isDefault ? 1 : 0,
        }
        if (editingId.value) {
            await updateOrderAddress(editingId.value, payload)
        } else {
            await createOrderAddress(payload)
        }
        modalVisible.value = false
        await loadAddresses()
    } catch (submitError) {
        message.error(submitError instanceof Error ? submitError.message : '保存地址失败')
    } finally {
        submitting.value = false
    }
}

function confirmDelete(address: UserAddress) {
    Modal.confirm({
        title: '删除收货地址',
        content: `确认删除 ${address.receiverName} 的这个收货地址吗？`,
        okText: '删除',
        okType: 'danger',
        cancelText: '取消',
        async onOk() {
            await deleteOrderAddress(address.id)
            await loadAddresses()
        },
    })
}

async function setDefaultAddress(address: UserAddress) {
    if (address.isDefault === 1) {
        return
    }
    await updateOrderAddress(address.id, {
        receiverName: address.receiverName,
        receiverPhone: address.receiverPhone,
        province: address.province,
        city: address.city,
        district: address.district,
        detailAddress: address.detailAddress,
        postalCode: address.postalCode || undefined,
        tagName: address.tagName || undefined,
        isDefault: 1,
    })
    await loadAddresses()
}

onMounted(() => {
    void loadAddresses()
})
</script>

<template>
    <a-card class="profile-panel address-panel" :bordered="false" title="收货地址">
        <div class="address-toolbar">
            <span>管理常用收货地址，购买商品时可直接选择。</span>
            <a-button class="manual-ant-btn manual-ant-btn-primary" @click="openCreateModal">
                <PlusOutlined />
                新增地址
            </a-button>
        </div>

        <a-skeleton v-if="loading" active :paragraph="{ rows: 6 }" />

        <div v-else class="address-list">
            <a-empty v-if="!addresses.length" description="暂无收货地址" />

            <div v-for="item in addresses" :key="item.id" class="address-item">
                <div class="address-head">
                    <div>
                        <strong>{{ item.receiverName }}</strong>
                        <span>{{ item.receiverPhone }}</span>
                    </div>
                    <a-space>
                        <a-tag v-if="item.isDefault === 1" color="green">默认地址</a-tag>
                        <a-button v-else type="link" @click="setDefaultAddress(item)">设为默认</a-button>
                    </a-space>
                </div>

                <p>{{ item.province }}{{ item.city }}{{ item.district }}</p>
                <p>{{ item.detailAddress }}</p>
                <small>{{ item.tagName || '收货地址' }} {{ item.postalCode ? `/ ${item.postalCode}` : '' }}</small>

                <div class="address-actions">
                    <a-button class="manual-ant-btn manual-ant-btn-soft" @click="openEditModal(item)">
                        <EditOutlined />
                        编辑
                    </a-button>
                    <a-button class="manual-ant-btn manual-ant-btn-ghost" @click="confirmDelete(item)">
                        <DeleteOutlined />
                        删除
                    </a-button>
                </div>
            </div>
        </div>

        <a-modal
            v-model:open="modalVisible"
            :title="modalTitle"
            :confirm-loading="submitting"
            ok-text="保存"
            cancel-text="取消"
            @ok="submitAddress"
        >
            <a-form layout="vertical" class="address-form">
                <a-form-item label="收货人" required>
                    <a-input v-model:value="form.receiverName" size="large" placeholder="请输入收货人姓名" />
                </a-form-item>
                <a-form-item label="手机号" required>
                    <a-input v-model:value="form.receiverPhone" size="large" placeholder="请输入收货手机号" />
                </a-form-item>
                <a-form-item label="省份" required>
                    <a-input v-model:value="form.province" size="large" placeholder="如：上海市" />
                </a-form-item>
                <a-form-item label="城市" required>
                    <a-input v-model:value="form.city" size="large" placeholder="如：上海市" />
                </a-form-item>
                <a-form-item label="区县" required>
                    <a-input v-model:value="form.district" size="large" placeholder="如：徐汇区" />
                </a-form-item>
                <a-form-item label="详细地址" required>
                    <a-textarea v-model:value="form.detailAddress" :rows="3" placeholder="街道、门牌号、楼栋房间号" />
                </a-form-item>
                <a-form-item label="地址标签">
                    <a-input v-model:value="form.tagName" size="large" placeholder="家、公司等，可不填" />
                </a-form-item>
                <a-form-item label="邮编">
                    <a-input v-model:value="form.postalCode" size="large" placeholder="可不填" />
                </a-form-item>
                <a-form-item class="full-span">
                    <a-checkbox v-model:checked="form.isDefault" :checked-value="1" :unchecked-value="0">
                        设为默认收货地址
                    </a-checkbox>
                </a-form-item>
            </a-form>
        </a-modal>
    </a-card>
</template>

<style scoped>
.address-panel :deep(.ant-card-body),
.address-list {
    display: grid;
    gap: 16px;
}

.address-toolbar {
    display: flex;
    justify-content: space-between;
    gap: 14px;
    align-items: center;
}

.address-toolbar span {
    color: var(--text-muted);
}

.address-item {
    display: grid;
    gap: 10px;
    padding: 18px;
    border-radius: 16px;
    background: rgba(255, 247, 238, 0.92);
}

.address-head,
.address-actions {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    align-items: center;
}

.address-head div {
    display: flex;
    gap: 10px;
    align-items: center;
    flex-wrap: wrap;
}

.address-head strong {
    color: var(--text-strong);
}

.address-head span,
p,
small {
    color: var(--text);
}

p {
    margin: 0;
}

.address-form {
    display: grid;
    gap: 0 14px;
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.address-form :deep(.ant-form-item:nth-child(6)),
.full-span {
    grid-column: 1 / -1;
}

@media (max-width: 760px) {
    .address-toolbar,
    .address-head,
    .address-actions {
        align-items: flex-start;
        flex-direction: column;
    }

    .address-toolbar :deep(.ant-btn) {
        width: 100%;
    }

    .address-form {
        grid-template-columns: 1fr;
    }
}
</style>
