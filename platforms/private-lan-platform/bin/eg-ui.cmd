@echo off

rem
rem Launches the Elastic Grid UI
rem

title Elastic Grid UI
set EG_HOME="%~dp0.."
set CONFIG=%EG_HOME%\config\ui.config
set command_line=%*

"%JAVA_HOME%\bin\java" -jar "%~dp0../lib/rio-ui.jar" %CONFIG% %command_line%

