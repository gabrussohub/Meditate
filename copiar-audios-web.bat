@echo off
REM Copia os audios para o webapp (docs\audio)
set SRC=%~dp0
set DST=%~dp0docs\audio
if not exist "%DST%" mkdir "%DST%"
copy /Y "%SRC%AUD-*.mp3" "%DST%\" >nul 2>&1
copy /Y "%SRC%AUD-*.m4a" "%DST%\" >nul 2>&1
echo Pronto! Arquivos em %DST%
dir "%DST%"
pause
