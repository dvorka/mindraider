#! /bin/bash
# MindRaider trigger by Martin Dvorak; mindraider@users.sourceforge.net
clear

echo "Starting MindRaider..."
cd ..
export JAVA_HOME="`pwd`/jre1.6.0"

echo Java home set to: $JAVA_HOME
"$JAVA_HOME/bin/java" -Xmx256m -Dlog4j.configuration.debug=./conf/log4j.properties -Dlog4j.configuration=./conf/distribution.log4j.properties -jar run.jar $1 $2 $3 $4 $5


