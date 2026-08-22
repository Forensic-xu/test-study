# 带 Token 的请求客户端
#
# 有了它，用例里不用每次写 headers={"Authorization": "Bearer xxx"}
# 直接 client.get("/api/products") 就行。

from typing import Any, Dict, Optional

import requests

from common.assertions import auth_headers
from config.settings import BASE_URL, REQUEST_TIMEOUT


class ApiClient:

    def __init__(self, token: str, session: Optional[requests.Session] = None):
        self.token = token
        self.session = session or requests.Session()

    def _merge_headers(self, headers: Optional[Dict[str, str]] = None) -> Dict[str, str]:
        merged = auth_headers(self.token)
        if headers:
            merged.update(headers)
        return merged

    def request(self, method: str, path: str, **kwargs: Any) -> requests.Response:
        # path 写 "/api/xxx" 就行，BASE_URL 在这里拼
        url = f"{BASE_URL}{path}"
        kwargs.setdefault("timeout", REQUEST_TIMEOUT)
        headers = kwargs.pop("headers", None)
        return self.session.request(
            method,
            url,
            headers=self._merge_headers(headers),
            **kwargs,
        )

    def get(self, path: str, **kwargs: Any) -> requests.Response:
        return self.request("GET", path, **kwargs)

    def post(self, path: str, **kwargs: Any) -> requests.Response:
        return self.request("POST", path, **kwargs)

    def put(self, path: str, **kwargs: Any) -> requests.Response:
        return self.request("PUT", path, **kwargs)

    def delete(self, path: str, **kwargs: Any) -> requests.Response:
        return self.request("DELETE", path, **kwargs)

    def close(self) -> None:
        self.session.close()
