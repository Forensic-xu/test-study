# 订单用例 — 创建、查看、取消、重复取消

from api.order_api import cancel_order, create_order, get_order
from common.assertions import assert_api_error, assert_api_success
from data.orders import DEFAULT_ORDER_ITEM


def test_create_order_by_items(user_token):
    # 下单用 user01，不走购物车，直接传 items
    resp = create_order(user_token, items=[DEFAULT_ORDER_ITEM], remark="pytest-lesson5")
    body = assert_api_success(resp)
    assert body["data"]["status"] == "PENDING"
    assert body["data"]["id"] > 0


def test_order_create_view_cancel_flow(user_token):
    # 一条龙：下单 → 看详情 → 取消
    create_body = assert_api_success(
        create_order(user_token, items=[DEFAULT_ORDER_ITEM], remark="pytest-flow")
    )
    order_id = create_body["data"]["id"]

    detail_body = assert_api_success(get_order(user_token, order_id))
    assert detail_body["data"]["status"] == "PENDING"

    cancel_body = assert_api_success(cancel_order(user_token, order_id))
    assert cancel_body["data"]["status"] == "CANCELLED"


def test_cancel_order_twice_returns_409(user_token):
    # 已经 CANCELLED 再取消 → 409/40005（幂等保护）
    order_id = assert_api_success(
        create_order(user_token, items=[DEFAULT_ORDER_ITEM], remark="pytest-dup-cancel")
    )["data"]["id"]

    assert_api_success(cancel_order(user_token, order_id))
    assert_api_error(cancel_order(user_token, order_id), 409, 40005)


def test_user_list_own_orders(user_client):
    # 用 ApiClient 写法和用 order_api 函数都可以，看你喜欢哪种
    body = assert_api_success(user_client.get("/api/orders", params={"page": 1, "size": 5}))
    assert isinstance(body["data"]["records"], list)
