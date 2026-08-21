<template>
  <div class="page-card" data-testid="inventory-page">
    <div class="toolbar">
      <el-input
        v-model="productIdInput"
        data-testid="inventory-product-id"
        placeholder="商品ID"
        style="width: 160px"
      />
      <el-button data-testid="inventory-search" type="primary" :loading="loading" @click="loadAll">查询</el-button>
    </div>

    <el-descriptions v-if="inventory" :column="2" border class="mb16" data-testid="inventory-info">
      <el-descriptions-item label="商品ID">{{ inventory.productId }}</el-descriptions-item>
      <el-descriptions-item label="商品名称">{{ inventory.productName }}</el-descriptions-item>
      <el-descriptions-item label="当前库存">
        <span data-testid="inventory-current-stock">{{ inventory.stock }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="状态">{{ inventory.status }}</el-descriptions-item>
    </el-descriptions>

    <div v-if="inventory" class="toolbar">
      <el-input-number v-model="changeQty" data-testid="inventory-quantity" :min="1" />
      <el-input v-model="remark" data-testid="inventory-remark" placeholder="备注" style="width: 220px" />
      <el-button data-testid="inventory-increase" type="success" :loading="acting" @click="onIncrease">增加库存</el-button>
      <el-button data-testid="inventory-decrease" type="warning" :loading="acting" @click="onDecrease">减少库存</el-button>
      <el-button data-testid="inventory-records" @click="loadRecords">刷新流水</el-button>
    </div>

    <el-table :data="records" v-loading="loading" data-testid="inventory-records-table">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="productId" label="商品" width="90" />
      <el-table-column prop="beforeStock" label="变化前" width="90" />
      <el-table-column prop="changeQuantity" label="变化数量" width="100" />
      <el-table-column prop="afterStock" label="变化后" width="90" />
      <el-table-column prop="operationType" label="操作类型" width="160" />
      <el-table-column prop="operatorId" label="操作人" width="90" />
      <el-table-column prop="remark" label="备注" min-width="140" />
      <el-table-column prop="createdAt" label="时间" min-width="160" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  decreaseInventory,
  getInventory,
  increaseInventory,
  listInventoryRecords,
} from '@/api/inventory'
import type { InventoryRecordVO, InventoryVO } from '@/types/api'

const productIdInput = ref('1')
const loading = ref(false)
const acting = ref(false)
const inventory = ref<InventoryVO | null>(null)
const records = ref<InventoryRecordVO[]>([])
const changeQty = ref(1)
const remark = ref('')

async function loadAll() {
  const id = Number(productIdInput.value)
  if (!id) {
    ElMessage.warning('请输入有效商品ID')
    return
  }
  loading.value = true
  try {
    inventory.value = await getInventory(id)
    records.value = await listInventoryRecords(id)
  } finally {
    loading.value = false
  }
}

async function loadRecords() {
  if (!inventory.value) return
  records.value = await listInventoryRecords(inventory.value.productId)
}

async function onIncrease() {
  if (!inventory.value) return
  acting.value = true
  try {
    inventory.value = await increaseInventory(inventory.value.productId, changeQty.value, remark.value)
    ElMessage.success('库存已增加')
    await loadRecords()
  } finally {
    acting.value = false
  }
}

async function onDecrease() {
  if (!inventory.value) return
  acting.value = true
  try {
    inventory.value = await decreaseInventory(inventory.value.productId, changeQty.value, remark.value)
    ElMessage.success('库存已减少')
    await loadRecords()
  } finally {
    acting.value = false
  }
}
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}
</style>
