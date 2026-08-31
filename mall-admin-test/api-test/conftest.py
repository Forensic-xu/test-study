# 全局 conftest：所有用例跑之前/之中要用的公共准备
#
# pytest 会自动找这个文件，不用 import。
# 放这里的东西，tests/ 下每个 test_ 函数都能直接用（当参数写就行）。

import pytest
import requests

from common.auth_helper import fetch_token
from common.http_client import ApiClient
from common.log_config import get_logger, setup_logging
from config.settings import (
    ADMIN_PASSWORD,
    ADMIN_USERNAME,
    BASE_URL,
    USER02_PASSWORD,
    USER02_USERNAME,
    USER_PASSWORD,
    USER_USERNAME,
)

logger = get_logger("conftest")


def pytest_configure(config):
    # 第 8 课：一启动 pytest 就打开日志（文件 + 控制台）
    setup_logging()


def pytest_sessionstart(session):
    # 一上来先 ping 一下后端，没启动就别白跑了
    logger.info("检查后端: %s", BASE_URL)
    try:
        resp = requests.get(f"{BASE_URL}/swagger-ui.html", timeout=5)
        if resp.status_code >= 500:
            logger.error("后端异常 HTTP %s", resp.status_code)
            pytest.exit(f"后端挂了（HTTP {resp.status_code}）: {BASE_URL}")
        logger.info("后端可达 HTTP %s", resp.status_code)
    except requests.RequestException as exc:
        logger.error("连不上后端: %s", exc)
        pytest.exit(f"连不上后端 {BASE_URL}，先把 Spring Boot 启起来。{exc}")


def pytest_runtest_logreport(report):
    # 每条用例跑完记一笔（只看 call 阶段，setup/teardown 不重复记）
    if report.when != "call":
        return
    if report.passed:
        logger.info("PASSED %s", report.nodeid)
    elif report.failed:
        logger.error("FAILED %s", report.nodeid)
    elif report.skipped:
        logger.warning("SKIPPED %s", report.nodeid)


@pytest.fixture(scope="session")
def base_url() -> str:
    # 等同 Postman 里的 {{base_url}}
    return BASE_URL


@pytest.fixture(scope="session")
def http_session():
    # 整个 pytest 过程共用一个 Session，少建 TCP 连接
    session = requests.Session()
    yield session
    session.close()


@pytest.fixture(scope="session")
def admin_token() -> str:
    # session 级别 = 只登录一次，后面所有用例复用
    return fetch_token(ADMIN_USERNAME, ADMIN_PASSWORD)


@pytest.fixture(scope="session")
def user_token() -> str:
    return fetch_token(USER_USERNAME, USER_PASSWORD)


@pytest.fixture(scope="session")
def user02_token() -> str:
    # 专门用来测「越权」：user02 动 user01 的数据
    return fetch_token(USER02_USERNAME, USER02_PASSWORD)


@pytest.fixture(scope="session")
def tokens(admin_token, user_token, user02_token) -> dict:
    # 一次拿齐三个 token，省得用例里写三个参数
    return {"admin": admin_token, "user01": user_token, "user02": user02_token}


@pytest.fixture(scope="session")
def admin_client(admin_token, http_session) -> ApiClient:
    # 带 token 的客户端，用例里直接 client.get("/api/xxx") 就行
    client = ApiClient(admin_token, session=http_session)
    yield client
    client.close()


@pytest.fixture(scope="session")
def user_client(user_token, http_session) -> ApiClient:
    client = ApiClient(user_token, session=http_session)
    yield client
    client.close()


@pytest.fixture(scope="session")
def user02_client(user02_token, http_session) -> ApiClient:
    client = ApiClient(user02_token, session=http_session)
    yield client
    client.close()
