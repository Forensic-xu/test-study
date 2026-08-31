"""接口封装层：按业务模块拆分。"""

from api.auth_api import login
from api.category_api import create_category, delete_category, list_categories
from api.inventory_api import (
    decrease_inventory,
    get_inventory,
    increase_inventory,
    list_inventory_records,
)
from api.order_api import (
    cancel_order,
    complete_order,
    create_order,
    get_order,
    list_orders,
    pay_order,
    ship_order,
)
from api.product_api import create_product, delete_product, get_product, list_products

__all__ = [
    "login",
    "list_categories",
    "create_category",
    "delete_category",
    "list_products",
    "get_product",
    "create_product",
    "delete_product",
    "get_inventory",
    "increase_inventory",
    "decrease_inventory",
    "list_inventory_records",
    "create_order",
    "get_order",
    "list_orders",
    "cancel_order",
    "pay_order",
    "ship_order",
    "complete_order",
]
