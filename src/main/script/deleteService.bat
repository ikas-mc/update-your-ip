@echo off
set SERVICE_NAME=update-ip
set SERVICE_DESC=update ip service

set BASEDIR=%CD%

echo delete service...

"update-ip-service.exe" //DS//%SERVICE_NAME% %BASEDIR%\logs