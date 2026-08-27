# 登录相关数据入口
#
# 真正内容在 data/json/login_error_cases.json
# 这里只负责「读出来」，测试文件还是 from data.accounts import ...

from common.case_loader import load_json

LOGIN_ERROR_CASES = load_json("login_error_cases.json")
