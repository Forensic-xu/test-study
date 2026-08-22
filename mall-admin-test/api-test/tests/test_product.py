# 商品用例 — 对应 Postman 第 3 课（列表、详情、404）

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
    # admin_token 是 conftest 注入的，不用自己登录
    resp = list_products(admin_token, **DEFAULT_LIST_PARAMS)
    body = assert_api_success(resp)
    records = assert_page_records(body, min_count=1)

    first = records[0]
    assert "id" in first and "name" in first


def test_product_detail_success(admin_token):
    resp = get_product(admin_token, EXISTING_PRODUCT_ID)
    body = assert_api_success(resp)

    assert_data_id(body, EXISTING_PRODUCT_ID)
    assert_data_has_keys(body, ["name", "price", "stock", "status"])


def test_product_not_found(admin_token):
    # 故意查一个不存在的 id
    resp = get_product(admin_token, NOT_EXIST_PRODUCT_ID)
    assert_api_error(resp, 404, 20001)
