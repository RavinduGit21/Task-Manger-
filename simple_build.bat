@echo off
echo Simple Task Manager Build...
echo.

REM Create clean App folder
if exist "App" rmdir /s /q "App"
mkdir App
mkdir App\lib

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
    powershell -Command "try { Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-simple-1.7.36/slf4j-simple-1.7.36.jar' -OutFile 'lib\slf4j-simple-1.7.36.jar' -UseBasicParsing } catch { Write-Host 'Download failed' }"
)

REM Create target directory
if not exist "target\classes" mkdir target\classes

REM Compile Java code
echo Compiling Java code...
javac -cp "lib/*" -source 1.8 -target 1.8 -d target/classes src/main/java/com/example/taskmanager/Main.java src/main/java/com/example/taskmanager/model/Task.java src/main/java/com/example/taskmanager/db/DatabaseHandler.java src/main/java/com/example/taskmanager/ui/MainUI.java src/main/java/com/example/taskmanager/ui/TaskTableModel.java src/main/java/com/example/taskmanager/ui/TaskDialog.java src/main/java/com/example/taskmanager/ui/PriorityCellRenderer.java src/main/java/com/example/taskmanager/ui/DatePickerPopup.java src/main/java/com/example/taskmanager/ui/SettingsDialog.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Create simple JAR file
echo Creating JAR file...
cd target\classes
jar cf ..\TaskManager.jar com\example\taskmanager\Main.class com\example\taskmanager\model\Task.class com\example\taskmanager\db\DatabaseHandler.class com\example\taskmanager\ui\MainUI.class com\example\taskmanager\ui\TaskTableModel.class com\example\taskmanager\ui\TaskDialog.class com\example\taskmanager\ui\PriorityCellRenderer.class com\example\taskmanager\ui\DatePickerPopup.class com\example\taskmanager\ui\SettingsDialog.class
cd ..\..

REM Copy JAR to App folder
copy "TaskManager.jar" "App\TaskManager.jar"

REM Copy dependencies to App\lib
copy "lib\*.jar" "App\lib\"

REM Create simple run script
echo Creating run script...
(
echo @echo off
echo echo Starting Task Manager...
echo java -cp "TaskManager.jar;lib\*" com.example.taskmanager.Main
echo pause
) > "App\run.bat"

REM Create simple README
echo Creating README file...
(
echo Task Manager Application
echo =======================
echo.
echo To run the application:
echo 1. Double-click run.bat
echo.
echo Requirements:
echo - Java 8 or higher
echo.
echo Features:
echo - Dark theme UI
echo - Task management
echo - Priority levels with colors
echo - Search and filter
echo - Customizable fonts
) > "App\README.txt"

echo.
echo ========================================
echo Build completed!
echo ========================================
echo.
echo App folder contains:
echo - TaskManager.jar (application)
echo - lib\ (dependencies)
echo - run.bat (run script)
echo - README.txt (instructions)
echo.
echo To run: Double-click run.bat
echo.
pause
