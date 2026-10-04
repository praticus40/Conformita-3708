@echo off
setlocal
echo Chiudi l'applicazione Conformita 37/08 prima di continuare.
pause

set DATADIR=%~dp0..\app\data
if not exist "%DATADIR%" (
  echo Cartella non trovata: %DATADIR%
  echo Esegui l'app almeno una volta o crea manualmente app\data
  exit /b 1
)

del /Q "%DATADIR%\conformita.mv.db" 2>nul
del /Q "%DATADIR%\conformita.trace.db" 2>nul
del /Q "%DATADIR%\conformita*.lock.db" 2>nul
del /Q "%DATADIR%\schema-version" 2>nul

echo Database H2 rimosso in %DATADIR%
echo Riavvia l'app per ricreare lo schema.
pause
