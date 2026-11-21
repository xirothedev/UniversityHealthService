@echo off
title UHS - Check setup
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run.ps1" check
echo.
pause
