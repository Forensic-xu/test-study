"""订单相关接口封装。"""

from typing import Any, Dict, List, Optional

import requests

from common.assertions import auth_headers
from config.settings import BASE_URL, REQUEST_TIMEOUT


def create_order(
    token: str,
    items: Optional[List[Dict[str, Any]]] = None,
    cart_item_ids: Optional[List[int]] = None,
    checkout_all: bool = False,
    remark: Optional[str] = None,
    **kwargs,
) -> requests.Response:
    """POST /api/orders"""
    url = f"{BASE_URL}/api/orders"
    payload: Dict[str, Any] = {}
    if items is not None:
        payload["items"] = items
    if cart_item_ids is not None:
        payload["cartItemIds"] = cart_item_ids
    if checkout_all:
        payload["checkoutAll"] = True
    if remark is not None:
        payload["remark"] = remark
    return requests.post(
        url,
        headers=auth_headers(token),
        json=payload,
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def get_order(token: str, order_id: int, **kwargs) -> requests.Response:
    """GET /api/orders/{id}"""
    url = f"{BASE_URL}/api/orders/{order_id}"
    return requests.get(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)


def list_orders(token: str, page: int = 1, size: int = 10, status: Optional[str] = None, **kwargs) -> requests.Response:
    """GET /api/orders"""
    url = f"{BASE_URL}/api/orders"
    params: Dict[str, Any] = {"page": page, "size": size}
    if status is not None:
        params["status"] = status
    return requests.get(
        url,
        headers=auth_headers(token),
        params=params,
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def cancel_order(token: str, order_id: int, **kwargs) -> requests.Response:
    """PUT /api/orders/{id}/cancel"""
    url = f"{BASE_URL}/api/orders/{order_id}/cancel"
    return requests.put(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)
