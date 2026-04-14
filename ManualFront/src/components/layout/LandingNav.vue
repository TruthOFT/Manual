<script setup lang="ts">
import { UserOutlined } from '@ant-design/icons-vue'
import { computed } from 'vue'

import { useUserStore } from '@/stores/user'

const navItems = [
    { label: 'Featured', href: '#products' },
    { label: 'Artisans', href: '#artisans' },
    { label: 'Orders', href: '#orders' },
    { label: 'Join', href: '#cta' },
]

const userStore = useUserStore()

const displayName = computed(() => userStore.currentUser?.username || userStore.currentUser?.userAccount || 'Store Partner')
</script>

<template>
    <div class="nav">
        <RouterLink class="brand" to="/" aria-label="HandMade Manual home">
            <span class="mark">hm</span>
            <span class="brand-copy">
                <strong>HandMade Manual</strong>
                <small>creative shop system</small>
            </span>
        </RouterLink>

        <nav class="links" aria-label="Section navigation">
            <a v-for="item in navItems" :key="item.href" :href="item.href">
                {{ item.label }}
            </a>
        </nav>

        <div class="actions">
            <template v-if="userStore.isLoggedIn">
                <div class="user-pill">
                    <a-avatar :size="32" class="user-avatar">
                        <template #icon>
                            <UserOutlined />
                        </template>
                    </a-avatar>
                    <span>{{ displayName }}</span>
                </div>
            </template>
            <template v-else>
                <RouterLink to="/register">
                    <a-button class="manual-ant-btn manual-ant-btn-soft" size="large">Register</a-button>
                </RouterLink>
                <RouterLink to="/login">
                    <a-button class="manual-ant-btn manual-ant-btn-primary" size="large">Login</a-button>
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
    text-transform: uppercase;
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
}

.user-avatar {
    background: linear-gradient(135deg, #ffbf8f, #f28a67);
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
