<template>
  <div class="page-card" data-testid="users-page">
    <div class="toolbar">
      <el-input
        v-model="query.username"
        data-testid="user-search-username"
        placeholder="用户名"
        clearable
        style="width: 180px"
      />
      <el-select v-model="query.status" data-testid="user-search-status" clearable placeholder="状态" style="width: 140px">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button data-testid="user-search" type="primary" :loading="loading" @click="loadData">查询</el-button>
      <el-button data-testid="user-create" type="success" @click="openCreate">新增用户</el-button>
    </div>

    <el-table :data="pagedRows" v-loading="loading" data-testid="user-table">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">{{ row.status === 1 ? '启用' : '禁用' }}</template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link type="primary" :data-testid="`user-edit-${row.id}`" @click="openEdit(row)">编辑</el-button>
          <el-button
            link
            type="warning"
            :data-testid="`user-disable-${row.id}`"
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button link type="danger" :data-testid="`user-delete-${row.id}`" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      data-testid="user-pagination"
      layout="total, prev, pager, next"
      :total="rows.length"
      v-model:current-page="page"
      :page-size="pageSize"
    />

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑用户' : '新增用户'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item v-if="!editingId" label="用户名">
          <el-input v-model="form.username" data-testid="user-form-username" />
        </el-form-item>
        <el-form-item :label="editingId ? '新密码' : '密码'">
          <el-input v-model="form.password" data-testid="user-form-password" type="password" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" data-testid="user-form-nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" data-testid="user-form-phone" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" data-testid="user-form-role" style="width: 100%">
            <el-option label="ADMIN" value="ADMIN" />
            <el-option label="USER" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button data-testid="user-form-cancel" @click="dialogVisible = false">取消</el-button>
        <el-button data-testid="user-form-submit" type="primary" :loading="saving" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createUser, deleteUser, listUsers, updateUser } from '@/api/user'
import type { UserVO } from '@/types/api'

const loading = ref(false)
const saving = ref(false)
const rows = ref<UserVO[]>([])
const page = ref(1)
const pageSize = 10
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const query = reactive<{ username: string; status?: number }>({ username: '', status: undefined })
const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  role: 'USER',
})

const pagedRows = computed(() => {
  const start = (page.value - 1) * pageSize
  return rows.value.slice(start, start + pageSize)
})

async function loadData() {
  loading.value = true
  try {
    rows.value = await listUsers({
      username: query.username || undefined,
      status: query.status,
    })
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { username: '', password: '', nickname: '', phone: '', role: 'USER' })
  dialogVisible.value = true
}

function openEdit(row: UserVO) {
  editingId.value = row.id
  Object.assign(form, {
    username: row.username,
    password: '',
    nickname: row.nickname || '',
    phone: row.phone || '',
    role: row.role,
  })
  dialogVisible.value = true
}

async function onSave() {
  saving.value = true
  try {
    if (editingId.value) {
      const payload: Record<string, unknown> = {
        nickname: form.nickname,
        phone: form.phone,
        role: form.role,
      }
      if (form.password) payload.password = form.password
      await updateUser(editingId.value, payload)
    } else {
      if (!form.username || !form.password) {
        ElMessage.warning('用户名和密码不能为空')
        return
      }
      await createUser({ ...form })
    }
    dialogVisible.value = false
    ElMessage.success('保存成功')
    await loadData()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row: UserVO) {
  await updateUser(row.id, { status: row.status === 1 ? 0 : 1 })
  ElMessage.success('状态已更新')
  await loadData()
}

async function onDelete(row: UserVO) {
  await ElMessageBox.confirm(`确定要删除用户“${row.username}”吗？`, '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
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
