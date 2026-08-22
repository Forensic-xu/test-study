"""商品接口自动化测试 — 对应 Postman 第 3 课 + 第 2 课断言模式"""

from api.product_api import get_product, list_products
from common.assertions import (
    assert_api_error,
    assert_api_success,
    assert_data_has_keys,
    assert_data_id,
    assert_page_records,
)
from data.products import DEFAULT_LIST_PARAMS, EXISTING_PRODUCT_ID, NOT_EXIST_PRODUCT_ID


def test_product_list_success(admin_token):
    """商品分页列表 → 200 / code=200，data.records 为数组"""
    resp = list_products(admin_token, **DEFAULT_LIST_PARAMS)
    body = assert_api_success(resp)
    records = assert_page_records(body, min_count=1)

    first = records[0]
    assert "id" in first and "name" in first, f"商品记录缺少基础字段: {first}"


def test_product_detail_success(admin_token):
    """商品详情 → 200，data.id 与路径参数一致"""
    resp = get_product(admin_token, EXISTING_PRODUCT_ID)
    body = assert_api_success(resp)

    assert_data_id(body, EXISTING_PRODUCT_ID)
    assert_data_has_keys(body, ["name", "price", "stock", "status"])


def test_product_not_found(admin_token):
    """不存在商品 → 404 / code=20001"""
    resp = get_product(admin_token, NOT_EXIST_PRODUCT_ID)
    body = assert_api_error(resp, 404, 20001)
    assert body.get("message"), "错误响应应包含 message"
