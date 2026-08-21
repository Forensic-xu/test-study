@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ========================================
echo  mall-admin-test frontend
echo  http://127.0.0.1:5176
echo ========================================
echo.

where node >nul 2>&1
if errorlevel 1 (
  echo [ERROR] 未找到 Node.js，请先安装 Node.js 18+
  pause
  exit /b 1
)

where npm >nul 2>&1
if errorlevel 1 (
  echo [ERROR] 未找到 npm
  pause
  exit /b 1
)

if not exist "node_modules\" (
  echo [INFO] 首次启动，正在执行 npm install ...
  call npm install
  if errorlevel 1 (
    echo [ERROR] npm install 失败
    pause
    exit /b 1
  )
  echo.
)

REM 若 5176 已被占用，先释放（常见于上次 Vite 未关闭）
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5176" ^| findstr "LISTENING"') do (
  echo [WARN] 端口 5176 已被占用，PID=%%a，正在结束该进程...
  taskkill /F /PID %%a >nul 2>&1
)
timeout /t 1 /nobreak >nul

echo [INFO] 启动 Vite 开发服务器 ^(端口 5176^) ...
echo [INFO] 请确保后端已在 http://127.0.0.1:8080 运行
echo.
call npm run dev

pause
