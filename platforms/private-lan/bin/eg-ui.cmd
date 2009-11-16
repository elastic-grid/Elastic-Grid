@echo off

title Elastic Grid UI

rem This script provides the command and control utility for starting
rem Elastic Grid administration console

rem Use local variables
setlocal

rem Set local variables
if "%EG_HOME%" == "" set EG_HOME=%~dp0..
set RIO_HOME=%EG_HOME%

set CONFIG=%EG_HOME%\config\ui.groovy
set command_line=%*

"%JAVA_HOME%\bin\java" -jar "%~dp0../lib/rio-ui.jar" %command_line%

