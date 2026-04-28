import { createRouter, createWebHistory } from 'vue-router'

import AdminLayout from '@/components/layout/AdminLayout.vue'
import { pinia } from '@/stores'
import { useAdminAuthStore } from '@/stores/auth'
import AdminLoginView from '@/views/AdminLoginView.vue'
import CouponsView from '@/views/CouponsView.vue'
import CustomersView from '@/views/CustomersView.vue'
import DashboardView from '@/views/DashboardView.vue'
import ProductsView from '@/views/ProductsView.vue'
import StaffView from '@/views/StaffView.vue'

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
          path: 'products',
          name: 'products',
          component: ProductsView,
        },
        {
          path: 'customers',
          name: 'customers',
          component: CustomersView,
        },
        {
          path: 'staff',
          name: 'staff',
          component: StaffView,
        },
        {
          path: 'coupons',
          name: 'coupons',
          component: CouponsView,
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
