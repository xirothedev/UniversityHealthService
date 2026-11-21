@echo off
title UHS - Start app
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run.ps1" start
echo.
pause
