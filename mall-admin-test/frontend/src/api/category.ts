import request from '@/utils/request'
import type { ApiResult, CategoryVO } from '@/types/api'

export async function listCategories(status?: number): Promise<CategoryVO[]> {
  const { data } = await request.get<ApiResult<CategoryVO[]>>('/categories', {
    params: status === undefined ? undefined : { status },
  })
  return data.data
}

export async function createCategory(payload: { name: string; status?: number }): Promise<CategoryVO> {
  const { data } = await request.post<ApiResult<CategoryVO>>('/categories', payload)
  return data.data
}

export async function updateCategory(
  id: number,
  payload: { name?: string; status?: number },
): Promise<CategoryVO> {
  const { data } = await request.put<ApiResult<CategoryVO>>(`/categories/${id}`, payload)
  return data.data
}

export async function deleteCategory(id: number): Promise<void> {
  await request.delete(`/categories/${id}`)
}
