"""订单接口测试 — conftest 客户端 + 接口关联（创建→查看→取消）"""

from api.order_api import cancel_order, create_order, get_order, list_orders
from common.assertions import assert_api_error, assert_api_success
from data.orders import DEFAULT_ORDER_ITEM


def test_create_order_by_items(user_token):
    """user01 直接 items 下单 → PENDING"""
    resp = create_order(user_token, items=[DEFAULT_ORDER_ITEM], remark="pytest-lesson5")
    body = assert_api_success(resp)
    assert body["data"]["status"] == "PENDING"
    assert body["data"]["id"] > 0
    assert len(body["data"]["items"]) >= 1


def test_order_create_view_cancel_flow(user_token):
    """创建 → 查看详情 → 取消（状态 PENDING → CANCELLED）"""
    create_resp = create_order(user_token, items=[DEFAULT_ORDER_ITEM], remark="pytest-flow")
    create_body = assert_api_success(create_resp)
    order_id = create_body["data"]["id"]

    detail_resp = get_order(user_token, order_id)
    detail_body = assert_api_success(detail_resp)
    assert detail_body["data"]["id"] == order_id
    assert detail_body["data"]["status"] == "PENDING"

    cancel_resp = cancel_order(user_token, order_id)
    cancel_body = assert_api_success(cancel_resp)
    assert cancel_body["data"]["status"] == "CANCELLED"


def test_cancel_order_twice_returns_409(user_token):
    """重复取消 → 409 / 40005"""
    create_resp = create_order(user_token, items=[DEFAULT_ORDER_ITEM], remark="pytest-dup-cancel")
    create_body = assert_api_success(create_resp)
    order_id = create_body["data"]["id"]

    first_cancel = cancel_order(user_token, order_id)
    assert_api_success(first_cancel)

    second_cancel = cancel_order(user_token, order_id)
    assert_api_error(second_cancel, 409, 40005)


def test_user_list_own_orders(user_client):
    """user_client 查看自己的订单列表。"""
    resp = user_client.get("/api/orders", params={"page": 1, "size": 5})
    body = assert_api_success(resp)
    assert isinstance(body["data"]["records"], list)
