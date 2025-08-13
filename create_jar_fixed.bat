@echo off
echo Creating Task Manager JAR file with proper manifest...
echo.

REM Clean up any existing JAR
if exist "TaskManager.jar" del "TaskManager.jar"

REM Create JAR file with manifest
cd target\classes
jar cfm ..\TaskManager.jar ..\..\manifest.txt com\example\taskmanager\Main.class com\example\taskmanager\model\Task.class com\example\taskmanager\db\DatabaseHandler.class com\example\taskmanager\ui\MainUI.class com\example\taskmanager\ui\TaskTableModel.class com\example\taskmanager\ui\TaskDialog.class com\example\taskmanager\ui\PriorityCellRenderer.class com\example\taskmanager\ui\DatePickerPopup.class com\example\taskmanager\ui\SettingsDialog.class
cd ..\..

REM Copy to App folder
copy "TaskManager.jar" "App\TaskManager.jar"

echo.
echo JAR file created successfully!
echo Location: App\TaskManager.jar
echo.
echo Testing JAR file...
java -jar TaskManager.jar
echo.
pause
