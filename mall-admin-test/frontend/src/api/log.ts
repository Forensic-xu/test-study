import request from '@/utils/request'
import type { ApiResult, OperationLogVO, PageResult } from '@/types/api'

export async function pageOperationLogs(params: {
  page?: number
  size?: number
  username?: string
}): Promise<PageResult<OperationLogVO>> {
  const { data } = await request.get<ApiResult<PageResult<OperationLogVO>>>('/operation-logs', {
    params,
  })
  return data.data
}
