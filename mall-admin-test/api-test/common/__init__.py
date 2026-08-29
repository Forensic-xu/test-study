"""公共模块。"""

from common.assertions import (
    assert_api_error,
    assert_api_success,
    assert_data_has_keys,
    assert_data_id,
    assert_message_contains,
    assert_page_records,
    auth_headers,
    get_token_from_login_body,
    parse_json,
)
from common.case_loader import case_ids, load_json

__all__ = [
    "assert_api_success",
    "assert_api_error",
    "assert_message_contains",
    "assert_data_has_keys",
    "assert_data_id",
    "assert_page_records",
    "parse_json",
    "get_token_from_login_body",
    "auth_headers",
    "load_json",
    "case_ids",
]
