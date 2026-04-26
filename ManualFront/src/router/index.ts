import { createRouter, createWebHistory } from 'vue-router'

import ProfileLayout from '@/components/layout/ProfileLayout.vue'
import { pinia } from '@/stores'
import { useUserStore } from '@/stores/user'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import ProductDetailView from '@/views/ProductDetailView.vue'
import ProductsView from '@/views/ProductsView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ProfileAddressesView from '@/views/profile/ProfileAddressesView.vue'
import ProfileCouponsView from '@/views/profile/ProfileCouponsView.vue'
import ProfileFavoritesView from '@/views/profile/ProfileFavoritesView.vue'
import ProfileHomeView from '@/views/profile/ProfileHomeView.vue'
import ProfileOrdersView from '@/views/profile/ProfileOrdersView.vue'
import ProfileSettingsView from '@/views/profile/ProfileSettingsView.vue'

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
            path: '/products',
            name: 'products',
            component: ProductsView,
            meta: {
                public: true,
            },
        },
        {
            path: '/products/:id',
            name: 'product-detail',
            component: ProductDetailView,
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
            path: '/profile',
            component: ProfileLayout,
            meta: {
                requiresAuth: true,
            },
            children: [
                {
                    path: '',
                    name: 'profile',
                    component: ProfileHomeView,
                },
                {
                    path: 'orders',
                    name: 'profile-orders',
                    component: ProfileOrdersView,
                },
                {
                    path: 'favorites',
                    name: 'profile-favorites',
                    component: ProfileFavoritesView,
                },
                {
                    path: 'coupons',
                    name: 'profile-coupons',
                    component: ProfileCouponsView,
                },
                {
                    path: 'addresses',
                    name: 'profile-addresses',
                    component: ProfileAddressesView,
                },
                {
                    path: 'settings',
                    name: 'profile-settings',
                    component: ProfileSettingsView,
                },
            ],
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
            path: '/profile',
        }
    }

    if (to.meta.requiresAuth && !userStore.isLoggedIn) {
        return {
            path: '/login',
            query: {
                redirect: to.fullPath,
            },
        }
    }

    return true
})

export default router
