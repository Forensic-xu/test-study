# 参数化用例的小工具

from typing import Any, Dict, List, Sequence


def case_ids(cases: Sequence[Dict[str, Any]], key: str = "case_id") -> List[str]:
    # 让 pytest 报告里显示 [wrong_password] 而不是 [case0]
    return [str(case[key]) for case in cases]
