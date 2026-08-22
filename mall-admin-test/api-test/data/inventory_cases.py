# 库存测试数据

DEFAULT_PRODUCT_ID = 1  # 种子数据里一般有这个商品

# quantity 填 0 或负数：走的是参数校验，code 是 90001（不是 50001）
INVALID_QUANTITY_CASES = [
    {
        "case_id": "zero_quantity",
        "quantity": 0,
        "expected_http": 400,
        "expected_code": 90001,
    },
    {
        "case_id": "negative_quantity",
        "quantity": -1,
        "expected_http": 400,
        "expected_code": 90001,
    },
]

# 先加 10 再减 5，测完会改数据库里的库存（本地练习无所谓）
INVENTORY_CHANGE_SUCCESS_CASES = [
    {"case_id": "increase_10", "action": "increase", "delta": 10},
    {"case_id": "decrease_5", "action": "decrease", "delta": 5},
]
