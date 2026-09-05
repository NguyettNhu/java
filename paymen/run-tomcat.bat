@echo off
setlocal

set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
set "CATALINA_HOME=C:\apache-tomcat-10.1"
set "CATALINA_BASE=C:\apache-tomcat-10.1"

copy /Y "%~dp0target\paymen.war" "%CATALINA_HOME%\webapps\paymen.war"
if errorlevel 1 exit /b 1

call "%CATALINA_HOME%\bin\catalina.bat" run
