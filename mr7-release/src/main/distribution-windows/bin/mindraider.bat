echo off
cls
title MR Trigger
cd ..

set JAVA_HOME=%CD%\jre1.6.0

echo Java home set to: %JAVA_HOME%
jre1.6.0\bin\java -Xmx256m -Dlog4j.configuration=./conf/distribution.log4j.properties -jar run.jar %1 %2 %3 %4 %5


