"""认证辅助：登录并获取 token。"""

from api.auth_api import login
from common.assertions import assert_api_success, get_token_from_login_body


def fetch_token(username: str, password: str) -> str:
    """登录并返回 JWT token。"""
    resp = login(username, password)
    body = assert_api_success(resp)
    return get_token_from_login_body(body)
