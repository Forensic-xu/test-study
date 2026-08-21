import request from '@/utils/request'
import type { ApiResult, InventoryRecordVO, InventoryVO } from '@/types/api'

export async function getInventory(productId: number): Promise<InventoryVO> {
  const { data } = await request.get<ApiResult<InventoryVO>>(`/inventory/${productId}`)
  return data.data
}

export async function increaseInventory(
  productId: number,
  quantity: number,
  remark?: string,
): Promise<InventoryVO> {
  const { data } = await request.put<ApiResult<InventoryVO>>(`/inventory/${productId}/increase`, {
    quantity,
    remark,
  })
  return data.data
}

export async function decreaseInventory(
  productId: number,
  quantity: number,
  remark?: string,
): Promise<InventoryVO> {
  const { data } = await request.put<ApiResult<InventoryVO>>(`/inventory/${productId}/decrease`, {
    quantity,
    remark,
  })
  return data.data
}

export async function listInventoryRecords(productId: number): Promise<InventoryRecordVO[]> {
  const { data } = await request.get<ApiResult<InventoryRecordVO[]>>(`/inventory/${productId}/records`)
  return data.data
}
