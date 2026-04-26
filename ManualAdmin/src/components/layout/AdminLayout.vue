<script setup lang="ts">
import {
    AppstoreOutlined,
    BarsOutlined,
    DownOutlined,
    FolderOpenOutlined,
    GiftOutlined,
    HomeOutlined,
    LogoutOutlined,
    NotificationOutlined,
    SettingOutlined,
    ShoppingCartOutlined,
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
    { label: '仪表盘', path: '/', icon: HomeOutlined },
    { label: '用户管理', path: '/users', icon: TeamOutlined },
    { label: '顾客管理', path: '/customers', icon: TeamOutlined },
    { label: '店员管理', path: '/staff', icon: SolutionOutlined },
    { label: '商品管理', path: '/products', icon: AppstoreOutlined },
    { label: '优惠券管理', path: '/coupons', icon: GiftOutlined },
    { label: '订单管理', path: '/orders', icon: ShoppingCartOutlined },
    { label: '分类管理', path: '/categories', icon: FolderOpenOutlined },
    { label: '系统设置', path: '/settings', icon: SettingOutlined },
]

const pageTitle = computed(() => navItems.find((item) => item.path === route.path)?.label || '管理后台')
const displayName = computed(() => currentUser.value?.username || currentUser.value?.userAccount || '管理员')
const avatarText = computed(() => displayName.value.slice(0, 1).toUpperCase())
const avatarUrl = computed(() => resolveAvatarUrl(currentUser.value?.avatarUrl))

function isActive(path: string) {
    if (path === '/') {
        return route.path === '/'
    }
    return route.path.startsWith(path)
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
                <span class="mark">ma</span>
                <span v-if="!collapsed" class="brand-copy">
                    <strong>ManualAdmin</strong>
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
                    <component :is="item.icon" />
                    <span v-if="!collapsed">{{ item.label }}</span>
                </RouterLink>
            </nav>

            <a-card v-if="!collapsed" class="status-card" :bordered="false">
                <p>后台已启用管理员身份校验，未登录或非 admin 账号会自动跳转到登录页。</p>
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
    grid-template-columns: 280px minmax(0, 1fr);
    min-height: 100vh;
    background:
        linear-gradient(180deg, rgba(238, 243, 255, 0.7), rgba(244, 247, 251, 0.92)),
        var(--bg);
}

.admin-aside {
    display: grid;
    gap: 18px;
    align-content: start;
    padding: 28px 18px;
    background: linear-gradient(180deg, var(--navy), #112947);
    color: rgba(255, 255, 255, 0.86);
}

.admin-aside.collapsed {
    width: 92px;
    padding-inline: 14px;
}

.brand {
    display: inline-flex;
    align-items: center;
    gap: 14px;
    padding: 8px 6px;
}

.mark {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    border-radius: 16px;
    background: linear-gradient(135deg, #7bb6ff, #ffd683);
    color: #102139;
    font-family: var(--font-display);
    font-size: 1.1rem;
    font-weight: 700;
}

.brand-copy {
    display: grid;
}

.brand-copy strong {
    color: #ffffff;
    font-family: var(--font-display);
}

.brand-copy small {
    color: rgba(255, 255, 255, 0.56);
    letter-spacing: 0.08em;
}

.collapse-trigger {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    min-height: 46px;
    padding: 0 14px;
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 18px;
    background: rgba(255, 255, 255, 0.08);
    color: rgba(255, 255, 255, 0.9);
    cursor: pointer;
}

.admin-nav {
    display: grid;
    gap: 10px;
}

.nav-link {
    display: flex;
    align-items: center;
    gap: 12px;
    min-height: 50px;
    padding: 0 16px;
    border-radius: 18px;
    color: rgba(255, 255, 255, 0.72);
    transition:
        transform 0.2s ease,
        background-color 0.2s ease,
        color 0.2s ease;
}

.nav-link:hover,
.nav-link.active {
    transform: translateX(2px);
    background: rgba(255, 255, 255, 0.12);
    color: #ffffff;
}

.status-card {
    border-radius: 22px;
    background: linear-gradient(145deg, rgba(255, 255, 255, 0.12), rgba(123, 182, 255, 0.12));
    color: rgba(255, 255, 255, 0.86);
}

.admin-main {
    display: grid;
    grid-template-rows: auto 1fr;
    min-width: 0;
    padding: 24px;
}

.topbar {
    display: flex;
    justify-content: space-between;
    gap: 18px;
    align-items: center;
    padding: 8px 4px 24px;
}

.eyebrow {
    color: var(--blue);
    font-size: 0.82rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

h1 {
    margin-top: 8px;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: clamp(2rem, 3vw, 3rem);
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
    width: 46px;
    height: 46px;
    border: 0;
    border-radius: 16px;
    background: var(--surface);
    box-shadow: var(--shadow);
    color: var(--text-strong);
    cursor: pointer;
}

.admin-avatar-trigger {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    min-height: 50px;
    padding: 4px 10px 4px 4px;
    border: 0;
    border-radius: 18px;
    background: var(--surface);
    box-shadow: var(--shadow);
    color: var(--text-muted);
    cursor: pointer;
    transition:
        transform 0.2s ease,
        color 0.2s ease;
}

.admin-avatar-trigger:hover,
.admin-avatar-trigger:focus-visible {
    transform: translateY(-1px);
    color: var(--text-strong);
}

.account-menu :deep(.ant-dropdown-menu-item) {
    min-width: 140px;
    gap: 8px;
}

.content-shell {
    min-width: 0;
}

@media (max-width: 980px) {
    .admin-page {
        grid-template-columns: 1fr;
    }

    .admin-aside {
        grid-auto-flow: row;
    }

    .topbar {
        flex-direction: column;
        align-items: flex-start;
    }
}
</style>
