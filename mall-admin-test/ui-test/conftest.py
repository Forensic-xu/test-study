"""pytest fixtures：每个用例独立浏览器，用例结束自动 quit。"""

import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options


@pytest.fixture
def driver():
    """function 作用域：一条用例一个浏览器，互不影响。"""
    options = Options()
    options.add_argument("--window-size=1280,800")
    # CI 无界面时可开：options.add_argument("--headless=new")
    drv = webdriver.Chrome(options=options)
    drv.implicitly_wait(0)  # 只用显式等待，避免和 WebDriverWait 混用
    yield drv
    drv.quit()
