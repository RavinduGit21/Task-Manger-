@echo off
echo Creating Task Manager Distribution Package...
echo.

REM First build the EXE
echo Building executable...
call build_exe.bat

if errorlevel 1 (
    echo Build failed! Cannot create distribution package.
    pause
    exit /b 1
)

REM Create distribution folder
if not exist "Distribution" mkdir Distribution
if not exist "Distribution\TaskManager" mkdir Distribution\TaskManager

REM Copy App folder contents to distribution
echo Copying application files...
xcopy "App\*" "Distribution\TaskManager\" /E /I /Y

REM Create version info file
echo Creating version info...
echo Task Manager v1.0.0 > "Distribution\TaskManager\VERSION.txt"
echo Build Date: %date% %time% >> "Distribution\TaskManager\VERSION.txt"
echo Java Version: 1.8+ >> "Distribution\TaskManager\VERSION.txt"
echo Platform: Windows >> "Distribution\TaskManager\VERSION.txt"

REM Create distribution README
echo Creating distribution README...
copy "App\README.txt" "Distribution\TaskManager\README.txt"

REM Create quick start guide
echo Creating quick start guide...
(
echo ========================================
echo TASK MANAGER - QUICK START GUIDE
echo ========================================
echo.
echo INSTALLATION:
echo 1. Extract this folder to your desired location
echo 2. Ensure Java 8+ is installed on your system
echo 3. Run install.bat as administrator (optional)
echo 4. Double-click TaskManager.exe to start
echo.
echo REQUIREMENTS:
echo - Windows 7/8/10/11
echo - Java Runtime Environment 8 or higher
echo - 100MB free disk space
echo.
echo FEATURES:
echo ✓ Dark theme UI
echo ✓ Task management (CRUD operations)
echo ✓ Priority levels with color coding
echo ✓ Search and filter
echo ✓ Customizable appearance
echo ✓ Persistent data storage
echo.
echo SUPPORT:
echo If you encounter issues:
echo 1. Check that Java is installed: java -version
echo 2. Try running TaskManager.jar directly
echo 3. Ensure all files in lib/ folder are present
echo.
echo Enjoy using Task Manager!
echo ========================================
) > "Distribution\TaskManager\QUICK_START.txt"

REM Create ZIP package
echo Creating ZIP package...
cd Distribution
powershell -Command "Compress-Archive -Path 'TaskManager' -DestinationPath 'TaskManager-v1.0.0-Windows.zip' -Force"
cd ..

echo.
echo ========================================
echo Distribution package created!
echo ========================================
echo.
echo Location: Distribution\TaskManager-v1.0.0-Windows.zip
echo.
echo This ZIP file contains:
echo ✓ Complete Task Manager application
echo ✓ All required dependencies
echo ✓ Installation scripts
echo ✓ Documentation
echo ✓ Application logo
echo.
echo You can now distribute this ZIP file to users!
echo.
pause
