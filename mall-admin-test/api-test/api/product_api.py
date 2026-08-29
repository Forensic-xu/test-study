# 商品接口：查列表、查详情、创建、删除
# 都要带 token；写操作一般用 admin_token

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
    url = f"{BASE_URL}/api/products"
    params: Dict[str, Any] = {"page": page, "size": size}
    if name is not None:
        params["name"] = name
    if category_id is not None:
        params["categoryId"] = category_id  # 注意：接口用驼峰 categoryId
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
    url = f"{BASE_URL}/api/products/{product_id}"
    return requests.get(
        url,
        headers=auth_headers(token),
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def create_product(
    token: str,
    name: str,
    category_id: int,
    price,
    stock: int = 0,
    status: str = "ON_SALE",
    description: Optional[str] = None,
    **kwargs,
) -> requests.Response:
    url = f"{BASE_URL}/api/products"
    payload: Dict[str, Any] = {
        "name": name,
        "categoryId": category_id,
        "price": price,
        "stock": stock,
        "status": status,
    }
    if description is not None:
        payload["description"] = description
    return requests.post(
        url,
        headers=auth_headers(token),
        json=payload,
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def delete_product(token: str, product_id: int, **kwargs) -> requests.Response:
    url = f"{BASE_URL}/api/products/{product_id}"
    return requests.delete(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)
