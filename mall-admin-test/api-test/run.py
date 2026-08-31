"""一键运行测试（第 9 课）。

用法（在 api-test 目录下）：
  python run.py          # 全量
  python run.py smoke    # 冒烟 3 条
  python run.py report   # 全量 + HTML 报告
"""

import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent


def _pytest_cmd(mode: str) -> list:
    cmd = [sys.executable, "-m", "pytest", "-v"]
    if mode == "smoke":
        cmd.extend(["-m", "smoke"])
    elif mode == "report":
        html_dir = ROOT / "htmlreport"
        html_dir.mkdir(exist_ok=True)
        cmd.extend(
            [
                "--html=htmlreport/report.html",
                "--self-contained-html",
            ]
        )
    elif mode != "all":
        print(f"未知模式: {mode}")
        print("可用: smoke | report | （空=全量）")
        sys.exit(2)
    return cmd


def main() -> int:
    mode = sys.argv[1].lower() if len(sys.argv) > 1 else "all"
    cmd = _pytest_cmd(mode)
    print("执行:", " ".join(cmd))
    print()
    return subprocess.call(cmd, cwd=ROOT)


if __name__ == "__main__":
    raise SystemExit(main())
