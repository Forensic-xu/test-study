<template>
  <div class="page-card" data-testid="dashboard-page">
    <h2>Dashboard</h2>
    <el-row :gutter="16" v-loading="loading">
      <el-col :span="6" v-for="item in cards" :key="item.key">
        <el-card :data-testid="`dashboard-card-${item.key}`">
          <div class="label">{{ item.label }}</div>
          <div class="value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { listCategories } from '@/api/category'
import { pageOrders } from '@/api/order'
import { pageProducts } from '@/api/product'
import { listUsers } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const cards = reactive([
  { key: 'products', label: '商品数量', value: '-' },
  { key: 'categories', label: '分类数量', value: '-' },
  { key: 'users', label: '用户数量', value: '-' },
  { key: 'orders', label: '订单数量', value: '-' },
  { key: 'low-stock', label: '库存预警(≤5)', value: '-' },
])

onMounted(async () => {
  loading.value = true
  try {
    const products = await pageProducts({ page: 1, size: 100 })
    cards[0].value = String(products.total)
    cards[4].value = String(products.records.filter((p) => p.stock <= 5).length)

    const categories = await listCategories()
    cards[1].value = String(categories.length)

    const orders = await pageOrders({ page: 1, size: 1 })
    cards[3].value = String(orders.total)

    if (userStore.isAdmin) {
      const users = await listUsers()
      cards[2].value = String(users.length)
    } else {
      cards[2].value = '-'
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.label {
  color: #909399;
  margin-bottom: 8px;
}
.value {
  font-size: 28px;
  font-weight: 700;
}
.el-col {
  margin-bottom: 16px;
}
</style>
