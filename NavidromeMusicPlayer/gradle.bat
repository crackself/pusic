@ECHO OFF

SET APP_HOME=%~dp0
SET JAVA_EXE=java

IF DEFINED JAVA_HOME (
    SET JAVA_EXE="%JAVA_HOME%\bin\java.exe"
)

IF NOT EXIST %JAVA_EXE% (
    ECHO ERROR: Java not found. Please set JAVA_HOME.
    EXIT /B 1
)

%JAVA_EXE% -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*
