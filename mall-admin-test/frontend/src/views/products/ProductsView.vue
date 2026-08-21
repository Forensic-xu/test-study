<template>
  <div class="page-card" data-testid="products-page">
    <div class="toolbar">
      <el-input
        v-model="query.name"
        data-testid="product-search-name"
        placeholder="商品名称"
        clearable
        style="width: 180px"
      />
      <el-select
        v-model="query.categoryId"
        data-testid="product-search-category"
        clearable
        placeholder="分类"
        style="width: 160px"
      >
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-select
        v-model="query.status"
        data-testid="product-search-status"
        clearable
        placeholder="状态"
        style="width: 140px"
      >
        <el-option label="ON_SALE" value="ON_SALE" />
        <el-option label="OFF_SALE" value="OFF_SALE" />
      </el-select>
      <el-button data-testid="product-search" type="primary" :loading="loading" @click="loadData">查询</el-button>
      <el-button data-testid="product-create" type="success" @click="openCreate">新增商品</el-button>
    </div>

    <el-table :data="rows" v-loading="loading" data-testid="product-table">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="商品名称" min-width="140" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="90" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column prop="createdAt" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :data-testid="`product-detail-${row.id}`" @click="openDetail(row)">详情</el-button>
          <el-button link type="primary" :data-testid="`product-edit-${row.id}`" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" :data-testid="`product-delete-${row.id}`" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      data-testid="product-pagination"
      layout="total, prev, pager, next"
      :total="total"
      v-model:current-page="query.page"
      :page-size="query.size"
      @current-change="loadData"
    />

    <el-dialog v-model="visible" :title="dialogTitle" width="520px">
      <el-form label-width="90px">
        <el-form-item label="名称">
          <el-input v-model="form.name" data-testid="product-form-name" :disabled="mode === 'detail'" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="form.categoryId"
            data-testid="product-form-category"
            style="width: 100%"
            :disabled="mode === 'detail'"
          >
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number
            v-model="form.price"
            data-testid="product-form-price"
            :min="0.01"
            :step="1"
            :disabled="mode === 'detail'"
          />
        </el-form-item>
        <el-form-item v-if="mode === 'create'" label="初始库存">
          <el-input-number v-model="form.stock" data-testid="product-form-stock" :min="0" />
        </el-form-item>
        <el-form-item v-else label="库存">
          <span data-testid="product-form-stock-readonly">{{ form.stock }}（请到库存管理调整）</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="form.status"
            data-testid="product-form-status"
            style="width: 100%"
            :disabled="mode === 'detail'"
          >
            <el-option label="ON_SALE" value="ON_SALE" />
            <el-option label="OFF_SALE" value="OFF_SALE" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            data-testid="product-form-description"
            type="textarea"
            :disabled="mode === 'detail'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button data-testid="product-form-cancel" @click="visible = false">关闭</el-button>
        <el-button
          v-if="mode !== 'detail'"
          data-testid="product-form-submit"
          type="primary"
          :loading="saving"
          @click="onSave"
        >
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCategories } from '@/api/category'
import { createProduct, deleteProduct, pageProducts, updateProduct } from '@/api/product'
import type { CategoryVO, ProductVO } from '@/types/api'

const loading = ref(false)
const saving = ref(false)
const rows = ref<ProductVO[]>([])
const categories = ref<CategoryVO[]>([])
const total = ref(0)
const visible = ref(false)
const mode = ref<'create' | 'edit' | 'detail'>('create')
const editingId = ref<number | null>(null)

const query = reactive({
  page: 1,
  size: 10,
  name: '',
  categoryId: undefined as number | undefined,
  status: undefined as string | undefined,
})

const form = reactive({
  name: '',
  categoryId: undefined as number | undefined,
  price: 100,
  stock: 0,
  status: 'ON_SALE',
  description: '',
})

const dialogTitle = computed(() => {
  if (mode.value === 'create') return '新增商品'
  if (mode.value === 'edit') return '编辑商品'
  return '商品详情'
})

async function loadData() {
  loading.value = true
  try {
    const data = await pageProducts({
      page: query.page,
      size: query.size,
      name: query.name || undefined,
      categoryId: query.categoryId,
      status: query.status,
    })
    rows.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function openCreate() {
  mode.value = 'create'
  editingId.value = null
  Object.assign(form, {
    name: '',
    categoryId: categories.value[0]?.id,
    price: 100,
    stock: 0,
    status: 'ON_SALE',
    description: '',
  })
  visible.value = true
}

function openEdit(row: ProductVO) {
  mode.value = 'edit'
  editingId.value = row.id
  Object.assign(form, {
    name: row.name,
    categoryId: row.categoryId,
    price: Number(row.price),
    stock: row.stock,
    status: row.status,
    description: row.description || '',
  })
  visible.value = true
}

function openDetail(row: ProductVO) {
  openEdit(row)
  mode.value = 'detail'
}

async function onSave() {
  if (!form.name.trim()) {
    ElMessage.warning('商品名称不能为空')
    return
  }
  if (!form.categoryId) {
    ElMessage.warning('请选择分类')
    return
  }
  if (!form.price || form.price <= 0) {
    ElMessage.warning('价格必须大于0')
    return
  }
  saving.value = true
  try {
    if (mode.value === 'create') {
      if (form.stock < 0) {
        ElMessage.warning('库存不能小于0')
        return
      }
      await createProduct({
        name: form.name.trim(),
        categoryId: form.categoryId,
        price: form.price,
        stock: form.stock,
        status: form.status,
        description: form.description,
      })
    } else if (editingId.value) {
      await updateProduct(editingId.value, {
        name: form.name.trim(),
        categoryId: form.categoryId,
        price: form.price,
        status: form.status,
        description: form.description,
      })
    }
    visible.value = false
    ElMessage.success('保存成功')
    await loadData()
  } finally {
    saving.value = false
  }
}

async function onDelete(row: ProductVO) {
  await ElMessageBox.confirm(`确定要删除商品“${row.name}”吗？`, '提示', { type: 'warning' })
  await deleteProduct(row.id)
  ElMessage.success('删除成功')
  await loadData()
}

onMounted(async () => {
  categories.value = await listCategories()
  await loadData()
})
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
