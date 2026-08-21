<template>
  <div class="page-card" data-testid="categories-page">
    <div class="toolbar">
      <el-button data-testid="category-create" type="success" @click="openCreate">新增分类</el-button>
      <el-button data-testid="category-search" type="primary" :loading="loading" @click="loadData">刷新</el-button>
    </div>

    <el-table :data="rows" v-loading="loading" data-testid="category-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">{{ row.status === 1 ? '启用' : '禁用' }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" :data-testid="`category-edit-${row.id}`" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" :data-testid="`category-delete-${row.id}`" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="editingId ? '编辑分类' : '新增分类'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" data-testid="category-form-name" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" data-testid="category-form-status" style="width: 100%">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button data-testid="category-form-cancel" @click="visible = false">取消</el-button>
        <el-button data-testid="category-form-submit" type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCategory, deleteCategory, listCategories, updateCategory } from '@/api/category'
import type { CategoryVO } from '@/types/api'

const loading = ref(false)
const saving = ref(false)
const rows = ref<CategoryVO[]>([])
const visible = ref(false)
const editingId = ref<number | null>(null)
const form = reactive({ name: '', status: 1 })

async function loadData() {
  loading.value = true
  try {
    rows.value = await listCategories()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  form.name = ''
  form.status = 1
  visible.value = true
}

function openEdit(row: CategoryVO) {
  editingId.value = row.id
  form.name = row.name
  form.status = row.status
  visible.value = true
}

async function onSave() {
  if (!form.name.trim()) {
    ElMessage.warning('分类名称不能为空')
    return
  }
  saving.value = true
  try {
    if (editingId.value) {
      await updateCategory(editingId.value, { name: form.name.trim(), status: form.status })
    } else {
      await createCategory({ name: form.name.trim(), status: form.status })
    }
    visible.value = false
    ElMessage.success('保存成功')
    await loadData()
  } finally {
    saving.value = false
  }
}

async function onDelete(row: CategoryVO) {
  await ElMessageBox.confirm(`确定要删除分类“${row.name}”吗？`, '提示', { type: 'warning' })
  await deleteCategory(row.id)
  ElMessage.success('删除成功')
  await loadData()
}

onMounted(loadData)
</script>
