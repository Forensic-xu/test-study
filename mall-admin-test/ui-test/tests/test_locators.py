"""第 2 课：定位与显式等待小练习（不依赖登录成功路径）。"""

from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

from common.ui import wait_visible
from config import BASE_URL, WAIT_TIMEOUT


def test_login_page_testid_visible(driver):
    """打开登录页后，关键 testid 应可见。"""
    driver.get(f"{BASE_URL}/login")

    wait_visible(driver, "login-page")
    wait_visible(driver, "login-title")
    wait_visible(driver, "login-username")
    wait_visible(driver, "login-password")
    wait_visible(driver, "login-submit")


def test_prefer_testid_over_css_class(driver):
    """对比：data-testid 稳定；.el-input__inner 这类 class 易碎。"""
    driver.get(f"{BASE_URL}/login")

    by_testid = wait_visible(driver, "login-username")
    by_class = WebDriverWait(driver, WAIT_TIMEOUT).until(
        EC.visibility_of_element_located((By.CSS_SELECTOR, "input.el-input__inner"))
    )

    # 两者都能找到，但项目约定优先 testid
    assert by_testid.get_attribute("data-testid") == "login-username"
    assert by_class.tag_name == "input"
