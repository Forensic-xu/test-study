"""第 1～2 / 5 课：登录页（Page Object）。"""

from config import ADMIN_PASSWORD, ADMIN_USERNAME
from pages.login_page import LoginPage


def test_login_success(driver):
    """正确账号密码 → 进入 Dashboard。"""
    layout = LoginPage(driver).open().login(ADMIN_USERNAME, ADMIN_PASSWORD)

    layout.visible("dashboard-page")
    assert "admin" in layout.header_username()


def test_login_wrong_password(driver):
    """错误密码 → 停留登录页并显示错误。"""
    page = LoginPage(driver).open()
    err = page.login_expect_error(ADMIN_USERNAME, "WrongPass!")

    assert err.text.strip() != ""
    assert "login" in driver.current_url
