import { defineStore } from 'pinia'

import { getCurrentAdmin, loginAdmin, logoutAdmin } from '@/api/auth'
import type { AdminLoginRequest, LoginUser } from '@/types/auth'

type AuthState = {
    currentUser: LoginUser | null
    initialized: boolean
}

function normalizeAdmin(user: LoginUser | null) {
    if (!user || user.userRole !== 'admin') {
        return null
    }
    return user
}

export const useAdminAuthStore = defineStore('admin-auth', {
    state: (): AuthState => ({
        currentUser: null,
        initialized: false,
    }),
    getters: {
        isLoggedIn: (state) => Boolean(state.currentUser),
        isAdmin: (state) => state.currentUser?.userRole === 'admin',
    },
    actions: {
        setCurrentUser(user: LoginUser | null) {
            this.currentUser = normalizeAdmin(user)
            this.initialized = true
        },
        clearCurrentUser() {
            this.currentUser = null
            this.initialized = true
        },
        async fetchCurrentUser() {
            try {
                const user = await getCurrentAdmin()
                this.currentUser = normalizeAdmin(user)
            } catch {
                this.currentUser = null
            } finally {
                this.initialized = true
            }
            return this.currentUser
        },
        async login(payload: AdminLoginRequest) {
            const user = await loginAdmin(payload)
            if (user.userRole !== 'admin') {
                try {
                    await logoutAdmin()
                } catch {
                    // Ignore logout failures here and surface the real auth error.
                }
                this.clearCurrentUser()
                throw new Error('仅管理员账号可登录管理端')
            }
            this.setCurrentUser(user)
            return user
        },
        async logout() {
            try {
                await logoutAdmin()
            } finally {
                this.clearCurrentUser()
            }
        },
    },
})
