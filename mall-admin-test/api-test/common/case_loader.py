# 测试数据加载工具
#
# 第 6 课重点：用例数据可以放 JSON，改场景不用改 Python 代码。
# load_json("login_error_cases.json") 会去 data/json/ 下找文件。

import json
from pathlib import Path
from typing import Any, Dict, List, Sequence, Union

# data/json/ 目录（相对本文件：common/ -> 上一级 api-test/ -> data/json）
_JSON_DIR = Path(__file__).resolve().parent.parent / "data" / "json"


def load_json(filename: str) -> Union[Dict[str, Any], List[Any]]:
    """从 data/json/ 读一个 JSON 文件，返回 dict 或 list。"""
    path = _JSON_DIR / filename
    if not path.exists():
        raise FileNotFoundError(f"找不到测试数据文件: {path}")
    with path.open(encoding="utf-8") as f:
        return json.load(f)


def case_ids(cases: Sequence[Dict[str, Any]], key: str = "case_id") -> List[str]:
    # 让 pytest 报告里显示 [wrong_password] 而不是 [case0]
    return [str(case[key]) for case in cases]
