"""从 data/ 加载参数化用例的辅助函数。"""

from typing import Any, Dict, List, Sequence


def case_ids(cases: Sequence[Dict[str, Any]], key: str = "case_id") -> List[str]:
    """提取用例 id 列表，供 pytest ids= 使用。"""
    return [str(case[key]) for case in cases]
