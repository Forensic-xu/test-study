# 第 8 课：统一日志配置
#
# 跑 pytest 时：
#   - 终端能看到 INFO（方便你看进度）
#   - logs/pytest.log 里也有完整记录（方便事后翻）

import logging
from pathlib import Path

# api-test/logs/pytest.log
LOG_DIR = Path(__file__).resolve().parent.parent / "logs"
LOG_FILE = LOG_DIR / "pytest.log"


def setup_logging(level: int = logging.INFO) -> None:
    """初始化日志：同时写文件 + 控制台。重复调用不会叠 handler。"""
    root = logging.getLogger()
    if root.handlers:
        return

    LOG_DIR.mkdir(parents=True, exist_ok=True)

    fmt = logging.Formatter(
        "%(asctime)s [%(levelname)s] %(name)s - %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )

    file_handler = logging.FileHandler(LOG_FILE, encoding="utf-8")
    file_handler.setLevel(level)
    file_handler.setFormatter(fmt)

    console_handler = logging.StreamHandler()
    console_handler.setLevel(level)
    console_handler.setFormatter(fmt)

    root.setLevel(level)
    root.addHandler(file_handler)
    root.addHandler(console_handler)


def get_logger(name: str) -> logging.Logger:
    return logging.getLogger(name)
