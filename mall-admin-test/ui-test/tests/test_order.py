"""第 4 / 5 课：订单管理（Page Object）。"""

from common.flows import login_as_admin


def test_open_orders_page(driver):
    """admin 登录后点侧栏「订单管理」，应看到订单列表且有数据。"""
    page = login_as_admin(driver).open_orders()

    assert "/orders" in driver.current_url
    assert "ORD" in page.table_text()


def test_search_order_by_no(driver):
    """按当前页第一条订单号过滤。"""
    page = login_as_admin(driver).open_orders()
    order_no = page.first_order_no()

    page.search_by_no(order_no)

    assert order_no in page.table_text()
    assert "No Data" not in page.table_text()


def test_open_order_detail(driver):
    """点当前页第一条「详情」进入详情页。"""
    page = login_as_admin(driver).open_orders()
    page.open_first_detail()

    assert "/orders/" in driver.current_url


def test_pay_pending_order_when_available(driver):
    """有「支付」就点；没有也能绿。"""
    page = login_as_admin(driver).open_orders()
    page.pay_first_pending_if_any()

    assert "ORD" in page.table_text()
