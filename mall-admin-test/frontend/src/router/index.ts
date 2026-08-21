import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/request'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { public: true },
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: 'Dashboard', roles: ['ADMIN', 'USER'] },
      },
      {
        path: 'users',
        name: 'users',
        component: () => import('@/views/users/UsersView.vue'),
        meta: { title: '用户管理', roles: ['ADMIN'] },
      },
      {
        path: 'categories',
        name: 'categories',
        component: () => import('@/views/categories/CategoriesView.vue'),
        meta: { title: '分类管理', roles: ['ADMIN'] },
      },
      {
        path: 'products',
        name: 'products',
        component: () => import('@/views/products/ProductsView.vue'),
        meta: { title: '商品管理', roles: ['ADMIN'] },
      },
      {
        path: 'inventory',
        name: 'inventory',
        component: () => import('@/views/inventory/InventoryView.vue'),
        meta: { title: '库存管理', roles: ['ADMIN'] },
      },
      {
        path: 'orders',
        name: 'orders',
        component: () => import('@/views/orders/OrdersView.vue'),
        meta: { title: '订单管理', roles: ['ADMIN', 'USER'] },
      },
      {
        path: 'orders/:id',
        name: 'order-detail',
        component: () => import('@/views/orders/OrderDetailView.vue'),
        meta: { title: '订单详情', roles: ['ADMIN', 'USER'] },
      },
      {
        path: 'logs',
        name: 'logs',
        component: () => import('@/views/logs/LogsView.vue'),
        meta: { title: '操作日志', roles: ['ADMIN'] },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  if (to.meta.public) {
    return true
  }
  const token = getToken()
  if (!token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  const userStore = useUserStore()
  const roles = to.meta.roles as string[] | undefined
  if (roles && userStore.userInfo && !roles.includes(userStore.userInfo.role)) {
    return { path: '/dashboard' }
  }
  return true
})

export default router
