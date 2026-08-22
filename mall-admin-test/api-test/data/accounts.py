# 登录失败场景 — 数据放这里，test_auth.py 用参数化一条逻辑跑完
#
# case_id 会显示在 pytest 报告里，比如 [wrong_password]

LOGIN_ERROR_CASES = [
    {
        "case_id": "wrong_password",
        "username": "admin",
        "password": "wrong",
        "expected_http": 401,
        "expected_code": 10002,
    },
    {
        "case_id": "empty_username",
        "username": "",
        "password": "Admin@123",
        "expected_http": 400,
        "expected_code": 90001,
    },
    {
        "case_id": "disabled_user",
        "username": "disabled",
        "password": "User@123",
        "expected_http": 403,
        "expected_code": 10003,
    },
    {
        "case_id": "user_not_found",
        "username": "not_exist_user",
        "password": "any",
        "expected_http": 404,
        "expected_code": 10001,
    },
]
