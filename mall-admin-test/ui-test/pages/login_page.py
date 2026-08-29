"""登录页。"""

from pages.base_page import BasePage
from pages.layout_page import LayoutPage
from config import BASE_URL


class LoginPage(BasePage):
    def open(self) -> "LoginPage":
        self.driver.get(f"{BASE_URL}/login")
        self.visible("login-page")
        return self

    def login(self, username: str, password: str) -> LayoutPage:
        self.fill("login-username", username)
        self.fill("login-password", password)
        self.click("login-submit")
        self.visible("admin-layout")
        return LayoutPage(self.driver)

    def login_expect_error(self, username: str, password: str):
        self.fill("login-username", username)
        self.fill("login-password", password)
        self.click("login-submit")
        return self.visible("login-error")
