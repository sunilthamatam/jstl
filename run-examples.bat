@echo off

echo Running all JSTL examples...
echo.

REM Navigate to examples module and run
cd jstl-examples
call mvn exec:java
if errorlevel 1 (
    echo Error: Failed to run examples
    exit /b 1
)

echo.
echo All examples completed!
