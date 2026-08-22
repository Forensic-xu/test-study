"""公共工具：断言、响应解析等。"""

from typing import Any, Dict, Iterable, List, Optional


def parse_json(resp) -> Dict[str, Any]:
    """解析响应 JSON，失败时给出清晰报错。"""
    try:
        return resp.json()
    except ValueError as exc:
        raise AssertionError(
            f"响应不是合法 JSON，status={resp.status_code}, body={resp.text[:200]}"
        ) from exc


def assert_api_success(resp, expected_http: int = 200, expected_code: int = 200) -> Dict[str, Any]:
    """断言接口成功：HTTP Status + 业务 code。"""
    assert resp.status_code == expected_http, (
        f"HTTP 状态码不符: 期望 {expected_http}, 实际 {resp.status_code}, body={resp.text}"
    )
    body = parse_json(resp)
    assert body.get("code") == expected_code, (
        f"业务 code 不符: 期望 {expected_code}, 实际 {body.get('code')}, message={body.get('message')}"
    )
    return body


def assert_api_error(resp, expected_http: int, expected_code: int) -> Dict[str, Any]:
    """断言接口业务/HTTP 错误场景。"""
    assert resp.status_code == expected_http, (
        f"HTTP 状态码不符: 期望 {expected_http}, 实际 {resp.status_code}, body={resp.text}"
    )
    body = parse_json(resp)
    assert body.get("code") == expected_code, (
        f"业务 code 不符: 期望 {expected_code}, 实际 {body.get('code')}, message={body.get('message')}"
    )
    return body


def assert_message_contains(body: Dict[str, Any], keyword: str) -> None:
    """断言 message 包含关键字（可选加强校验）。"""
    message = str(body.get("message", ""))
    assert keyword in message, f"message 应包含 '{keyword}'，实际: {message}"


def assert_data_has_keys(body: Dict[str, Any], keys: Iterable[str]) -> Dict[str, Any]:
    """断言 data 对象包含指定字段。"""
    data = body.get("data")
    assert isinstance(data, dict), f"data 应为对象: {body}"
    for key in keys:
        assert key in data, f"data 缺少字段 '{key}': {data}"
    return data


def assert_data_id(body: Dict[str, Any], expected_id: int) -> None:
    """断言 data.id 等于期望值。"""
    actual_id = body.get("data", {}).get("id")
    assert actual_id == expected_id, f"data.id 不符: 期望 {expected_id}, 实际 {actual_id}"


def assert_page_records(body: Dict[str, Any], min_count: int = 1) -> List[Dict[str, Any]]:
    """断言分页结构 data.records 为数组且数量满足要求。"""
    data = body.get("data") or {}
    records = data.get("records")
    assert isinstance(records, list), f"data.records 应为 list: {body}"
    assert len(records) >= min_count, (
        f"records 数量不足: 期望 >= {min_count}, 实际 {len(records)}"
    )
    total = data.get("total")
    assert total is not None, f"分页缺少 total 字段: {data}"
    return records


def get_token_from_login_body(body: Dict[str, Any]) -> str:
    """从登录成功响应中提取 token。"""
    token = body.get("data", {}).get("token")
    assert token, f"登录响应缺少 token: {body}"
    return token


def auth_headers(token: str) -> Dict[str, str]:
    """构造 Bearer Token 请求头。"""
    return {"Authorization": f"Bearer {token}"}
