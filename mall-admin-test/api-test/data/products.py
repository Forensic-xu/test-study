"""商品相关测试数据。"""

# 种子数据中通常存在的商品 ID
EXISTING_PRODUCT_ID = 1

# 不存在的商品 ID（用于 404 测试）
NOT_EXIST_PRODUCT_ID = 999999

# 列表查询默认参数
DEFAULT_LIST_PARAMS = {
    "page": 1,
    "size": 5,
    "status": "ON_SALE",
}
