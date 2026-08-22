# 登录接口（唯一不需要 token 的写操作之一）

from typing import Any, Dict

import requests

from config.settings import BASE_URL, REQUEST_TIMEOUT


def login(username: str, password: str, **kwargs) -> requests.Response:
    url = f"{BASE_URL}/api/auth/login"
    payload: Dict[str, Any] = {"username": username, "password": password}
    return requests.post(url, json=payload, timeout=REQUEST_TIMEOUT, **kwargs)
