@echo off
REM Use this after you change the code: it deletes the old build and starts again.
cd /d "%~dp0"
if exist target rmdir /s /q target
call "%~dp0START-PharmaCare.bat"
