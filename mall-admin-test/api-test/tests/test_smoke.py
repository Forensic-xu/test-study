"""冒烟用例：验证环境与 ApiClient fixture。"""

import pytest

from common.assertions import assert_api_success


@pytest.mark.smoke
def test_backend_reachable(base_url):
    """后端地址可配置。"""
    assert base_url.startswith("http")


@pytest.mark.smoke
def test_admin_client_list_products(admin_client):
    """admin_client fixture：能调通商品列表。"""
    resp = admin_client.get("/api/products", params={"page": 1, "size": 1})
    body = assert_api_success(resp)
    assert isinstance(body["data"]["records"], list)


@pytest.mark.smoke
def test_user_client_list_cart(user_client):
    """user_client fixture：能调通购物车。"""
    resp = user_client.get("/api/cart")
    body = assert_api_success(resp)
    assert isinstance(body["data"], list)
