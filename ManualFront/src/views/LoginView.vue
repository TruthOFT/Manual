<script setup lang="ts">
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { loginUser } from '@/api/user'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const form = reactive({
    userAccount: '',
    userPassword: '',
    rememberMe: false,
})

const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

watch(
    () => route.query.account,
    (account) => {
        if (typeof account === 'string' && account.trim()) {
            form.userAccount = account
        }
    },
    { immediate: true },
)

watch(
    () => route.query.registered,
    (registered) => {
        successMessage.value = registered === '1' ? '注册成功，请登录后进入门店管理系统。' : ''
    },
    { immediate: true },
)

async function handleSubmit() {
    errorMessage.value = ''

    if (!form.userAccount.trim() || !form.userPassword.trim()) {
        errorMessage.value = '请输入账号和密码。'
        return
    }

    loading.value = true
    try {
        const loginUserInfo = await loginUser({
            userAccount: form.userAccount.trim(),
            userPassword: form.userPassword,
            rememberMe: form.rememberMe,
        })
        userStore.setCurrentUser(loginUserInfo)
        const redirect = typeof route.query.redirect === 'string' && route.query.redirect.length > 0
            ? route.query.redirect
            : '/'
        await router.push(redirect)
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '登录失败，请稍后重试。'
    } finally {
        loading.value = false
    }
}
</script>

<template>
    <main class="auth-page">
        <div class="auth-orb orb-left"></div>
        <div class="auth-orb orb-right"></div>

        <section class="auth-shell">
            <a-card class="story-card" :bordered="false">
                <p class="eyebrow">会员登录</p>
                <h1>开启您的手作美学之旅，探索更多专属灵感。</h1>
                <p class="lead">
                    登录后，我们将为您提供量身定制的精选推荐，让您第一时间发现心仪的匠心之作与创意单品。
                </p>

                <div class="feature-grid">
                    <a-card class="mini-card" :bordered="false">
                        <span>今日精选</span>
                        <strong>12 款</strong>
                        <small>陶艺、香氛与木作系列同步更新。</small>
                    </a-card>
                    <a-card class="mini-card" :bordered="false">
                        <span>专属推荐</span>
                        <strong>100%</strong>
                        <small>基于您的喜好，量身推荐契合灵魂的好物。</small>
                    </a-card>
                </div>
            </a-card>

            <a-card class="form-card" :bordered="false">
                <p class="card-label">欢迎回来</p>
                <h2>登录账号</h2>
                <p class="card-copy">使用您的账号登录，继续探索手作创意之旅。</p>

                <a-alert v-if="successMessage" class="alert" type="success" :message="successMessage" show-icon />
                <a-alert v-if="errorMessage" class="alert" type="error" :message="errorMessage" show-icon />

                <a-form :model="form" layout="vertical" class="form">
                    <a-form-item label="账号" name="userAccount">
                        <a-input v-model:value="form.userAccount" size="large" placeholder="请输入登录账号">
                            <template #prefix>
                                <UserOutlined />
                            </template>
                        </a-input>
                    </a-form-item>

                    <a-form-item label="密码" name="userPassword">
                        <a-input-password v-model:value="form.userPassword" size="large" placeholder="请输入登录密码">
                            <template #prefix>
                                <LockOutlined />
                            </template>
                        </a-input-password>
                    </a-form-item>

                    <div class="form-foot">
                        <a-checkbox v-model:checked="form.rememberMe">7 天内保持登录</a-checkbox>
                    </div>

                    <a-button
                        class="manual-ant-btn manual-ant-btn-primary"
                        size="large"
                        html-type="button"
                        block
                        :loading="loading"
                        @click="handleSubmit"
                    >
                        登录
                    </a-button>
                </a-form>

                <p class="switch-copy">
                    还没有账号？
                    <RouterLink :to="{ path: '/register', query: route.query.redirect ? { redirect: String(route.query.redirect) } : undefined }">
                        立即注册
                    </RouterLink>
                </p>
            </a-card>
        </section>
    </main>
</template>

<style scoped>
.auth-page {
    position: relative;
    min-height: 100vh;
    padding: 28px;
    overflow: hidden;
}

.auth-shell {
    position: relative;
    z-index: 1;
    display: grid;
    grid-template-columns: minmax(0, 1.05fr) minmax(360px, 460px);
    gap: 28px;
    align-items: stretch;
    width: min(1180px, 100%);
    min-height: calc(100vh - 56px);
    margin: 0 auto;
}

.story-card,
.form-card {
    border-radius: 36px;
    box-shadow: var(--shadow);
}

.story-card {
    background:
        linear-gradient(140deg, rgba(255, 250, 244, 0.95), rgba(255, 240, 226, 0.9)),
        radial-gradient(circle at top right, rgba(255, 197, 109, 0.22), transparent 30%);
}

.form-card {
    align-self: center;
    background: rgba(255, 252, 247, 0.92);
}

.auth-orb {
    position: absolute;
    border-radius: 999px;
    filter: blur(10px);
}

.orb-left {
    top: 80px;
    left: -40px;
    width: 240px;
    height: 240px;
    background: rgba(255, 190, 145, 0.3);
}

.orb-right {
    right: -50px;
    bottom: 60px;
    width: 300px;
    height: 300px;
    background: rgba(151, 193, 169, 0.18);
}

.eyebrow,
.card-label {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    color: var(--coral-deep);
    font-size: 0.82rem;
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.eyebrow::before,
.card-label::before {
    content: '';
    width: 32px;
    height: 10px;
    border-radius: 999px;
    background: linear-gradient(90deg, var(--gold), var(--coral));
}

h1,
h2,
.mini-card strong {
    margin: 0;
    font-family: var(--font-display);
    color: var(--text-strong);
}

h1 {
    max-width: 15ch;
    margin: 18px 0 16px;
    font-size: clamp(2.4rem, 4.5vw, 4rem);
    line-height: 1.4;
    letter-spacing: 2px;
    color: #d4a555;
    font-family: 'KaiTi', 'STKaiti', 'SimSun', 'Georgia', serif;
    text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.15);
}

h2 {
    margin: 14px 0 10px;
    font-size: clamp(2rem, 3vw, 3rem);
    line-height: 1;
}

.lead,
.card-copy,
.mini-card small,
.switch-copy {
    color: var(--text);
}

.lead {
    color: #e47b5d;
    font-family: 'KaiTi', 'STKaiti', 'SimSun', 'Georgia', serif;
    font-size: 1.1rem;
    line-height: 1.6;
}

.feature-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 18px;
    margin-top: 34px;
}

.mini-card {
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.7);
}

.mini-card span {
    color: var(--text-muted);
    font-size: 0.92rem;
}

.mini-card strong {
    display: block;
    margin: 10px 0 8px;
    font-size: 2rem;
    color: #d4a555;
}

.alert {
    margin-top: 18px;
}

.form {
    margin-top: 24px;
}

.form-foot {
    display: flex;
    justify-content: space-between;
    margin-bottom: 18px;
    color: var(--text);
}

.switch-copy {
    margin-top: 18px;
    text-align: center;
    font-weight: 700;
}

.switch-copy a {
    color: var(--coral-deep);
}

@media (max-width: 980px) {
    .auth-shell {
        grid-template-columns: 1fr;
        min-height: auto;
    }

    h1 {
        max-width: none;
    }
}

@media (max-width: 720px) {
    .auth-page {
        padding: 16px;
    }

    .feature-grid {
        grid-template-columns: 1fr;
    }
}
</style>
