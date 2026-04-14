import { createRouter, createWebHistory } from 'vue-router'

import { pinia } from '@/stores'
import { useUserStore } from '@/stores/user'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
            meta: {
                public: true,
            },
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView,
            meta: {
                public: true,
                guestOnly: true,
            },
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterView,
            meta: {
                public: true,
                guestOnly: true,
            },
        },
        {
            path: '/about',
            name: 'about',
            component: () => import('@/views/AboutView.vue'),
            meta: {
                public: true,
            },
        },
    ],
})

router.beforeEach(async (to) => {
    const userStore = useUserStore(pinia)

    if (!userStore.initialized) {
        await userStore.fetchCurrentUser()
    }

    if (to.meta.guestOnly && userStore.isLoggedIn) {
        return {
            path: '/',
        }
    }

    return true
})

export default router
