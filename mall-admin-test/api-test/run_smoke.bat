@echo off
chcp 65001 >nul
cd /d "%~dp0"

if exist ".venv1\Scripts\python.exe" (
  set PY=.venv1\Scripts\python.exe
) else if exist ".venv\Scripts\python.exe" (
  set PY=.venv\Scripts\python.exe
) else (
  set PY=python
)

echo [INFO] 冒烟测试（3 条）
"%PY%" run.py smoke
pause
