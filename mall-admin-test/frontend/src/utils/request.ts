import axios, { type AxiosError, type AxiosInstance, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import type { ApiResult } from '@/types/api'

const TOKEN_KEY = 'mall_admin_token'
const USER_KEY = 'mall_admin_user'

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearAuthStorage(): void {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getStoredUser(): string | null {
  return localStorage.getItem(USER_KEY)
}

export function setStoredUser(userJson: string): void {
  localStorage.setItem(USER_KEY, userJson)
}

const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response: AxiosResponse<ApiResult>) => {
    const payload = response.data
    if (payload && typeof payload.code === 'number' && payload.code !== 200) {
      ElMessage.error(payload.message || '请求失败')
      return Promise.reject(payload)
    }
    return response
  },
  (error: AxiosError<ApiResult>) => {
    const status = error.response?.status
    const payload = error.response?.data
    const message = payload?.message || error.message || '网络异常'

    if (status === 401) {
      clearAuthStorage()
      ElMessage.error(message || '未登录或登录已失效')
      if (router.currentRoute.value.path !== '/login') {
        router.replace({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
      }
    } else if (status === 403) {
      ElMessage.error(message || '无权限访问')
    } else if (status === 404) {
      ElMessage.error(message || '资源不存在')
    } else if (status === 409) {
      ElMessage.error(message || '业务冲突')
    } else if (status === 400) {
      ElMessage.error(message || '参数错误')
    } else if (status && status >= 500) {
      ElMessage.error(message || '服务器异常')
    } else {
      ElMessage.error(message)
    }
    return Promise.reject(error)
  },
)

export default request
