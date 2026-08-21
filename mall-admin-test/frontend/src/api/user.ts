import request from '@/utils/request'
import type { ApiResult, UserVO } from '@/types/api'

export async function listUsers(params?: {
  username?: string
  role?: string
  status?: number
}): Promise<UserVO[]> {
  const { data } = await request.get<ApiResult<UserVO[]>>('/users', { params })
  return data.data
}

export async function createUser(payload: Record<string, unknown>): Promise<UserVO> {
  const { data } = await request.post<ApiResult<UserVO>>('/users', payload)
  return data.data
}

export async function updateUser(id: number, payload: Record<string, unknown>): Promise<UserVO> {
  const { data } = await request.put<ApiResult<UserVO>>(`/users/${id}`, payload)
  return data.data
}

export async function deleteUser(id: number): Promise<void> {
  await request.delete(`/users/${id}`)
}
