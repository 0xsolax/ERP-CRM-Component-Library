@echo off
REM 从模板复制新项目：调用同目录 rename-project.ps1（需 Windows 自带 PowerShell，无需 Git Bash/rsync）
REM 路径：docs/sh/windows/ — 与 docs/sh/unix/ 分列不同环境脚本
REM 若提示无法加载脚本，请使用：
REM   powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0rename-project.ps1"
REM 失败时 pause 避免窗口一闪而过（双击运行时）

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0rename-project.ps1"
set ERR=%ERRORLEVEL%
if %ERR% neq 0 (
  echo.
  echo [错误] 脚本退出码: %ERR%，请向上查看 PowerShell 报错。
  pause
)
exit /b %ERR%
