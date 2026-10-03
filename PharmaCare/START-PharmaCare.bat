@echo off
setlocal
title PharmaCare - Pharmacy Management System
cd /d "%~dp0"
set "APP_JAR=target\pharmacy-management-system-1.0.0.jar"

REM ===== If the app is already running, just open the browser =====
netstat -ano | findstr ":8080 " | findstr "LISTENING" >nul
if %errorlevel%==0 (
    echo  PharmaCare is already running. Opening browser...
    start "" http://localhost:8080
    timeout /t 3 >nul
    exit /b
)

REM ===== Step 1: Find a Java JDK, version 17 or newer =====
set "JDK="
if exist "%~dp0runtime\jdk\bin\javac.exe" set "JDK=%~dp0runtime\jdk"
if not defined JDK if defined JAVA_HOME call :tryJdk "%JAVA_HOME%"
if not defined JDK for /d %%D in ("%USERPROFILE%\.jdks\*") do if not defined JDK call :tryJdk "%%~fD"
if not defined JDK for /f "delims=" %%J in ('where javac 2^>nul') do if not defined JDK call :tryJdk "%%~dpJ.."

REM Nothing found: download a portable JDK into this folder (one time, about 190 MB)
if not defined JDK (
    echo.
    echo  [1/3] Java 17+ not found. Downloading portable Java - one time only...
    echo        This can take 2-5 minutes. Please wait.
    if not exist runtime mkdir runtime
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri 'https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse' -OutFile 'runtime\jdk.zip'; Expand-Archive -Path 'runtime\jdk.zip' -DestinationPath 'runtime\tmp' -Force; Move-Item -Path (Get-ChildItem 'runtime\tmp' -Directory | Select-Object -First 1).FullName -Destination 'runtime\jdk'; Remove-Item 'runtime\jdk.zip','runtime\tmp' -Recurse -Force"
    if exist "%~dp0runtime\jdk\bin\javac.exe" set "JDK=%~dp0runtime\jdk"
)
if not defined JDK (
    echo.
    echo  ERROR: Could not find or download Java.
    echo  Install "Temurin 17 JDK" from https://adoptium.net and run this file again.
    pause
    exit /b 1
)
echo  [1/3] Using Java: %JDK%

REM ===== Step 2: Build the app (only the first time) =====
if not exist "%APP_JAR%" (
    echo.
    echo  [2/3] Building PharmaCare - first time only, takes 2-5 minutes...
    echo.
    set "JAVA_HOME=%JDK%"
    call "%~dp0mvnw.cmd" -B -ntp -DskipTests package
)
if not exist "%APP_JAR%" (
    echo.
    echo  ERROR: Build failed. Check your internet connection and run this file again.
    pause
    exit /b 1
)

REM ===== Step 3: Start the app =====
echo.
echo  [3/3] Starting PharmaCare... your browser will open in 10-20 seconds.
echo        Keep this window open. Close it to stop the app.
echo.
"%JDK%\bin\java.exe" -jar "%APP_JAR%"

echo.
echo  PharmaCare has stopped.
pause
exit /b

REM ===== Helper: accept a folder only if it is a JDK version 17 or newer =====
:tryJdk
if not exist "%~1\bin\javac.exe" exit /b
"%~1\bin\java.exe" -version 2>&1 | findstr /r /c:"version \"1[7-9]" /c:"version \"[2-9][0-9]" >nul
if %errorlevel%==0 set "JDK=%~f1"
exit /b
