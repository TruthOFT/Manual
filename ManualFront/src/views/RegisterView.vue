<script setup lang="ts">
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { registerUser } from '@/api/user'

const route = useRoute()
const router = useRouter()

const form = reactive({
    userAccount: '',
    userPassword: '',
    checkPassword: '',
})

const loading = ref(false)
const errorMessage = ref('')

async function handleSubmit() {
    errorMessage.value = ''

    if (!form.userAccount.trim() || !form.userPassword.trim() || !form.checkPassword.trim()) {
        errorMessage.value = '请完整填写所有字段。'
        return
    }

    if (form.userAccount.trim().length < 4) {
        errorMessage.value = '账号长度至少为 4 位。'
        return
    }

    if (form.userPassword.length < 8) {
        errorMessage.value = '密码长度至少为 8 位。'
        return
    }

    if (form.userPassword !== form.checkPassword) {
        errorMessage.value = '两次输入的密码不一致。'
        return
    }

    loading.value = true
    try {
        await registerUser({
            userAccount: form.userAccount.trim(),
            userPassword: form.userPassword,
            checkPassword: form.checkPassword,
        })
        const nextQuery: Record<string, string> = {
            account: form.userAccount.trim(),
            registered: '1',
        }
        if (typeof route.query.redirect === 'string' && route.query.redirect.length > 0) {
            nextQuery.redirect = route.query.redirect
        }
        await router.push({
            path: '/login',
            query: nextQuery,
        })
    } catch (error) {
        errorMessage.value = error instanceof Error ? error.message : '注册失败，请稍后重试。'
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
                <p class="eyebrow">创建门店账号</p>
                <h1>把你的商品、门店节奏与门店故事放进一个清晰统一的系统中。</h1>
                <p class="lead">
                    当前注册只保留后端已经支持的最小字段，后续继续接业务接口时不需要再重做这一页。
                </p>

                <div class="feature-grid">
                    <a-card class="mini-card" :bordered="false">
                        <strong>精选上新</strong>
                        <p>把主推商品、节日礼盒和服务系列统一整理到一个流程里。</p>
                    </a-card>
                    <a-card class="mini-card" :bordered="false">
                        <strong>门店节奏</strong>
                        <p>通过首页快速查看销量变化、主推分类和订单动态。</p>
                    </a-card>
                    <a-card class="mini-card" :bordered="false">
                        <strong>门店协作</strong>
                        <p>把手作人、工作室与品牌展示统一在同一套门店语言里。</p>
                    </a-card>
                </div>
            </a-card>

            <a-card class="form-card" :bordered="false">
                <p class="card-label">开始注册</p>
                <h2>创建账号</h2>
                <p class="card-copy">先完成基础注册，后面接入真实业务接口时就不需要再重做页面结构。</p>

                <a-alert v-if="errorMessage" class="alert" type="error" :message="errorMessage" show-icon />

                <a-form :model="form" layout="vertical" class="form">
                    <a-form-item label="账号" name="userAccount">
                        <a-input v-model:value="form.userAccount" size="large" placeholder="请输入至少 4 位账号">
                            <template #prefix>
                                <UserOutlined />
                            </template>
                        </a-input>
                    </a-form-item>

                    <a-form-item label="密码" name="userPassword">
                        <a-input-password v-model:value="form.userPassword" size="large"
                            placeholder="请输入至少 8 位密码">
                            <template #prefix>
                                <LockOutlined />
                            </template>
                        </a-input-password>
                    </a-form-item>

                    <a-form-item label="确认密码" name="checkPassword">
                        <a-input-password v-model:value="form.checkPassword" size="large" placeholder="请再次输入密码">
                            <template #prefix>
                                <LockOutlined />
                            </template>
                        </a-input-password>
                    </a-form-item>

                    <a-button
                        class="manual-ant-btn manual-ant-btn-primary"
                        size="large"
                        html-type="button"
                        block
                        :loading="loading"
                        @click="handleSubmit"
                    >
                        注册账号
                    </a-button>
                </a-form>

                <p class="switch-copy">
                    已有账号？
                    <RouterLink
                        :to="{ path: '/login', query: route.query.redirect ? { redirect: String(route.query.redirect) } : undefined }">
                        去登录
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
    grid-template-columns: minmax(0, 1.08fr) minmax(360px, 460px);
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
        linear-gradient(155deg, rgba(255, 252, 246, 0.95), rgba(255, 237, 219, 0.9)),
        radial-gradient(circle at top left, rgba(255, 197, 109, 0.24), transparent 34%);
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
    top: 44px;
    left: -60px;
    width: 280px;
    height: 280px;
    background: rgba(255, 195, 125, 0.22);
}

.orb-right {
    right: -50px;
    bottom: 44px;
    width: 260px;
    height: 260px;
    background: rgba(228, 123, 93, 0.16);
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
    max-width: 12ch;
    margin: 18px 0 16px;
    font-size: clamp(3rem, 5vw, 5.2rem);
    line-height: 0.96;
}

h2 {
    margin: 14px 0 10px;
    font-size: clamp(2rem, 3vw, 3rem);
    line-height: 1;
}

.lead,
.card-copy,
.mini-card p,
.switch-copy {
    color: var(--text);
}

.feature-grid {
    display: grid;
    gap: 16px;
    margin-top: 34px;
}

.mini-card {
    border-radius: 24px;
    background: rgba(255, 255, 255, 0.7);
}

.mini-card strong {
    display: block;
    margin-bottom: 8px;
    font-size: 1.5rem;
}

.mini-card p {
    margin: 0;
}

.alert {
    margin-top: 18px;
}

.form {
    margin-top: 24px;
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
}
</style>
