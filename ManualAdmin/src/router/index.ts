import { createRouter, createWebHistory } from 'vue-router'

import AdminLayout from '@/components/layout/AdminLayout.vue'
import { pinia } from '@/stores'
import { useAdminAuthStore } from '@/stores/auth'
import AdminLoginView from '@/views/AdminLoginView.vue'
import ArtisanApplicationsView from '@/views/ArtisanApplicationsView.vue'
import CategoriesView from '@/views/CategoriesView.vue'
import DashboardView from '@/views/DashboardView.vue'
import OrdersView from '@/views/OrdersView.vue'
import ProductsView from '@/views/ProductsView.vue'
import SettingsView from '@/views/SettingsView.vue'
import UsersView from '@/views/UsersView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'admin-login',
      component: AdminLoginView,
      meta: {
        guestOnly: true,
      },
    },
    {
      path: '/',
      component: AdminLayout,
      meta: {
        requiresAdmin: true,
      },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: DashboardView,
        },
        {
          path: 'users',
          name: 'users',
          component: UsersView,
        },
        {
          path: 'artisan-applications',
          name: 'artisan-applications',
          component: ArtisanApplicationsView,
        },
        {
          path: 'products',
          name: 'products',
          component: ProductsView,
        },
        {
          path: 'orders',
          name: 'orders',
          component: OrdersView,
        },
        {
          path: 'categories',
          name: 'categories',
          component: CategoriesView,
        },
        {
          path: 'settings',
          name: 'settings',
          component: SettingsView,
        },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  const authStore = useAdminAuthStore(pinia)
  if (!authStore.initialized) {
    await authStore.fetchCurrentUser()
  }

  const requiresAdmin = to.matched.some((record) => record.meta.requiresAdmin)
  const guestOnly = to.matched.some((record) => record.meta.guestOnly)

  if (guestOnly) {
    if (authStore.isAdmin) {
      const redirect = typeof to.query.redirect === 'string' ? to.query.redirect : '/'
      return redirect
    }
    return true
  }

  if (requiresAdmin && !authStore.isAdmin) {
    return {
      name: 'admin-login',
      query: {
        redirect: to.fullPath,
      },
    }
  }

  return true
})

export default router