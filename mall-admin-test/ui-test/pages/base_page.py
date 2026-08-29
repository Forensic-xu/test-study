"""Page Object 基类：封装 driver + 通用操作。"""

from selenium.webdriver.remote.webdriver import WebDriver

from common.ui import click_by_testid, fill_by_testid, wait, wait_clickable, wait_visible


class BasePage:
    def __init__(self, driver: WebDriver):
        self.driver = driver

    def fill(self, testid: str, text: str) -> None:
        fill_by_testid(self.driver, testid, text)

    def click(self, testid: str) -> None:
        click_by_testid(self.driver, testid)

    def visible(self, testid: str):
        return wait_visible(self.driver, testid)

    def clickable(self, testid: str):
        return wait_clickable(self.driver, testid)

    def wait_until(self, condition, timeout: int = None):
        if timeout is None:
            return wait(self.driver).until(condition)
        return wait(self.driver, timeout).until(condition)
