# 购物车用例 — 重点测「别人的购物车我动不了」

import pytest

from api.cart_api import add_to_cart, delete_cart_item, list_cart, update_cart_item
from common.assertions import assert_api_error, assert_api_success
from data.cart import DEFAULT_PRODUCT_ID


@pytest.fixture(scope="module")
def user01_cart_item_id(user_token):
    # 先让 user01 加一件商品，拿到 cart_item_id 给后面的越权测试用
    # 不依赖种子数据里的 id，避免库里没有那条记录
    resp = add_to_cart(user_token, product_id=DEFAULT_PRODUCT_ID, quantity=1)
    body = assert_api_success(resp)
    cart_item_id = body["data"]["id"]
    assert cart_item_id
    return cart_item_id


def test_user_token_can_list_cart(user_token):
    resp = list_cart(user_token)
    body = assert_api_success(resp)
    assert isinstance(body.get("data"), list)


def test_different_users_have_different_tokens(admin_token, user_token):
    # 两个账号登出来 token 肯定不一样
    assert admin_token != user_token
    assert len(admin_token) > 20


def test_user_add_to_cart(user_token):
    resp = add_to_cart(user_token, product_id=DEFAULT_PRODUCT_ID, quantity=1)
    body = assert_api_success(resp)
    assert body["data"]["productId"] == DEFAULT_PRODUCT_ID


@pytest.mark.parametrize(
    "actor_token_fixture,action",
    [
        ("user02_token", "delete"),
        ("admin_token", "delete"),   # admin 也不能删 user01 的购物车
        ("user02_token", "update"),
    ],
    ids=["user02_delete", "admin_delete", "user02_update"],
)
def test_cross_user_cart_forbidden(request, user01_cart_item_id, actor_token_fixture, action):
    # request.getfixturevalue：参数化里动态拿 token fixture
    token = request.getfixturevalue(actor_token_fixture)

    if action == "delete":
        resp = delete_cart_item(token, user01_cart_item_id)
    else:
        resp = update_cart_item(token, user01_cart_item_id, quantity=99)

    assert_api_error(resp, 403, 30004)
