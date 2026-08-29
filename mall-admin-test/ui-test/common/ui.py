"""浏览器与定位小工具（第 2 课：先抽公共方法，不做 Page Object）。"""

from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.remote.webdriver import WebDriver
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

from config import WAIT_TIMEOUT


def by_testid(testid: str):
    """本项目首选定位：CSS + data-testid。"""
    return By.CSS_SELECTOR, f'[data-testid="{testid}"]'


def wait(driver: WebDriver, timeout: int = WAIT_TIMEOUT) -> WebDriverWait:
    return WebDriverWait(driver, timeout)


def wait_visible(driver: WebDriver, testid: str, timeout: int = WAIT_TIMEOUT):
    return wait(driver, timeout).until(EC.visibility_of_element_located(by_testid(testid)))


def wait_clickable(driver: WebDriver, testid: str, timeout: int = WAIT_TIMEOUT):
    return wait(driver, timeout).until(EC.element_to_be_clickable(by_testid(testid)))


def fill_by_testid(driver: WebDriver, testid: str, text: str) -> None:
    """填写输入框。Vue/Element Plus 用 Ctrl+A 清空，避免 clear() 清不干净。"""
    el = wait_visible(driver, testid)
    el.click()
    el.send_keys(Keys.CONTROL, "a")
    el.send_keys(Keys.BACKSPACE)
    el.send_keys(text)


def click_by_testid(driver: WebDriver, testid: str) -> None:
    wait_clickable(driver, testid).click()
