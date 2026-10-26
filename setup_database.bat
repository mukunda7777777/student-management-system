@echo off
title Student Management System - Database Setup
echo ========================================================
echo   Running Database Setup for Student Management System
echo ========================================================

java -cp "target\classes;lib\*" com.sms.util.DBInitializer
echo.
pause
