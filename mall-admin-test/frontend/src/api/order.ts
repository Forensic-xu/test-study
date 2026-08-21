import request from '@/utils/request'
import type { ApiResult, OrderVO, PageResult } from '@/types/api'

export async function pageOrders(params: {
  page?: number
  size?: number
  status?: string
}): Promise<PageResult<OrderVO>> {
  const { data } = await request.get<ApiResult<PageResult<OrderVO>>>('/orders', { params })
  return data.data
}

export async function getOrder(id: number): Promise<OrderVO> {
  const { data } = await request.get<ApiResult<OrderVO>>(`/orders/${id}`)
  return data.data
}

export async function payOrder(id: number): Promise<OrderVO> {
  const { data } = await request.put<ApiResult<OrderVO>>(`/orders/${id}/pay`)
  return data.data
}

export async function shipOrder(id: number): Promise<OrderVO> {
  const { data } = await request.put<ApiResult<OrderVO>>(`/orders/${id}/ship`)
  return data.data
}

export async function completeOrder(id: number): Promise<OrderVO> {
  const { data } = await request.put<ApiResult<OrderVO>>(`/orders/${id}/complete`)
  return data.data
}

export async function cancelOrder(id: number): Promise<OrderVO> {
  const { data } = await request.put<ApiResult<OrderVO>>(`/orders/${id}/cancel`)
  return data.data
}
