# 购物车：谁登录就只能操作谁的购物车
# 越权删/改别人的项 → 403 / 30004

from typing import Any, Dict

import requests

from common.assertions import auth_headers
from config.settings import BASE_URL, REQUEST_TIMEOUT


def list_cart(token: str, **kwargs) -> requests.Response:
    url = f"{BASE_URL}/api/cart"
    return requests.get(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)


def add_to_cart(token: str, product_id: int, quantity: int, **kwargs) -> requests.Response:
    url = f"{BASE_URL}/api/cart"
    payload: Dict[str, Any] = {"productId": product_id, "quantity": quantity}
    return requests.post(
        url,
        headers=auth_headers(token),
        json=payload,
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def update_cart_item(token: str, cart_item_id: int, quantity: int, **kwargs) -> requests.Response:
    url = f"{BASE_URL}/api/cart/{cart_item_id}"
    return requests.put(
        url,
        headers=auth_headers(token),
        json={"quantity": quantity},
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def delete_cart_item(token: str, cart_item_id: int, **kwargs) -> requests.Response:
    url = f"{BASE_URL}/api/cart/{cart_item_id}"
    return requests.delete(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)
