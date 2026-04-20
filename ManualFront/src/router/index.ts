import { createRouter, createWebHistory } from 'vue-router'

import ArtisanLayout from '@/components/layout/ArtisanLayout.vue'
import ProfileLayout from '@/components/layout/ProfileLayout.vue'
import ArtisansView from '@/views/ArtisansView.vue'
import ArtisanDetailView from '@/views/ArtisanDetailView.vue'
import CustomView from '@/views/CustomView.vue'
import { pinia } from '@/stores'
import { useUserStore } from '@/stores/user'
import ArtisanCustomRequirementDetailView from '@/views/artisan/ArtisanCustomRequirementDetailView.vue'
import ArtisanCustomRequirementsView from '@/views/artisan/ArtisanCustomRequirementsView.vue'
import ArtisanDashboardView from '@/views/artisan/ArtisanDashboardView.vue'
import ArtisanOrderDetailView from '@/views/artisan/ArtisanOrderDetailView.vue'
import ArtisanOrdersView from '@/views/artisan/ArtisanOrdersView.vue'
import ArtisanProductEditorView from '@/views/artisan/ArtisanProductEditorView.vue'
import ArtisanProductsView from '@/views/artisan/ArtisanProductsView.vue'
import ArtisanProfileView from '@/views/artisan/ArtisanProfileView.vue'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import ProductDetailView from '@/views/ProductDetailView.vue'
import ProductsView from '@/views/ProductsView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ProfileAddressesView from '@/views/profile/ProfileAddressesView.vue'
import ProfileArtisanApplicationView from '@/views/profile/ProfileArtisanApplicationView.vue'
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
            path: '/artisans',
            name: 'artisans',
            component: ArtisansView,
            meta: {
                public: true,
            },
        },
        {
            path: '/artisans/:id',
            name: 'artisan-detail',
            component: ArtisanDetailView,
            meta: {
                public: true,
            },
        },
        {
            path: '/custom',
            name: 'custom',
            component: CustomView,
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
            path: '/artisan',
            component: ArtisanLayout,
            meta: {
                requiresAuth: true,
                requiresArtisan: true,
            },
            children: [
                {
                    path: '',
                    name: 'artisan-dashboard',
                    component: ArtisanDashboardView,
                },
                {
                    path: 'profile',
                    name: 'artisan-profile',
                    component: ArtisanProfileView,
                },
                {
                    path: 'products',
                    name: 'artisan-products',
                    component: ArtisanProductsView,
                },
                {
                    path: 'products/new',
                    name: 'artisan-product-new',
                    component: ArtisanProductEditorView,
                },
                {
                    path: 'products/:id',
                    name: 'artisan-product-detail',
                    component: ArtisanProductEditorView,
                },
                {
                    path: 'products/:id/edit',
                    name: 'artisan-product-edit',
                    component: ArtisanProductEditorView,
                },
                {
                    path: 'orders',
                    name: 'artisan-orders',
                    component: ArtisanOrdersView,
                },
                {
                    path: 'orders/:id',
                    name: 'artisan-order-detail',
                    component: ArtisanOrderDetailView,
                },
                {
                    path: 'custom-requirements',
                    name: 'artisan-custom-requirements',
                    component: ArtisanCustomRequirementsView,
                },
                {
                    path: 'custom-requirements/:id',
                    name: 'artisan-custom-requirement-detail',
                    component: ArtisanCustomRequirementDetailView,
                },
            ],
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
                    path: 'addresses',
                    name: 'profile-addresses',
                    component: ProfileAddressesView,
                },
                {
                    path: 'artisan-application',
                    name: 'profile-artisan-application',
                    component: ProfileArtisanApplicationView,
                    meta: {
                        requiresNormalUser: true,
                    },
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
            path: userStore.currentUser?.userRole === 'artisan' ? '/artisan' : '/profile',
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

    if (to.meta.requiresArtisan && userStore.currentUser?.userRole !== 'artisan') {
        return {
            path: '/',
        }
    }

    if (to.meta.requiresNormalUser && userStore.currentUser?.userRole !== 'user') {
        return {
            path: '/profile',
        }
    }

    return true
})

export default router
