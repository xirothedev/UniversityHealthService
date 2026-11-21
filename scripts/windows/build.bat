@echo off
title UHS - Quick build
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0run.ps1" build
echo.
pause
