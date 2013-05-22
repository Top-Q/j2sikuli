@echo OFF
rem Runs Sikuli agent

set current_dir=%~dp0
set current_drive=%~d0
%current_drive%
cd %current_dir%

setlocal ENABLEDELAYEDEXPANSION

javaw -jar j2sikuli.jar

@echo ON
