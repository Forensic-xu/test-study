"""项目级 conftest：全局 fixture + 运行前检查。

pytest 会自动加载本文件（位于 tests/ 的父目录）。
"""

import pytest
import requests

from common.auth_helper import fetch_token
from common.http_client import ApiClient
from config.settings import (
    ADMIN_PASSWORD,
    ADMIN_USERNAME,
    BASE_URL,
    USER02_PASSWORD,
    USER02_USERNAME,
    USER_PASSWORD,
    USER_USERNAME,
)


def pytest_sessionstart(session):
    """全部用例开始前：确认后端可访问。"""
    try:
        resp = requests.get(f"{BASE_URL}/swagger-ui.html", timeout=5)
        if resp.status_code >= 500:
            pytest.exit(f"后端不可用（HTTP {resp.status_code}）: {BASE_URL}")
    except requests.RequestException as exc:
        pytest.exit(f"无法连接后端 {BASE_URL}，请先启动 Spring Boot。原因: {exc}")


@pytest.fixture(scope="session")
def base_url() -> str:
    return BASE_URL


@pytest.fixture(scope="session")
def http_session():
    """复用 TCP 连接，整个测试会话共享一个 Session。"""
    session = requests.Session()
    yield session
    session.close()


@pytest.fixture(scope="session")
def admin_token() -> str:
    return fetch_token(ADMIN_USERNAME, ADMIN_PASSWORD)


@pytest.fixture(scope="session")
def user_token() -> str:
    return fetch_token(USER_USERNAME, USER_PASSWORD)


@pytest.fixture(scope="session")
def user02_token() -> str:
    return fetch_token(USER02_USERNAME, USER02_PASSWORD)


@pytest.fixture(scope="session")
def tokens(admin_token, user_token, user02_token) -> dict:
    return {"admin": admin_token, "user01": user_token, "user02": user02_token}


@pytest.fixture(scope="session")
def admin_client(admin_token, http_session) -> ApiClient:
    """ADMIN 身份的 API 客户端。"""
    client = ApiClient(admin_token, session=http_session)
    yield client
    client.close()


@pytest.fixture(scope="session")
def user_client(user_token, http_session) -> ApiClient:
    """user01 身份的 API 客户端。"""
    client = ApiClient(user_token, session=http_session)
    yield client
    client.close()


@pytest.fixture(scope="session")
def user02_client(user02_token, http_session) -> ApiClient:
    """user02 身份的 API 客户端。"""
    client = ApiClient(user02_token, session=http_session)
    yield client
    client.close()
