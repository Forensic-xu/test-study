# 冒烟用例：环境通了没？跑 pytest -m smoke 只跑这几条

import pytest

from common.assertions import assert_api_success


@pytest.mark.smoke
def test_backend_reachable(base_url):
    assert base_url.startswith("http")


@pytest.mark.smoke
def test_admin_client_list_products(admin_client):
    # 验证 admin_client 这个 fixture 能正常发请求
    body = assert_api_success(admin_client.get("/api/products", params={"page": 1, "size": 1}))
    assert isinstance(body["data"]["records"], list)


@pytest.mark.smoke
def test_user_client_list_cart(user_client):
    body = assert_api_success(user_client.get("/api/cart"))
    assert isinstance(body["data"], list)
