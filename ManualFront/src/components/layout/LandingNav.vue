<script setup lang="ts">
import { DownOutlined, LogoutOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons-vue'
import { computed } from 'vue'
import { useRouter } from 'vue-router'

import { useUserStore } from '@/stores/user'

const navItems = [
    { label: '首页', to: '/' },
    { label: '商品', to: '/products' },
]

const router = useRouter()
const userStore = useUserStore()

const displayName = computed(() => userStore.currentUser?.username || userStore.currentUser?.userAccount || '手作用户')

async function handleMenuClick({ key }: { key: string }) {
    if (key === 'profile') {
        await router.push('/profile')
        return
    }
    if (key === 'settings') {
        await router.push('/profile/settings')
        return
    }
    if (key === 'logout') {
        await userStore.logout()
        await router.push('/')
    }
}
</script>

<template>
    <div class="nav">
        <RouterLink class="brand" to="/" aria-label="手作商城首页">
            <span class="mark">hm</span>
            <span class="brand-copy">
                <strong>手作创意商城</strong>
                <small>前台展示与个人中心</small>
            </span>
        </RouterLink>

        <nav class="links" aria-label="页面导航">
            <RouterLink v-for="item in navItems" :key="item.to" :to="item.to">
                {{ item.label }}
            </RouterLink>
        </nav>

        <div class="actions">
            <template v-if="userStore.isLoggedIn">
                <a-dropdown placement="bottomRight" trigger="click">
                    <button class="user-pill" type="button">
                        <a-avatar :size="32" class="user-avatar" :src="userStore.currentUser?.avatarUrl">
                            <template #icon>
                                <UserOutlined />
                            </template>
                        </a-avatar>
                        <span>{{ displayName }}</span>
                        <DownOutlined class="menu-arrow" />
                    </button>
                    <template #overlay>
                        <a-menu @click="handleMenuClick">
                            <a-menu-item key="profile">
                                <UserOutlined />
                                <span>个人中心</span>
                            </a-menu-item>
                            <a-menu-item key="settings">
                                <SettingOutlined />
                                <span>账号设置</span>
                            </a-menu-item>
                            <a-menu-item key="logout">
                                <LogoutOutlined />
                                <span>退出登录</span>
                            </a-menu-item>
                        </a-menu>
                    </template>
                </a-dropdown>
            </template>
            <template v-else>
                <RouterLink to="/login">
                    <a-button class="manual-ant-btn manual-ant-btn-primary" size="large">登录</a-button>
                </RouterLink>
            </template>
        </div>
    </div>
</template>

<style scoped>
.nav {
    display: grid;
    grid-template-columns: auto 1fr auto;
    align-items: center;
    gap: 20px;
    width: 100%;
    padding: 16px 20px;
    border: 1px solid var(--line);
    border-radius: 999px;
    background: var(--surface);
    box-shadow: var(--shadow);
    backdrop-filter: blur(20px);
}

.brand {
    display: inline-flex;
    align-items: center;
    gap: 12px;
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
    font-size: 0.78rem;
}

.links {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
    min-width: 0;
    font-weight: 700;
}

.links a {
    color: var(--text);
    transition: color 0.24s ease;
}

.links a.router-link-active,
.links a:hover,
.links a:focus-visible {
    color: var(--text-strong);
}

.actions {
    display: inline-flex;
    align-items: center;
    justify-content: flex-end;
    gap: 12px;
}

.user-pill {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    min-height: 52px;
    padding: 0 14px;
    border-radius: 999px;
    background: linear-gradient(135deg, rgba(255, 243, 231, 0.95), rgba(255, 234, 214, 0.95));
    border: 1px solid rgba(212, 149, 115, 0.3);
    color: var(--text-strong);
    font-weight: 800;
    white-space: nowrap;
    cursor: pointer;
}

.user-avatar {
    background: linear-gradient(135deg, #ffbf8f, #f28a67);
}

.menu-arrow {
    color: var(--text-muted);
    font-size: 0.8rem;
}

@media (max-width: 1120px) {
    .nav {
        grid-template-columns: 1fr;
        justify-items: center;
        border-radius: 30px;
    }

    .links {
        flex-wrap: wrap;
    }
}

@media (max-width: 760px) {
    .nav {
        padding: 18px;
    }

    .links,
    .actions {
        width: 100%;
    }

    .links {
        justify-content: center;
    }

    .actions {
        flex-direction: column;
    }

    .actions :deep(.ant-btn) {
        width: 100%;
    }

    .user-pill {
        width: 100%;
        justify-content: center;
    }
}
</style>
