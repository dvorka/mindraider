echo off
cls
title MR Trigger
cd ..

if "%JAVA_HOME%" == "" (
  echo Error:
  echo   JAVA_HOME environment variable is NOT set!
  echo .
  echo Use My Computer/Properties/Advanced/Environment variables
  echo to set it e.g to C:\j2sdk1.5 Then don't forget to restart
  echo the shell!
  echo .
  echo Fallback to PATH...
  java -Xmx256m -Dlog4j.configuration=./conf/distribution.log4j.properties -jar run.jar %1 %2 %3 %4 %5
) else (
  rem echo Java home set to: %JAVA_HOME%
  "%JAVA_HOME%/bin/java" -Xmx256m -Dlog4j.configuration=./conf/distribution.log4j.properties -Dlog4j.configuration.debug=./conf/log4j.properties -jar run.jar %1 %2 %3 %4 %5
)


