export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  page: number
  size: number
  total: number
  pages: number
  records: T[]
}

export interface LoginData {
  token: string
  tokenType: string
  expiresInHours: number
  userId: number
  username: string
  nickname: string
  role: 'ADMIN' | 'USER'
}

export interface UserInfo {
  userId: number
  username: string
  nickname: string
  role: 'ADMIN' | 'USER'
}

export interface UserVO {
  id: number
  username: string
  nickname: string
  email?: string
  phone?: string
  role: string
  status: number
  createdAt?: string
  updatedAt?: string
}

export interface CategoryVO {
  id: number
  name: string
  status: number
  createdAt?: string
  updatedAt?: string
}

export interface ProductVO {
  id: number
  name: string
  categoryId: number
  categoryName?: string
  price: number
  stock: number
  status: 'ON_SALE' | 'OFF_SALE'
  description?: string
  createdAt?: string
  updatedAt?: string
}

export interface InventoryVO {
  productId: number
  productName: string
  stock: number
  status: string
}

export interface InventoryRecordVO {
  id: number
  productId: number
  beforeStock: number
  changeQuantity: number
  afterStock: number
  operationType: string
  operatorId?: number
  remark?: string
  createdAt?: string
}

export interface OrderItemVO {
  id: number
  productId: number
  productName: string
  productPrice: number
  quantity: number
  subtotal: number
}

export interface OrderVO {
  id: number
  orderNo: string
  userId: number
  totalAmount: number
  status: string
  remark?: string
  createdAt?: string
  updatedAt?: string
  items?: OrderItemVO[]
}

export interface OperationLogVO {
  id: number
  userId?: number
  username?: string
  module: string
  action: string
  method?: string
  requestUri?: string
  requestParams?: string
  httpStatus?: number
  ip?: string
  detail?: string
  status: number
  createdAt?: string
}
