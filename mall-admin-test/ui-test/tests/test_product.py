"""第 3 / 5 课：商品管理（Page Object）。"""

from common.flows import login_as_admin
from pages.products_page import ProductsPage


def test_open_products_page(driver):
    """admin 登录后点侧栏「商品管理」，应看到商品列表页。"""
    page = login_as_admin(driver).open_products()

    assert "/products" in driver.current_url
    assert page.table_text() is not None


def test_search_product_by_name(driver):
    """按名称搜索种子商品「低价零食」。"""
    page = login_as_admin(driver).open_products()
    page.search_by_name("低价零食")

    assert "低价零食" in page.table_text()


def test_open_product_detail(driver):
    """打开列表中某一商品的详情弹窗。"""
    login_as_admin(driver)
    page = ProductsPage(driver).open_direct()

    page.open_first_detail()
    assert page.form_name_disabled()
