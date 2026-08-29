    # mall-admin-test 接口自动化

Python + Requests + Pytest，被测系统：`mall-admin-test` 后端 API。

## 1. 环境准备

### 1.1 启动后端

确保后端运行在：http://127.0.0.1:8080

### 1.2 创建虚拟环境（推荐）

```bash
cd mall-admin-test/api-test
python -m venv .venv

# Windows PowerShell
.\.venv\Scripts\Activate.ps1

# 安装依赖
pip install -r requirements.txt
```

### 1.3 配置环境变量（可选）

```bash
copy .env.example .env
```

不创建 `.env` 也可以：默认使用 `config/settings.py` 里的本地配置。

## 2. 运行测试

```bash
# 全部用例
pytest

# 冒烟用例
pytest -m smoke

# 按模块
pytest tests/test_order.py -v

# 生成 HTML 报告
pytest --html=htmlreport/report.html --self-contained-html
```

## 3. 项目结构

```text
api-test/
├── conftest.py              # 全局 fixture + 后端检查
├── config/settings.py       # 环境配置
├── common/                  # 断言、登录、ApiClient、load_json
├── api/                     # 接口封装（auth/product/cart/order…）
├── data/
│   ├── accounts.py 等       # 数据入口（兼容 import）
│   └── json/                # 第 6 课：用例 JSON 外置
├── tests/                   # 用例 test_*.py
├── pytest.ini
└── requirements.txt
```

## 4. 核心依赖

| 包 | 用途 |
|----|------|
| requests | 发 HTTP 请求 |
| pytest | 测试框架 |
| python-dotenv | 读取 `.env` 配置 |
| pytest-html | HTML 测试报告 |

学习笔记：`doc/learning/stage-05-pytest/lesson-01-first-test.md`
