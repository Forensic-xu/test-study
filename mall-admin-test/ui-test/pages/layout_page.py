"""登录后的管理布局（侧栏菜单）。"""

from pages.base_page import BasePage
from pages.orders_page import OrdersPage
from pages.products_page import ProductsPage


class LayoutPage(BasePage):
    def header_username(self) -> str:
        return self.visible("header-username").text

    def open_products(self) -> ProductsPage:
        self.click("menu-products")
        page = ProductsPage(self.driver)
        page.wait_loaded()
        return page

    def open_orders(self) -> OrdersPage:
        self.click("menu-orders")
        page = OrdersPage(self.driver)
        page.wait_loaded()
        return page
