@echo off
REM Jenkins / CI 用：无 pause，退出码传给 Jenkins
chcp 65001 >nul
cd /d "%~dp0"

set MODE=%~1
if "%MODE%"=="" set MODE=smoke

REM 没有虚拟环境时自动创建（Jenkins 从 Git 拉代码后需要）
if not exist ".venv1\Scripts\python.exe" (
  echo [CI] creating .venv1 ...
  python -m venv .venv1
  if errorlevel 1 exit /b 1
)

if exist ".venv1\Scripts\python.exe" (
  set PY=.venv1\Scripts\python.exe
) else if exist ".venv\Scripts\python.exe" (
  set PY=.venv\Scripts\python.exe
) else (
  set PY=python
)

echo [CI] mode=%MODE%
echo [CI] python=%PY%
echo.

"%PY%" -m pip install -r requirements.txt -q
if errorlevel 1 exit /b 1

"%PY%" run.py %MODE%
exit /b %ERRORLEVEL%
