<template>
  <el-container class="layout" data-testid="admin-layout">
    <el-aside width="220px" class="aside">
      <div class="logo" data-testid="app-logo">mall-admin-test</div>
      <el-menu :default-active="activeMenu" router data-testid="side-menu">
        <el-menu-item index="/dashboard" data-testid="menu-dashboard">
          <span>Dashboard</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/users" data-testid="menu-users">
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/categories" data-testid="menu-categories">
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/products" data-testid="menu-products">
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/inventory" data-testid="menu-inventory">
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/orders" data-testid="menu-orders">
          <span>{{ userStore.isAdmin ? '订单管理' : '我的订单' }}</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.isAdmin" index="/logs" data-testid="menu-logs">
          <span>操作日志</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="header-right">
          <span data-testid="header-username">用户：{{ userStore.userInfo?.username }}</span>
          <el-button data-testid="logout-btn" type="primary" link @click="onLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => {
  if (route.path.startsWith('/orders')) return '/orders'
  return route.path
})

const currentTitle = computed(() => (route.meta.title as string) || 'Dashboard')

function onLogout() {
  userStore.logout()
  router.replace('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
.aside {
  background: #1f2d3d;
  color: #fff;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #fff;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.aside :deep(.el-menu) {
  border-right: none;
  background: transparent;
}
.aside :deep(.el-menu-item) {
  color: #c0c4cc;
}
.aside :deep(.el-menu-item.is-active) {
  background: #409eff !important;
  color: #fff;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
