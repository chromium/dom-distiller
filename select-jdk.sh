#!/usr/bin/env bash
#
# Setup Java environment for Ubuntu/Debian with multiple JDKs
#

if [ "$(id -u)" = "0" ]; then	#already root
	update-alternatives --config java
else				#normal users
	sudo update-alternatives --config java
fi
JAVA_LOCATION=`readlink -f /usr/bin/java`
JAVA_HOME=`echo $JAVA_LOCATION | awk -F '/jre/bin/java' '{print $1}'`
CLASSPATH=$JAVA_HOME'/lib/dt.jar:'$JAVA_HOME'/lib/tools.jar'
export JAVA_HOME CLASSPATH
echo "DONE!\nRun 'source $0' again to change settings back."
