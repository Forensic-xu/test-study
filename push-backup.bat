@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion
cd /d "%~dp0"

echo ========================================
echo  test-study 推送到远程备份
echo  仓库: %CD%
echo ========================================
echo.

where git >nul 2>&1
if errorlevel 1 (
  echo [ERROR] 未找到 git，请先安装 Git
  pause
  exit /b 1
)

git rev-parse --is-inside-work-tree >nul 2>&1
if errorlevel 1 (
  echo [ERROR] 当前目录不是 git 仓库
  pause
  exit /b 1
)

for /f %%b in ('git branch --show-current') do set BRANCH=%%b
echo [INFO] 当前分支: %BRANCH%
git remote get-url origin 2>nul && echo [INFO] 远程: origin
echo.

echo [INFO] 暂存所有变更 ^(git add -A^) ...
git add -A

git diff --cached --quiet
if errorlevel 1 (
  for /f "delims=" %%t in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd_HH-mm-ss"') do set "TS=%%t"
  if not defined TS set "TS=manual"
  set "MSG=backup !TS!"
  echo [INFO] 提交: !MSG!
  git commit -m "!MSG!"
  if errorlevel 1 (
    echo [ERROR] git commit 失败
    pause
    exit /b 1
  )
) else (
  echo [INFO] 工作区无新变更，跳过 commit
)

echo [INFO] 推送到 origin/%BRANCH% ...
git push -u origin %BRANCH%
if errorlevel 1 (
  echo [ERROR] git push 失败
  echo        请检查: 网络、GitHub 登录、分支权限
  pause
  exit /b 1
)

echo.
echo [OK] 备份推送完成
git log -1 --oneline
echo.
pause
