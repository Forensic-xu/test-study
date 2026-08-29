"""商品管理页。"""

from selenium.webdriver.common.by import By

from pages.base_page import BasePage
from config import BASE_URL


class ProductsPage(BasePage):
    def open_direct(self) -> "ProductsPage":
        self.driver.get(f"{BASE_URL}/products")
        self.wait_loaded()
        return self

    def wait_loaded(self) -> None:
        self.visible("products-page")
        self.visible("product-table")

    def search_by_name(self, name: str) -> None:
        self.fill("product-search-name", name)
        self.click("product-search")

    def table_text(self) -> str:
        return self.visible("product-table").text

    def open_first_detail(self) -> None:
        btns = self.driver.find_elements(By.CSS_SELECTOR, '[data-testid^="product-detail-"]')
        assert btns, "商品列表为空，请确认后端种子数据已导入"
        btns[0].click()
        self.visible("product-form-name")

    def form_name_disabled(self) -> bool:
        el = self.visible("product-form-name")
        return el.get_attribute("disabled") is not None or el.get_property("disabled") is True
