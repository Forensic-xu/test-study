"""购物车相关接口封装。"""

from typing import Any, Dict, Optional

import requests

from common.assertions import auth_headers
from config.settings import BASE_URL, REQUEST_TIMEOUT


def list_cart(token: str, **kwargs) -> requests.Response:
    """GET /api/cart"""
    url = f"{BASE_URL}/api/cart"
    return requests.get(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)


def add_to_cart(token: str, product_id: int, quantity: int, **kwargs) -> requests.Response:
    """POST /api/cart"""
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
    """PUT /api/cart/{id}"""
    url = f"{BASE_URL}/api/cart/{cart_item_id}"
    return requests.put(
        url,
        headers=auth_headers(token),
        json={"quantity": quantity},
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def delete_cart_item(token: str, cart_item_id: int, **kwargs) -> requests.Response:
    """DELETE /api/cart/{id}"""
    url = f"{BASE_URL}/api/cart/{cart_item_id}"
    return requests.delete(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)
