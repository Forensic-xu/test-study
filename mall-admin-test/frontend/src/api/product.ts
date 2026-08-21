import request from '@/utils/request'
import type { ApiResult, PageResult, ProductVO } from '@/types/api'

export async function pageProducts(params: {
  page?: number
  size?: number
  name?: string
  categoryId?: number
  status?: string
}): Promise<PageResult<ProductVO>> {
  const { data } = await request.get<ApiResult<PageResult<ProductVO>>>('/products', { params })
  return data.data
}

export async function getProduct(id: number): Promise<ProductVO> {
  const { data } = await request.get<ApiResult<ProductVO>>(`/products/${id}`)
  return data.data
}

export async function createProduct(payload: Record<string, unknown>): Promise<ProductVO> {
  const { data } = await request.post<ApiResult<ProductVO>>('/products', payload)
  return data.data
}

export async function updateProduct(id: number, payload: Record<string, unknown>): Promise<ProductVO> {
  const { data } = await request.put<ApiResult<ProductVO>>(`/products/${id}`, payload)
  return data.data
}

export async function deleteProduct(id: number): Promise<void> {
  await request.delete(`/products/${id}`)
}
