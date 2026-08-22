# 库存用例 — 对应 Postman 第 6 课

import pytest

from api.inventory_api import (
    decrease_inventory,
    get_inventory,
    increase_inventory,
    list_inventory_records,
)
from common.assertions import assert_api_error, assert_api_success
from common.case_loader import case_ids
from data.inventory_cases import (
    DEFAULT_PRODUCT_ID,
    INVALID_QUANTITY_CASES,
    INVENTORY_CHANGE_SUCCESS_CASES,
)


def test_get_inventory_success(admin_token):
    resp = get_inventory(admin_token, DEFAULT_PRODUCT_ID)
    body = assert_api_success(resp)
    assert body["data"]["productId"] == DEFAULT_PRODUCT_ID
    assert isinstance(body["data"]["stock"], int)


@pytest.mark.parametrize("case", INVENTORY_CHANGE_SUCCESS_CASES, ids=case_ids(INVENTORY_CHANGE_SUCCESS_CASES))
def test_inventory_change_success(admin_token, case):
    # 库存是「在当前值上加减」，不是直接覆盖，所以要先查再算
    before_resp = get_inventory(admin_token, DEFAULT_PRODUCT_ID)
    stock_before = assert_api_success(before_resp)["data"]["stock"]

    if case["action"] == "increase":
        resp = increase_inventory(admin_token, DEFAULT_PRODUCT_ID, case["delta"], remark=case["case_id"])
        expected_stock = stock_before + case["delta"]
    else:
        resp = decrease_inventory(admin_token, DEFAULT_PRODUCT_ID, case["delta"], remark=case["case_id"])
        expected_stock = stock_before - case["delta"]

    body = assert_api_success(resp)
    assert body["data"]["stock"] == expected_stock


@pytest.mark.parametrize("case", INVALID_QUANTITY_CASES, ids=case_ids(INVALID_QUANTITY_CASES))
def test_inventory_invalid_quantity(admin_token, case):
    resp = increase_inventory(admin_token, DEFAULT_PRODUCT_ID, case["quantity"])
    assert_api_error(resp, case["expected_http"], case["expected_code"])


def test_inventory_decrease_exceeds_stock(admin_token):
    # 减的数量 > 当前库存 → 409/50002
    before_resp = get_inventory(admin_token, DEFAULT_PRODUCT_ID)
    current_stock = assert_api_success(before_resp)["data"]["stock"]

    resp = decrease_inventory(admin_token, DEFAULT_PRODUCT_ID, current_stock + 9999)
    assert_api_error(resp, 409, 50002)


def test_inventory_records_not_empty(admin_token):
    # 上面增减过，流水表里应该有记录
    resp = list_inventory_records(admin_token, DEFAULT_PRODUCT_ID)
    body = assert_api_success(resp)
    assert len(body["data"]) >= 1
