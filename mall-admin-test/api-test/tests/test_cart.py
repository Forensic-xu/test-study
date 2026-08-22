"""购物车接口测试 — fixture 多用户 token + 数据隔离 403"""

import pytest

from api.cart_api import add_to_cart, delete_cart_item, list_cart, update_cart_item
from common.assertions import assert_api_error, assert_api_success
from data.cart import DEFAULT_PRODUCT_ID


@pytest.fixture(scope="module")
def user01_cart_item_id(user_token):
    """由 user01 创建一条购物车记录，供越权测试使用。"""
    resp = add_to_cart(user_token, product_id=DEFAULT_PRODUCT_ID, quantity=1)
    body = assert_api_success(resp)
    cart_item_id = body["data"]["id"]
    assert cart_item_id, f"加入购物车未返回 id: {body}"
    return cart_item_id


def test_user_token_can_list_cart(user_token):
    """user01 查看自己的购物车 → 200"""
    resp = list_cart(user_token)
    body = assert_api_success(resp)
    assert isinstance(body.get("data"), list)


def test_different_users_have_different_tokens(admin_token, user_token):
    """不同账号登录应得到不同 token（fixture 各自独立）。"""
    assert admin_token != user_token
    assert len(admin_token) > 20
    assert len(user_token) > 20


def test_user_add_to_cart(user_token):
    """user01 加入购物车 → 200"""
    resp = add_to_cart(user_token, product_id=DEFAULT_PRODUCT_ID, quantity=1)
    body = assert_api_success(resp)
    assert body["data"]["productId"] == DEFAULT_PRODUCT_ID
    assert body["data"]["quantity"] >= 1


@pytest.mark.parametrize(
    "actor_token_fixture,action",
    [
        ("user02_token", "delete"),
        ("admin_token", "delete"),
        ("user02_token", "update"),
    ],
    ids=["user02_delete", "admin_delete", "user02_update"],
)
def test_cross_user_cart_forbidden(
    request, user01_cart_item_id, actor_token_fixture, action
):
    """越权操作 user01 的购物车项 → 403 / 30004"""
    token = request.getfixturevalue(actor_token_fixture)

    if action == "delete":
        resp = delete_cart_item(token, user01_cart_item_id)
    else:
        resp = update_cart_item(token, user01_cart_item_id, quantity=99)

    assert_api_error(resp, 403, 30004)
