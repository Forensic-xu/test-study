# 库存接口：增减库存要 ADMIN
#
# 和改商品不是一回事，走单独的 /api/inventory 路径。
# quantity 必须 > 0，否则 400/90001；减太多 409/50002

from typing import Any, Dict, Optional

import requests

from common.assertions import auth_headers
from config.settings import BASE_URL, REQUEST_TIMEOUT


def get_inventory(token: str, product_id: int, **kwargs) -> requests.Response:
    url = f"{BASE_URL}/api/inventory/{product_id}"
    return requests.get(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)


def increase_inventory(
    token: str,
    product_id: int,
    quantity: int,
    remark: Optional[str] = None,
    **kwargs,
) -> requests.Response:
    url = f"{BASE_URL}/api/inventory/{product_id}/increase"
    payload: Dict[str, Any] = {"quantity": quantity}
    if remark is not None:
        payload["remark"] = remark
    return requests.put(
        url,
        headers=auth_headers(token),
        json=payload,
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def decrease_inventory(
    token: str,
    product_id: int,
    quantity: int,
    remark: Optional[str] = None,
    **kwargs,
) -> requests.Response:
    url = f"{BASE_URL}/api/inventory/{product_id}/decrease"
    payload: Dict[str, Any] = {"quantity": quantity}
    if remark is not None:
        payload["remark"] = remark
    return requests.put(
        url,
        headers=auth_headers(token),
        json=payload,
        timeout=REQUEST_TIMEOUT,
        **kwargs,
    )


def list_inventory_records(token: str, product_id: int, **kwargs) -> requests.Response:
    # 每次增减都会记流水，可以用来核对 beforeStock/afterStock
    url = f"{BASE_URL}/api/inventory/{product_id}/records"
    return requests.get(url, headers=auth_headers(token), timeout=REQUEST_TIMEOUT, **kwargs)
