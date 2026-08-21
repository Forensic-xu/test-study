<template>
  <div class="page-card" data-testid="orders-page">
    <div class="toolbar">
      <el-input
        v-model="query.orderNo"
        data-testid="order-search-no"
        placeholder="订单号(前端过滤)"
        clearable
        style="width: 200px"
      />
      <el-select
        v-model="query.status"
        data-testid="order-search-status"
        clearable
        placeholder="状态"
        style="width: 160px"
      >
        <el-option v-for="s in statuses" :key="s" :label="s" :value="s" />
      </el-select>
      <el-button data-testid="order-search" type="primary" :loading="loading" @click="loadData">查询</el-button>
    </div>

    <el-table :data="filteredRows" v-loading="loading" data-testid="order-table">
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column prop="userId" label="用户" width="90" />
      <el-table-column prop="totalAmount" label="总金额" width="110" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="createdAt" label="创建时间" min-width="160" />
      <el-table-column prop="updatedAt" label="更新时间" min-width="160" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :data-testid="`order-detail-${row.id}`" @click="goDetail(row.id)">
            详情
          </el-button>
          <el-button
            v-if="isAdmin && row.status === 'PENDING'"
            link
            type="success"
            :data-testid="`order-pay-${row.id}`"
            @click="onPay(row.id)"
          >
            支付
          </el-button>
          <el-button
            v-if="isAdmin && row.status === 'PAID'"
            link
            type="warning"
            :data-testid="`order-ship-${row.id}`"
            @click="onShip(row.id)"
          >
            发货
          </el-button>
          <el-button
            v-if="isAdmin && row.status === 'SHIPPED'"
            link
            type="primary"
            :data-testid="`order-complete-${row.id}`"
            @click="onComplete(row.id)"
          >
            完成
          </el-button>
          <el-button
            v-if="row.status === 'PENDING'"
            link
            type="danger"
            :data-testid="`order-cancel-${row.id}`"
            @click="onCancel(row.id)"
          >
            取消
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      data-testid="order-pagination"
      layout="total, prev, pager, next"
      :total="total"
      v-model:current-page="query.page"
      :page-size="query.size"
      @current-change="loadData"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cancelOrder, completeOrder, pageOrders, payOrder, shipOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import type { OrderVO } from '@/types/api'

const router = useRouter()
const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)
const loading = ref(false)
const rows = ref<OrderVO[]>([])
const total = ref(0)
const statuses = ['PENDING', 'PAID', 'SHIPPED', 'COMPLETED', 'CANCELLED']
const query = reactive({
  page: 1,
  size: 10,
  status: undefined as string | undefined,
  orderNo: '',
})

const filteredRows = computed(() => {
  if (!query.orderNo.trim()) return rows.value
  return rows.value.filter((o) => o.orderNo.includes(query.orderNo.trim()))
})

async function loadData() {
  loading.value = true
  try {
    const data = await pageOrders({
      page: query.page,
      size: query.size,
      status: query.status,
    })
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  router.push(`/orders/${id}`)
}

async function onPay(id: number) {
  await payOrder(id)
  ElMessage.success('已支付')
  await loadData()
}

async function onShip(id: number) {
  await shipOrder(id)
  ElMessage.success('已发货')
  await loadData()
}

async function onComplete(id: number) {
  await completeOrder(id)
  ElMessage.success('已完成')
  await loadData()
}

async function onCancel(id: number) {
  await cancelOrder(id)
  ElMessage.success('已取消')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
