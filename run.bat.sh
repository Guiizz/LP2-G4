@echo off
cd /d "%~dp0"
dir /s /b src\*.java > sources.txt
javac -cp "libs\*" -d out @sources.txt
java -cp "out;libs\*" Main