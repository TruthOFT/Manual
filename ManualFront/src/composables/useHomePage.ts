import { onMounted, ref } from 'vue'

import { getHomePage } from '@/api/home'
import type { HomePageData } from '@/types/home'

let cachedHomePage: HomePageData | null = null
let pendingRequest: Promise<HomePageData> | null = null

async function fetchHomePage(forceRefresh = false) {
    if (!forceRefresh && cachedHomePage) {
        return cachedHomePage
    }
    if (!forceRefresh && pendingRequest) {
        return pendingRequest
    }
    pendingRequest = getHomePage()
        .then((data) => {
            cachedHomePage = data
            return data
        })
        .finally(() => {
            pendingRequest = null
        })
    return pendingRequest
}

export function useHomePage() {
    const homeData = ref<HomePageData | null>(cachedHomePage)
    const loading = ref(!cachedHomePage)
    const errorMessage = ref('')

    async function loadHomePage(forceRefresh = false) {
        if (!forceRefresh && cachedHomePage) {
            homeData.value = cachedHomePage
            loading.value = false
            return cachedHomePage
        }
        loading.value = true
        errorMessage.value = ''
        try {
            const data = await fetchHomePage(forceRefresh)
            homeData.value = data
            return data
        } catch (error) {
            errorMessage.value = error instanceof Error ? error.message : '加载首页数据失败，请稍后重试。'
            throw error
        } finally {
            loading.value = false
        }
    }

    onMounted(() => {
        void loadHomePage()
    })

    return {
        errorMessage,
        homeData,
        loadHomePage,
        loading,
    }
}
