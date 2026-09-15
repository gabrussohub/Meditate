@echo off
REM Copia os 10 audios da pasta "Sonia Meditacao" para assets/audio
set SRC=%~dp0..
set DST=%~dp0app\src\main\assets\audio
if not exist "%DST%" mkdir "%DST%"
copy /Y "%SRC%\AUD-*.mp3" "%DST%\" >nul 2>&1
copy /Y "%SRC%\AUD-*.m4a" "%DST%\" >nul 2>&1
echo Pronto! Arquivos em %DST%
dir "%DST%"
pause
