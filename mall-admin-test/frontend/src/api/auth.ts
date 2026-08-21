import request from '@/utils/request'
import type { ApiResult, LoginData } from '@/types/api'

export async function loginApi(payload: { username: string; password: string }): Promise<LoginData> {
  const { data } = await request.post<ApiResult<LoginData>>('/auth/login', payload)
  return data.data
}
