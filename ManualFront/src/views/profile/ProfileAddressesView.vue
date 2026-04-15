<script setup lang="ts">
import { addressItems } from '@/mocks/profile'
</script>

<template>
    <a-card class="profile-panel" :bordered="false" title="收货地址">
        <div class="address-list">
            <div v-for="item in addressItems" :key="item.id" class="address-item">
                <div class="address-head">
                    <strong>{{ item.receiverName }}</strong>
                    <a-tag v-if="item.isDefault" color="green">默认地址</a-tag>
                </div>
                <p>{{ item.phone }}</p>
                <p>{{ item.region }}</p>
                <p>{{ item.detailAddress }}</p>
            </div>
        </div>
    </a-card>
</template>

<style scoped>
.address-list {
    display: grid;
    gap: 16px;
}

.address-item {
    padding: 18px;
    border-radius: 20px;
    background: rgba(255, 247, 238, 0.92);
}

.address-head {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 8px;
}

.address-head strong {
    color: var(--text-strong);
}

p {
    color: var(--text);
}
</style>
