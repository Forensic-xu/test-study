# 第 7 课：接口关联（说人话版）
#
# 你在 Postman 里常干这种事：
#   1）登录，把 token 存成 {{token}}
#   2）创建分类，把返回的 id 存成 {{category_id}}
#   3）删分类时 URL 写成 /api/categories/{{category_id}}
#
# Pytest 里一样，只不过不用 Postman 变量面板，
# 直接用 Python 变量：category_id = 上一步响应里的 data.id
#
# 记住一句话：
#   「上一步返回什么，下一步就接着用什么。」

import time

from api.category_api import create_category, delete_category
from api.inventory_api import get_inventory, increase_inventory
from api.order_api import (
    complete_order,
    create_order,
    get_order,
    pay_order,
    ship_order,
)
from api.product_api import create_product, delete_product, get_product
from common.assertions import assert_api_error, assert_api_success
from data.orders import DEFAULT_ORDER_ITEM


def _unique(prefix: str) -> str:
    """起个不重复的名字，免得和库里已有分类/商品撞名。

    用时间戳拼一下就行，比如 pytest-cat-1730000000123。
    """
    return f"{prefix}-{int(time.time() * 1000)}"


def test_category_create_then_delete(admin_token):
    """最短关联：创建分类 → 记下 id → 用这个 id 删除。

    admin_token：conftest 里已经帮你登录好的管理员 token，
    这里直接当参数用就行（= Postman 里已经设好的 {{token}}）。
    """
    # ① 发「创建分类」请求；断言成功后拿到整段 JSON 响应体
    create_body = assert_api_success(
        create_category(admin_token, name=_unique("pytest-cat"))
    )

    # ② 【关联点】从响应里抠出 id，存进变量
    #    以后删分类、建商品挂到这个分类，都用它
    category_id = create_body["data"]["id"]

    # ③ 删的时候把变量传进去 —— 这就是「接口关联」
    #    不是写死某个数字，而是用刚创建出来的那一个
    assert_api_success(delete_category(admin_token, category_id))


def test_product_create_inventory_then_cleanup(admin_token):
    """长一点的关联链（业务上更真实）：

    建分类 → category_id
      → 建商品（商品必须挂在某个分类下）→ product_id
      → 查商品 / 加库存（都要用 product_id）
      → 删商品 → 再删分类（顺序不能反）

    为什么最后要清理？
    自动化会反复跑，不删干净库里会堆一堆垃圾数据。
    """
    # ---------- 第 1 步：先有一个空分类 ----------
    category_id = assert_api_success(
        create_category(admin_token, name=_unique("pytest-chain-cat"))
    )["data"]["id"]
    # 上面这行是「创建 + 取 id」写在一起的简写，意思和上一个用例一样

    # ---------- 第 2 步：用 category_id 创建商品 ----------
    product_body = assert_api_success(
        create_product(
            admin_token,
            name=_unique("pytest-chain-prod"),
            category_id=category_id,  # 挂到刚建的分类上
            price=9.9,
            stock=1,  # 初始库存 1
            status="ON_SALE",
            description="lesson07",
        )
    )
    # 再抠出商品 id，后面查详情、改库存、删除都靠它
    product_id = product_body["data"]["id"]

    # ---------- 第 3 步：用 product_id 查详情，确认真是刚建的那个 ----------
    detail = assert_api_success(get_product(admin_token, product_id))
    #断言判断，成功直接通过，失败抛出异常
    assert detail["data"]["id"] == product_id

    # ---------- 第 4 步：用同一个 product_id 加库存 ----------
    # 先查当前库存，再 increase 3，最后核对：新库存 = 旧库存 + 3
    inv_before = assert_api_success(get_inventory(admin_token, product_id))["data"]["stock"]
    after = assert_api_success(
        increase_inventory(admin_token, product_id, quantity=3, remark="lesson07")
    )["data"]["stock"]
    assert after == inv_before + 3

    # ---------- 第 5 步：打扫战场（顺序很重要）----------
    # 业务规则：分类下还有商品时，不允许删分类（会 409/20007）
    # 所以必须：先删商品，再删分类
    assert_api_success(delete_product(admin_token, product_id))
    assert_api_success(delete_category(admin_token, category_id))


def test_category_delete_blocked_when_has_product(admin_token):
    """故意测「删不掉」的情况（也是关联）。

    流程：
      1）建分类、再建一个挂在它下面的商品
      2）这时候去删分类 → 系统应该拒绝（HTTP 409，业务码 20007）
      3）测完了再自己清干净，别留脏数据

    为什么要测失败场景？
    因为线上用户也会乱点删除；接口必须挡住，不能把有商品的分类删了。
    """
    category_id = assert_api_success(
        create_category(admin_token, name=_unique("pytest-block-cat"))
    )["data"]["id"]

    product_id = assert_api_success(
        create_product(
            admin_token,
            name=_unique("pytest-block-prod"),
            category_id=category_id,  # 商品挂在这个分类下
            price=1.0,
            stock=0,
            status="OFF_SALE",
        )
    )["data"]["id"]

    # 分类下还有商品 → 期望失败，不是成功
    # assert_api_error：检查 HTTP 状态码 + 业务 code 都要对上
    assert_api_error(delete_category(admin_token, category_id), 409, 20007)

    # 用例自己负责清理：先商品后分类，这次就能删掉了
    assert_api_success(delete_product(admin_token, product_id))
    assert_api_success(delete_category(admin_token, category_id))


def test_order_pay_ship_complete_chain(user_token, admin_token):
    """订单状态机关联（跨两个角色）。

    普通用户 user01：只能下单、看自己的单
    管理员 admin：才能支付 / 发货 / 完成

    所以这个用例要两个 token：
      user_token  → 创建订单，拿到 order_id
      admin_token → 带着同一个 order_id 往下推状态

    状态流转（必须按顺序，跳步会失败）：
      PENDING（待支付）
        → PAID（已支付）
        → SHIPPED（已发货）
        → COMPLETED（已完成）
    """
    # ① 用户下单。DEFAULT_ORDER_ITEM 里是种子商品 productId=1、数量 1
    order_id = assert_api_success(
        create_order(user_token, items=[DEFAULT_ORDER_ITEM], remark="lesson07-chain")
    )["data"]["id"]
    # 此时订单一般是 PENDING；后面三步都用这个 order_id

    # ② 管理员支付：PENDING → PAID
    assert assert_api_success(pay_order(admin_token, order_id))["data"]["status"] == "PAID"

    # ③ 管理员发货：PAID → SHIPPED
    assert assert_api_success(ship_order(admin_token, order_id))["data"]["status"] == "SHIPPED"

    # ④ 管理员完成：SHIPPED → COMPLETED
    assert (
        assert_api_success(complete_order(admin_token, order_id))["data"]["status"] == "COMPLETED"
    )

    # ⑤ 再用用户身份查一遍详情，确认整条链真的落到 COMPLETED
    #    （不只看改状态接口的返回，再查一次更稳）
    final = assert_api_success(get_order(user_token, order_id))
    assert final["data"]["status"] == "COMPLETED"
    assert final["data"]["id"] == order_id
