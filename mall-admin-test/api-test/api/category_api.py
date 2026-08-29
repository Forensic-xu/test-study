# 分类接口（ADMIN）

from typing import Any, Dict, Optional

import requests

from common.assertions import auth_headers
from config.settings import BASE_URL, REQUEST_TIMEOUT


def list_categories(token: str, **kwargs) -> requests.Response:
    url = f"{BASE_URL}/api/categories"
    return requests.get(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)


def create_category(
    token: str,
    name: str,
    status: int = 1,
    **kwargs,
) -> requests.Response:
    url = f"{BASE_URL}/api/categories"
    payload: Dict[str, Any] = {"name": name, "status": status}
    return requests.post(
        url,
        headers=auth_headers(token),
        json=payload,
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def delete_category(token: str, category_id: int, **kwargs) -> requests.Response:
    # 分类下还有商品 → 409 / 20007
    url = f"{BASE_URL}/api/categories/{category_id}"
    return requests.delete(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)
