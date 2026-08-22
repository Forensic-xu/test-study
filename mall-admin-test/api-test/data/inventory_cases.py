"""库存相关测试数据。"""

DEFAULT_PRODUCT_ID = 1

# 非法 quantity → 400 / 90001（@Min(1) 参数校验）
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

# 正常增减场景：(action, delta)
INVENTORY_CHANGE_SUCCESS_CASES = [
    {"case_id": "increase_10", "action": "increase", "delta": 10},
    {"case_id": "decrease_5", "action": "decrease", "delta": 5},
]
