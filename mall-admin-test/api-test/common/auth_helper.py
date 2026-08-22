# 登录拿 token，fixture 里会用到

from api.auth_api import login
from common.assertions import assert_api_success, get_token_from_login_body


def fetch_token(username: str, password: str) -> str:
    # 登一次，把 JWT 字符串拿出来
    resp = login(username, password)
    body = assert_api_success(resp)
    return get_token_from_login_body(body)
