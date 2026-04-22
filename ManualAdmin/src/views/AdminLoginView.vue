<script setup lang="ts">
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { message, type FormInstance } from 'ant-design-vue'
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useAdminAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAdminAuthStore()
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formState = reactive({
    userAccount: '',
    userPassword: '',
    rememberMe: true,
})

async function handleSubmit() {
    if (submitting.value) {
        return
    }
    try {
        await formRef.value?.validate()
    } catch {
        return
    }
    submitting.value = true
    try {
        await authStore.login(formState)
        const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
        await router.replace(redirect)
    } catch (error) {
        message.error(error instanceof Error ? error.message : '登录失败')
    } finally {
        submitting.value = false
    }
}
</script>

<template>
    <main class="login-page">
        <section class="hero-panel">
            <p class="eyebrow">Manual Admin</p>
            <h1>管理员工作台</h1>
            <p class="intro">
                统一处理用户、商品、订单和分类数据。仅支持 admin 角色登录。
            </p>
            <ul class="feature-list">
                <li>用户管理与账号维护</li>
                <li>商品审核与分类配置</li>
                <li>订单排查与后台运营支持</li>
            </ul>
        </section>

        <section class="form-panel">
            <a-card :bordered="false" class="login-card">
                <div class="card-head">
                    <p>管理员登录</p>
                    <span>请输入后台账号信息</span>
                </div>

                <a-form
                    ref="formRef"
                    :model="formState"
                    layout="vertical"
                    name="admin-login-form"
                    @finish="handleSubmit"
                >
                    <a-form-item
                        label="账号"
                        name="userAccount"
                        :rules="[{ required: true, message: '请输入账号' }]"
                    >
                        <a-input
                            v-model:value="formState.userAccount"
                            size="large"
                            placeholder="请输入管理员账号"
                            @pressEnter="handleSubmit"
                        >
                            <template #prefix>
                                <UserOutlined />
                            </template>
                        </a-input>
                    </a-form-item>

                    <a-form-item
                        label="密码"
                        name="userPassword"
                        :rules="[{ required: true, message: '请输入密码' }]"
                    >
                        <a-input-password
                            v-model:value="formState.userPassword"
                            size="large"
                            placeholder="请输入密码"
                            @pressEnter="handleSubmit"
                        >
                            <template #prefix>
                                <LockOutlined />
                            </template>
                        </a-input-password>
                    </a-form-item>

                    <div class="form-actions">
                        <a-checkbox v-model:checked="formState.rememberMe">7 天免登录</a-checkbox>
                    </div>

                    <a-button
                        block
                        size="large"
                        type="primary"
                        class="submit-button"
                        :loading="submitting"
                        @click="handleSubmit"
                    >
                        登录管理端
                    </a-button>
                </a-form>
            </a-card>
        </section>
    </main>
</template>

<style scoped>
.login-page {
    display: grid;
    grid-template-columns: minmax(0, 1.2fr) minmax(360px, 460px);
    min-height: 100vh;
    background:
        radial-gradient(circle at top left, rgba(255, 214, 131, 0.28), transparent 36%),
        radial-gradient(circle at bottom right, rgba(47, 111, 228, 0.24), transparent 32%),
        linear-gradient(135deg, #f7f3eb 0%, #eef3ff 52%, #f7fbff 100%);
}

.hero-panel {
    display: grid;
    align-content: center;
    gap: 18px;
    padding: 72px min(8vw, 96px);
}

.eyebrow {
    margin: 0;
    color: #2f6fe4;
    font-size: 0.84rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
}

h1 {
    margin: 0;
    color: #102139;
    font-family: var(--font-display);
    font-size: clamp(2.8rem, 6vw, 4.8rem);
    line-height: 0.96;
}

.intro {
    max-width: 520px;
    margin: 0;
    color: #4c6077;
    font-size: 1.02rem;
    line-height: 1.8;
}

.feature-list {
    display: grid;
    gap: 12px;
    padding: 0;
    margin: 8px 0 0;
    list-style: none;
    color: #17345a;
}

.feature-list li {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    font-weight: 600;
}

.feature-list li::before {
    content: '';
    width: 10px;
    height: 10px;
    border-radius: 999px;
    background: linear-gradient(135deg, #2f6fe4, #f0b44c);
}

.form-panel {
    display: grid;
    place-items: center;
    padding: 32px;
}

.login-card {
    width: min(100%, 420px);
    padding: 18px;
    border-radius: 28px;
    background: rgba(255, 255, 255, 0.84);
    box-shadow: 0 24px 60px rgba(16, 33, 57, 0.14);
    backdrop-filter: blur(14px);
}

.card-head {
    margin-bottom: 28px;
}

.card-head p {
    margin: 0;
    color: #102139;
    font-family: var(--font-display);
    font-size: 1.8rem;
}

.card-head span {
    color: #60758d;
}

.form-actions {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    color: #60758d;
}

.submit-button {
    height: 48px;
    border: 0;
    border-radius: 16px;
    background: linear-gradient(135deg, #1d5fda, #0f8a7a);
    box-shadow: 0 14px 28px rgba(29, 95, 218, 0.24);
}

@media (max-width: 960px) {
    .login-page {
        grid-template-columns: 1fr;
    }

    .hero-panel {
        padding-bottom: 24px;
    }

    .form-panel {
        padding-top: 0;
    }
}
</style>
