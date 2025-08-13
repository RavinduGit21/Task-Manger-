@echo off
setlocal enabledelayedexpansion

set CLASSES_DIR=target\classes
set SRC_DIR=src\main\java
set LIB_DIR=lib
set SQLITE_JAR=%LIB_DIR%\sqlite-jdbc-3.45.3.0.jar
set SLF4J_API_JAR=%LIB_DIR%\slf4j-api-2.0.13.jar
set SLF4J_SIMPLE_JAR=%LIB_DIR%\slf4j-simple-2.0.13.jar

if not exist %LIB_DIR% (
  mkdir %LIB_DIR%
)

if not exist %SQLITE_JAR% (
  echo Downloading SQLite JDBC...
  powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar' -OutFile '%SQLITE_JAR%'" || goto :error
)

if not exist %SLF4J_API_JAR% (
  echo Downloading SLF4J API...
  powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar' -OutFile '%SLF4J_API_JAR%'" || goto :error
)

if not exist %SLF4J_SIMPLE_JAR% (
  echo Downloading SLF4J Simple...
  powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.13/slf4j-simple-2.0.13.jar' -OutFile '%SLF4J_SIMPLE_JAR%'" || goto :error
)

if exist %CLASSES_DIR% rmdir /s /q %CLASSES_DIR%
mkdir %CLASSES_DIR%

dir /s /b %SRC_DIR%\*.java > sources.txt

javac -source 1.8 -target 1.8 -encoding UTF-8 -d %CLASSES_DIR% @sources.txt || goto :error

del sources.txt >nul 2>&1

echo Running Task Manager...
java -cp %CLASSES_DIR%;%SQLITE_JAR%;%SLF4J_API_JAR%;%SLF4J_SIMPLE_JAR% com.example.taskmanager.Main

goto :eof

:error
echo.
echo Build failed. Ensure Java JDK 8 (javac) is installed and on PATH.
pause
exit /b 1 