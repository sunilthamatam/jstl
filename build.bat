@echo off
setlocal enabledelayedexpansion

echo ================================
echo Building JSTL Native Library
echo ================================
echo.

REM Create build directory in jstl-core
if not exist "jstl-core\build" mkdir "jstl-core\build"
cd jstl-core\build

REM Run CMake
echo Running CMake...
cmake ..
if errorlevel 1 (
    echo Error: CMake configuration failed
    exit /b 1
)

REM Build the native library
echo.
echo Building native library...
cmake --build . --config Release
if errorlevel 1 (
    echo Error: Build failed
    exit /b 1
)

echo.
echo Native library built successfully!
echo   Library location: %CD%\lib\
dir /b lib\

cd ..\..

echo.
echo ================================
echo Building Java Code
echo ================================
echo.

REM Build Java code
call mvn install
if errorlevel 1 (
    echo Error: Maven build failed
    exit /b 1
)

echo.
echo Build completed successfully!
echo.
echo To run examples:
echo   run-examples.bat
echo.
echo Or run individual examples:
echo   cd jstl-examples
echo   mvn exec:java                           REM Runs all examples
echo   mvn exec:java -Parraylist               REM ArrayList example
echo   mvn exec:java -Phashmap                 REM HashMap example
echo   mvn exec:java -Phashset                 REM HashSet example
