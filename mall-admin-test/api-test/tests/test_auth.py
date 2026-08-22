# 登录用例 — 对应 Postman 第 1 课

import pytest

from api.auth_api import login
from common.assertions import assert_api_error, assert_api_success, get_token_from_login_body
from common.case_loader import case_ids
from config.settings import ADMIN_PASSWORD, ADMIN_USERNAME
from data.accounts import LOGIN_ERROR_CASES


def test_login_success():
    # 正常登录：HTTP 200，code 200，还得有 token
    resp = login(ADMIN_USERNAME, ADMIN_PASSWORD)
    body = assert_api_success(resp)

    token = get_token_from_login_body(body)
    assert isinstance(token, str) and len(token) > 0
    assert body["data"]["role"] == "ADMIN"


@pytest.mark.parametrize("case", LOGIN_ERROR_CASES, ids=case_ids(LOGIN_ERROR_CASES))
def test_login_error_cases(case):
    # 下面 4 组数据在 data/accounts.py，改数据不用动这里的代码
    resp = login(case["username"], case["password"])
    assert_api_error(resp, case["expected_http"], case["expected_code"])
