<script setup lang="ts">
import { reactive } from 'vue'

const form = reactive({
    siteName: '手工创意平台管理端',
    contactEmail: 'admin@manual.local',
    enableAuditReminder: true,
    enableOrderWarning: true,
    dashboardRefreshMinutes: 15,
})
</script>

<template>
    <a-card class="panel" :bordered="false" title="系统设置">
        <a-form layout="vertical" class="settings-grid">
            <a-form-item label="站点名称">
                <a-input v-model:value="form.siteName" size="large" />
            </a-form-item>

            <a-form-item label="联系邮箱">
                <a-input v-model:value="form.contactEmail" size="large" />
            </a-form-item>

            <a-form-item label="工作台刷新间隔（分钟）">
                <a-input-number v-model:value="form.dashboardRefreshMinutes" size="large" :min="5" :controls="false" />
            </a-form-item>

            <a-form-item label="审核提醒">
                <a-switch v-model:checked="form.enableAuditReminder" />
            </a-form-item>

            <a-form-item label="订单预警">
                <a-switch v-model:checked="form.enableOrderWarning" />
            </a-form-item>
        </a-form>

        <a-button type="primary" size="large">保存配置</a-button>
    </a-card>
</template>

<style scoped>
.panel {
    border-radius: 28px;
    background: var(--surface);
    box-shadow: var(--shadow);
}

.settings-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0 18px;
    margin-bottom: 24px;
}

@media (max-width: 900px) {
    .settings-grid {
        grid-template-columns: 1fr;
    }
}
</style>
