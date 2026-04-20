<script setup lang="ts">
const columns = [
    { title: '订单号', dataIndex: 'orderNo', key: 'orderNo' },
    { title: '买家', dataIndex: 'buyerName', key: 'buyerName' },
    { title: '商品', dataIndex: 'productName', key: 'productName' },
    { title: '金额', dataIndex: 'payAmount', key: 'payAmount' },
    { title: '状态', dataIndex: 'orderStatus', key: 'orderStatus' },
]

const dataSource = [
    { key: '1', orderNo: 'mo202604130001', buyerName: '林知夏', productName: '柴烧釉陶马克杯', payAmount: '¥ 178.00', orderStatus: '待发货' },
    { key: '2', orderNo: 'mo202604130002', buyerName: '周聿风', productName: '黑胡桃榫卯首饰盒', payAmount: '¥ 256.00', orderStatus: '待收货' },
    { key: '3', orderNo: 'mo202604130003', buyerName: '林知夏', productName: '苏绣花影真丝团扇', payAmount: '¥ 318.00', orderStatus: '已完成' },
]
</script>

<template>
    <a-card class="panel" :bordered="false" title="订单管理">
        <template #extra>
            <a-space>
                <a-tag color="gold">待发货 12</a-tag>
                <a-tag color="blue">待收货 8</a-tag>
                <a-tag color="green">已完成 45</a-tag>
            </a-space>
        </template>

        <a-table :columns="columns" :data-source="dataSource" :pagination="{ pageSize: 8 }">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'orderStatus'">
                    <a-tag :color="record.orderStatus === '待发货' ? 'gold' : record.orderStatus === '待收货' ? 'blue' : 'green'">
                        {{ record.orderStatus }}
                    </a-tag>
                </template>
            </template>
        </a-table>
    </a-card>
</template>

<style scoped>
.panel {
    border-radius: 28px;
    background: var(--surface);
    box-shadow: var(--shadow);
}
</style>
