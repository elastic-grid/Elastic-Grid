@echo off

title Elastic Grid UI

rem This script provides the command and control utility for starting
rem Elastic Grid administration console

rem Use local variables
setlocal

rem Set local variables
if "%EG_HOME%" == "" set EG_HOME=%~dp0..
set RIO_HOME=%EG_HOME%
if "%JINI_HOME%" == "" goto noJiniHome
goto haveJiniHome

:noJiniHome
if not exist "%EG_HOME%\lib\apache-river" goto jiniNotFound
set JINI_HOME=%EG_HOME%\lib\apache-river
goto haveJiniHome

:jiniNotFound
echo Cannot locate expected Jini (River) distribution, either set JINI_HOME or download Elastic Grid with dependencies, exiting
goto exitWithError

:haveJiniHome
set JINI_LIB=%JINI_HOME%\lib

set CONFIG=%EG_HOME%\config\ui.groovy
set command_line=%*

"%JAVA_HOME%\bin\java" -jar "%~dp0../lib/rio-ui.jar" %CONFIG% %command_line%

