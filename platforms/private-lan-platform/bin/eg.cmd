@echo off

rem This script provides the command and control utility for starting
rem Rio services and the Rio command line interface

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
echo Cannot locate expected Jini (River) distribution, either set JINI_HOME or download Rio with dependencies, exiting
goto exitWithError

:haveJiniHome
set JINI_LIB=%JINI_HOME%\lib

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
set JAVACMD=%JAVA_HOME%\bin\java.exe
goto endOfJavaHome

:noJavaHome
set JAVACMD=java.exe
:endOfJavaHome

if "%JAVA_MEM_OPTIONS%" == "" set JAVA_MEM_OPTIONS=-Xms256m -Xmx256m

rem Parse command line
if "%1"=="" goto interactive
if "%1"=="start" goto start

:interactive
rem set cliExt=%EG_HOME%\config\rio_cli.config
rem set cliExt=""
set command_line=%*
set launchTarget=com.elasticgrid.tools.cli.CLI
set classpath=-cp "%EG_HOME%\lib\rio-cli.jar";"%JINI_LIB%\jsk-lib.jar";"%JINI_LIB%\jsk-platform.jar";"%EG_HOME%\lib\spring\spring.jar";"%EG_HOME%\lib\jakarta-commons\commons-logging.jar";"%EG_HOME%\lib\groovy\groovy-all-1.6.0.jar"
set props="-DRIO_HOME=%RIO_HOME% -DJINI_HOME=%JINI_HOME%"
"%JAVACMD%" %classpath% -Xms256m -Xmx256m ^
    -Djava.util.logging.config.file="%EG_HOME%\config\eg-cli.logging.properties" ^
    -DRIO_HOME="%EG_HOME%" -DJINI_HOME="%JINI_HOME%" -Djava.security.policy="%EG_HOME%\policy\policy.all" ^
    %launchTarget% %cliExt% %command_line%
goto end

:start

rem Get the service starter
shift
if "%1"=="" goto noService
set starterConfig="%EG_HOME%\config\start-%1.groovy"
if not exist "%starterConfig%" goto noStarter
shift

echo "starter config [%starterConfig%]"
set RIO_LOG_DIR="%EG_HOME%\logs"
set RIO_NATIVE_DIR="%EG_HOME%\lib\native";"%EG_HOME%\lib\hyperic"
set PATH=%PATH%;%RIO_NATIVE_DIR%

set classpath=-cp "%EG_HOME%\lib\boot.jar";"%JINI_HOME%\lib\start.jar";"%JAVA_HOME%\lib\tools.jar";"%EG_HOME%\lib\jakarta-commons\commons-logging.jar";"%EG_HOME%\lib\groovy\groovy-all-1.6.0.jar";"%EG_HOME%\lib\elastic-grid\elastic-grid-core-${pom.version}.jar"
set agentpath=-javaagent:"%EG_HOME%\lib\boot.jar"

set launchTarget=com.sun.jini.start.ServiceStarter

"%JAVA_HOME%\bin\java" -server %JAVA_MEM_OPTIONS% %classpath% %agentpath% ^
    -Djava.security.policy="%EG_HOME%\policy\policy.all" ^
    -Djava.protocol.handler.pkgs=net.jini.url ^
    -Djava.library.path=%RIO_NATIVE_DIR% ^
    -DJINI_HOME="%JINI_HOME%" ^
    -DRIO_HOME="%EG_HOME%" ^
    -DEG_HOME="%EG_HOME%" ^
    -Dorg.rioproject.home="%EG_HOME%" ^
    -DRIO_NATIVE_DIR=%RIO_NATIVE_DIR% ^
    -DRIO_LOG_DIR=%RIO_LOG_DIR% ^
    %launchTarget% ^
    %starterConfig%
goto end

:noStarter
echo Cannot locate expected service starter file [start-%1.config] in [%EG_HOME%\config], exiting"
goto exitWithError

:noService
echo "A service to start is required, exiting"

:exitWithError
exit /B 1

:end
endlocal
title Command Prompt
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
exit /B 0
