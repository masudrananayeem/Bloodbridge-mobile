@echo off
setlocal
set "GRADLE_VERSION=8.9"
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "INSTALL_DIR=%GRADLE_USER_HOME%\bloodbridge-bootstrap\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%INSTALL_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat"
if not exist "%GRADLE_BIN%" (
  if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"
  echo Downloading official Gradle %GRADLE_VERSION% distribution...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $archive=Join-Path '%INSTALL_DIR%' 'gradle-%GRADLE_VERSION%-bin.zip'; Invoke-WebRequest -UseBasicParsing 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile $archive; Expand-Archive -LiteralPath $archive -DestinationPath '%INSTALL_DIR%' -Force; Remove-Item $archive -Force"
  if errorlevel 1 exit /b 1
)
if not exist "%GRADLE_BIN%" (
  echo Gradle bootstrap failed. Check your internet connection and try again.
  exit /b 1
)
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
