@echo off
set IS_WINDOWS=false
echo %OS% | find "Windows" > nul && set IS_WINDOWS=true
if "%IS_WINDOWS%"=="true" (
	goto BEGIN
) else (
    goto ERROR
)

:BEGIN

@title "Configuring Disk %1"
@REM cls
color 07

@echo on
REM execute base on config
diskpart /s %1
goto END

:ERROR
echo "Current system is not Windows"
echo "%OS%"
:END