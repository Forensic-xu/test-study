<template>
  <div class="page-card" data-testid="order-detail-page" v-loading="loading">
    <div class="toolbar">
      <el-button data-testid="order-detail-back" @click="$router.push('/orders')">返回列表</el-button>
      <el-button
        v-if="isAdmin && order?.status === 'PENDING'"
        data-testid="order-detail-pay"
        type="success"
        @click="onPay"
      >
        支付
      </el-button>
      <el-button
        v-if="isAdmin && order?.status === 'PAID'"
        data-testid="order-detail-ship"
        type="warning"
        @click="onShip"
      >
        发货
      </el-button>
      <el-button
        v-if="isAdmin && order?.status === 'SHIPPED'"
        data-testid="order-detail-complete"
        type="primary"
        @click="onComplete"
      >
        完成
      </el-button>
      <el-button
        v-if="order?.status === 'PENDING'"
        data-testid="order-detail-cancel"
        type="danger"
        @click="onCancel"
      >
        取消
      </el-button>
    </div>

    <el-descriptions v-if="order" :column="2" border data-testid="order-detail-info">
      <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="用户ID">{{ order.userId }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ order.status }}</el-descriptions-item>
      <el-descriptions-item label="总金额">{{ order.totalAmount }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ order.createdAt }}</el-descriptions-item>
      <el-descriptions-item label="更新时间">{{ order.updatedAt }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ order.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <h3>商品明细</h3>
    <el-table :data="order?.items || []" data-testid="order-detail-items">
      <el-table-column prop="productId" label="商品ID" width="90" />
      <el-table-column prop="productName" label="商品名称" />
      <el-table-column prop="productPrice" label="下单价格" width="110" />
      <el-table-column prop="quantity" label="数量" width="90" />
      <el-table-column prop="subtotal" label="小计" width="110" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cancelOrder, completeOrder, getOrder, payOrder, shipOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import type { OrderVO } from '@/types/api'

const route = useRoute()
const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)
const loading = ref(false)
const order = ref<OrderVO | null>(null)

async function loadData() {
  loading.value = true
  try {
    order.value = await getOrder(Number(route.params.id))
  } finally {
    loading.value = false
  }
}

async function onPay() {
  if (!order.value) return
  order.value = await payOrder(order.value.id)
  ElMessage.success('已支付')
}

async function onShip() {
  if (!order.value) return
  order.value = await shipOrder(order.value.id)
  ElMessage.success('已发货')
}

async function onComplete() {
  if (!order.value) return
  order.value = await completeOrder(order.value.id)
  ElMessage.success('已完成')
}

async function onCancel() {
  if (!order.value) return
  order.value = await cancelOrder(order.value.id)
  ElMessage.success('已取消')
}

onMounted(loadData)
</script>

<style scoped>
h3 {
  margin: 20px 0 12px;
}
</style>
