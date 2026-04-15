<script setup lang="ts">
import { profileOrders } from '@/mocks/profile'
</script>

<template>
    <a-card class="profile-panel" :bordered="false" title="我的订单">
        <a-list :data-source="profileOrders" item-layout="horizontal">
            <template #renderItem="{ item }">
                <a-list-item>
                    <a-list-item-meta>
                        <template #avatar>
                            <a-avatar shape="square" :size="64" :src="item.productCover" />
                        </template>
                        <template #title>{{ item.productName }}</template>
                        <template #description>{{ item.orderNo }} / {{ item.orderTime }}</template>
                    </a-list-item-meta>
                    <div class="order-side">
                        <a-tag color="orange">{{ item.status }}</a-tag>
                        <strong>CNY {{ item.amount }}</strong>
                    </div>
                </a-list-item>
            </template>
        </a-list>
    </a-card>
</template>

<style scoped>
.order-side {
    display: grid;
    justify-items: end;
    gap: 10px;
}

.order-side strong {
    color: var(--coral-deep);
}
</style>
