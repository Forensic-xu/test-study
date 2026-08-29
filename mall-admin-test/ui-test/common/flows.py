"""常用页面流程：基于 Page Object。"""

from selenium.webdriver.remote.webdriver import WebDriver

from config import ADMIN_PASSWORD, ADMIN_USERNAME
from pages.layout_page import LayoutPage
from pages.login_page import LoginPage


def login_as_admin(driver: WebDriver) -> LayoutPage:
    """打开登录页并用 admin 登录，返回布局页对象。"""
    return LoginPage(driver).open().login(ADMIN_USERNAME, ADMIN_PASSWORD)
