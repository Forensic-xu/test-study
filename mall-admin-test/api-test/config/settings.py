"""项目配置：优先读 .env，否则使用本地默认值。"""

import os
from pathlib import Path

from dotenv import load_dotenv

# 加载 api-test/.env（若存在）
_ENV_PATH = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(_ENV_PATH)

BASE_URL = os.getenv("BASE_URL", "http://127.0.0.1:8080").rstrip("/")

ADMIN_USERNAME = os.getenv("ADMIN_USERNAME", "admin")
ADMIN_PASSWORD = os.getenv("ADMIN_PASSWORD", "Admin@123")

USER_USERNAME = os.getenv("USER_USERNAME", "user01")
USER_PASSWORD = os.getenv("USER_PASSWORD", "User@123")

USER02_USERNAME = os.getenv("USER02_USERNAME", "user02")
USER02_PASSWORD = os.getenv("USER02_PASSWORD", "User@123")

DISABLED_USERNAME = os.getenv("DISABLED_USERNAME", "disabled")
DISABLED_PASSWORD = os.getenv("DISABLED_PASSWORD", "User@123")

# 请求超时（秒）
REQUEST_TIMEOUT = int(os.getenv("REQUEST_TIMEOUT", "10"))
