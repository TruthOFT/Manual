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
        errorMessage.value = 'Please fill in every field.'
        return
    }

    if (form.userAccount.trim().length < 4) {
        errorMessage.value = 'Account length must be at least 4 characters.'
        return
    }

    if (form.userPassword.length < 8) {
        errorMessage.value = 'Password length must be at least 8 characters.'
        return
    }

    if (form.userPassword !== form.checkPassword) {
        errorMessage.value = 'The two passwords do not match.'
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
        errorMessage.value = error instanceof Error ? error.message : 'Registration failed. Please try again later.'
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
                <p class="eyebrow">create your shop account</p>
                <h1>Bring your products, artisan rhythm, and store story into one clean system.</h1>
                <p class="lead">
                    Registration uses only the minimal fields already supported by the planned backend contract, so later integration stays simple.
                </p>

                <div class="feature-grid">
                    <a-card class="mini-card" :bordered="false">
                        <strong>Curated drops</strong>
                        <p>Keep hero products, seasonal sets, and custom runs organized in one flow.</p>
                    </a-card>
                    <a-card class="mini-card" :bordered="false">
                        <strong>Store pulse</strong>
                        <p>Use the home page to read sales rhythm, featured categories, and order motion.</p>
                    </a-card>
                    <a-card class="mini-card" :bordered="false">
                        <strong>Artisan network</strong>
                        <p>Keep makers, studios, and display stories within the same product language.</p>
                    </a-card>
                </div>
            </a-card>

            <a-card class="form-card" :bordered="false">
                <p class="card-label">start onboarding</p>
                <h2>Create an account</h2>
                <p class="card-copy">This page is intentionally small now, so the later backend hookup does not require another redesign.</p>

                <a-alert v-if="errorMessage" class="alert" type="error" :message="errorMessage" show-icon />

                <a-form layout="vertical" class="form" @finish="handleSubmit">
                    <a-form-item label="Account" name="userAccount">
                        <a-input v-model:value="form.userAccount" size="large" placeholder="At least 4 characters">
                            <template #prefix>
                                <UserOutlined />
                            </template>
                        </a-input>
                    </a-form-item>

                    <a-form-item label="Password" name="userPassword">
                        <a-input-password v-model:value="form.userPassword" size="large" placeholder="At least 8 characters">
                            <template #prefix>
                                <LockOutlined />
                            </template>
                        </a-input-password>
                    </a-form-item>

                    <a-form-item label="Confirm password" name="checkPassword">
                        <a-input-password v-model:value="form.checkPassword" size="large" placeholder="Repeat password">
                            <template #prefix>
                                <LockOutlined />
                            </template>
                        </a-input-password>
                    </a-form-item>

                    <a-button class="manual-ant-btn manual-ant-btn-primary" size="large" html-type="submit" block :loading="loading">Create account</a-button>
                </a-form>

                <p class="switch-copy">
                    Already registered?
                    <RouterLink :to="{ path: '/login', query: route.query.redirect ? { redirect: String(route.query.redirect) } : undefined }">
                        Sign in
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
