@echo OFF
rem Create Sikuli Agent

set current_dir=%~dp0
set current_drive=%~d0

%RUNNER_ROOT%\thirdParty\ant\bin\ant.bat -f createAgent.xml 

@echo on