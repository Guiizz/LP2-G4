@echo off
cd /d "%~dp0"
if not exist out mkdir out
echo Compilar...
powershell -Command "Get-ChildItem -Path src -Recurse -Filter *.java | Resolve-Path -Relative | Out-File -Encoding ascii sources.txt"
javac -encoding UTF-8 -cp "libs\*" -d out @sources.txt
if errorlevel 1 ( echo Erro de compilacao && pause && exit /b 1 )
echo Compilacao OK
java -cp "out;libs\*" Main
pause
