@echo off
echo Building Task Manager Executable...
echo.

REM Create App folder structure
if not exist "App" mkdir App
if not exist "App\lib" mkdir App\lib
if not exist "App\assets" mkdir App\assets

REM Download Launch4j if not exists
if not exist "launch4j.jar" (
    echo Downloading Launch4j...
    powershell -Command "try { Invoke-WebRequest -Uri 'https://github.com/launch4j/launch4j/releases/download/v1.50/launch4j-1.50-win32.exe' -OutFile 'launch4j-installer.exe' -UseBasicParsing } catch { Write-Host 'Download failed, trying alternative method...' }"
    
    if not exist "launch4j-installer.exe" (
        echo Trying alternative download method...
        powershell -Command "try { (New-Object Net.WebClient).DownloadFile('https://github.com/launch4j/launch4j/releases/download/v1.50/launch4j-1.50-win32.exe', 'launch4j-installer.exe') } catch { Write-Host 'Alternative download also failed' }"
    )
    
    if exist "launch4j-installer.exe" (
        echo Installing Launch4j...
        launch4j-installer.exe /S
        timeout /t 5 /nobreak >nul
        del launch4j-installer.exe
    ) else (
        echo Launch4j download failed. Please download manually from:
        echo https://github.com/launch4j/launch4j/releases/download/v1.50/launch4j-1.50-win32.exe
        echo.
        echo After downloading, install it and run this script again.
        pause
        exit /b 1
    )
)

REM Download required JARs if not exists
if not exist "lib" mkdir lib

if not exist "lib\sqlite-jdbc-3.42.0.0.jar" (
    echo Downloading SQLite JDBC...
    powershell -Command "try { Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.42.0.0/sqlite-jdbc-3.42.0.0.jar' -OutFile 'lib\sqlite-jdbc-3.42.0.0.jar' -UseBasicParsing } catch { Write-Host 'Download failed' }"
)

if not exist "lib\slf4j-api-1.7.36.jar" (
    echo Downloading SLF4J API...
    powershell -Command "try { Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar' -OutFile 'lib\slf4j-api-1.7.36.jar' -UseBasicParsing } catch { Write-Host 'Download failed' }"
)

if not exist "lib\slf4j-simple-1.7.36.jar" (
    echo Downloading SLF4J Simple...
    powershell -Command "try { Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar' -OutFile 'lib\slf4j-simple-1.7.36.jar' -UseBasicParsing } catch { Write-Host 'Download failed' }"
)

REM Check if required JARs exist
if not exist "lib\sqlite-jdbc-3.42.0.0.jar" (
    echo Error: Required JAR files not found. Please check your internet connection.
    pause
    exit /b 1
)

REM Create target directory
if not exist "target\classes" mkdir target\classes

REM Compile Java code - fix the path issue
echo Compiling Java code...
javac -cp "lib/*" -source 1.8 -target 1.8 -d target/classes src/main/java/com/example/taskmanager/Main.java src/main/java/com/example/taskmanager/model/Task.java src/main/java/com/example/taskmanager/db/DatabaseHandler.java src/main/java/com/example/taskmanager/ui/MainUI.java src/main/java/com/example/taskmanager/ui/TaskTableModel.java src/main/java/com/example/taskmanager/ui/TaskDialog.java src/main/java/com/example/taskmanager/ui/PriorityCellRenderer.java src/main/java/com/example/taskmanager/ui/DatePickerPopup.java src/main/java/com/example/taskmanager/ui/SettingsDialog.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Create JAR file
echo Creating JAR file...
cd target\classes
jar cfm ..\TaskManager.jar ..\..\manifest.txt com/example/taskmanager/**/*.class
cd ..\..

REM Copy JAR to App folder
copy "target\TaskManager.jar" "App\TaskManager.jar"

REM Copy dependencies to App\lib
copy "lib\*.jar" "App\lib\"

REM Copy logo to App\assets
if exist "logo.png" (
    copy "logo.png" "App\assets\logo.png"
) else (
    echo Warning: logo.png not found. Creating placeholder...
    echo # Placeholder for logo.png > "App\assets\logo.png"
    echo # Replace with your actual logo image >> "App\assets\logo.png"
)

REM Convert JAR to EXE using Launch4j
echo Converting JAR to EXE...
if exist "C:\Program Files (x86)\Launch4j\launch4jc.exe" (
    "C:\Program Files (x86)\Launch4j\launch4jc.exe" launch4j-config.xml
) else if exist "C:\Program Files\Launch4j\launch4jc.exe" (
    "C:\Program Files\Launch4j\launch4jc.exe" launch4j-config.xml
) else (
    echo Launch4j not found. Please install it manually or run the installer.
    echo You can download it from: https://launch4j.sourceforge.net/
    echo.
    echo For now, we'll create the JAR file which you can run directly.
    echo.
    echo ========================================
    echo JAR file created successfully!
    echo ========================================
    echo.
    echo Your Task Manager application is ready in the 'App' folder:
    echo - App\TaskManager.jar (JAR file - run with: java -jar TaskManager.jar)
    echo - App\lib\ (dependencies)
    echo - App\assets\logo.png (application logo)
    echo.
    echo To create an EXE, please install Launch4j and run this script again.
    echo.
    pause
    exit /b 0
)

REM Move EXE to App folder
if exist "TaskManager.exe" (
    move "TaskManager.exe" "App\TaskManager.exe"
    echo.
    echo ========================================
    echo Build completed successfully!
    echo ========================================
    echo.
    echo Your Task Manager application is ready in the 'App' folder:
    echo - App\TaskManager.exe (main executable)
    echo - App\TaskManager.jar (JAR file)
    echo - App\lib\ (dependencies)
    echo - App\assets\logo.png (application logo)
    echo.
    echo You can now distribute the entire 'App' folder!
    echo.
) else (
    echo EXE creation failed!
    pause
    exit /b 1
)

pause
