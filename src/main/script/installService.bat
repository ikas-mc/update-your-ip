@echo off
set SERVICE_NAME=update-ip
set SERVICE_DESC=update ip service

set JAVA_HOME=%JAVA_HOME%

set BASEDIR=%CD%
set CLASSPATH=lib/*;conf
set MAIN_CLASS=ikas.java.project.updateip.Launcher
set STOP_CLASS=ikas.java.project.updateip.Launcher

echo install service...

"update-ip-service.exe" //IS//%SERVICE_NAME% ^
        --DisplayName="%SERVICE_NAME%" ^
        --Description="%SERVICE_DESC%" ^
        --Classpath="%CLASSPATH%" ^
        --JavaHome="%JAVA_HOME%" ^
        --Jvm="%JVM%" ^
        --JvmMs=40 ^
        --JvmMx=40 ^
        --Startup=auto ^
        ++JvmOptions=-Dfile.encoding=utf-8 ^
        --StartMode=jvm ^
        --StartClass=%MAIN_CLASS% ^
        --StartMethod=windowsService ^
        ++StartParams=start ^
        --StopMode=jvm ^
        --StopClass=%STOP_CLASS% ^
        --StopMethod=windowsService ^
        --StopParams=stop ^
        --LogPath=%BASEDIR%\logs ^
        --StdOutput=auto ^
        --StdError=auto ^
        --PidFile=%BASEDIR%\logs\pid
echo ok
pause

