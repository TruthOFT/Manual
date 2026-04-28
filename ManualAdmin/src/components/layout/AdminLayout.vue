<script setup lang="ts">
import {
    AppstoreOutlined,
    BarsOutlined,
    DownOutlined,
    GiftOutlined,
    HomeOutlined,
    LogoutOutlined,
    NotificationOutlined,
    SolutionOutlined,
    TeamOutlined,
} from '@ant-design/icons-vue'
import { storeToRefs } from 'pinia'
import { computed, ref } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'

import { API_CONTEXT_PATH, BASE_URL } from '@/api/request'
import { useAdminAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAdminAuthStore()
const { currentUser } = storeToRefs(authStore)
const collapsed = ref(false)
const loggingOut = ref(false)

const navItems = [
    { label: '商品管理', path: '/products', icon: AppstoreOutlined, color: '#10b981', emoji: '📦' },
    { label: '顾客管理', path: '/customers', icon: TeamOutlined, color: '#3b82f6', emoji: '👥' },
    { label: '店员管理', path: '/staff', icon: SolutionOutlined, color: '#8b5cf6', emoji: '👨‍💼' },
    { label: '优惠券管理', path: '/coupons', icon: GiftOutlined, color: '#ef4444', emoji: '🎫' },
]

const pageTitle = computed(() => navItems.find((item) => item.path === route.path)?.label || '仪表盘')
const displayName = computed(() => currentUser.value?.username || currentUser.value?.userAccount || '管理员')
const avatarText = computed(() => displayName.value.slice(0, 1).toUpperCase())
const avatarUrl = computed(() => resolveAvatarUrl(currentUser.value?.avatarUrl))

function isActive(path: string) {
    return route.path === path
}

function resolveAvatarUrl(url?: string | null) {
    if (!url) {
        return undefined
    }
    const trimmed = url.trim()
    if (!trimmed) {
        return undefined
    }
    if (/^(https?:|data:|blob:)/i.test(trimmed)) {
        return trimmed
    }
    const normalized = trimmed.replace(/\\/g, '/')
    if (normalized.startsWith(API_CONTEXT_PATH + '/upload/')) {
        return `${BASE_URL}${normalized}`
    }
    if (normalized.startsWith('/upload/')) {
        return `${BASE_URL}${API_CONTEXT_PATH}${normalized}`
    }
    return trimmed
}

async function handleLogout() {
    loggingOut.value = true
    try {
        await authStore.logout()
    } catch {
        authStore.clearCurrentUser()
    } finally {
        loggingOut.value = false
    }
    await router.replace({
        name: 'admin-login',
    })
}
</script>

<template>
    <main class="admin-page">
        <aside class="admin-aside" :class="{ collapsed }">
            <RouterLink class="brand" to="/">
                <span class="mark">管</span>
                <span v-if="!collapsed" class="brand-copy">
                    <strong>管理端</strong>
                    <small>admin console</small>
                </span>
            </RouterLink>

            <button class="collapse-trigger" type="button" @click="collapsed = !collapsed">
                <BarsOutlined />
                <span v-if="!collapsed">收起导航</span>
            </button>

            <nav class="admin-nav" aria-label="管理后台导航">
                <RouterLink
                    v-for="item in navItems"
                    :key="item.path"
                    :to="item.path"
                    class="nav-link"
                    :class="{ active: isActive(item.path) }"
                >
                    <span class="nav-emoji">{{ item.emoji }}</span>
                    <component :is="item.icon" class="nav-icon" :style="{ color: isActive(item.path) ? item.color : '' }" />
                    <span v-if="!collapsed" class="nav-label">{{ item.label }}</span>
                </RouterLink>
            </nav>

            <a-card v-if="!collapsed" class="status-card" :bordered="false">
                <p>欢迎回来，{{ displayName }}！</p>
            </a-card>
        </aside>

        <section class="admin-main">
            <header class="topbar">
                <div>
                    <p class="eyebrow">管理后台</p>
                    <h1>{{ pageTitle }}</h1>
                </div>

                <div class="topbar-actions">
                    <a-badge dot color="#2f6fe4">
                        <button class="icon-action" type="button" aria-label="通知中心">
                            <NotificationOutlined />
                        </button>
                    </a-badge>

                    <a-dropdown trigger="click" placement="bottomRight">
                        <button class="admin-avatar-trigger" type="button" :aria-label="`${displayName} 账号菜单`">
                            <a-avatar :size="42" :src="avatarUrl">
                                {{ avatarText }}
                            </a-avatar>
                            <DownOutlined />
                        </button>

                        <template #overlay>
                            <a-menu class="account-menu">
                                <a-menu-item key="logout" :disabled="loggingOut" @click="handleLogout">
                                    <LogoutOutlined />
                                    <span>{{ loggingOut ? '退出中' : '退出登录' }}</span>
                                </a-menu-item>
                            </a-menu>
                        </template>
                    </a-dropdown>
                </div>
            </header>

            <section class="content-shell">
                <RouterView />
            </section>
        </section>
    </main>
</template>

<style scoped>
.admin-page {
    display: grid;
    grid-template-columns: 260px minmax(0, 1fr);
    min-height: 100vh;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
}

.admin-aside {
    display: grid;
    gap: 20px;
    align-content: start;
    padding: 32px 20px;
    background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 50%, #2563eb 100%);
    color: rgba(255, 255, 255, 0.9);
    box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
}

.admin-aside.collapsed {
    width: 90px;
    padding-inline: 16px;
}

.brand {
    display: inline-flex;
    align-items: center;
    gap: 16px;
    padding: 12px 8px;
    text-decoration: none;
}

.mark {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 52px;
    height: 52px;
    border-radius: 18px;
    background: linear-gradient(135deg, #60a5fa 0%, #fbbf24 100%);
    color: #1e3a8a;
    font-family: var(--font-display);
    font-size: 1.6rem;
    font-weight: 700;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.brand-copy {
    display: grid;
    gap: 2px;
}

.brand-copy strong {
    color: #ffffff;
    font-family: var(--font-display);
    font-size: 1.2rem;
}

.brand-copy small {
    color: rgba(255, 255, 255, 0.6);
    letter-spacing: 0.08em;
    font-size: 0.75rem;
}

.collapse-trigger {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    min-height: 44px;
    padding: 0 16px;
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.9);
    cursor: pointer;
    transition: all 0.2s ease;
}

.collapse-trigger:hover {
    background: rgba(255, 255, 255, 0.15);
}

.admin-nav {
    display: grid;
    gap: 12px;
}

.nav-link {
    display: flex;
    align-items: center;
    gap: 14px;
    min-height: 54px;
    padding: 0 18px;
    border-radius: 16px;
    color: rgba(255, 255, 255, 0.75);
    text-decoration: none;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.nav-link::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    width: 4px;
    height: 100%;
    background: currentColor;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.nav-link:hover {
    transform: translateX(4px);
    background: rgba(255, 255, 255, 0.12);
    color: #ffffff;
}

.nav-link.active {
    background: rgba(255, 255, 255, 0.15);
    color: #ffffff;
    font-weight: 600;
}

.nav-link.active::before {
    opacity: 1;
}

.nav-emoji {
    font-size: 1.4rem;
    line-height: 1;
}

.nav-icon {
    font-size: 1.2rem;
}

.nav-label {
    font-size: 0.95rem;
}

.status-card {
    border-radius: 20px;
    background: linear-gradient(145deg, rgba(255, 255, 255, 0.15), rgba(96, 165, 250, 0.15));
    color: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
}

.status-card :deep(.ant-card-body) {
    padding: 16px;
}

.status-card p {
    margin: 0;
    font-size: 0.9rem;
}

.admin-main {
    display: grid;
    grid-template-rows: auto 1fr;
    min-width: 0;
    padding: 28px 32px;
}

.topbar {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    align-items: center;
    padding-bottom: 28px;
}

.eyebrow {
    color: #2563eb;
    font-size: 0.85rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    margin: 0;
}

h1 {
    margin: 8px 0 0;
    color: #1e293b;
    font-family: var(--font-display);
    font-size: clamp(2rem, 3vw, 2.5rem);
    font-weight: 700;
}

.topbar-actions {
    display: flex;
    align-items: center;
    gap: 16px;
}

.icon-action {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    border: 0;
    border-radius: 16px;
    background: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    color: #1e293b;
    cursor: pointer;
    transition: all 0.2s ease;
}

.icon-action:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.admin-avatar-trigger {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    min-height: 52px;
    padding: 6px 12px 6px 6px;
    border: 0;
    border-radius: 20px;
    background: #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    color: #64748b;
    cursor: pointer;
    transition: all 0.2s ease;
}

.admin-avatar-trigger:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
    color: #1e293b;
}

.account-menu :deep(.ant-dropdown-menu-item) {
    min-width: 150px;
    gap: 10px;
}

.content-shell {
    min-width: 0;
}

@media (max-width: 980px) {
    .admin-page {
        grid-template-columns: 1fr;
    }

    .admin-aside {
        position: fixed;
        left: 0;
        top: 0;
        bottom: 0;
        z-index: 100;
        transform: translateX(-100%);
        transition: transform 0.3s ease;
    }

    .admin-aside.collapsed {
        transform: translateX(-100%);
    }

    .topbar {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
    }
}
</style>
