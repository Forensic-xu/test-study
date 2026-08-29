"""订单管理页。"""

from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC

from pages.base_page import BasePage


class OrdersPage(BasePage):
    def wait_loaded(self) -> None:
        self.visible("orders-page")
        self.visible("order-table")
        self.wait_until(
            lambda d: "ORD" in d.find_element(By.CSS_SELECTOR, '[data-testid="order-table"]').text
        )

    def table_text(self) -> str:
        return self.visible("order-table").text

    def first_order_no(self) -> str:
        rows = self.driver.find_elements(
            By.CSS_SELECTOR, '[data-testid="order-table"] .el-table__body tr'
        )
        assert rows, "订单列表为空，请确认后端有订单数据"
        order_no = rows[0].find_elements(By.CSS_SELECTOR, "td")[0].text.strip()
        assert order_no.startswith("ORD"), f"未读到订单号: {order_no!r}"
        return order_no

    def search_by_no(self, order_no: str) -> None:
        self.fill("order-search-no", order_no)
        self.click("order-search")

    def open_first_detail(self) -> None:
        btns = self.driver.find_elements(By.CSS_SELECTOR, '[data-testid^="order-detail-"]')
        assert btns, "没有详情按钮"
        btns[0].click()
        self.visible("order-detail-page")
        self.visible("order-detail-info")
        self.visible("order-detail-items")

    def pay_first_pending_if_any(self) -> bool:
        """有支付按钮则点并返回 True；否则 False。"""
        pay_btns = self.driver.find_elements(By.CSS_SELECTOR, '[data-testid^="order-pay-"]')
        if not pay_btns:
            return False
        testid = pay_btns[0].get_attribute("data-testid")
        pay_btns[0].click()
        self.wait_until(
            EC.invisibility_of_element_located((By.CSS_SELECTOR, f'[data-testid="{testid}"]'))
        )
        return True
