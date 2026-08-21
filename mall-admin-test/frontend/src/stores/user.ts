import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { loginApi } from '@/api/auth'
import type { LoginData, UserInfo } from '@/types/api'
import { clearAuthStorage, getStoredUser, getToken, setStoredUser, setToken } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(getToken())
  const userInfo = ref<UserInfo | null>(parseUser(getStoredUser()))

  const isLogin = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

  async function login(username: string, password: string): Promise<LoginData> {
    const data = await loginApi({ username, password })
    token.value = data.token
    setToken(data.token)
    const info: UserInfo = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      role: data.role,
    }
    userInfo.value = info
    setStoredUser(JSON.stringify(info))
    return data
  }

  function logout(): void {
    token.value = null
    userInfo.value = null
    clearAuthStorage()
  }

  return { token, userInfo, isLogin, isAdmin, login, logout }
})

function parseUser(raw: string | null): UserInfo | null {
  if (!raw) return null
  try {
    return JSON.parse(raw) as UserInfo
  } catch {
    return null
  }
}
