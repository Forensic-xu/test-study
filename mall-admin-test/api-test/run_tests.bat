@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ========================================
echo  mall-admin-test api-test
echo  python run.py report  （同本脚本）
echo ========================================
echo.

if exist ".venv1\Scripts\python.exe" (
  set PY=.venv1\Scripts\python.exe
) else if exist ".venv\Scripts\python.exe" (
  set PY=.venv\Scripts\python.exe
) else (
  set PY=python
)

if not exist "htmlreport\" mkdir htmlreport

echo [INFO] 运行 pytest，报告输出到 htmlreport\report.html
echo.
"%PY%" run.py report

echo.
if exist "logs\pytest.log" (
  echo [INFO] 日志文件: logs\pytest.log
)
if exist "htmlreport\report.html" (
  echo [INFO] HTML 报告: htmlreport\report.html
)
echo.
pause
