# mall-admin-test · UI 自动化（Selenium）

前端：`http://127.0.0.1:5176`（需同时启动后端 `:8080`）

## 快速开始

```bash
cd mall-admin-test/ui-test
pip install -r requirements.txt
pytest
```

本机需已安装 **Google Chrome**。Selenium 4.6+ 会自动管理 ChromeDriver。

## 目录

```text
ui-test/
├── conftest.py              # driver fixture
├── config.py
├── common/
│   ├── ui.py                # 底层定位 / 等待
│   └── flows.py             # login_as_admin → LayoutPage
├── pages/                   # 第 5 课 Page Object
│   ├── base_page.py
│   ├── login_page.py
│   ├── layout_page.py
│   ├── products_page.py
│   └── orders_page.py
└── tests/
    ├── test_login.py
    ├── test_locators.py
    ├── test_product.py
    └── test_order.py
```
