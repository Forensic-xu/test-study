"""商品相关接口封装。"""

from typing import Any, Dict, Optional

import requests

from common.assertions import auth_headers
from config.settings import BASE_URL, REQUEST_TIMEOUT


def list_products(
    token: str,
    page: int = 1,
    size: int = 10,
    name: Optional[str] = None,
    category_id: Optional[int] = None,
    status: Optional[str] = None,
    **kwargs,
) -> requests.Response:
    """GET /api/products"""
    url = f"{BASE_URL}/api/products"
    params: Dict[str, Any] = {"page": page, "size": size}
    if name is not None:
        params["name"] = name
    if category_id is not None:
        params["categoryId"] = category_id
    if status is not None:
        params["status"] = status
    return requests.get(
        url,
        headers=auth_headers(token),
        params=params,
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def get_product(token: str, product_id: int, **kwargs) -> requests.Response:
    """GET /api/products/{id}"""
    url = f"{BASE_URL}/api/products/{product_id}"
    return requests.get(
        url,
        headers=auth_headers(token),
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )
