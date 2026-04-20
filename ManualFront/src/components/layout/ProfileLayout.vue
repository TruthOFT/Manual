<script setup lang="ts">
import {
    EnvironmentOutlined,
    HeartOutlined,
    HomeOutlined,
    SettingOutlined,
    ShoppingOutlined,
    SolutionOutlined,
    UserOutlined,
} from '@ant-design/icons-vue'
import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'

import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const isNormalUser = computed(() => userStore.currentUser?.userRole === 'user')

const navItems = computed(() => {
    const items = [
        {
            label: '个人中心',
            path: '/profile',
            icon: UserOutlined,
        },
        {
            label: '我的订单',
            path: '/profile/orders',
            icon: ShoppingOutlined,
        },
        {
            label: '我的收藏',
            path: '/profile/favorites',
            icon: HeartOutlined,
        },
        {
            label: '收货地址',
            path: '/profile/addresses',
            icon: EnvironmentOutlined,
        },
    ]
    if (isNormalUser.value) {
        items.push({
            label: '申请成为匠人',
            path: '/profile/artisan-application',
            icon: SolutionOutlined,
        })
    }
    items.push({
        label: '账号设置',
        path: '/profile/settings',
        icon: SettingOutlined,
    })
    return items
})

const displayName = computed(
    () => userStore.currentUser?.username || userStore.currentUser?.userAccount || '手作用户',
)

function isActive(path: string) {
    if (path === '/profile') {
        return route.path === '/profile'
    }
    return route.path.startsWith(path)
}
</script>

<template>
    <main class="profile-page">
        <section class="profile-shell">
            <aside class="profile-aside">
                <RouterLink class="brand" to="/">
                    <span class="mark">hm</span>
                    <span class="brand-copy">
                        <strong>手作创意商城</strong>
                        <small>个人中心</small>
                    </span>
                </RouterLink>

                <a-card class="profile-user-card" :bordered="false">
                    <div class="profile-user">
                        <a-avatar :size="58" :src="userStore.currentUser?.avatarUrl" class="profile-avatar">
                            <template #icon>
                                <UserOutlined />
                            </template>
                        </a-avatar>
                        <div>
                            <strong>{{ displayName }}</strong>
                            <p>{{ userStore.currentUser?.userAccount }}</p>
                        </div>
                    </div>
                </a-card>

                <div class="storefront-actions">
                    <RouterLink v-if="userStore.currentUser?.userRole === 'artisan'" to="/artisan">
                        <a-button class="manual-ant-btn manual-ant-btn-cream" size="large" block>
                            切换到匠人工作台
                        </a-button>
                    </RouterLink>
                    <RouterLink v-else-if="isNormalUser" to="/profile/artisan-application">
                        <a-button class="manual-ant-btn manual-ant-btn-cream" size="large" block>
                            申请成为匠人
                        </a-button>
                    </RouterLink>
                    <RouterLink to="/">
                        <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" block>
                            <HomeOutlined />
                            返回首页
                        </a-button>
                    </RouterLink>
                    <RouterLink to="/products">
                        <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" block>
                            继续逛商品
                        </a-button>
                    </RouterLink>
                </div>

                <nav class="profile-nav" aria-label="个人中心导航">
                    <RouterLink
                        v-for="item in navItems"
                        :key="item.path"
                        :to="item.path"
                        class="nav-link"
                        :class="{ active: isActive(item.path) }"
                    >
                        <component :is="item.icon" />
                        <span>{{ item.label }}</span>
                    </RouterLink>
                </nav>
            </aside>

            <section class="profile-content">
                <RouterView />
            </section>
        </section>
    </main>
</template>

<style scoped>
.profile-page {
    min-height: 100vh;
    padding: 24px 24px 56px;
}

.profile-shell {
    display: grid;
    grid-template-columns: 280px minmax(0, 1fr);
    gap: 24px;
    width: min(1240px, 100%);
    margin: 0 auto;
}

.profile-aside {
    display: grid;
    gap: 18px;
    align-content: start;
}

.brand {
    display: inline-flex;
    align-items: center;
    gap: 12px;
    padding: 12px 8px;
}

.mark {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    border-radius: 16px;
    background: linear-gradient(135deg, #ffbf8f, #f28a67);
    box-shadow: 0 12px 24px rgba(226, 122, 82, 0.28);
    font-family: var(--font-display);
    font-size: 1.25rem;
    color: var(--text-strong);
}

.brand-copy {
    display: grid;
}

.brand-copy strong {
    color: var(--text-strong);
}

.brand-copy small {
    color: var(--text-muted);
    letter-spacing: 0.08em;
}

.profile-user-card,
.profile-content :deep(.profile-panel),
.profile-content :deep(.profile-hero-card) {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.profile-user {
    display: flex;
    align-items: center;
    gap: 14px;
}

.profile-user strong {
    display: block;
    color: var(--text-strong);
    font-size: 1.05rem;
}

.profile-user p {
    color: var(--text-muted);
}

.profile-avatar {
    background: linear-gradient(135deg, #ffbf8f, #f28a67);
}

.profile-nav {
    display: grid;
    gap: 10px;
}

.storefront-actions {
    display: grid;
    gap: 12px;
}

.storefront-actions :deep(.ant-btn) {
    width: 100%;
}

.nav-link {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 14px 16px;
    border: 1px solid rgba(212, 149, 115, 0.16);
    border-radius: 20px;
    background: rgba(255, 253, 248, 0.78);
    color: var(--text);
    font-weight: 800;
    transition:
        transform 0.24s ease,
        background-color 0.24s ease,
        border-color 0.24s ease,
        box-shadow 0.24s ease;
}

.nav-link:hover,
.nav-link:focus-visible,
.nav-link.active {
    transform: translateY(-2px);
    background: linear-gradient(135deg, rgba(255, 243, 231, 0.96), rgba(255, 232, 213, 0.96));
    border-color: rgba(212, 149, 115, 0.34);
    box-shadow: 0 18px 36px rgba(164, 109, 79, 0.12);
    color: var(--text-strong);
}

.profile-content {
    min-width: 0;
}

@media (max-width: 980px) {
    .profile-shell {
        grid-template-columns: 1fr;
    }
}
</style>