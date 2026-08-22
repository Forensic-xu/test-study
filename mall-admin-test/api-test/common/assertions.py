# 断言工具：把 Postman Tests 里重复的 pm.test 抽到这里
#
# 本项目约定：每个接口都要看两层
#   1. HTTP 状态码（resp.status_code）
#   2. 业务码（body["code"]）
# 两层都对才算过。

from typing import Any, Dict, Iterable, List


def parse_json(resp) -> Dict[str, Any]:
    # 返回的不是 JSON 时，别报一堆看不懂的错
    try:
        return resp.json()
    except ValueError as exc:
        raise AssertionError(
            f"响应不是 JSON，status={resp.status_code}, body={resp.text[:200]}"
        ) from exc


def assert_api_success(resp, expected_http: int = 200, expected_code: int = 200) -> Dict[str, Any]:
    # 成功场景：默认 HTTP 200 + code 200
    assert resp.status_code == expected_http, (
        f"HTTP 不对: 要 {expected_http}, 实际 {resp.status_code}, body={resp.text}"
    )
    body = parse_json(resp)
    assert body.get("code") == expected_code, (
        f"code 不对: 要 {expected_code}, 实际 {body.get('code')}, msg={body.get('message')}"
    )
    return body


def assert_api_error(resp, expected_http: int, expected_code: int) -> Dict[str, Any]:
    # 失败场景：比如 404/20001、409/50002，HTTP 和 code 都要对上
    assert resp.status_code == expected_http, (
        f"HTTP 不对: 要 {expected_http}, 实际 {resp.status_code}, body={resp.text}"
    )
    body = parse_json(resp)
    assert body.get("code") == expected_code, (
        f"code 不对: 要 {expected_code}, 实际 {body.get('code')}, msg={body.get('message')}"
    )
    return body


def assert_message_contains(body: Dict[str, Any], keyword: str) -> None:
    message = str(body.get("message", ""))
    assert keyword in message, f"message 里应有「{keyword}」，实际是: {message}"


def assert_data_has_keys(body: Dict[str, Any], keys: Iterable[str]) -> Dict[str, Any]:
    # 详情接口常用：确认返回里带了 name、price 这些字段
    data = body.get("data")
    assert isinstance(data, dict), f"data 应该是对象: {body}"
    for key in keys:
        assert key in data, f"data 里缺字段「{key}」: {data}"
    return data


def assert_data_id(body: Dict[str, Any], expected_id: int) -> None:
    actual_id = body.get("data", {}).get("id")
    assert actual_id == expected_id, f"id 不对: 要 {expected_id}, 实际 {actual_id}"


def assert_page_records(body: Dict[str, Any], min_count: int = 1) -> List[Dict[str, Any]]:
    # 列表接口：data.records 是数组，还得有 total
    data = body.get("data") or {}
    records = data.get("records")
    assert isinstance(records, list), f"records 应该是列表: {body}"
    assert len(records) >= min_count, (
        f"记录太少: 至少要 {min_count} 条, 实际 {len(records)}"
    )
    total = data.get("total")
    assert total is not None, f"分页缺 total: {data}"
    return records


def get_token_from_login_body(body: Dict[str, Any]) -> str:
    token = body.get("data", {}).get("token")
    assert token, f"登录成功但没 token: {body}"
    return token


def auth_headers(token: str) -> Dict[str, str]:
    # 需要登录的接口都要带这个头，等同 Postman 的 Bearer Token
    return {"Authorization": f"Bearer {token}"}
