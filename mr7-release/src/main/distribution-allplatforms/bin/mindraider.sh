#! /bin/bash
# MindRaider trigger by Martin Dvorak; mindraider@users.sourceforge.net

echo "Starting MindRaider..."
cd ..

export SCRIPT_HOME=`pwd`

if [ -z "$JAVA_HOME" ]
 then
   echo "To start MindRaider, JAVA_HOME environment property must be set!"
   java  -Xmx256m -Dlog4j.configuration=./conf/distribution.log4j.properties -classpath "$CLASSPATH" -jar run.jar $1 $2 $3 $4 $5
 else
   echo "Using JAVA_HOME: $JAVA_HOME"
  "$JAVA_HOME/bin/java" -Xmx256m -Dlog4j.configuration.debug=./conf/log4j.properties -Dlog4j.configuration=./conf/distribution.log4j.properties -classpath "$CLASSPATH" -jar run.jar $1 $2 $3 $4 $5
fi

# eof
