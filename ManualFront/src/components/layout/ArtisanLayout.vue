<script setup lang="ts">
import {
    AppstoreOutlined,
    ArrowLeftOutlined,
    HomeOutlined,
    InboxOutlined,
    ProfileOutlined,
    ShopOutlined,
    ShoppingOutlined,
    UserOutlined,
} from '@ant-design/icons-vue'
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'

import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const navItems = [
    {
        label: '工作台概览',
        path: '/artisan',
        icon: HomeOutlined,
    },
    {
        label: '店铺资料',
        path: '/artisan/profile',
        icon: ShopOutlined,
    },
    {
        label: '商品管理',
        path: '/artisan/products',
        icon: AppstoreOutlined,
    },
    {
        label: '订单履约',
        path: '/artisan/orders',
        icon: ShoppingOutlined,
    },
    {
        label: '定制需求',
        path: '/artisan/custom-requirements',
        icon: InboxOutlined,
    },
]

const displayName = computed(() => userStore.currentUser?.username || userStore.currentUser?.userAccount || '匠人账号')

function isActive(path: string) {
    if (path === '/artisan') {
        return route.path === '/artisan'
    }
    return route.path.startsWith(path)
}
</script>

<template>
    <main class="artisan-page">
        <section class="artisan-shell">
            <aside class="artisan-aside">
                <RouterLink class="brand" to="/artisan">
                    <span class="mark">hm</span>
                    <span class="brand-copy">
                        <strong>匠人工作台</strong>
                        <small>artisan center</small>
                    </span>
                </RouterLink>

                <a-card class="artisan-user-card" :bordered="false">
                    <div class="artisan-user">
                        <a-avatar :size="58" :src="userStore.currentUser?.avatarUrl" class="artisan-avatar">
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
                    <RouterLink to="/">
                        <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" block>
                            <ArrowLeftOutlined />
                            返回前台
                        </a-button>
                    </RouterLink>
                    <RouterLink to="/profile">
                        <a-button class="manual-ant-btn manual-ant-btn-soft" size="large" block>
                            <ProfileOutlined />
                            用户中心
                        </a-button>
                    </RouterLink>
                </div>

                <nav class="artisan-nav" aria-label="匠人工作台导航">
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

            <section class="artisan-content">
                <RouterView />
            </section>
        </section>
    </main>
</template>

<style scoped>
.artisan-page {
    min-height: 100vh;
    padding: 24px 24px 56px;
}

.artisan-shell {
    display: grid;
    grid-template-columns: 300px minmax(0, 1fr);
    gap: 24px;
    width: min(1280px, 100%);
    margin: 0 auto;
}

.artisan-aside {
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
    background: linear-gradient(135deg, #ffc56d, #e47b5d);
    box-shadow: 0 12px 24px rgba(228, 123, 93, 0.25);
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

.artisan-user-card,
.artisan-content :deep(.artisan-panel),
.artisan-content :deep(.artisan-hero-card) {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.artisan-user {
    display: flex;
    align-items: center;
    gap: 14px;
}

.artisan-user strong {
    display: block;
    color: var(--text-strong);
}

.artisan-user p {
    color: var(--text-muted);
}

.artisan-avatar {
    background: linear-gradient(135deg, #ffc56d, #e47b5d);
}

.storefront-actions {
    display: grid;
    gap: 12px;
}

.storefront-actions :deep(.ant-btn) {
    width: 100%;
}

.artisan-nav {
    display: grid;
    gap: 10px;
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

.artisan-content {
    min-width: 0;
}

@media (max-width: 980px) {
    .artisan-shell {
        grid-template-columns: 1fr;
    }
}
</style>
