@echo off
cls
set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_07
set PATH=%JAVA_HOME%\bin;%PATH%
set M2_REPO=C:\Documents and Settings\dvorka\.m2\repository

cd ..

rem mvn eclipse:eclipse
rem mvn package
mvn -e install
rem mvn compile
rem mvn clean
rem mvn assembly:assembly

cd bin