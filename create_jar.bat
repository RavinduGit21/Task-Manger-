@echo off
echo Creating Task Manager JAR file manually...
echo.

REM Create JAR file
cd target\classes
jar cf ..\TaskManager.jar com\example\taskmanager\Main.class
jar uf ..\TaskManager.jar com\example\taskmanager\model\Task.class
jar uf ..\TaskManager.jar com\example\taskmanager\db\DatabaseHandler.class
jar uf ..\TaskManager.jar com\example\taskmanager\ui\MainUI.class
jar uf ..\TaskManager.jar com\example\taskmanager\ui\TaskTableModel.class
jar uf ..\TaskManager.jar com\example\taskmanager\ui\TaskDialog.class
jar uf ..\TaskManager.jar com\example\taskmanager\ui\PriorityCellRenderer.class
jar uf ..\TaskManager.jar com\example\taskmanager\ui\DatePickerPopup.class
jar uf ..\TaskManager.jar com\example\taskmanager\ui\SettingsDialog.class
cd ..\..

REM Add manifest
jar ufm TaskManager.jar manifest.txt

REM Copy to App folder
copy "TaskManager.jar" "App\TaskManager.jar"

echo.
echo JAR file created successfully!
echo Location: App\TaskManager.jar
echo.
pause
