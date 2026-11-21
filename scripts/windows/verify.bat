@echo off
title UHS - Full build
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run.ps1" verify
echo.
pause
