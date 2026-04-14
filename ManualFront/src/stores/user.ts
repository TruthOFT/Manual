import { defineStore } from 'pinia'

import { getCurrentUser, logoutUser } from '@/api/user'
import type { LoginUser } from '@/types/user'

type UserState = {
    currentUser: LoginUser | null
    initialized: boolean
}

export const useUserStore = defineStore('user', {
    state: (): UserState => ({
        currentUser: null,
        initialized: false,
    }),
    getters: {
        isLoggedIn: (state) => Boolean(state.currentUser),
    },
    actions: {
        setCurrentUser(user: LoginUser | null) {
            this.currentUser = user
            this.initialized = true
        },
        async fetchCurrentUser() {
            try {
                const user = await getCurrentUser()
                this.currentUser = user
            } catch {
                this.currentUser = null
            } finally {
                this.initialized = true
            }
        },
        async logout() {
            await logoutUser()
            this.currentUser = null
        },
    },
})
