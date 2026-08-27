# 库存相关数据入口
#
# 真正内容在 data/json/inventory_cases.json

from common.case_loader import load_json

_DATA = load_json("inventory_cases.json")

DEFAULT_PRODUCT_ID = _DATA["default_product_id"]
INVALID_QUANTITY_CASES = _DATA["invalid_quantity_cases"]
INVENTORY_CHANGE_SUCCESS_CASES = _DATA["change_success_cases"]
