<template>
  <div class="login-page" data-testid="login-page">
    <el-card class="login-card">
      <h2 data-testid="login-title">mall-admin-test 登录</h2>
      <p class="hint">admin / Admin@123 · user01 / User@123</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            data-testid="login-username"
            placeholder="请输入用户名"
            autocomplete="username"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            data-testid="login-password"
            type="password"
            show-password
            placeholder="请输入密码"
            autocomplete="current-password"
            @keyup.enter="onSubmit"
          />
        </el-form-item>
        <el-button
          data-testid="login-submit"
          type="primary"
          class="submit"
          :loading="loading"
          @click="onSubmit"
        >
          登录
        </el-button>
      </el-form>
      <div v-if="errorMsg" class="error" data-testid="login-error">{{ errorMsg }}</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const errorMsg = ref('')
const form = reactive({
  username: 'admin',
  password: 'Admin@123',
})

const rules: FormRules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
}

async function onSubmit() {
  errorMsg.value = ''
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(form.username.trim(), form.password)
    const redirect = (route.query.redirect as string) || '/dashboard'
    await router.replace(redirect)
  } catch (err: unknown) {
    const axiosErr = err as { response?: { data?: { message?: string } }; message?: string }
    errorMsg.value = axiosErr.response?.data?.message || axiosErr.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2d3d, #3a5a7a);
}
.login-card {
  width: 420px;
}
.submit {
  width: 100%;
}
.hint {
  color: #909399;
  font-size: 13px;
  margin-top: -8px;
}
.error {
  margin-top: 12px;
  color: #f56c6c;
}
</style>
