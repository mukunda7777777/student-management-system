@echo off
title Student Management System (SMS)
echo ========================================================
echo   Launching Student Management System (SMS)...
echo ========================================================

set "CP=target\classes;lib\*"

java -cp "%CP%" com.sms.Main
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================================
    echo  Application exited with code %ERRORLEVEL%
    echo  If this is a database connection error, ensure:
    echo   1. MySQL server is running on localhost:3306
    echo   2. Run db/schema.sql and db/sample_data.sql
    echo   3. Check src/main/resources/db.properties credentials
    echo ========================================================
    pause
)
