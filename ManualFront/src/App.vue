<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView } from 'vue-router'

import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(() => {
    if (!userStore.initialized) {
        void userStore.fetchCurrentUser()
    }
})
</script>

<template>
    <RouterView />
</template>
